#include "gtest/gtest.h"

#include "array_types/subtyped_builtin_auto_array/SubtypedBuiltinAutoArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace array_types
{
namespace subtyped_builtin_auto_array
{

class SubtypedBuiltinAutoArrayTest : public ::testing::Test
{
protected:
    void writeSubtypedBuiltinAutoArrayToByteArray(zserio::BitStreamWriter& writer, size_t length)
    {
        writer.writeVarSize(static_cast<uint32_t>(length));
        for (size_t i = 0; i < length; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 8);
    }

    void checkBitSizeOf(size_t numElements)
    {
        std::vector<ArrayElement> array;
        array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            array.push_back(static_cast<ArrayElement>(i));
        SubtypedBuiltinAutoArray subtypedBuiltinAutoArray;
        subtypedBuiltinAutoArray.setArray(array);

        const size_t bitPosition = 2;
        const size_t subtypedBuiltinAutoArrayBitSize = 8 + numElements * 8;
        ASSERT_EQ(subtypedBuiltinAutoArrayBitSize,
                subtypedBuiltinAutoArray.bitSizeOf(bitPosition));
    }

    void checkInitializeOffsets(size_t numElements)
    {
        std::vector<ArrayElement> array;
        array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            array.push_back(static_cast<ArrayElement>(i));
        SubtypedBuiltinAutoArray subtypedBuiltinAutoArray;
        subtypedBuiltinAutoArray.setArray(array);

        const size_t bitPosition = 2;
        const size_t expectedEndBitPosition = bitPosition + 8 + numElements * 8;
        ASSERT_EQ(expectedEndBitPosition, subtypedBuiltinAutoArray.initializeOffsets(bitPosition));
    }

    void checkRead(size_t numElements)
    {
        zserio::BitStreamWriter writer;
        writeSubtypedBuiltinAutoArrayToByteArray(writer, numElements);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SubtypedBuiltinAutoArray subtypedBuiltinAutoArray(reader);

        const std::vector<ArrayElement>& array = subtypedBuiltinAutoArray.getArray();
        ASSERT_EQ(numElements, array.size());
        for (size_t i = 0; i < numElements; ++i)
            ASSERT_EQ(i, array[i]);
    }

    void checkWrite(size_t numElements)
    {
        std::vector<ArrayElement> array;
        array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            array.push_back(static_cast<ArrayElement>(i));
        SubtypedBuiltinAutoArray subtypedBuiltinAutoArray;
        subtypedBuiltinAutoArray.setArray(array);

        zserio::BitStreamWriter writer;
        subtypedBuiltinAutoArray.write(writer);

        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        SubtypedBuiltinAutoArray readSubtypedBuiltinAutoArray(reader);
        const std::vector<ArrayElement>& readArray = readSubtypedBuiltinAutoArray.getArray();
        ASSERT_EQ(numElements, readArray.size());
        for (size_t i = 0; i < numElements; ++i)
            ASSERT_EQ(i, readArray[i]);
    }

    static const size_t AUTO_ARRAY_LENGTH1;
    static const size_t AUTO_ARRAY_LENGTH2;
};

const size_t SubtypedBuiltinAutoArrayTest::AUTO_ARRAY_LENGTH1 = 5;
const size_t SubtypedBuiltinAutoArrayTest::AUTO_ARRAY_LENGTH2 = 10;

TEST_F(SubtypedBuiltinAutoArrayTest, bitSizeOfLength1)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH1);
}

TEST_F(SubtypedBuiltinAutoArrayTest, bitSizeOfLength2)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH2);
}

TEST_F(SubtypedBuiltinAutoArrayTest, initializeOffsetsLength1)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
}

TEST_F(SubtypedBuiltinAutoArrayTest, initializeOffsetsLength2)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
}

TEST_F(SubtypedBuiltinAutoArrayTest, readLength1)
{
    checkRead(AUTO_ARRAY_LENGTH1);
}

TEST_F(SubtypedBuiltinAutoArrayTest, readLength2)
{
    checkRead(AUTO_ARRAY_LENGTH2);
}

TEST_F(SubtypedBuiltinAutoArrayTest, writeLength1)
{
    checkWrite(AUTO_ARRAY_LENGTH1);
}

TEST_F(SubtypedBuiltinAutoArrayTest, writeLength2)
{
    checkWrite(AUTO_ARRAY_LENGTH2);
}

} // namespace subtyped_builtin_auto_array
} // namespace array_types
