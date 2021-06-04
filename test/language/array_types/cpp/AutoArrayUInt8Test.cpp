#include "gtest/gtest.h"

#include "array_types/auto_array_uint8/AutoArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace array_types
{
namespace auto_array_uint8
{

class AutoArrayUInt8Test : public ::testing::Test
{
protected:
    void writeAutoArrayToByteArray(zserio::BitStreamWriter& writer, size_t length)
    {
        writer.writeVarSize(static_cast<uint32_t>(length));
        for (size_t i = 0; i < length; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 8);
    }

    void checkBitSizeOf(size_t numElements)
    {
        std::vector<uint8_t> uint8Array;
        uint8Array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            uint8Array.push_back(static_cast<uint8_t>(i));
        AutoArray autoArray;
        autoArray.setUint8Array(uint8Array);

        const size_t bitPosition = 2;
        const size_t autoArrayBitSize = 8 + numElements * 8;
        ASSERT_EQ(autoArrayBitSize, autoArray.bitSizeOf(bitPosition));
    }

    void checkInitializeOffsets(size_t numElements)
    {
        std::vector<uint8_t> uint8Array;
        uint8Array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            uint8Array.push_back(static_cast<uint8_t>(i));
        AutoArray autoArray;
        autoArray.setUint8Array(uint8Array);

        const size_t bitPosition = 2;
        const size_t expectedEndBitPosition = bitPosition + 8 + numElements * 8;
        ASSERT_EQ(expectedEndBitPosition, autoArray.initializeOffsets(bitPosition));
    }

    void checkRead(size_t numElements)
    {
        zserio::BitStreamWriter writer;
        writeAutoArrayToByteArray(writer, numElements);
        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        AutoArray autoArray(reader);

        const std::vector<uint8_t>& uint8Array = autoArray.getUint8Array();
        ASSERT_EQ(numElements, uint8Array.size());
        for (size_t i = 0; i < numElements; ++i)
            ASSERT_EQ(i, uint8Array[i]);
    }

    void checkWrite(size_t numElements)
    {
        std::vector<uint8_t> uint8Array;
        uint8Array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            uint8Array.push_back(static_cast<uint8_t>(i));
        AutoArray autoArray;
        autoArray.setUint8Array(uint8Array);

        zserio::BitStreamWriter writer;
        autoArray.write(writer);

        size_t writeBufferByteSize;
        const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
        zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
        AutoArray readAutoArray(reader);
        const std::vector<uint8_t>& readUint8Array = readAutoArray.getUint8Array();
        ASSERT_EQ(numElements, readUint8Array.size());
        for (size_t i = 0; i < numElements; ++i)
            ASSERT_EQ(i, readUint8Array[i]);
    }

    static const size_t AUTO_ARRAY_LENGTH1;
    static const size_t AUTO_ARRAY_LENGTH2;
};

const size_t AutoArrayUInt8Test::AUTO_ARRAY_LENGTH1 = 5;
const size_t AutoArrayUInt8Test::AUTO_ARRAY_LENGTH2 = 10;

TEST_F(AutoArrayUInt8Test, bitSizeOfLength1)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayUInt8Test, bitSizeOfLength2)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayUInt8Test, initializeOffsetsLength1)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayUInt8Test, initializeOffsetsLength2)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayUInt8Test, readLength1)
{
    checkRead(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayUInt8Test, readLength2)
{
    checkRead(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayUInt8Test, writeLength1)
{
    checkWrite(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayUInt8Test, writeLength2)
{
    checkWrite(AUTO_ARRAY_LENGTH2);
}

} // namespace auto_array_uint8
} // namespace array_types
