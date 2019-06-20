#include "gtest/gtest.h"

#include "array_types/auto_array_recursion/AutoArrayRecursion.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace array_types
{
namespace auto_array_recursion
{

class AutoArrayRecursionTest : public ::testing::Test
{
protected:
    void fillAutoArrayRecursion(AutoArrayRecursion& autoArrayRecursion, size_t numElements)
    {
        autoArrayRecursion.setId(0);

        zserio::ObjectArray<AutoArrayRecursion>& autoArray = autoArrayRecursion.getAutoArrayRecursion();
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
        writer.writeVarUInt64(static_cast<uint64_t>(numElements));
        for (size_t i = 1; i <= numElements; ++i)
        {
            writer.writeBits(static_cast<uint32_t>(i), 8);
            writer.writeVarUInt64(0);
        }
    }

    void checkAutoArrayRecursion(const AutoArrayRecursion& autoArrayRecursion, size_t numElements)
    {
        ASSERT_EQ(0, autoArrayRecursion.getId());
        const zserio::ObjectArray<AutoArrayRecursion>& autoArray = autoArrayRecursion.getAutoArrayRecursion();
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

    void checkRead(size_t numElements)
    {
        zserio::BitStreamWriter writer;
        writeAutoArrayRecursionToByteArray(writer, numElements);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

        AutoArrayRecursion autoArrayRecursion(reader);
        checkAutoArrayRecursion(autoArrayRecursion, numElements);
    }

    void checkWrite(size_t numElements)
    {
        AutoArrayRecursion autoArrayRecursion;
        fillAutoArrayRecursion(autoArrayRecursion, numElements);

        zserio::BitStreamWriter writer;
        autoArrayRecursion.write(writer);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);

        AutoArrayRecursion readAutoArrayRecursion(reader);
        checkAutoArrayRecursion(readAutoArrayRecursion, numElements);
    }

    static const size_t AUTO_ARRAY_LENGTH1;
    static const size_t AUTO_ARRAY_LENGTH2;
};

const size_t AutoArrayRecursionTest::AUTO_ARRAY_LENGTH1 = 5;
const size_t AutoArrayRecursionTest::AUTO_ARRAY_LENGTH2 = 10;

TEST_F(AutoArrayRecursionTest, bitSizeOfLength1)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayRecursionTest, bitSizeOfLength2)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayRecursionTest, initializeOffsetsLength1)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayRecursionTest, initializeOffsetsLength2)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayRecursionTest, readLength1)
{
    checkRead(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayRecursionTest, readLength2)
{
    checkRead(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayRecursionTest, writeLength1)
{
    checkWrite(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayRecursionTest, writeLength2)
{
    checkWrite(AUTO_ARRAY_LENGTH2);
}

} // namespace auto_array_recursion
} // namespace array_types
