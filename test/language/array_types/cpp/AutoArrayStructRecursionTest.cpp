#include "gtest/gtest.h"

#include "array_types/auto_array_struct_recursion/AutoArrayRecursion.h"

#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace auto_array_struct_recursion
{

class AutoArrayStructRecursionTest : public ::testing::Test
{
protected:
    void fillAutoArrayRecursion(AutoArrayRecursion& autoArrayRecursion, size_t numElements)
    {
        autoArrayRecursion.setId(0);

        auto& autoArray = autoArrayRecursion.getAutoArrayRecursion();
        for (size_t i = 1; i <= numElements; ++i)
        {
            AutoArrayRecursion element;
            element.setId(static_cast<uint8_t>(i));
            autoArray.push_back(element);
        }
    }

    size_t getAutoArrayRecursionBitSizeOf(size_t numElements)
    {
        return 8 + 8 + numElements * (8 + 8);
    }

    void writeAutoArrayRecursionToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        writer.writeBits(0, 8);
        writer.writeVarSize(static_cast<uint32_t>(numElements));
        for (size_t i = 1; i <= numElements; ++i)
        {
            writer.writeBits(static_cast<uint32_t>(i), 8);
            writer.writeVarSize(0);
        }
    }

    void checkAutoArrayRecursion(const AutoArrayRecursion& autoArrayRecursion, size_t numElements)
    {
        ASSERT_EQ(0, autoArrayRecursion.getId());
        const auto& autoArray = autoArrayRecursion.getAutoArrayRecursion();
        ASSERT_EQ(numElements, autoArray.size());
        for (size_t i = 1; i <= numElements; ++i)
        {
            const AutoArrayRecursion& element = autoArray.at(i - 1);
            ASSERT_EQ(i, element.getId());
            ASSERT_EQ(0, element.getAutoArrayRecursion().size());
        }
    }

    void checkBitSizeOf(size_t numElements)
    {
        AutoArrayRecursion autoArrayRecursion;
        fillAutoArrayRecursion(autoArrayRecursion, numElements);

        const size_t bitPosition = 2;
        const size_t autoArrayBitSize = getAutoArrayRecursionBitSizeOf(numElements);
        ASSERT_EQ(autoArrayBitSize, autoArrayRecursion.bitSizeOf(bitPosition));
    }

    void checkInitializeOffsets(size_t numElements)
    {
        AutoArrayRecursion autoArrayRecursion;
        fillAutoArrayRecursion(autoArrayRecursion, numElements);

        const size_t bitPosition = 2;
        const size_t expectedEndBitPosition = bitPosition + getAutoArrayRecursionBitSizeOf(numElements);
        ASSERT_EQ(expectedEndBitPosition, autoArrayRecursion.initializeOffsets(bitPosition));
    }

    void checkReadConstructor(size_t numElements)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        writeAutoArrayRecursionToByteArray(writer, numElements);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        AutoArrayRecursion autoArrayRecursion(reader);
        checkAutoArrayRecursion(autoArrayRecursion, numElements);
    }

    void checkWriteRead(size_t numElements)
    {
        AutoArrayRecursion autoArrayRecursion;
        fillAutoArrayRecursion(autoArrayRecursion, numElements);

        zserio::BitStreamWriter writer(bitBuffer);
        autoArrayRecursion.write(writer);

        ASSERT_EQ(autoArrayRecursion.bitSizeOf(), writer.getBitPosition());
        ASSERT_EQ(autoArrayRecursion.initializeOffsets(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        AutoArrayRecursion readAutoArrayRecursion(reader);
        checkAutoArrayRecursion(readAutoArrayRecursion, numElements);
    }

    void checkWriteReadFile(size_t numElements)
    {
        AutoArrayRecursion autoArrayRecursion;
        fillAutoArrayRecursion(autoArrayRecursion, numElements);

        const std::string fileName = BLOB_NAME_BASE + std::to_string(numElements) + ".blob";
        zserio::serializeToFile(autoArrayRecursion, fileName);

        AutoArrayRecursion readAutoArrayRecursion = zserio::deserializeFromFile<AutoArrayRecursion>(fileName);
        checkAutoArrayRecursion(readAutoArrayRecursion, numElements);
    }

    static const std::string BLOB_NAME_BASE;
    static const size_t AUTO_ARRAY_LENGTH1;
    static const size_t AUTO_ARRAY_LENGTH2;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string AutoArrayStructRecursionTest::BLOB_NAME_BASE =
        "language/array_types/auto_array_struct_recursion_";
const size_t AutoArrayStructRecursionTest::AUTO_ARRAY_LENGTH1 = 5;
const size_t AutoArrayStructRecursionTest::AUTO_ARRAY_LENGTH2 = 10;

TEST_F(AutoArrayStructRecursionTest, bitSizeOfLength1)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayStructRecursionTest, bitSizeOfLength2)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayStructRecursionTest, initializeOffsetsLength1)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayStructRecursionTest, initializeOffsetsLength2)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayStructRecursionTest, readConstructorLength1)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayStructRecursionTest, readConstructorLength2)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayStructRecursionTest, writeReadLength1)
{
    checkWriteRead(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayStructRecursionTest, writeReadLength2)
{
    checkWriteRead(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayStructRecursionTest, writeReadFileLength1)
{
    checkWriteReadFile(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayStructRecursionTest, writeReadFileLength2)
{
    checkWriteReadFile(AUTO_ARRAY_LENGTH2);
}

} // namespace auto_array_struct_recursion
} // namespace array_types
