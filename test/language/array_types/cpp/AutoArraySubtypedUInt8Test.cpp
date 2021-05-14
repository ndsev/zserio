#include "gtest/gtest.h"

#include "array_types/auto_array_subtyped_uint8/AutoArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace array_types
{
namespace auto_array_subtyped_uint8
{

class AutoArraySubtypedUInt8Test : public ::testing::Test
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
        AutoArray autoArray;
        autoArray.setArray(array);

        const size_t bitPosition = 2;
        const size_t subtypedBuiltinAutoArrayBitSize = 8 + numElements * 8;
        ASSERT_EQ(subtypedBuiltinAutoArrayBitSize,
                autoArray.bitSizeOf(bitPosition));
    }

    void checkInitializeOffsets(size_t numElements)
    {
        std::vector<ArrayElement> array;
        array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            array.push_back(static_cast<ArrayElement>(i));
        AutoArray autoArray;
        autoArray.setArray(array);

        const size_t bitPosition = 2;
        const size_t expectedEndBitPosition = bitPosition + 8 + numElements * 8;
        ASSERT_EQ(expectedEndBitPosition, autoArray.initializeOffsets(bitPosition));
    }

    void checkRead(size_t numElements)
    {
        zserio::BitStreamWriter writer;
        writeSubtypedBuiltinAutoArrayToByteArray(writer, numElements);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        AutoArray autoArray(reader);

        const std::vector<ArrayElement>& array = autoArray.getArray();
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
        AutoArray autoArray;
        autoArray.setArray(array);

        zserio::BitStreamWriter writer;
        autoArray.write(writer);

        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        AutoArray readAutoArray(reader);
        const std::vector<ArrayElement>& readArray = readAutoArray.getArray();
        ASSERT_EQ(numElements, readArray.size());
        for (size_t i = 0; i < numElements; ++i)
            ASSERT_EQ(i, readArray[i]);
    }

    static const size_t AUTO_ARRAY_LENGTH1;
    static const size_t AUTO_ARRAY_LENGTH2;
};

const size_t AutoArraySubtypedUInt8Test::AUTO_ARRAY_LENGTH1 = 5;
const size_t AutoArraySubtypedUInt8Test::AUTO_ARRAY_LENGTH2 = 10;

TEST_F(AutoArraySubtypedUInt8Test, bitSizeOfLength1)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArraySubtypedUInt8Test, bitSizeOfLength2)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArraySubtypedUInt8Test, initializeOffsetsLength1)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArraySubtypedUInt8Test, initializeOffsetsLength2)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArraySubtypedUInt8Test, readLength1)
{
    checkRead(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArraySubtypedUInt8Test, readLength2)
{
    checkRead(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArraySubtypedUInt8Test, writeLength1)
{
    checkWrite(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArraySubtypedUInt8Test, writeLength2)
{
    checkWrite(AUTO_ARRAY_LENGTH2);
}

} // namespace auto_array_subtyped_uint8
} // namespace array_types
