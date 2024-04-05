#include "array_types/packed_variable_array_uint8/PackedVariableArray.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_variable_array_uint8
{

using allocator_type = PackedVariableArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class PackedVariableArrayUInt8Test : public ::testing::Test
{
protected:
    void fillPackedVariableArray(PackedVariableArray& packedVariableArray, size_t numElements)
    {
        packedVariableArray.setNumElements(static_cast<uint32_t>(numElements));

        auto& uint8Array = packedVariableArray.getUint8Array();
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

    size_t getPackedVariableArrayBitSize(size_t numElements)
    {
        size_t bitSize = 8; // array size: numElements
        bitSize += 1; // packing descriptor: isPacked
        if (numElements > 1)
            bitSize += 6; // packing descriptor: maxBitNumber
        bitSize += 8; // first element
        bitSize += (numElements - 1) * (PACKED_ARRAY_MAX_BIT_NUMBER + 1); // all deltas

        return bitSize;
    }

    void writePackedVariableArrayToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        writer.writeVarSize(static_cast<uint32_t>(numElements));
        writer.writeBool(true);
        writer.writeBits(PACKED_ARRAY_MAX_BIT_NUMBER, 6);
        writer.writeBits(PACKED_ARRAY_ELEMENT0, 8);
        if (numElements > 1)
        {
            writer.writeSignedBits(
                    PACKED_ARRAY_DELTA * 2, static_cast<uint8_t>(PACKED_ARRAY_MAX_BIT_NUMBER + 1));
            for (size_t i = 0; i < numElements - 2; ++i)
            {
                writer.writeSignedBits(
                        PACKED_ARRAY_DELTA, static_cast<uint8_t>(PACKED_ARRAY_MAX_BIT_NUMBER + 1));
            }
        }
    }

    void checkPackedVariableArray(const PackedVariableArray& packedVariableArray, size_t numElements)
    {
        const auto& uint8Array = packedVariableArray.getUint8Array();
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
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        const size_t bitPosition = 2;
        const size_t autoArrayBitSize = getPackedVariableArrayBitSize(numElements);
        ASSERT_EQ(autoArrayBitSize, packedVariableArray.bitSizeOf(bitPosition));
    }

    void checkInitializeOffsets(size_t numElements)
    {
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        const size_t bitPosition = 2;
        const size_t expectedEndBitPosition = bitPosition + getPackedVariableArrayBitSize(numElements);
        ASSERT_EQ(expectedEndBitPosition, packedVariableArray.initializeOffsets(bitPosition));
    }

    void checkReadConstructor(size_t numElements)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        writePackedVariableArrayToByteArray(writer, numElements);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        PackedVariableArray packedVariableArray(reader);
        checkPackedVariableArray(packedVariableArray, numElements);
    }

    void checkWriteRead(size_t numElements)
    {
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        zserio::BitStreamWriter writer(bitBuffer);
        packedVariableArray.write(writer);

        ASSERT_EQ(packedVariableArray.bitSizeOf(), writer.getBitPosition());
        ASSERT_EQ(packedVariableArray.initializeOffsets(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        PackedVariableArray readPackedVariableArray(reader);
        checkPackedVariableArray(readPackedVariableArray, numElements);
    }

    void checkWriteReadFile(size_t numElements)
    {
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        const std::string fileName = BLOB_NAME_BASE + std::to_string(numElements) + ".blob";
        zserio::serializeToFile(packedVariableArray, fileName);

        PackedVariableArray readPackedVariableArray =
                zserio::deserializeFromFile<PackedVariableArray>(fileName);
        checkPackedVariableArray(readPackedVariableArray, numElements);
    }

    static const std::string BLOB_NAME_BASE;

    static const size_t VARIABLE_ARRAY_LENGTH1;
    static const size_t VARIABLE_ARRAY_LENGTH2;
    static const size_t VARIABLE_ARRAY_LENGTH3;

    static const uint8_t PACKED_ARRAY_ELEMENT0;
    static const int16_t PACKED_ARRAY_DELTA;
    static const uint8_t PACKED_ARRAY_MAX_BIT_NUMBER;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string PackedVariableArrayUInt8Test::BLOB_NAME_BASE =
        "language/array_types/packed_variable_array_uint8_";

const size_t PackedVariableArrayUInt8Test::VARIABLE_ARRAY_LENGTH1 = 1;
const size_t PackedVariableArrayUInt8Test::VARIABLE_ARRAY_LENGTH2 = 5;
const size_t PackedVariableArrayUInt8Test::VARIABLE_ARRAY_LENGTH3 = 10;

const uint8_t PackedVariableArrayUInt8Test::PACKED_ARRAY_ELEMENT0 = 255;
const int16_t PackedVariableArrayUInt8Test::PACKED_ARRAY_DELTA = -2;
const uint8_t PackedVariableArrayUInt8Test::PACKED_ARRAY_MAX_BIT_NUMBER = 3;

TEST_F(PackedVariableArrayUInt8Test, bitSizeOfLength1)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayUInt8Test, bitSizeOfLength2)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayUInt8Test, bitSizeOfLength3)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayUInt8Test, initializeOffsetsLength1)
{
    checkInitializeOffsets(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayUInt8Test, initializeOffsetsLength2)
{
    checkInitializeOffsets(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayUInt8Test, initializeOffsetsLength3)
{
    checkInitializeOffsets(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayUInt8Test, readConstructorLength1)
{
    checkReadConstructor(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayUInt8Test, readConstructorLength2)
{
    checkReadConstructor(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayUInt8Test, readConstructorLength3)
{
    checkReadConstructor(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayUInt8Test, writeReadLength1)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayUInt8Test, writeReadLength2)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayUInt8Test, writeReadLength3)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayUInt8Test, writeReadFileLength1)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayUInt8Test, writeReadFileLength2)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayUInt8Test, writeReadFileLength3)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH3);
}

} // namespace packed_variable_array_uint8
} // namespace array_types
