#include "array_types/packed_variable_array_struct_recursion/PackedVariableArray.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_variable_array_struct_recursion
{

class PackedVariableArrayStructRecursionTest : public ::testing::Test
{
protected:
    void fillPackedVariableArray(PackedVariableArray& packedVariableArray, size_t numElements)
    {
        const uint8_t byteCount = 1;
        packedVariableArray.setByteCount(byteCount);
        packedVariableArray.setNumElements(static_cast<uint32_t>(numElements));
        auto& blocks = packedVariableArray.getPackedBlocks();
        blocks.reserve(numElements);
        for (size_t i = 0; i < numElements; ++i)
        {
            blocks.push_back(createBlock(byteCount, false));
        }
        packedVariableArray.initializeChildren();
        packedVariableArray.initializeOffsets();
    }

    Block createBlock(uint8_t byteCount, bool isLast)
    {
        Block block;
        auto& dataBytes = block.getDataBytes();
        dataBytes.reserve(byteCount);
        for (uint8_t i = 0; i < byteCount; ++i)
        {
            dataBytes.push_back(i);
        }

        if (isLast)
        {
            block.setBlockTerminator(0);
            return block;
        }
        else
        {
            const uint8_t blockTerminator = static_cast<uint8_t>(byteCount + 1);
            block.setBlockTerminator(blockTerminator);
            block.setNextData(createBlock(blockTerminator, blockTerminator > 5));
            return block;
        }
    }

    size_t getUnpackedVariableArrayBitSize(size_t numElements)
    {
        size_t bitSize = 8; // byteCount
        bitSize += 8; // numElements
        const uint8_t byteCount = 1;
        for (size_t i = 0; i < numElements; ++i)
        {
            bitSize += getUnpackedBlockBitSize(byteCount, false);
        }

        return bitSize;
    }

    size_t getUnpackedBlockBitSize(uint8_t byteCount, bool isLast)
    {
        size_t bitSize = 8U * byteCount; // dataBytes[byteCount]
        bitSize += 8; // blockTerminator
        if (!isLast)
        {
            const uint8_t blockTerminator = static_cast<uint8_t>(byteCount + 1);
            bitSize += getUnpackedBlockBitSize(blockTerminator, blockTerminator > 5);
        }

        return bitSize;
    }

    void checkBitSizeOf(size_t numElements)
    {
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        const double unpackedBitSize = static_cast<double>(getUnpackedVariableArrayBitSize(numElements));
        const double packedBitSize = static_cast<double>(packedVariableArray.bitSizeOf());
        const double minCompressionRatio = 0.9;
        ASSERT_GT(unpackedBitSize * minCompressionRatio, packedBitSize)
                << "Unpacked array has " << std::to_string(unpackedBitSize) << " bits, "
                << "packed array has " << std::to_string(packedBitSize) << " bits, "
                << "compression ratio is " << std::to_string(packedBitSize / unpackedBitSize * 100) << "%!";
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
        ASSERT_EQ(packedVariableArray, readPackedVariableArray);
    }

    void checkWriteReadFile(size_t numElements)
    {
        PackedVariableArray packedVariableArray;
        fillPackedVariableArray(packedVariableArray, numElements);

        const std::string fileName = BLOB_NAME_BASE + std::to_string(numElements) + ".blob";
        zserio::serializeToFile(packedVariableArray, fileName);

        PackedVariableArray readPackedVariableArray =
                zserio::deserializeFromFile<PackedVariableArray>(fileName);
        ASSERT_EQ(packedVariableArray, readPackedVariableArray);
    }

    static const std::string BLOB_NAME_BASE;

    static const size_t VARIABLE_ARRAY_LENGTH1;
    static const size_t VARIABLE_ARRAY_LENGTH2;
    static const size_t VARIABLE_ARRAY_LENGTH3;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(20 * 1024 * 8);
};

const std::string PackedVariableArrayStructRecursionTest::BLOB_NAME_BASE =
        "language/array_types/packed_variable_array_struct_recursion_";

const size_t PackedVariableArrayStructRecursionTest::VARIABLE_ARRAY_LENGTH1 = 100;
const size_t PackedVariableArrayStructRecursionTest::VARIABLE_ARRAY_LENGTH2 = 500;
const size_t PackedVariableArrayStructRecursionTest::VARIABLE_ARRAY_LENGTH3 = 1000;

TEST_F(PackedVariableArrayStructRecursionTest, bitSizeOfLength1)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayStructRecursionTest, bitSizeOfLength2)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayStructRecursionTest, bitSizeOfLength3)
{
    checkBitSizeOf(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayStructRecursionTest, writeReadLength1)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayStructRecursionTest, writeReadLength2)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayStructRecursionTest, writeReadLength3)
{
    checkWriteRead(VARIABLE_ARRAY_LENGTH3);
}

TEST_F(PackedVariableArrayStructRecursionTest, writeReadFileLength1)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH1);
}

TEST_F(PackedVariableArrayStructRecursionTest, writeReadFileLength2)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH2);
}

TEST_F(PackedVariableArrayStructRecursionTest, writeReadFileLength3)
{
    checkWriteReadFile(VARIABLE_ARRAY_LENGTH3);
}

} // namespace packed_variable_array_struct_recursion
} // namespace array_types
