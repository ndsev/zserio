#include <array>
#include <memory>
#include <string>

#include "complex_allocation/MainStructure.h"
#include "gtest/gtest.h"
#include "test_utils/MemoryResources.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

using namespace zserio::literals;
using namespace test_utils;

namespace complex_allocation
{

using allocator_type = MainStructure::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

class ComplexAllocationTest : public ::testing::Test
{
protected:
    void writeMainStructure(zserio::BitStreamWriter& writer, bool hasArray)
    {
        // stringField
        writer.writeString(zserio::string<>(STRING_FIELD));

        // stringArray[]
        writer.writeVarSize(STRING_ARRAY_SIZE);
        writer.writeString(std::string(STRING_ARRAY_ELEMENT0));
        writer.writeString(std::string(STRING_ARRAY_ELEMENT1));
        writer.writeString(std::string(STRING_ARRAY_ELEMENT2));

        // hasArray
        writer.writeBool(hasArray);

        // choiceField
        if (hasArray)
        {
            writer.writeVarSize(CHOICE_COMPOUND_ARRAY_SIZE);
            writer.writeBits(CHOICE_COMPOUND_ELEMENT0_VALUE16, 16);
            writer.writeBool(CHOICE_COMPOUND_ELEMENT0_IS_VALID);
            writer.writeBits(CHOICE_COMPOUND_ELEMENT1_VALUE16, 16);
            writer.writeBool(CHOICE_COMPOUND_ELEMENT1_IS_VALID);
        }
        else
        {
            writer.writeBits(CHOICE_COMPOUND_ELEMENT0_VALUE16, 16);
            writer.writeBool(CHOICE_COMPOUND_ELEMENT0_IS_VALID);
        }

        // unionField
        if (hasArray)
        {
            writer.writeVarSize(
                    static_cast<uint32_t>(allocation_union::AllocationUnion::ChoiceTag::CHOICE_array));
            writer.writeVarSize(UNION_COMPOUND_ARRAY_SIZE);
        }
        else
        {
            writer.writeVarSize(
                    static_cast<uint32_t>(allocation_union::AllocationUnion::ChoiceTag::CHOICE_compound));
        }
        writer.writeBits(UNION_COMPOUND_ELEMENT0_VALUE16, 16);
        writer.writeBool(UNION_COMPOUND_ELEMENT0_IS_VALID);

        // structField
        writer.writeVarSize(STRUCT_BIT7_ARRAY_SIZE);
        writer.writeBits(STRUCT_BIT7_ARRAY_ELEMENT0, 7);
        writer.writeBits(STRUCT_BIT7_ARRAY_ELEMENT1, 7);
        writer.writeBits(STRUCT_BIT7_ARRAY_ELEMENT2, 7);
        writer.writeString(zserio::string<>(STRUCT_STRING_FIELD));
        writer.writeString(zserio::string<>(STRUCT_DEFAULT_STRING_FIELD));
        writer.writeVarSize(STRUCT_PACKED_UINT16_ARRAY_SIZE);
        writer.writeBool(true);
        writer.writeBits(STRUCT_PACKED_UINT16_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(STRUCT_PACKED_UINT16_ARRAY_ELEMENT0, 16);
        writer.writeSignedBits(STRUCT_PACKED_UINT16_ARRAY_DELTA, STRUCT_PACKED_UINT16_ARRAY_MAX_BIT_NUMBER + 1);
        writer.writeSignedBits(STRUCT_PACKED_UINT16_ARRAY_DELTA, STRUCT_PACKED_UINT16_ARRAY_MAX_BIT_NUMBER + 1);
        writer.writeVarSize(STRUCT_PACKED_ELEMENT_ARRAY_SIZE);
        // x0
        writer.writeBool(true);
        writer.writeBits(STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X, 32);
        // y0
        writer.writeBool(true);
        writer.writeBits(STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y, 32);
        // z0
        writer.writeBool(true);
        writer.writeBits(STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z, 32);
        // x1
        writer.writeSignedBits(
                STRUCT_PACKED_ELEMENT_ARRAY_DELTA, STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER + 1);
        // y1
        writer.writeSignedBits(
                STRUCT_PACKED_ELEMENT_ARRAY_DELTA, STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER + 1);
        // z1
        writer.writeSignedBits(
                STRUCT_PACKED_ELEMENT_ARRAY_DELTA, STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER + 1);
        // x2
        writer.writeSignedBits(
                STRUCT_PACKED_ELEMENT_ARRAY_DELTA, STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER + 1);
        // y2
        writer.writeSignedBits(
                STRUCT_PACKED_ELEMENT_ARRAY_DELTA, STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER + 1);
        // z2
        writer.writeSignedBits(
                STRUCT_PACKED_ELEMENT_ARRAY_DELTA, STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER + 1);

        // structOptionalField
        writer.writeBool(true);
        writer.writeVarSize(STRUCT_OPTIONAL_NAMES0_SIZE);
        writer.writeString(std::string(STRUCT_OPTIONAL_NAMES0_ELEMENT0));
        writer.writeString(std::string(STRUCT_OPTIONAL_NAMES0_ELEMENT1));
        writer.writeBool(true);

        writer.writeBool(true);
        writer.writeVarSize(STRUCT_OPTIONAL_NAMES1_SIZE);
        writer.writeString(std::string(STRUCT_OPTIONAL_NAMES1_ELEMENT0));
        writer.writeBool(false);

        // externalField
        writer.writeVarSize(EXTERNAL_FIELD_VAR_SIZE);
        writer.writeBits(EXTERNAL_FIELD_DATA >> (8U - (EXTERNAL_FIELD_VAR_SIZE % 8U)), EXTERNAL_FIELD_VAR_SIZE);

        // externalArray
        writer.writeVarSize(EXTERNAL_ARRAY_SIZE);
        writer.writeVarSize(EXTERNAL_ARRAY_ELEMENT0_VAR_SIZE);
        writer.writeBits(EXTERNAL_ARRAY_ELEMENT0_DATA >> (8U - (EXTERNAL_ARRAY_ELEMENT0_VAR_SIZE % 8U)),
                EXTERNAL_ARRAY_ELEMENT0_VAR_SIZE);
        writer.writeVarSize(EXTERNAL_ARRAY_ELEMENT1_VAR_SIZE);
        writer.writeBits(EXTERNAL_ARRAY_ELEMENT1_DATA >> (8U - (EXTERNAL_ARRAY_ELEMENT1_VAR_SIZE % 8U)),
                EXTERNAL_ARRAY_ELEMENT1_VAR_SIZE);

        // bytesField
        writer.writeVarSize(BYTES_FIELD_VAR_SIZE);
        writer.writeBits(BYTES_FIELD_DATA, BYTES_FIELD_VAR_SIZE * 8);

        // bytesArray
        writer.writeVarSize(BYTES_ARRAY_SIZE);
        writer.writeVarSize(BYTES_ARRAY_ELEMENT0_VAR_SIZE);
        writer.writeBits(BYTES_ARRAY_ELEMENT0_DATA, BYTES_ARRAY_ELEMENT0_VAR_SIZE * 8);
        writer.writeVarSize(BYTES_ARRAY_ELEMENT1_VAR_SIZE);
        writer.writeBits(BYTES_ARRAY_ELEMENT1_DATA, BYTES_ARRAY_ELEMENT1_VAR_SIZE * 8);
    }

    void fillStringArray(vector_type<string_type>& stringArray, const allocator_type& allocator)
    {
        stringArray.reserve(STRING_ARRAY_SIZE);
        stringArray.emplace_back(STRING_ARRAY_ELEMENT0, allocator);
        stringArray.emplace_back(STRING_ARRAY_ELEMENT1, allocator);
        stringArray.emplace_back(STRING_ARRAY_ELEMENT2, allocator);
    }

    void fillChoiceField(
            allocation_choice::AllocationChoice& choiceField, const allocator_type& allocator, bool hasArray)
    {
        choiceField.initialize(hasArray);
        if (hasArray)
        {
            vector_type<allocation_choice::ChoiceCompound> array(allocator);
            array.reserve(CHOICE_COMPOUND_ARRAY_SIZE);
            array.emplace_back(CHOICE_COMPOUND_ELEMENT0_VALUE16, CHOICE_COMPOUND_ELEMENT0_IS_VALID, allocator);
            array.emplace_back(CHOICE_COMPOUND_ELEMENT1_VALUE16, CHOICE_COMPOUND_ELEMENT1_IS_VALID, allocator);
            choiceField.setArray(std::move(array));
        }
        else
        {
            choiceField.setCompound(allocation_choice::ChoiceCompound(
                    CHOICE_COMPOUND_ELEMENT0_VALUE16, CHOICE_COMPOUND_ELEMENT0_IS_VALID, allocator));
        }
    }

    void fillUnionField(
            allocation_union::AllocationUnion& unionField, const allocator_type& allocator, bool hasArray)
    {
        if (hasArray)
        {
            vector_type<allocation_union::UnionCompound> array(allocator);
            array.reserve(UNION_COMPOUND_ARRAY_SIZE);
            array.emplace_back(UNION_COMPOUND_ELEMENT0_VALUE16, UNION_COMPOUND_ELEMENT0_IS_VALID, allocator);
            unionField.setArray(std::move(array));
        }
        else
        {
            unionField.setCompound(allocation_union::UnionCompound(
                    UNION_COMPOUND_ELEMENT0_VALUE16, UNION_COMPOUND_ELEMENT0_IS_VALID, allocator));
        }
    }

    void fillStructField(allocation_struct::AllocationStruct& structField)
    {
        auto& bit7Array = structField.getBit7Array();
        bit7Array.reserve(STRUCT_BIT7_ARRAY_SIZE);
        bit7Array.push_back(STRUCT_BIT7_ARRAY_ELEMENT0);
        bit7Array.push_back(STRUCT_BIT7_ARRAY_ELEMENT1);
        bit7Array.push_back(STRUCT_BIT7_ARRAY_ELEMENT2);
        auto& stringField = structField.getStringField();
        stringField.assign(STRUCT_STRING_FIELD);
        auto& packedUInt16Array = structField.getPackedUInt16Array();
        packedUInt16Array.reserve(STRUCT_PACKED_UINT16_ARRAY_SIZE);
        packedUInt16Array.push_back(STRUCT_PACKED_UINT16_ARRAY_ELEMENT0);
        packedUInt16Array.push_back(STRUCT_PACKED_UINT16_ARRAY_ELEMENT1);
        packedUInt16Array.push_back(STRUCT_PACKED_UINT16_ARRAY_ELEMENT2);
        auto& packedElementArray = structField.getPackedElementArray();
        packedElementArray.reserve(STRUCT_PACKED_ELEMENT_ARRAY_SIZE);
        packedElementArray.emplace_back(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X,
                STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y, STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z);
        packedElementArray.emplace_back(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA),
                STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA),
                STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA));
        packedElementArray.emplace_back(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA) * 2,
                STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA) * 2,
                STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA) * 2);
    }

    void fillOptionalField(allocation_struct_optional::AllocationStructOptional& structOptionalField,
            const allocator_type& allocator)
    {
        vector_type<string_type> names0(allocator);
        names0.reserve(STRUCT_OPTIONAL_NAMES0_SIZE);
        names0.emplace_back(STRUCT_OPTIONAL_NAMES0_ELEMENT0, allocator);
        names0.emplace_back(STRUCT_OPTIONAL_NAMES0_ELEMENT1, allocator);
        structOptionalField.setNames(std::move(names0));
        structOptionalField.setHasNext(true);

        allocation_struct_optional::AllocationStructOptional others(allocator);
        vector_type<string_type> names1(allocator);
        names1.reserve(STRUCT_OPTIONAL_NAMES1_SIZE);
        names1.emplace_back(STRUCT_OPTIONAL_NAMES1_ELEMENT0, allocator);
        others.setNames(std::move(names1));
        others.setHasNext(false);
        structOptionalField.setOthers(std::move(others));
    }

    BitBuffer createExternalField(const allocator_type& allocator)
    {
        const std::array<uint8_t, 2> externalFieldData = {
                static_cast<uint8_t>(EXTERNAL_FIELD_DATA >> 8U), static_cast<uint8_t>(EXTERNAL_FIELD_DATA)};

        return BitBuffer(externalFieldData.data(), EXTERNAL_FIELD_VAR_SIZE, allocator);
    }

    void fillExternalArray(vector_type<BitBuffer>& externalArray, const allocator_type& allocator)
    {
        externalArray.reserve(EXTERNAL_ARRAY_SIZE);
        externalArray.emplace_back(&EXTERNAL_ARRAY_ELEMENT0_DATA, EXTERNAL_ARRAY_ELEMENT0_VAR_SIZE, allocator);
        const std::array<uint8_t, 2> externalElement1Data = {
                static_cast<uint8_t>(EXTERNAL_ARRAY_ELEMENT1_DATA >> 8U),
                static_cast<uint8_t>(EXTERNAL_ARRAY_ELEMENT1_DATA)};
        externalArray.emplace_back(externalElement1Data.data(), EXTERNAL_ARRAY_ELEMENT1_VAR_SIZE, allocator);
    }

    vector_type<uint8_t> createBytesField(const allocator_type& allocator)
    {
        return vector_type<uint8_t>(
                {static_cast<uint8_t>(BYTES_FIELD_DATA >> 8U), static_cast<uint8_t>(BYTES_FIELD_DATA)},
                allocator);
    }

    void fillBytesArray(vector_type<vector_type<uint8_t>>& bytesArray, const allocator_type& allocator)
    {
        bytesArray.reserve(BYTES_ARRAY_SIZE);
        const std::array<uint8_t, 2> bytesArrayElement0Data = {
                static_cast<uint8_t>(BYTES_ARRAY_ELEMENT0_DATA >> 8U),
                static_cast<uint8_t>(BYTES_ARRAY_ELEMENT0_DATA)};
        bytesArray.emplace_back(bytesArrayElement0Data.begin(), bytesArrayElement0Data.end(), allocator);
        bytesArray.emplace_back(BYTES_ARRAY_ELEMENT1_VAR_SIZE, BYTES_ARRAY_ELEMENT1_DATA, allocator);
    }

    void fillMainStructure(MainStructure& mainStructure, const allocator_type& allocator, bool hasArray)
    {
        // stringField
        mainStructure.getStringField() = STRING_FIELD;

        // stringArray[]
        fillStringArray(mainStructure.getStringArray(), allocator);

        // hasArray
        mainStructure.setHasArray(hasArray);

        // choiceField
        fillChoiceField(mainStructure.getChoiceField(), allocator, hasArray);

        // unionField
        fillUnionField(mainStructure.getUnionField(), allocator, hasArray);

        // structField
        fillStructField(mainStructure.getStructField());

        // structOptionalField
        fillOptionalField(mainStructure.getStructOptionalField(), allocator);

        // externalField
        mainStructure.setExternalField(createExternalField(allocator));

        // externalArray
        fillExternalArray(mainStructure.getExternalArray(), allocator);

        // bytesField
        mainStructure.setBytesField(createBytesField(allocator));

        // bytesArray
        fillBytesArray(mainStructure.getBytesArray(), allocator);
    }

    void checkMainStructure(const MainStructure& mainStructure, bool hasArray)
    {
        // stringField
        ASSERT_EQ(STRING_FIELD, mainStructure.getStringField());

        // stringArray[]
        const auto& stringArray = mainStructure.getStringArray();
        ASSERT_EQ(STRING_ARRAY_SIZE, stringArray.size());
        ASSERT_EQ(STRING_ARRAY_ELEMENT0, stringArray[0]);
        ASSERT_EQ(STRING_ARRAY_ELEMENT1, stringArray[1]);
        ASSERT_EQ(STRING_ARRAY_ELEMENT2, stringArray[2]);

        // hasArray
        ASSERT_EQ(hasArray, mainStructure.getHasArray());

        // choiceField
        ASSERT_EQ(hasArray, mainStructure.getChoiceField().getHasArray());
        if (hasArray)
        {
            const auto& choiceFieldArray = mainStructure.getChoiceField().getArray();
            ASSERT_EQ(CHOICE_COMPOUND_ARRAY_SIZE, choiceFieldArray.size());
            ASSERT_EQ(CHOICE_COMPOUND_ELEMENT0_VALUE16, choiceFieldArray[0].getValue16());
            ASSERT_EQ(CHOICE_COMPOUND_ELEMENT0_IS_VALID, choiceFieldArray[0].getIsValid());
            ASSERT_EQ(CHOICE_COMPOUND_ELEMENT1_VALUE16, choiceFieldArray[1].getValue16());
            ASSERT_EQ(CHOICE_COMPOUND_ELEMENT1_IS_VALID, choiceFieldArray[1].getIsValid());
        }
        else
        {
            const auto& choiceFieldCompound = mainStructure.getChoiceField().getCompound();
            ASSERT_EQ(CHOICE_COMPOUND_ELEMENT0_VALUE16, choiceFieldCompound.getValue16());
            ASSERT_EQ(CHOICE_COMPOUND_ELEMENT0_IS_VALID, choiceFieldCompound.getIsValid());
        }

        // unionField
        if (hasArray)
        {
            ASSERT_EQ(allocation_union::AllocationUnion::ChoiceTag::CHOICE_array,
                    mainStructure.getUnionField().choiceTag());
            const auto& unionFieldArray = mainStructure.getUnionField().getArray();
            ASSERT_EQ(UNION_COMPOUND_ARRAY_SIZE, unionFieldArray.size());
            ASSERT_EQ(UNION_COMPOUND_ELEMENT0_VALUE16, unionFieldArray[0].getValue16());
            ASSERT_EQ(UNION_COMPOUND_ELEMENT0_IS_VALID, unionFieldArray[0].getIsValid());
        }
        else
        {
            ASSERT_EQ(allocation_union::AllocationUnion::ChoiceTag::CHOICE_compound,
                    mainStructure.getUnionField().choiceTag());
            const auto& unionFieldCompound = mainStructure.getUnionField().getCompound();
            ASSERT_EQ(UNION_COMPOUND_ELEMENT0_VALUE16, unionFieldCompound.getValue16());
            ASSERT_EQ(UNION_COMPOUND_ELEMENT0_IS_VALID, unionFieldCompound.getIsValid());
        }

        // structField
        const auto& structField = mainStructure.getStructField();
        const auto& bit7Array = structField.getBit7Array();
        ASSERT_EQ(STRUCT_BIT7_ARRAY_SIZE, bit7Array.size());
        ASSERT_EQ(STRUCT_BIT7_ARRAY_ELEMENT0, bit7Array[0]);
        ASSERT_EQ(STRUCT_BIT7_ARRAY_ELEMENT1, bit7Array[1]);
        ASSERT_EQ(STRUCT_BIT7_ARRAY_ELEMENT2, bit7Array[2]);
        ASSERT_EQ(STRUCT_STRING_FIELD, structField.getStringField());
        ASSERT_EQ(STRUCT_DEFAULT_STRING_FIELD, structField.getDefaultStringField());
        const auto& packedUInt16Array = structField.getPackedUInt16Array();
        ASSERT_EQ(STRUCT_PACKED_UINT16_ARRAY_SIZE, packedUInt16Array.size());
        ASSERT_EQ(STRUCT_PACKED_UINT16_ARRAY_ELEMENT0, packedUInt16Array[0]);
        ASSERT_EQ(STRUCT_PACKED_UINT16_ARRAY_ELEMENT1, packedUInt16Array[1]);
        ASSERT_EQ(STRUCT_PACKED_UINT16_ARRAY_ELEMENT2, packedUInt16Array[2]);
        const auto& packedElementArray = structField.getPackedElementArray();
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_SIZE, packedElementArray.size());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X, packedElementArray[0].getValueX());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA),
                packedElementArray[1].getValueX());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA) * 2,
                packedElementArray[2].getValueX());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y, packedElementArray[0].getValueY());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA),
                packedElementArray[1].getValueY());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA) * 2,
                packedElementArray[2].getValueY());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z, packedElementArray[0].getValueZ());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA),
                packedElementArray[1].getValueZ());
        ASSERT_EQ(STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z +
                        static_cast<uint32_t>(STRUCT_PACKED_ELEMENT_ARRAY_DELTA) * 2,
                packedElementArray[2].getValueZ());

        // structOptionalField
        const auto& optionalField0 = mainStructure.getStructOptionalField();
        const auto& optionalField0Names = optionalField0.getNames();
        ASSERT_EQ(STRUCT_OPTIONAL_NAMES0_SIZE, optionalField0Names.size());
        ASSERT_EQ(STRUCT_OPTIONAL_NAMES0_ELEMENT0, optionalField0Names[0]);
        ASSERT_EQ(STRUCT_OPTIONAL_NAMES0_ELEMENT1, optionalField0Names[1]);
        ASSERT_EQ(true, optionalField0.getHasNext());
        const auto& optionalField1 = optionalField0.getOthers();
        const auto& optionalField1Names = optionalField1.getNames();
        ASSERT_EQ(STRUCT_OPTIONAL_NAMES1_SIZE, optionalField1Names.size());
        ASSERT_EQ(STRUCT_OPTIONAL_NAMES1_ELEMENT0, optionalField1Names[0]);
        ASSERT_EQ(false, optionalField1.getHasNext());

        // externalField
        const auto& externalField = mainStructure.getExternalField();
        ASSERT_EQ(EXTERNAL_FIELD_VAR_SIZE, externalField.getBitSize());
        const uint8_t* externalFieldBuffer = externalField.getBuffer();
        const uint16_t externalFieldData =
                (static_cast<uint16_t>(externalFieldBuffer[0] << 8U)) | (externalFieldBuffer[1]);
        ASSERT_EQ(EXTERNAL_FIELD_DATA, externalFieldData);

        // externalArray
        const auto& externalArray = mainStructure.getExternalArray();
        ASSERT_EQ(EXTERNAL_ARRAY_SIZE, externalArray.size());
        const auto& externalArrayElement0 = externalArray[0];
        ASSERT_EQ(EXTERNAL_ARRAY_ELEMENT0_VAR_SIZE, externalArrayElement0.getBitSize());
        const uint8_t* externalBufferElement0 = externalArrayElement0.getBuffer();
        ASSERT_EQ(EXTERNAL_ARRAY_ELEMENT0_DATA, externalBufferElement0[0]);
        const auto& externalArrayElement1 = externalArray[1];
        ASSERT_EQ(EXTERNAL_ARRAY_ELEMENT1_VAR_SIZE, externalArrayElement1.getBitSize());
        const uint8_t* externalBufferElement1 = externalArrayElement1.getBuffer();
        const uint16_t externalDataElement1 =
                (static_cast<uint16_t>(externalBufferElement1[0] << 8U)) | (externalBufferElement1[1]);
        ASSERT_EQ(EXTERNAL_ARRAY_ELEMENT1_DATA, externalDataElement1);

        // bytesField
        const auto& bytesField = mainStructure.getBytesField();
        ASSERT_EQ(BYTES_FIELD_VAR_SIZE, bytesField.size());
        const uint16_t bytesFieldData = (static_cast<uint16_t>(bytesField[0] << 8U)) | (bytesField[1]);
        ASSERT_EQ(BYTES_FIELD_DATA, bytesFieldData);

        // bytesArray
        const auto& bytesArray = mainStructure.getBytesArray();
        ASSERT_EQ(BYTES_ARRAY_SIZE, bytesArray.size());
        const auto& bytesArrayElement0 = bytesArray[0];
        ASSERT_EQ(BYTES_ARRAY_ELEMENT0_VAR_SIZE, bytesArrayElement0.size());
        const uint16_t bytesDataElement0 =
                (static_cast<uint16_t>(bytesArrayElement0[0] << 8U)) | (bytesArrayElement0[1]);
        ASSERT_EQ(BYTES_ARRAY_ELEMENT0_DATA, bytesDataElement0);
        const auto& bytesArrayElement1 = bytesArray[1];
        ASSERT_EQ(BYTES_ARRAY_ELEMENT1_VAR_SIZE, bytesArrayElement1.size());
        ASSERT_EQ(BYTES_ARRAY_ELEMENT1_DATA, bytesArrayElement1[0]);
    }

    void checkReadConstructor(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        zserio::BitStreamWriter writer(bitBuffer);
        writeMainStructure(writer, hasArray);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(reader, allocator);
            checkMainStructure(mainStructure, hasArray);

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkCopyConstructor(bool hasArray)
    {
        // if allocator is propagating, use invalid memory resource
        const bool hasPropagatingAllocator = hasCopyPropagatingAllocator();
        HeapMemoryResource heapMemoryResource;
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter memoryResourceScopedSetter(hasPropagatingAllocator
                        ? static_cast<zserio::pmr::MemoryResource&>(invalidMemoryResource)
                        : static_cast<zserio::pmr::MemoryResource&>(heapMemoryResource));

        zserio::BitStreamWriter writer(bitBuffer);
        writeMainStructure(writer, hasArray);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestMemoryResource<1024 * 4> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(reader, allocator);

            // call copy constructor
            const size_t numAllocations = memoryResource.getNumAllocations();
            const size_t allocatedSize = memoryResource.getAllocatedSize();
            MainStructure mainStructureCopy(mainStructure);
            checkMainStructure(mainStructureCopy, hasArray);
            ASSERT_EQ(mainStructure, mainStructureCopy);

            // check that copy has been allocated the same memory as the original
            size_t totalNumAllocations = memoryResource.getNumAllocations();
            size_t totalAllocatedSize = memoryResource.getAllocatedSize();
            if (!hasPropagatingAllocator)
            {
                totalNumAllocations += heapMemoryResource.getNumAllocations();
                totalAllocatedSize += heapMemoryResource.getAllocatedSize();
            }
            ASSERT_EQ(numAllocations * 2, totalNumAllocations);
            ASSERT_EQ(allocatedSize * 2, totalAllocatedSize);

            // check memory fragmentation in used memory resources
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
            if (!hasPropagatingAllocator)
            {
                ASSERT_EQ(0, heapMemoryResource.getNumDeallocations());
            }
        }

        // check memory leaks in used memory resources
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
        if (!hasPropagatingAllocator)
        {
            ASSERT_EQ(heapMemoryResource.getNumDeallocations(), heapMemoryResource.getNumAllocations());
        }
    }

    void checkMoveConstructor(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        zserio::BitStreamWriter writer(bitBuffer);
        writeMainStructure(writer, hasArray);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(reader, allocator);

            // call move constructor
            const size_t numAllocations = memoryResource.getNumAllocations();
            MainStructure mainStructureMove(std::move(mainStructure));
            checkMainStructure(mainStructureMove, hasArray);

            // check that move constructor does not allocate anything
            ASSERT_EQ(numAllocations, memoryResource.getNumAllocations());

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkCopyAssignmentOperator(bool hasArray)
    {
        // if allocator is propagating, use invalid memory resource
        const bool hasPropagatingAllocator =
                std::allocator_traits<allocator_type>::propagate_on_container_copy_assignment::value;

        InvalidMemoryResource invalidMemoryResource;
        HeapMemoryResource heapMemoryResource;
        MemoryResourceScopedSetter memoryResourceScopedSetter(hasPropagatingAllocator
                        ? static_cast<zserio::pmr::MemoryResource&>(invalidMemoryResource)
                        : static_cast<zserio::pmr::MemoryResource&>(heapMemoryResource));

        zserio::BitStreamWriter writer(bitBuffer);
        writeMainStructure(writer, hasArray);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestMemoryResource<1024 * 4> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(reader, allocator);

            // call copy assignment operator
            const size_t numAllocations = memoryResource.getNumAllocations();
            const size_t allocatedSize = memoryResource.getAllocatedSize();
            MainStructure mainStructureCopyAssignment = mainStructure;
            checkMainStructure(mainStructureCopyAssignment, hasArray);
            ASSERT_EQ(mainStructure, mainStructureCopyAssignment);

            // check that copy has been allocated the same memory as the original
            size_t totalNumAllocations = memoryResource.getNumAllocations();
            size_t totalAllocatedSize = memoryResource.getAllocatedSize();
            if (!hasPropagatingAllocator)
            {
                totalNumAllocations += heapMemoryResource.getNumAllocations();
                totalAllocatedSize += heapMemoryResource.getAllocatedSize();
            }
            ASSERT_EQ(numAllocations * 2, totalNumAllocations);
            ASSERT_EQ(allocatedSize * 2, totalAllocatedSize);

            // check memory fragmentation in used memory resources
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
            if (!hasPropagatingAllocator)
            {
                ASSERT_EQ(0, heapMemoryResource.getNumDeallocations());
            }
        }

        // check memory leaks in used memory resources
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
        if (!hasPropagatingAllocator)
        {
            ASSERT_EQ(heapMemoryResource.getNumDeallocations(), heapMemoryResource.getNumAllocations());
        }
    }

    void checkMoveAssignmentOperator(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        zserio::BitStreamWriter writer(bitBuffer);
        writeMainStructure(writer, hasArray);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(reader, allocator);

            // call move assignment operator
            const size_t numAllocations = memoryResource.getNumAllocations();
            MainStructure mainStructureMoveAssignment = std::move(mainStructure);
            checkMainStructure(mainStructureMoveAssignment, hasArray);

            // check that move assignment operator does not allocate anything
            ASSERT_EQ(numAllocations, memoryResource.getNumAllocations());

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkPropagatingCopyConstructor(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        zserio::BitStreamWriter writer(bitBuffer);
        writeMainStructure(writer, hasArray);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        TestMemoryResource<> memoryResource1("Memory Resource #1");
        TestMemoryResource<> memoryResource2("Memory Resource #2");
        TestMemoryResource<> memoryResource3("Memory Resource #3");
        {
            const allocator_type allocator1(&memoryResource1);
            MainStructure mainStructure(reader, allocator1);

            // replace last string in string array by string stored in different memory resource
            auto& stringArray = mainStructure.getStringArray();
            const allocator_type allocator2(&memoryResource2);
            stringArray.pop_back();
            stringArray.emplace_back(STRING_ARRAY_ELEMENT2, allocator2);

            // call propagating copy constructor
            const size_t numAllocations1 = memoryResource1.getNumAllocations();
            const size_t numDeallocations1 = memoryResource1.getNumDeallocations();
            const size_t allocatedSize1 = memoryResource1.getAllocatedSize();
            const size_t numAllocations2 = memoryResource2.getNumAllocations();
            const size_t allocatedSize2 = memoryResource2.getAllocatedSize();
            const allocator_type allocator3(&memoryResource3);
            MainStructure mainStructurePropagatingCopy(::zserio::PropagateAllocator, mainStructure, allocator3);
            checkMainStructure(mainStructurePropagatingCopy, hasArray);
            ASSERT_EQ(mainStructure, mainStructurePropagatingCopy);

            // check that propagating copy has been allocated in new memory resource only
            ASSERT_EQ(numAllocations1, memoryResource1.getNumAllocations());
            ASSERT_EQ(numDeallocations1, memoryResource1.getNumDeallocations());
            ASSERT_EQ(allocatedSize1, memoryResource1.getAllocatedSize());
            ASSERT_EQ(numAllocations2, memoryResource2.getNumAllocations());
            ASSERT_EQ(allocatedSize2, memoryResource2.getAllocatedSize());
            ASSERT_EQ(
                    numAllocations1 - numDeallocations1 + numAllocations2, memoryResource3.getNumAllocations());
            ASSERT_EQ(allocatedSize1 + allocatedSize2, memoryResource3.getAllocatedSize());

            // check memory fragmentation in used memory resources
            ASSERT_EQ(1, memoryResource1.getNumDeallocations()); // one string has been deallocated
            ASSERT_EQ(0, memoryResource2.getNumDeallocations());
            ASSERT_EQ(0, memoryResource3.getNumDeallocations());
        }

        // check memory leaks in used memory resources
        ASSERT_EQ(memoryResource1.getNumDeallocations(), memoryResource1.getNumAllocations());
        ASSERT_EQ(memoryResource2.getNumDeallocations(), memoryResource2.getNumAllocations());
        ASSERT_EQ(memoryResource3.getNumDeallocations(), memoryResource3.getNumAllocations());
    }

    void checkAllocatorConstructor(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(allocator);
            fillMainStructure(mainStructure, allocator, hasArray);
            checkMainStructure(mainStructure, hasArray);

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkFieldConstructor(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        TestMemoryResource<1024 * 3> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);

            // stringArray
            vector_type<string_type> stringArray(allocator);
            fillStringArray(stringArray, allocator);

            // choiceField
            allocation_choice::AllocationChoice choiceField(allocator);
            fillChoiceField(choiceField, allocator, hasArray);

            // unionField
            allocation_union::AllocationUnion unionField(allocator);
            fillUnionField(unionField, allocator, hasArray);

            // structField
            allocation_struct::AllocationStruct structField(allocator);
            fillStructField(structField);

            // structOptionalField
            allocation_struct_optional::AllocationStructOptional structOptionalField(allocator);
            fillOptionalField(structOptionalField, allocator);

            // externalArray
            vector_type<BitBuffer> externalArray(allocator);
            fillExternalArray(externalArray, allocator);

            // bytesArray
            vector_type<vector_type<uint8_t>> bytesArray(allocator);
            fillBytesArray(bytesArray, allocator);

            if (hasCopyPropagatingAllocator())
            {
                MainStructure mainStructure(STRING_FIELD, stringArray, hasArray, choiceField, unionField,
                        structField, structOptionalField, createExternalField(allocator), externalArray,
                        createBytesField(allocator), bytesArray, allocator);
                checkMainStructure(mainStructure, hasArray);

                // check memory fragmentation in used memory resource
                ASSERT_EQ(0, memoryResource.getNumDeallocations());
            }
            else
            {
                MainStructure mainStructure(STRING_FIELD, std::move(stringArray), hasArray,
                        std::move(choiceField), std::move(unionField), std::move(structField),
                        std::move(structOptionalField), createExternalField(allocator),
                        std::move(externalArray), createBytesField(allocator), std::move(bytesArray),
                        allocator);
                checkMainStructure(mainStructure, hasArray);

                // check memory fragmentation in used memory resource
                ASSERT_EQ(0, memoryResource.getNumDeallocations());
            }
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkSetters(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        TestMemoryResource<1024 * 3> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);

            // stringArray
            vector_type<string_type> stringArray(allocator);
            fillStringArray(stringArray, allocator);

            // choiceField
            allocation_choice::AllocationChoice choiceField(allocator);
            fillChoiceField(choiceField, allocator, hasArray);

            // unionField
            allocation_union::AllocationUnion unionField(allocator);
            fillUnionField(unionField, allocator, hasArray);

            // structField
            allocation_struct::AllocationStruct structField(allocator);
            fillStructField(structField);

            // structOptionalField
            allocation_struct_optional::AllocationStructOptional structOptionalField(allocator);
            fillOptionalField(structOptionalField, allocator);

            // externalArray
            vector_type<BitBuffer> externalArray(allocator);
            fillExternalArray(externalArray, allocator);

            // bytesArray
            vector_type<vector_type<uint8_t>> bytesArray(allocator);
            fillBytesArray(bytesArray, allocator);

            MainStructure mainStructure(allocator);
            if (hasCopyPropagatingAllocator())
            {
                mainStructure.setStringField(string_type(STRING_FIELD, allocator));
                mainStructure.setStringArray(stringArray);
                mainStructure.setHasArray(hasArray);
                mainStructure.setChoiceField(choiceField);
                mainStructure.setUnionField(unionField);
                mainStructure.setStructField(structField);
                mainStructure.setStructOptionalField(structOptionalField);
                mainStructure.setExternalField(createExternalField(allocator));
                mainStructure.setExternalArray(externalArray);
                mainStructure.setBytesField(createBytesField(allocator));
                mainStructure.setBytesArray(bytesArray);
            }
            else
            {
                mainStructure.setStringField(string_type(STRING_FIELD, allocator));
                mainStructure.setStringArray(std::move(stringArray));
                mainStructure.setHasArray(hasArray);
                mainStructure.setChoiceField(std::move(choiceField));
                mainStructure.setUnionField(std::move(unionField));
                mainStructure.setStructField(std::move(structField));
                mainStructure.setStructOptionalField(std::move(structOptionalField));
                mainStructure.setExternalField(createExternalField(allocator));
                mainStructure.setExternalArray(std::move(externalArray));
                mainStructure.setBytesField(createBytesField(allocator));
                mainStructure.setBytesArray(std::move(bytesArray));
            }

            checkMainStructure(mainStructure, hasArray);

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkBitSizeOf(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        zserio::BitStreamWriter writer(bitBuffer);
        writeMainStructure(writer, hasArray);
        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());

        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(reader, allocator);

            // check that no further memory allocation will take place
            const size_t numAllocations = memoryResource.getNumAllocations();
            const size_t allocatedSize = memoryResource.getAllocatedSize();
            const size_t expectedBitSize =
                    (hasArray) ? MAIN_STRUCTURE_WITH_ARRAYS_BIT_SIZE : MAIN_STRUCTURE_WITHOUT_ARRAYS_BIT_SIZE;
            ASSERT_EQ(expectedBitSize, mainStructure.bitSizeOf(0));
            ASSERT_EQ(numAllocations, memoryResource.getNumAllocations());
            ASSERT_EQ(allocatedSize, memoryResource.getAllocatedSize());

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkInitializeOffsets(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        zserio::BitStreamWriter writer(bitBuffer);
        writeMainStructure(writer, hasArray);
        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());

        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(reader, allocator);

            // check that no further memory allocation will take place
            const size_t numAllocations = memoryResource.getNumAllocations();
            const size_t allocatedSize = memoryResource.getAllocatedSize();
            const size_t bitPosition = 1;
            const size_t expectedBitSize =
                    (hasArray) ? MAIN_STRUCTURE_WITH_ARRAYS_BIT_SIZE : MAIN_STRUCTURE_WITHOUT_ARRAYS_BIT_SIZE;
            ASSERT_EQ(bitPosition + expectedBitSize, mainStructure.initializeOffsets(bitPosition));
            ASSERT_EQ(numAllocations, memoryResource.getNumAllocations());
            ASSERT_EQ(allocatedSize, memoryResource.getAllocatedSize());

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkOperatorEquality(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        TestMemoryResource<> memoryResource1("Memory Resource #1");
        TestMemoryResource<> memoryResource2("Memory Resource #2");
        {
            const allocator_type allocator1(&memoryResource1);
            MainStructure mainStructure1(allocator1);
            fillMainStructure(mainStructure1, allocator1, hasArray);

            const allocator_type allocator2(&memoryResource2);
            MainStructure mainStructure2(allocator2);
            fillMainStructure(mainStructure2, allocator2, hasArray);

            // check that no further memory allocation will take place
            const size_t numAllocations1 = memoryResource1.getNumAllocations();
            const size_t allocatedSize1 = memoryResource1.getAllocatedSize();
            const size_t numAllocations2 = memoryResource2.getNumAllocations();
            const size_t allocatedSize2 = memoryResource2.getAllocatedSize();
            ASSERT_TRUE(mainStructure1 == mainStructure2);
            ASSERT_EQ(numAllocations1, memoryResource1.getNumAllocations());
            ASSERT_EQ(allocatedSize1, memoryResource1.getAllocatedSize());
            ASSERT_EQ(numAllocations2, memoryResource2.getNumAllocations());
            ASSERT_EQ(allocatedSize2, memoryResource2.getAllocatedSize());

            // check memory fragmentation in used memory resources
            ASSERT_EQ(0, memoryResource1.getNumDeallocations());
            ASSERT_EQ(0, memoryResource2.getNumDeallocations());
        }

        // check memory leaks in used memory resources
        ASSERT_EQ(memoryResource1.getNumDeallocations(), memoryResource1.getNumAllocations());
        ASSERT_EQ(memoryResource2.getNumDeallocations(), memoryResource2.getNumAllocations());
    }

    void checkHashCode(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        TestMemoryResource<> memoryResource1("Memory Resource #1");
        TestMemoryResource<> memoryResource2("Memory Resource #2");
        {
            const allocator_type allocator1(&memoryResource1);
            MainStructure mainStructure1(allocator1);
            fillMainStructure(mainStructure1, allocator1, hasArray);

            const allocator_type allocator2(&memoryResource2);
            MainStructure mainStructure2(allocator2);
            fillMainStructure(mainStructure2, allocator2, hasArray);

            // check that no further memory allocation will take place
            const size_t numAllocations1 = memoryResource1.getNumAllocations();
            const size_t allocatedSize1 = memoryResource1.getAllocatedSize();
            const size_t numAllocations2 = memoryResource2.getNumAllocations();
            const size_t allocatedSize2 = memoryResource2.getAllocatedSize();
            ASSERT_EQ(mainStructure1.hashCode(), mainStructure2.hashCode());
            ASSERT_EQ(numAllocations1, memoryResource1.getNumAllocations());
            ASSERT_EQ(allocatedSize1, memoryResource1.getAllocatedSize());
            ASSERT_EQ(numAllocations2, memoryResource2.getNumAllocations());
            ASSERT_EQ(allocatedSize2, memoryResource2.getAllocatedSize());

            // check memory fragmentation in used memory resources
            ASSERT_EQ(0, memoryResource1.getNumDeallocations());
            ASSERT_EQ(0, memoryResource2.getNumDeallocations());
        }

        // check memory leaks in used memory resources
        ASSERT_EQ(memoryResource1.getNumDeallocations(), memoryResource1.getNumAllocations());
        ASSERT_EQ(memoryResource2.getNumDeallocations(), memoryResource2.getNumAllocations());
    }

    void checkWrite(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(allocator);
            fillMainStructure(mainStructure, allocator, hasArray);

            // call bitSizeOf to allocate possible packing context for packed arrays
            const size_t expectedBitSize =
                    (hasArray) ? MAIN_STRUCTURE_WITH_ARRAYS_BIT_SIZE : MAIN_STRUCTURE_WITHOUT_ARRAYS_BIT_SIZE;
            ASSERT_EQ(expectedBitSize, mainStructure.bitSizeOf(0));

            // check that no further memory allocation will take place
            const size_t numAllocations = memoryResource.getNumAllocations();
            const size_t allocatedSize = memoryResource.getAllocatedSize();
            zserio::BitStreamWriter writer(bitBuffer);
            mainStructure.write(writer);
            ASSERT_EQ(expectedBitSize, writer.getBitPosition());
            ASSERT_EQ(numAllocations, memoryResource.getNumAllocations());
            ASSERT_EQ(allocatedSize, memoryResource.getAllocatedSize());

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkFuncConstString(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(allocator);
            fillMainStructure(mainStructure, allocator, hasArray);

            // check that no further memory allocation will take place
            const size_t numAllocations = memoryResource.getNumAllocations();
            const size_t allocatedSize = memoryResource.getAllocatedSize();
            ASSERT_EQ("This is constant string longer than 32 bytes!"_sv, mainStructure.funcConstString());
            ASSERT_EQ(numAllocations, memoryResource.getNumAllocations());
            ASSERT_EQ(allocatedSize, memoryResource.getAllocatedSize());

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    void checkFuncConstCompound(bool hasArray)
    {
        // check that default memory resource won't be used
        InvalidMemoryResource invalidMemoryResource;
        MemoryResourceScopedSetter invalidMemoryResourceScopedSetter(invalidMemoryResource);

        TestMemoryResource<> memoryResource("Memory Resource #1");
        {
            const allocator_type allocator(&memoryResource);
            MainStructure mainStructure(allocator);
            fillMainStructure(mainStructure, allocator, hasArray);

            // check that no further memory allocation will take place
            const size_t numAllocations = memoryResource.getNumAllocations();
            const size_t allocatedSize = memoryResource.getAllocatedSize();
            const allocation_struct_optional::AllocationStructOptional& expectedConstCompound =
                    mainStructure.getStructOptionalField();
            ASSERT_EQ(expectedConstCompound, mainStructure.funcConstCompound());
            ASSERT_EQ(numAllocations, memoryResource.getNumAllocations());
            ASSERT_EQ(allocatedSize, memoryResource.getAllocatedSize());

            // check memory fragmentation in used memory resource
            ASSERT_EQ(0, memoryResource.getNumDeallocations());
        }

        // check memory leaks in used memory resource
        ASSERT_EQ(memoryResource.getNumDeallocations(), memoryResource.getNumAllocations());
    }

    static const bool ARRAY_IN_UNION_AND_CHOICE;
    static const bool COMPOUND_IN_UNION_AND_CHOICE;

private:
    bool hasCopyPropagatingAllocator()
    {
        // if allocator is propagating, use invalid memory resource
        InvalidMemoryResource dummyMemoryResource;
        allocator_type dummyAllocator(&dummyMemoryResource);

        return (dummyAllocator ==
                std::allocator_traits<allocator_type>::select_on_container_copy_construction(dummyAllocator));
    }

    static const char* const STRING_FIELD;

    static const uint32_t STRING_ARRAY_SIZE;
    static const char* const STRING_ARRAY_ELEMENT0;
    static const char* const STRING_ARRAY_ELEMENT1;
    static const char* const STRING_ARRAY_ELEMENT2;

    static const uint32_t CHOICE_COMPOUND_ARRAY_SIZE;
    static const uint16_t CHOICE_COMPOUND_ELEMENT0_VALUE16;
    static const bool CHOICE_COMPOUND_ELEMENT0_IS_VALID;
    static const uint16_t CHOICE_COMPOUND_ELEMENT1_VALUE16;
    static const bool CHOICE_COMPOUND_ELEMENT1_IS_VALID;

    static const uint32_t UNION_COMPOUND_ARRAY_SIZE;
    static const uint16_t UNION_COMPOUND_ELEMENT0_VALUE16;
    static const bool UNION_COMPOUND_ELEMENT0_IS_VALID;

    static const uint32_t STRUCT_BIT7_ARRAY_SIZE;
    static const uint8_t STRUCT_BIT7_ARRAY_ELEMENT0;
    static const uint8_t STRUCT_BIT7_ARRAY_ELEMENT1;
    static const uint8_t STRUCT_BIT7_ARRAY_ELEMENT2;
    static const char* const STRUCT_STRING_FIELD;
    static const char* const STRUCT_DEFAULT_STRING_FIELD;
    static const uint32_t STRUCT_PACKED_UINT16_ARRAY_SIZE;
    static const uint16_t STRUCT_PACKED_UINT16_ARRAY_ELEMENT0;
    static const uint16_t STRUCT_PACKED_UINT16_ARRAY_ELEMENT1;
    static const uint16_t STRUCT_PACKED_UINT16_ARRAY_ELEMENT2;
    static const int8_t STRUCT_PACKED_UINT16_ARRAY_DELTA;
    static const uint8_t STRUCT_PACKED_UINT16_ARRAY_MAX_BIT_NUMBER;
    static const uint32_t STRUCT_PACKED_ELEMENT_ARRAY_SIZE;
    static const uint32_t STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X;
    static const uint32_t STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y;
    static const uint32_t STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z;
    static const int8_t STRUCT_PACKED_ELEMENT_ARRAY_DELTA;
    static const uint8_t STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER;

    static const uint32_t STRUCT_OPTIONAL_NAMES0_SIZE;
    static const char* const STRUCT_OPTIONAL_NAMES0_ELEMENT0;
    static const char* const STRUCT_OPTIONAL_NAMES0_ELEMENT1;
    static const uint32_t STRUCT_OPTIONAL_NAMES1_SIZE;
    static const char* const STRUCT_OPTIONAL_NAMES1_ELEMENT0;

    static const uint8_t EXTERNAL_FIELD_VAR_SIZE;
    static const uint16_t EXTERNAL_FIELD_DATA;

    static const uint32_t EXTERNAL_ARRAY_SIZE;
    static const uint8_t EXTERNAL_ARRAY_ELEMENT0_VAR_SIZE;
    static const uint8_t EXTERNAL_ARRAY_ELEMENT0_DATA;
    static const uint8_t EXTERNAL_ARRAY_ELEMENT1_VAR_SIZE;
    static const uint16_t EXTERNAL_ARRAY_ELEMENT1_DATA;

    static const uint8_t BYTES_FIELD_VAR_SIZE;
    static const uint16_t BYTES_FIELD_DATA;

    static const uint32_t BYTES_ARRAY_SIZE;
    static const uint8_t BYTES_ARRAY_ELEMENT0_VAR_SIZE;
    static const uint16_t BYTES_ARRAY_ELEMENT0_DATA;
    static const uint8_t BYTES_ARRAY_ELEMENT1_VAR_SIZE;
    static const uint8_t BYTES_ARRAY_ELEMENT1_DATA;

    static const size_t MAIN_STRUCTURE_WITH_ARRAYS_BIT_SIZE;
    static const size_t MAIN_STRUCTURE_WITHOUT_ARRAYS_BIT_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const bool ComplexAllocationTest::ARRAY_IN_UNION_AND_CHOICE = true;
const bool ComplexAllocationTest::COMPOUND_IN_UNION_AND_CHOICE = false;

const char* const ComplexAllocationTest::STRING_FIELD = "String Field Must Be Longer Than 32 Bytes";

const uint32_t ComplexAllocationTest::STRING_ARRAY_SIZE = 3;
const char* const ComplexAllocationTest::STRING_ARRAY_ELEMENT0 =
        "String Array Element0 Must Be Longer Than 32 Bytes";
const char* const ComplexAllocationTest::STRING_ARRAY_ELEMENT1 =
        "String Array Element1 Must Be Longer Than 32 Bytes";
const char* const ComplexAllocationTest::STRING_ARRAY_ELEMENT2 =
        "String Array Element2 Must Be Longer Than 32 Bytes";

const uint32_t ComplexAllocationTest::CHOICE_COMPOUND_ARRAY_SIZE = 2;
const uint16_t ComplexAllocationTest::CHOICE_COMPOUND_ELEMENT0_VALUE16 = 0xAB;
const bool ComplexAllocationTest::CHOICE_COMPOUND_ELEMENT0_IS_VALID = true;
const uint16_t ComplexAllocationTest::CHOICE_COMPOUND_ELEMENT1_VALUE16 = 0xCD;
const bool ComplexAllocationTest::CHOICE_COMPOUND_ELEMENT1_IS_VALID = false;

const uint32_t ComplexAllocationTest::UNION_COMPOUND_ARRAY_SIZE = 1;
const uint16_t ComplexAllocationTest::UNION_COMPOUND_ELEMENT0_VALUE16 = 0xEF;
const bool ComplexAllocationTest::UNION_COMPOUND_ELEMENT0_IS_VALID = true;

const uint32_t ComplexAllocationTest::STRUCT_BIT7_ARRAY_SIZE = 3;
const uint8_t ComplexAllocationTest::STRUCT_BIT7_ARRAY_ELEMENT0 = 0x2B;
const uint8_t ComplexAllocationTest::STRUCT_BIT7_ARRAY_ELEMENT1 = 0x4D;
const uint8_t ComplexAllocationTest::STRUCT_BIT7_ARRAY_ELEMENT2 = 0x6F;
const char* const ComplexAllocationTest::STRUCT_STRING_FIELD =
        "Structure String Field Must Be Longer Than 32 Bytes";
const char* const ComplexAllocationTest::STRUCT_DEFAULT_STRING_FIELD =
        "Structure Default String Field Must Be Longer Than 32 Bytes";
const uint32_t ComplexAllocationTest::STRUCT_PACKED_UINT16_ARRAY_SIZE = 3;
const uint16_t ComplexAllocationTest::STRUCT_PACKED_UINT16_ARRAY_ELEMENT0 = 0xCAFC;
const uint16_t ComplexAllocationTest::STRUCT_PACKED_UINT16_ARRAY_ELEMENT1 = 0xCAFD;
const uint16_t ComplexAllocationTest::STRUCT_PACKED_UINT16_ARRAY_ELEMENT2 = 0xCAFE;
const int8_t ComplexAllocationTest::STRUCT_PACKED_UINT16_ARRAY_DELTA = 1;
const uint8_t ComplexAllocationTest::STRUCT_PACKED_UINT16_ARRAY_MAX_BIT_NUMBER = 1;
const uint32_t ComplexAllocationTest::STRUCT_PACKED_ELEMENT_ARRAY_SIZE = 3;
const uint32_t ComplexAllocationTest::STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_X = 0;
const uint32_t ComplexAllocationTest::STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Y = 10;
const uint32_t ComplexAllocationTest::STRUCT_PACKED_ELEMENT_ARRAY_ELEMENT0_Z = 100;
const int8_t ComplexAllocationTest::STRUCT_PACKED_ELEMENT_ARRAY_DELTA = 1;
const uint8_t ComplexAllocationTest::STRUCT_PACKED_ELEMENT_ARRAY_MAX_BIT_NUMBER = 1;

const uint32_t ComplexAllocationTest::STRUCT_OPTIONAL_NAMES0_SIZE = 2;
const char* const ComplexAllocationTest::STRUCT_OPTIONAL_NAMES0_ELEMENT0 =
        "Optional Name00 Must Be Longer Than 32 Bytes";
const char* const ComplexAllocationTest::STRUCT_OPTIONAL_NAMES0_ELEMENT1 =
        "Optional Name01 Must Be Longer Than 32 Bytes";
const uint32_t ComplexAllocationTest::STRUCT_OPTIONAL_NAMES1_SIZE = 1;
const char* const ComplexAllocationTest::STRUCT_OPTIONAL_NAMES1_ELEMENT0 =
        "Optional Name10 Must Be Longer Than 32 Bytes";

const uint8_t ComplexAllocationTest::EXTERNAL_FIELD_VAR_SIZE = 11;
const uint16_t ComplexAllocationTest::EXTERNAL_FIELD_DATA = 0xABE0;

const uint32_t ComplexAllocationTest::EXTERNAL_ARRAY_SIZE = 2;
const uint8_t ComplexAllocationTest::EXTERNAL_ARRAY_ELEMENT0_VAR_SIZE = 7;
const uint8_t ComplexAllocationTest::EXTERNAL_ARRAY_ELEMENT0_DATA = 0xAE;
const uint8_t ComplexAllocationTest::EXTERNAL_ARRAY_ELEMENT1_VAR_SIZE = 15;
const uint16_t ComplexAllocationTest::EXTERNAL_ARRAY_ELEMENT1_DATA = 0xEA;

const uint8_t ComplexAllocationTest::BYTES_FIELD_VAR_SIZE = 2;
const uint16_t ComplexAllocationTest::BYTES_FIELD_DATA = 0xC0DE;

const uint32_t ComplexAllocationTest::BYTES_ARRAY_SIZE = 2;
const uint8_t ComplexAllocationTest::BYTES_ARRAY_ELEMENT0_VAR_SIZE = 2;
const uint16_t ComplexAllocationTest::BYTES_ARRAY_ELEMENT0_DATA = 0xCAFE;
const uint8_t ComplexAllocationTest::BYTES_ARRAY_ELEMENT1_VAR_SIZE = 1;
const uint8_t ComplexAllocationTest::BYTES_ARRAY_ELEMENT1_DATA = 0xC0;

const size_t ComplexAllocationTest::MAIN_STRUCTURE_WITH_ARRAYS_BIT_SIZE = 3978;
const size_t ComplexAllocationTest::MAIN_STRUCTURE_WITHOUT_ARRAYS_BIT_SIZE = 3945;

TEST_F(ComplexAllocationTest, readConstructor)
{
    checkReadConstructor(ARRAY_IN_UNION_AND_CHOICE);
    checkReadConstructor(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, copyConstructor)
{
    checkCopyConstructor(ARRAY_IN_UNION_AND_CHOICE);
    checkCopyConstructor(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, moveConstructor)
{
    checkMoveConstructor(ARRAY_IN_UNION_AND_CHOICE);
    checkMoveConstructor(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, copyAssignmentOperator)
{
    checkCopyAssignmentOperator(ARRAY_IN_UNION_AND_CHOICE);
    checkCopyAssignmentOperator(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, moveAssignmentOperator)
{
    checkMoveAssignmentOperator(ARRAY_IN_UNION_AND_CHOICE);
    checkMoveAssignmentOperator(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, propagatingCopyConstructor)
{
    checkPropagatingCopyConstructor(ARRAY_IN_UNION_AND_CHOICE);
    checkPropagatingCopyConstructor(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, allocatorConstructor)
{
    checkAllocatorConstructor(ARRAY_IN_UNION_AND_CHOICE);
    checkAllocatorConstructor(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, fieldConstructor)
{
    checkFieldConstructor(ARRAY_IN_UNION_AND_CHOICE);
    checkFieldConstructor(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, setters)
{
    checkSetters(ARRAY_IN_UNION_AND_CHOICE);
    checkSetters(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, bitSizeOf)
{
    checkBitSizeOf(ARRAY_IN_UNION_AND_CHOICE);
    checkBitSizeOf(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, initializeOffsets)
{
    checkInitializeOffsets(ARRAY_IN_UNION_AND_CHOICE);
    checkInitializeOffsets(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, operatorEquality)
{
    checkOperatorEquality(ARRAY_IN_UNION_AND_CHOICE);
    checkOperatorEquality(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, hashCode)
{
    checkHashCode(ARRAY_IN_UNION_AND_CHOICE);
    checkHashCode(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, write)
{
    checkWrite(ARRAY_IN_UNION_AND_CHOICE);
    checkWrite(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, funcConstString)
{
    checkFuncConstString(ARRAY_IN_UNION_AND_CHOICE);
    checkFuncConstString(COMPOUND_IN_UNION_AND_CHOICE);
}

TEST_F(ComplexAllocationTest, funcConstCompound)
{
    checkFuncConstCompound(ARRAY_IN_UNION_AND_CHOICE);
    checkFuncConstCompound(COMPOUND_IN_UNION_AND_CHOICE);
}

} // namespace complex_allocation
