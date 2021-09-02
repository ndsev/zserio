#include "gtest/gtest.h"

#include "array_types/packed_auto_array_uint8/PackedAutoArray.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/RebindAlloc.h"

namespace array_types
{
namespace packed_auto_array_uint8
{

using allocator_type = PackedAutoArray::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

class PackedAutoArrayUInt8Test : public ::testing::Test
{
protected:
    void fillPackedAutoArray(PackedAutoArray& packedAutoArray, size_t numElements)
    {
        auto& uint8Array = packedAutoArray.getUint8Array();
        uint8Array.reserve(numElements);
        uint8_t value = PACKED_ARRAY_ELEMENT0;
        uint8Array.push_back(value);
        value = static_cast<uint8_t>(value + PACKED_ARRAY_DELTA);
        for (size_t i = 0; i < numElements - 1; ++i)
        {
            value = static_cast<uint8_t>(value + PACKED_ARRAY_DELTA);
            uint8Array.push_back(value);
        }
    }

    size_t getPackedAutoArrayBitSize(size_t numElements)
    {
        size_t bitSize = 8; // auto array size: varsize
        bitSize += 1; // packing descriptor: isPacked
        if (numElements > 1)
            bitSize += 6; // packing descriptor: maxBitNumber
        bitSize += 8; // first element
        bitSize += (numElements - 1) * (PACKED_ARRAY_MAX_BIT_NUMBER + 1); // all deltas

        return bitSize;
    }

    void writePackedAutoArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        writer.writeVarSize(static_cast<uint32_t>(numElements));
        writer.writeBool(true);
        writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(PACKED_ARRAY_ELEMENT0, 8);
        if (numElements > 1)
        {
            writer.writeSignedBits(PACKED_ARRAY_DELTA * 2, PACKED_ARRAY_MAX_BIT_NUMBER + 1);
            for (size_t i = 0; i < numElements - 2; ++i)
            {
                writer.writeSignedBits(PACKED_ARRAY_DELTA, PACKED_ARRAY_MAX_BIT_NUMBER + 1);
            }
        }
    }

    void checkPackedAutoArray(const PackedAutoArray& packedAutoArray, size_t numElements)
    {
        const auto& uint8Array = packedAutoArray.getUint8Array();
        ASSERT_EQ(numElements, uint8Array.size());
        uint8_t value = PACKED_ARRAY_ELEMENT0;
        ASSERT_EQ(value, uint8Array.at(0));
        value = static_cast<uint8_t>(value + PACKED_ARRAY_DELTA);
        for (size_t i = 1; i < numElements - 1; ++i)
        {
            value = static_cast<uint8_t>(value + PACKED_ARRAY_DELTA);
            ASSERT_EQ(value, uint8Array.at(i));
        }
    }

    void checkBitSizeOf(size_t numElements)
    {
        PackedAutoArray packedAutoArray;
        fillPackedAutoArray(packedAutoArray, numElements);

        const size_t bitPosition = 2;
        const size_t autoArrayBitSize = getPackedAutoArrayBitSize(numElements);
        ASSERT_EQ(autoArrayBitSize, packedAutoArray.bitSizeOf(bitPosition));
    }

    void checkInitializeOffsets(size_t numElements)
    {
        PackedAutoArray packedAutoArray;
        fillPackedAutoArray(packedAutoArray, numElements);

        const size_t bitPosition = 2;
        const size_t expectedEndBitPosition = bitPosition + getPackedAutoArrayBitSize(numElements);
        ASSERT_EQ(expectedEndBitPosition, packedAutoArray.initializeOffsets(bitPosition));
    }

    void checkReadConstructor(size_t numElements)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        writePackedAutoArrayToByteArray(writer, numElements);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        PackedAutoArray packedAutoArray(reader);
        checkPackedAutoArray(packedAutoArray, numElements);
    }

    void checkWrite(size_t numElements)
    {
        PackedAutoArray packedAutoArray;
        fillPackedAutoArray(packedAutoArray, numElements);

        zserio::BitStreamWriter writer(bitBuffer);
        packedAutoArray.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        PackedAutoArray readPackedAutoArray(reader);
        checkPackedAutoArray(readPackedAutoArray, numElements);
    }

    static const size_t AUTO_ARRAY_LENGTH1;
    static const size_t AUTO_ARRAY_LENGTH2;
    static const size_t AUTO_ARRAY_LENGTH3;

    static const uint8_t PACKED_ARRAY_ELEMENT0;
    static const int16_t PACKED_ARRAY_DELTA;
    static const uint8_t PACKED_ARRAY_MAX_BIT_NUMBER;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const size_t PackedAutoArrayUInt8Test::AUTO_ARRAY_LENGTH1 = 1;
const size_t PackedAutoArrayUInt8Test::AUTO_ARRAY_LENGTH2 = 5;
const size_t PackedAutoArrayUInt8Test::AUTO_ARRAY_LENGTH3 = 10;

const uint8_t PackedAutoArrayUInt8Test::PACKED_ARRAY_ELEMENT0 = 255;
const int16_t PackedAutoArrayUInt8Test::PACKED_ARRAY_DELTA = -2;
const uint8_t PackedAutoArrayUInt8Test::PACKED_ARRAY_MAX_BIT_NUMBER = 3;

TEST_F(PackedAutoArrayUInt8Test, bitSizeOfLength1)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayUInt8Test, bitSizeOfLength2)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayUInt8Test, bitSizeOfLength3)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH3);
}

TEST_F(PackedAutoArrayUInt8Test, initializeOffsetsLength1)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayUInt8Test, initializeOffsetsLength2)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayUInt8Test, initializeOffsetsLength3)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH3);
}

TEST_F(PackedAutoArrayUInt8Test, readConstructorLength1)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayUInt8Test, readConstructorLength2)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayUInt8Test, readConstructorLength3)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH3);
}

TEST_F(PackedAutoArrayUInt8Test, writeLength1)
{
    checkWrite(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayUInt8Test, writeLength2)
{
    checkWrite(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayUInt8Test, writeLength3)
{
    checkWrite(AUTO_ARRAY_LENGTH3);
}

} // namespace packed_auto_array_uint8
} // namespace array_types
