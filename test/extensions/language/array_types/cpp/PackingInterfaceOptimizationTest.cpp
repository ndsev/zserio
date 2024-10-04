#include "array_types/packing_interface_optimization/PackingInterfaceOptimization.h"
#include "gtest/gtest.h"
#include "test_utils/Assertions.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packing_interface_optimization
{

using allocator_type = PackingInterfaceOptimization::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class PackingInterfaceOptimizationTest : public ::testing::Test
{
protected:
    void fillUnpackedColors(vector_type<UnpackedColorStruct>& unpackedColors)
    {
        unpackedColors.emplace_back(true, UnpackedColorChoice{});
        unpackedColors.back().getColorChoice().setColorName("yellow");

        unpackedColors.emplace_back(false, UnpackedColorChoice{});
        unpackedColors.back().getColorChoice().setColorUnion(UnpackedColorUnion{});
        unpackedColors.back().getColorChoice().getColorUnion().setColorBitmask(
                UnpackedColorBitmask::Values::GREEN | UnpackedColorBitmask::Values::RED);

        unpackedColors.emplace_back(false, UnpackedColorChoice{});
        unpackedColors.back().getColorChoice().setColorUnion(UnpackedColorUnion{});
        unpackedColors.back().getColorChoice().getColorUnion().setColorEnum(UnpackedColorEnum::BLUE);
    }

    void fillMixedColors(vector_type<MixedColorStruct>& mixedColors)
    {
        mixedColors.emplace_back(true, MixedColorChoice{});
        mixedColors.back().getColorChoice().setColorName("purple");

        mixedColors.emplace_back(false, MixedColorChoice{});
        mixedColors.back().getColorChoice().setColorUnion(MixedColorUnion{});
        mixedColors.back().getColorChoice().getColorUnion().setColorBitmask(
                MixedColorBitmask::Values::BLUE | MixedColorBitmask::Values::GREEN);

        mixedColors.emplace_back(false, MixedColorChoice{});
        mixedColors.back().getColorChoice().setColorUnion(MixedColorUnion{});
        mixedColors.back().getColorChoice().getColorUnion().setColorEnum(MixedColorEnum::RED);
    }

    void fillPackedColors(vector_type<PackedColorStruct>& packedColors)
    {
        packedColors.emplace_back(true, PackedColorChoice{});
        packedColors.back().getColorChoice().setColorName("grey");

        packedColors.emplace_back(false, PackedColorChoice{});
        packedColors.back().getColorChoice().setColorUnion(PackedColorUnion{});
        packedColors.back().getColorChoice().getColorUnion().setColorBitmask(
                PackedColorBitmask::Values::BLUE | PackedColorBitmask::Values::RED);

        packedColors.emplace_back(false, PackedColorChoice{});
        packedColors.back().getColorChoice().setColorUnion(PackedColorUnion{});
        packedColors.back().getColorChoice().getColorUnion().setColorEnum(PackedColorEnum::GREEN);
    }

    void fillUnpackedColorsHolder(UnpackedColorsHolder& unpackedColorsHolder)
    {
        fillUnpackedColors(unpackedColorsHolder.getUnpackedColors());
        fillMixedColors(unpackedColorsHolder.getMixedColors());
    }

    void fillPackedColorsHolder(PackedColorsHolder& packedColorsHolder)
    {
        fillMixedColors(packedColorsHolder.getMixedColors());
        fillPackedColors(packedColorsHolder.getPackedColors());
    }

    void assertPackingInterfaceMethodsPresent(const std::string& typeName)
    {
        const std::string ctorDeclaration = typeName + "(ZserioPackingContext";
        const std::string ctorDefinition =
                typeName + "::" + typeName + "(" + typeName + "::ZserioPackingContext";
        ASSERT_METHOD_PRESENT(PATH, typeName, ctorDeclaration.c_str(), ctorDefinition.c_str());

        ASSERT_METHOD_PRESENT(PATH, typeName, "initPackingContext(", "::initPackingContext(");

        const std::string bitSizeOfMethodDeclaration = "bitSizeOf(ZserioPackingContext";
        const std::string bitSizeOfMethodDefinition = "bitSizeOf(" + typeName + "::ZserioPackingContext";
        ASSERT_METHOD_PRESENT(
                PATH, typeName, bitSizeOfMethodDeclaration.c_str(), bitSizeOfMethodDefinition.c_str());

        const std::string initializeOffsetsMethodDeclaration = "initializeOffsets(ZserioPackingContext";
        const std::string initializeOffsetsMethodDefinition =
                "initializeOffsets(" + typeName + "::ZserioPackingContext";
        ASSERT_METHOD_PRESENT(PATH, typeName, initializeOffsetsMethodDeclaration.c_str(),
                initializeOffsetsMethodDefinition.c_str());

        const std::string writeMethodDeclaration = "write(ZserioPackingContext";
        const std::string writeMethodDefinition = "write(" + typeName + "::ZserioPackingContext";
        ASSERT_METHOD_PRESENT(PATH, typeName, writeMethodDeclaration.c_str(), writeMethodDefinition.c_str());
    }

    void assertPackingInterfaceMethodsNotPresent(const std::string& typeName)
    {
        const std::string ctorDeclaration = typeName + "(ZserioPackingContext";
        const std::string ctorDefinition =
                typeName + "::" + typeName + "(" + typeName + "::ZserioPackingContext";
        ASSERT_METHOD_NOT_PRESENT(PATH, typeName, ctorDeclaration.c_str(), ctorDefinition.c_str());

        ASSERT_METHOD_NOT_PRESENT(PATH, typeName, "initPackingContext(", "::initPackingContext(");

        const std::string bitSizeOfMethodDeclaration = "bitSizeOf(ZserioPackingContext";
        const std::string bitSizeOfMethodDefinition = "bitSizeOf(" + typeName + "::ZserioPackingContext";
        ASSERT_METHOD_NOT_PRESENT(
                PATH, typeName, bitSizeOfMethodDeclaration.c_str(), bitSizeOfMethodDefinition.c_str());

        const std::string initializeOffsetsMethodDeclaration = "initializeOffsets(ZserioPackingContext";
        const std::string initializeOffsetsMethodDefinition =
                "initializeOffsets(" + typeName + "::ZserioPackingContext";
        ASSERT_METHOD_NOT_PRESENT(PATH, typeName, initializeOffsetsMethodDeclaration.c_str(),
                initializeOffsetsMethodDefinition.c_str());

        const std::string writeMethodDeclaration = "write(ZserioPackingContext";
        const std::string writeMethodDefinition = "write(" + typeName + "::ZserioPackingContext";
        ASSERT_METHOD_NOT_PRESENT(
                PATH, typeName, writeMethodDeclaration.c_str(), writeMethodDefinition.c_str());
    }

    static const std::string BLOB_NAME;
    static const std::string PATH;
};

const std::string PackingInterfaceOptimizationTest::BLOB_NAME =
        "language/array_types/packing_interface_optimization.blob";
const std::string PackingInterfaceOptimizationTest::PATH =
        "language/array_types/gen/array_types/packing_interface_optimization/";

TEST_F(PackingInterfaceOptimizationTest, writeReadFile)
{
    PackingInterfaceOptimization packingInterfaceOptimization;
    fillUnpackedColorsHolder(packingInterfaceOptimization.getUnpackedColorsHolder());
    fillPackedColorsHolder(packingInterfaceOptimization.getPackedColorsHolder());

    zserio::serializeToFile(packingInterfaceOptimization, BLOB_NAME);
    const auto readPackingInterfaceOptimization =
            zserio::deserializeFromFile<PackingInterfaceOptimization>(BLOB_NAME);
    ASSERT_EQ(packingInterfaceOptimization, readPackingInterfaceOptimization);
}

TEST_F(PackingInterfaceOptimizationTest, packingInterfaceOptimizationMethods)
{
    assertPackingInterfaceMethodsNotPresent("PackingInterfaceOptimization");
    ASSERT_STRING_IN_FILE_NOT_PRESENT(PATH + "PackingInterfaceOptimization.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, unpackedColorsHolderMethods)
{
    assertPackingInterfaceMethodsNotPresent("UnpackedColorsHolder");
    ASSERT_STRING_IN_FILE_NOT_PRESENT(PATH + "UnpackedColorsHolder.h", "class ZserioPackingContext");

    ASSERT_STRING_IN_FILE_NOT_PRESENT(PATH + "UnpackedColorsHolder.h",
            "::array_types::packing_interface_optimization::UnpackedColorStruct::ZserioPackingContext& "
            "context");

    ASSERT_STRING_IN_FILE_PRESENT(PATH + "UnpackedColorsHolder.h",
            "::array_types::packing_interface_optimization::MixedColorStruct::ZserioPackingContext& context");
}

TEST_F(PackingInterfaceOptimizationTest, packedColorsHolderMethods)
{
    assertPackingInterfaceMethodsNotPresent("PackedColorsHolder");
    ASSERT_STRING_IN_FILE_NOT_PRESENT(PATH + "PackedColorsHolder.h", "class ZserioPackingContext");

    ASSERT_STRING_IN_FILE_PRESENT(PATH + "PackedColorsHolder.h",
            "::array_types::packing_interface_optimization::MixedColorStruct::ZserioPackingContext& context");

    ASSERT_STRING_IN_FILE_PRESENT(PATH + "PackedColorsHolder.h",
            "::array_types::packing_interface_optimization::PackedColorStruct::ZserioPackingContext& context");
}

TEST_F(PackingInterfaceOptimizationTest, unpackedColorStructMethods)
{
    assertPackingInterfaceMethodsNotPresent("UnpackedColorStruct");
    ASSERT_STRING_IN_FILE_NOT_PRESENT(PATH + "UnpackedColorStruct.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, unpackedColorChoiceMethods)
{
    assertPackingInterfaceMethodsNotPresent("UnpackedColorChoice");
    ASSERT_STRING_IN_FILE_NOT_PRESENT(PATH + "UnpackedColorChoice.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, unpackedColorUnionMethods)
{
    assertPackingInterfaceMethodsNotPresent("UnpackedColorUnion");
    ASSERT_STRING_IN_FILE_NOT_PRESENT(PATH + "UnpackedColorUnion.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, unpackedColorEnumMethods)
{
    ASSERT_METHOD_NOT_PRESENT(PATH, "UnpackedColorEnum", "initPackingContext<::zserio::DeltaContext,",
            "initPackingContext(::zserio::DeltaContext&");
    ASSERT_METHOD_NOT_PRESENT(PATH, "UnpackedColorEnum", "bitSizeOf<::zserio::DeltaContext,",
            "bitSizeOf(::zserio::DeltaContext&");
    ASSERT_METHOD_NOT_PRESENT(PATH, "UnpackedColorEnum", "initializeOffsets<::zserio::DeltaContext,",
            "initializeOffsets(::zserio::DeltaContext&");
    ASSERT_METHOD_NOT_PRESENT(PATH, "UnpackedColorEnum",
            "read<::array_types::packing_interface_optimization::UnpackedColorEnum, ::zserio::DeltaContext",
            "read(::zserio::DeltaContext&");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, "UnpackedColorEnum", "write<::zserio::DeltaContext,", "write(::zserio::DeltaContext&");
}

TEST_F(PackingInterfaceOptimizationTest, unpackedColorBitmaskMethods)
{
    ASSERT_METHOD_NOT_PRESENT(PATH, "UnpackedColorBitmask", "UnpackedColorBitmask(::zserio::DeltaContext",
            "UnpackedColorBitmask::UnpackedColorBitmask(::zserio::DeltaContext");

    ASSERT_METHOD_NOT_PRESENT(PATH, "UnpackedColorBitmask", "initPackingContext(", "::initPackingContext(");
    ASSERT_METHOD_NOT_PRESENT(PATH, "UnpackedColorBitmask", "bitSizeOf(::zserio::DeltaContext",
            "::bitSizeOf(::zserio::DeltaContext");
    ASSERT_METHOD_NOT_PRESENT(PATH, "UnpackedColorBitmask", "initializeOffsets(::zserio::DeltaContext",
            "::initializeOffsets(::zserio::DeltaContext");
    ASSERT_METHOD_NOT_PRESENT(
            PATH, "UnpackedColorBitmask", "write(::zserio::DeltaContext", "::write(::zserio::DeltaContext");
}

TEST_F(PackingInterfaceOptimizationTest, mixedColorStructMethods)
{
    assertPackingInterfaceMethodsPresent("MixedColorStruct");
    ASSERT_STRING_IN_FILE_PRESENT(PATH + "MixedColorStruct.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, mixedColorChoiceMethods)
{
    assertPackingInterfaceMethodsPresent("MixedColorChoice");
    ASSERT_STRING_IN_FILE_PRESENT(PATH + "MixedColorChoice.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, mixedColorUnionMethods)
{
    assertPackingInterfaceMethodsPresent("MixedColorUnion");
    ASSERT_STRING_IN_FILE_PRESENT(PATH + "MixedColorUnion.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, mixedColorEnumMethods)
{
    ASSERT_METHOD_PRESENT(PATH, "MixedColorEnum", "initPackingContext<::zserio::DeltaContext,",
            "initPackingContext(::zserio::DeltaContext&");
    ASSERT_METHOD_PRESENT(
            PATH, "MixedColorEnum", "bitSizeOf<::zserio::DeltaContext,", "bitSizeOf(::zserio::DeltaContext&");
    ASSERT_METHOD_PRESENT(PATH, "MixedColorEnum", "initializeOffsets<::zserio::DeltaContext,",
            "initializeOffsets(::zserio::DeltaContext&");
    ASSERT_METHOD_PRESENT(PATH, "MixedColorEnum",
            "read<::array_types::packing_interface_optimization::MixedColorEnum, ::zserio::DeltaContext",
            "read(::zserio::DeltaContext&");
    ASSERT_METHOD_PRESENT(
            PATH, "MixedColorEnum", "write<::zserio::DeltaContext,", "write(::zserio::DeltaContext&");
}

TEST_F(PackingInterfaceOptimizationTest, mixedColorBitmaskMethods)
{
    ASSERT_METHOD_PRESENT(PATH, "MixedColorBitmask", "MixedColorBitmask(::zserio::DeltaContext",
            "MixedColorBitmask::MixedColorBitmask(::zserio::DeltaContext");

    ASSERT_METHOD_PRESENT(PATH, "MixedColorBitmask", "initPackingContext(", "::initPackingContext(");
    ASSERT_METHOD_PRESENT(PATH, "MixedColorBitmask", "bitSizeOf(::zserio::DeltaContext",
            "::bitSizeOf(::zserio::DeltaContext");
    ASSERT_METHOD_PRESENT(PATH, "MixedColorBitmask", "initializeOffsets(::zserio::DeltaContext",
            "::initializeOffsets(::zserio::DeltaContext");
    ASSERT_METHOD_PRESENT(
            PATH, "MixedColorBitmask", "write(::zserio::DeltaContext", "::write(::zserio::DeltaContext");
}

TEST_F(PackingInterfaceOptimizationTest, packedColorStructMethods)
{
    assertPackingInterfaceMethodsPresent("PackedColorStruct");
    ASSERT_STRING_IN_FILE_PRESENT(PATH + "PackedColorStruct.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, packedColorChoiceMethods)
{
    assertPackingInterfaceMethodsPresent("PackedColorChoice");
    ASSERT_STRING_IN_FILE_PRESENT(PATH + "PackedColorChoice.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, packedColorUnionMethods)
{
    assertPackingInterfaceMethodsPresent("PackedColorUnion");
    ASSERT_STRING_IN_FILE_PRESENT(PATH + "PackedColorUnion.h", "class ZserioPackingContext");
}

TEST_F(PackingInterfaceOptimizationTest, packedColorEnumMethods)
{
    ASSERT_METHOD_PRESENT(PATH, "PackedColorEnum", "initPackingContext<::zserio::DeltaContext,",
            "initPackingContext(::zserio::DeltaContext&");
    ASSERT_METHOD_PRESENT(
            PATH, "PackedColorEnum", "bitSizeOf<::zserio::DeltaContext,", "bitSizeOf(::zserio::DeltaContext&");
    ASSERT_METHOD_PRESENT(PATH, "PackedColorEnum", "initializeOffsets<::zserio::DeltaContext,",
            "initializeOffsets(::zserio::DeltaContext&");
    ASSERT_METHOD_PRESENT(PATH, "PackedColorEnum",
            "read<::array_types::packing_interface_optimization::PackedColorEnum, ::zserio::DeltaContext",
            "read(::zserio::DeltaContext&");
    ASSERT_METHOD_PRESENT(
            PATH, "PackedColorEnum", "write<::zserio::DeltaContext,", "write(::zserio::DeltaContext&");
}

TEST_F(PackingInterfaceOptimizationTest, packedColorBitmaskMethods)
{
    ASSERT_METHOD_PRESENT(PATH, "PackedColorBitmask", "PackedColorBitmask(::zserio::DeltaContext",
            "PackedColorBitmask::PackedColorBitmask(::zserio::DeltaContext");

    ASSERT_METHOD_PRESENT(PATH, "PackedColorBitmask", "initPackingContext(", "::initPackingContext(");
    ASSERT_METHOD_PRESENT(PATH, "PackedColorBitmask", "bitSizeOf(::zserio::DeltaContext",
            "::bitSizeOf(::zserio::DeltaContext");
    ASSERT_METHOD_PRESENT(PATH, "PackedColorBitmask", "initializeOffsets(::zserio::DeltaContext",
            "::initializeOffsets(::zserio::DeltaContext");
    ASSERT_METHOD_PRESENT(
            PATH, "PackedColorBitmask", "write(::zserio::DeltaContext", "::write(::zserio::DeltaContext");
}

} // namespace packing_interface_optimization
} // namespace array_types
