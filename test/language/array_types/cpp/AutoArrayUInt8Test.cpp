#include "array_types/auto_array_uint8/AutoArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace auto_array_uint8
{

using allocator_type = AutoArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class AutoArrayUInt8Test : public ::testing::Test
{
protected:
    void writeAutoArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        writer.writeVarSize(static_cast<uint32_t>(numElements));
        for (size_t i = 0; i < numElements; ++i)
            writer.writeBits(static_cast<uint32_t>(i), 8);
    }

    void checkBitSizeOf(size_t numElements)
    {
        vector_type<uint8_t> uint8Array;
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
        vector_type<uint8_t> uint8Array;
        uint8Array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            uint8Array.push_back(static_cast<uint8_t>(i));
        AutoArray autoArray;
        autoArray.setUint8Array(uint8Array);

        const size_t bitPosition = 2;
        const size_t expectedEndBitPosition = bitPosition + 8 + numElements * 8;
        ASSERT_EQ(expectedEndBitPosition, autoArray.initializeOffsets(bitPosition));
    }

    void checkReadConstructor(size_t numElements)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        writeAutoArrayToByteArray(writer, numElements);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        AutoArray autoArray(reader);

        const vector_type<uint8_t>& uint8Array = autoArray.getUint8Array();
        ASSERT_EQ(numElements, uint8Array.size());
        for (size_t i = 0; i < numElements; ++i)
            ASSERT_EQ(i, uint8Array[i]);
    }

    void checkWriteRead(size_t numElements)
    {
        vector_type<uint8_t> uint8Array;
        uint8Array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            uint8Array.push_back(static_cast<uint8_t>(i));
        AutoArray autoArray;
        autoArray.setUint8Array(uint8Array);

        zserio::BitStreamWriter writer(bitBuffer);
        autoArray.write(writer);

        ASSERT_EQ(autoArray.bitSizeOf(), writer.getBitPosition());
        ASSERT_EQ(autoArray.initializeOffsets(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        AutoArray readAutoArray(reader);
        const vector_type<uint8_t>& readUint8Array = readAutoArray.getUint8Array();
        ASSERT_EQ(numElements, readUint8Array.size());
        for (size_t i = 0; i < numElements; ++i)
            ASSERT_EQ(i, readUint8Array[i]);
    }

    void checkWriteReadFile(size_t numElements)
    {
        vector_type<uint8_t> uint8Array;
        uint8Array.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
            uint8Array.push_back(static_cast<uint8_t>(i));
        AutoArray autoArray;
        autoArray.setUint8Array(uint8Array);

        const std::string fileName = BLOB_NAME_BASE + std::to_string(numElements) + ".blob";
        zserio::serializeToFile(autoArray, fileName);

        AutoArray readAutoArray = zserio::deserializeFromFile<AutoArray>(fileName);
        const vector_type<uint8_t>& readUint8Array = readAutoArray.getUint8Array();
        ASSERT_EQ(numElements, readUint8Array.size());
        for (size_t i = 0; i < numElements; ++i)
            ASSERT_EQ(i, readUint8Array[i]);
    }

    static const std::string BLOB_NAME_BASE;
    static const size_t AUTO_ARRAY_LENGTH1;
    static const size_t AUTO_ARRAY_LENGTH2;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string AutoArrayUInt8Test::BLOB_NAME_BASE = "language/array_types/auto_array_uint8_";
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

TEST_F(AutoArrayUInt8Test, readConstructorLength1)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayUInt8Test, readConstructorLength2)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayUInt8Test, writeReadLength1)
{
    checkWriteRead(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayUInt8Test, writeReadLength2)
{
    checkWriteRead(AUTO_ARRAY_LENGTH2);
}

TEST_F(AutoArrayUInt8Test, writeReadFileLength1)
{
    checkWriteReadFile(AUTO_ARRAY_LENGTH1);
}

TEST_F(AutoArrayUInt8Test, writeReadFileLength2)
{
    checkWriteReadFile(AUTO_ARRAY_LENGTH2);
}

} // namespace auto_array_uint8
} // namespace array_types
