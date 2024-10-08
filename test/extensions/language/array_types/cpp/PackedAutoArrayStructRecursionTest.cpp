#include "array_types/packed_auto_array_struct_recursion/PackedAutoArrayRecursion.h"
#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace array_types
{
namespace packed_auto_array_struct_recursion
{

class PackedAutoArrayStructRecursionTest : public ::testing::Test
{
protected:
    void fillPackedAutoArrayRecursion(PackedAutoArrayRecursion& packedAutoArrayRecursion, size_t numElements)
    {
        packedAutoArrayRecursion.setId(0);

        auto& autoArray = packedAutoArrayRecursion.getPackedAutoArrayRecursion();
        for (size_t i = 1; i <= numElements; ++i)
        {
            PackedAutoArrayRecursion element;
            element.setId(static_cast<uint8_t>(i));
            autoArray.push_back(element);
        }
    }

    size_t getPackedAutoArrayRecursionBitSize(size_t numElements)
    {
        size_t bitSize = 8; // id
        bitSize += 8; // varsize (length of auto array)
        bitSize += 1; // packing descriptor: isPacked
        if (numElements > 1)
        {
            bitSize += 6; // packing descriptor: maxBitNumber
        }
        bitSize += 8 + 8; // first element
        bitSize += (numElements - 1) * (8 + 2); // all deltas

        return bitSize;
    }

    void writePackedAutoArrayRecursionToByteArray(zserio::BitStreamWriter& writer, size_t numElements)
    {
        writer.writeBits(0, 8);
        writer.writeVarSize(static_cast<uint32_t>(numElements));
        writer.writeBool(true);
        const uint8_t maxBitNumber = 1;
        writer.writeBits(maxBitNumber, 6);
        writer.writeBits(1, 8);
        writer.writeVarSize(0);
        for (size_t i = 1; i <= numElements; ++i)
        {
            writer.writeSignedBits(1, maxBitNumber + 1);
            writer.writeVarSize(0);
        }
    }

    void checkPackedAutoArrayRecursion(
            const PackedAutoArrayRecursion& packedAutoArrayRecursion, size_t numElements)
    {
        ASSERT_EQ(0, packedAutoArrayRecursion.getId());
        const auto& autoArray = packedAutoArrayRecursion.getPackedAutoArrayRecursion();
        ASSERT_EQ(numElements, autoArray.size());
        for (size_t i = 1; i <= numElements; ++i)
        {
            const PackedAutoArrayRecursion& element = autoArray.at(i - 1);
            ASSERT_EQ(i, element.getId());
            ASSERT_EQ(0, element.getPackedAutoArrayRecursion().size());
        }
    }

    void checkBitSizeOf(size_t numElements)
    {
        PackedAutoArrayRecursion packedAutoArrayRecursion;
        fillPackedAutoArrayRecursion(packedAutoArrayRecursion, numElements);

        const size_t bitPosition = 2;
        const size_t autoArrayBitSize = getPackedAutoArrayRecursionBitSize(numElements);
        ASSERT_EQ(autoArrayBitSize, packedAutoArrayRecursion.bitSizeOf(bitPosition));
    }

    void checkInitializeOffsets(size_t numElements)
    {
        PackedAutoArrayRecursion packedAutoArrayRecursion;
        fillPackedAutoArrayRecursion(packedAutoArrayRecursion, numElements);

        const size_t bitPosition = 2;
        const size_t expectedEndBitPosition = bitPosition + getPackedAutoArrayRecursionBitSize(numElements);
        ASSERT_EQ(expectedEndBitPosition, packedAutoArrayRecursion.initializeOffsets(bitPosition));
    }

    void checkReadConstructor(size_t numElements)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        writePackedAutoArrayRecursionToByteArray(writer, numElements);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        PackedAutoArrayRecursion packedAutoArrayRecursion(reader);
        checkPackedAutoArrayRecursion(packedAutoArrayRecursion, numElements);
    }

    void checkWriteRead(size_t numElements)
    {
        PackedAutoArrayRecursion packedAutoArrayRecursion;
        fillPackedAutoArrayRecursion(packedAutoArrayRecursion, numElements);

        zserio::BitStreamWriter writer(bitBuffer);
        packedAutoArrayRecursion.write(writer);

        ASSERT_EQ(packedAutoArrayRecursion.bitSizeOf(), writer.getBitPosition());
        ASSERT_EQ(packedAutoArrayRecursion.initializeOffsets(), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        PackedAutoArrayRecursion readPackedAutoArrayRecursion(reader);
        checkPackedAutoArrayRecursion(readPackedAutoArrayRecursion, numElements);
    }

    void checkWriteReadFile(size_t numElements)
    {
        PackedAutoArrayRecursion packedAutoArrayRecursion;
        fillPackedAutoArrayRecursion(packedAutoArrayRecursion, numElements);

        const std::string fileName = BLOB_NAME_BASE + std::to_string(numElements) + ".blob";
        zserio::serializeToFile(packedAutoArrayRecursion, fileName);

        PackedAutoArrayRecursion readPackedAutoArrayRecursion =
                zserio::deserializeFromFile<PackedAutoArrayRecursion>(fileName);
        checkPackedAutoArrayRecursion(readPackedAutoArrayRecursion, numElements);
    }

    static const std::string BLOB_NAME_BASE;
    static const size_t AUTO_ARRAY_LENGTH1;
    static const size_t AUTO_ARRAY_LENGTH2;
    static const size_t AUTO_ARRAY_LENGTH3;
    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const std::string PackedAutoArrayStructRecursionTest::BLOB_NAME_BASE =
        "language/array_types/packed_auto_array_struct_recursion_";
const size_t PackedAutoArrayStructRecursionTest::AUTO_ARRAY_LENGTH1 = 1;
const size_t PackedAutoArrayStructRecursionTest::AUTO_ARRAY_LENGTH2 = 5;
const size_t PackedAutoArrayStructRecursionTest::AUTO_ARRAY_LENGTH3 = 10;

TEST_F(PackedAutoArrayStructRecursionTest, bitSizeOfLength1)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayStructRecursionTest, bitSizeOfLength2)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayStructRecursionTest, bitSizeOfLength3)
{
    checkBitSizeOf(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayStructRecursionTest, initializeOffsetsLength1)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayStructRecursionTest, initializeOffsetsLength2)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayStructRecursionTest, initializeOffsetsLength3)
{
    checkInitializeOffsets(AUTO_ARRAY_LENGTH3);
}

TEST_F(PackedAutoArrayStructRecursionTest, readConstructorLength1)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayStructRecursionTest, readConstructorLength2)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayStructRecursionTest, readConstructorLength3)
{
    checkReadConstructor(AUTO_ARRAY_LENGTH3);
}

TEST_F(PackedAutoArrayStructRecursionTest, writeReadLength1)
{
    checkWriteRead(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayStructRecursionTest, writeReadLength2)
{
    checkWriteRead(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayStructRecursionTest, writeReadLength3)
{
    checkWriteRead(AUTO_ARRAY_LENGTH3);
}

TEST_F(PackedAutoArrayStructRecursionTest, writeReadFileLength1)
{
    checkWriteReadFile(AUTO_ARRAY_LENGTH1);
}

TEST_F(PackedAutoArrayStructRecursionTest, writeReadFileLength2)
{
    checkWriteReadFile(AUTO_ARRAY_LENGTH2);
}

TEST_F(PackedAutoArrayStructRecursionTest, writeReadFileLength3)
{
    checkWriteReadFile(AUTO_ARRAY_LENGTH3);
}

} // namespace packed_auto_array_struct_recursion
} // namespace array_types
