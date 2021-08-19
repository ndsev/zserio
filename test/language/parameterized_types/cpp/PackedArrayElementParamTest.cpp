#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "parameterized_types/packed_array_element_param/Database.h"

namespace parameterized_types
{
namespace packed_array_element_param
{

class PackedArrayElementParamTest : public ::testing::Test
{
protected:
    void fillDatabase(Database& database, uint16_t numBlocks)
    {
        database.setNumBlocks(numBlocks);
        auto& headers = database.getHeaders();
        for (uint16_t i = 0; i < numBlocks; ++i)
        {
            BlockHeader blockHeader;
            blockHeader.setNumItems(i + 1);
            blockHeader.setOffset(0);
            headers.push_back(blockHeader);
        }

        auto& blocks = database.getBlocks();
        for (auto it = headers.begin(); it != headers.end(); ++it)
        {
            Block block;
            auto& items = block.getItems();
            const uint16_t numItems = it->getNumItems();
            for (uint16_t j = 0; j < numItems; ++j)
                items.push_back(j * 2);
            blocks.push_back(block);
        }
    }

    size_t getUnpackedDatabaseBitSize(uint16_t numBlocks)
    {
        size_t bitSize = 16; // numBlocks
        bitSize += numBlocks * (16 + 32); // headers
        for (size_t i = 0; i < numBlocks; ++i)
            bitSize += 64 + (i + 1) * 64; // blocks[i]

        return bitSize;
    }

    void checkBitSizeOf(uint16_t numBlocks)
    {
        Database database;
        fillDatabase(database, numBlocks);

        const size_t unpackedBitSize = getUnpackedDatabaseBitSize(numBlocks);
        const size_t packedBitSize = database.bitSizeOf();

        const double minCompressionRatio = 0.12;
        ASSERT_GT(unpackedBitSize * minCompressionRatio, packedBitSize)
                << "Unpacked array has " << std::to_string(unpackedBitSize) << " bits, "
                << "packed array has " << std::to_string(packedBitSize) << " bits, "
                << "compression ratio is "
                << std::to_string(static_cast<double>(packedBitSize) /unpackedBitSize * 100) << "%!";
    }

    void checkWriteRead(uint16_t numBlocks)
    {
        Database database;
        fillDatabase(database, numBlocks);

        zserio::BitStreamWriter writer(bitBuffer);
        database.write(writer);

        ASSERT_EQ(database.bitSizeOf(), writer.getBitPosition());
        ASSERT_EQ(database.initializeOffsets(0), writer.getBitPosition());

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        Database readDatabase(reader);
        ASSERT_EQ(database, readDatabase);
    }

    static const uint16_t NUM_BLOCKS1;
    static const uint16_t NUM_BLOCKS2;
    static const uint16_t NUM_BLOCKS3;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(200 * 1024 * 8);
};

const uint16_t PackedArrayElementParamTest::NUM_BLOCKS1 = 50;
const uint16_t PackedArrayElementParamTest::NUM_BLOCKS2 = 100;
const uint16_t PackedArrayElementParamTest::NUM_BLOCKS3 = 1000;

TEST_F(PackedArrayElementParamTest, bitSizeOfLength1)
{
    checkBitSizeOf(NUM_BLOCKS1);
}

TEST_F(PackedArrayElementParamTest, bitSizeOfLength2)
{
    checkBitSizeOf(NUM_BLOCKS2);
}

TEST_F(PackedArrayElementParamTest, bitSizeOfLength3)
{
    checkBitSizeOf(NUM_BLOCKS3);
}

TEST_F(PackedArrayElementParamTest, writeReadLength1)
{
    checkWriteRead(NUM_BLOCKS1);
}

TEST_F(PackedArrayElementParamTest, writeReadLength2)
{
    checkWriteRead(NUM_BLOCKS2);
}

TEST_F(PackedArrayElementParamTest, writeReadLength3)
{
    checkWriteRead(NUM_BLOCKS3);
}

} // namespace packed_array_element_param
} // namespace parameterized_types
