#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "parameterized_types/array_element_param/Database.h"

namespace parameterized_types
{
namespace array_element_param
{

class ParameterizedTypesArrayElementParamTest : public ::testing::Test
{
protected:
    void fillDatabase(Database& database)
    {
        database.setNumBlocks(NUM_BLOCKS);
        zserio::ObjectArray<BlockHeader>& headers = database.getHeaders();
        for (uint16_t i = 0; i < NUM_BLOCKS; ++i)
        {
            BlockHeader blockHeader;
            blockHeader.setNumItems(i + 1);
            blockHeader.setOffset(0);
            headers.push_back(blockHeader);
        }

        zserio::ObjectArray<Block>& blocks = database.getBlocks();
        for (zserio::ObjectArray<BlockHeader>::iterator it = headers.begin(); it != headers.end(); ++it)
        {
            Block block;
            zserio::Int64Array& items = block.getItems();
            const uint16_t numItems = it->getNumItems();
            for (uint16_t j = 0; j < numItems; ++j)
                items.push_back(j * 2);
            blocks.push_back(block);
        }
    }

    void checkDatabaseInBitStream(zserio::BitStreamReader& reader, const Database& database)
    {
        const uint16_t numBlocks = database.getNumBlocks();

        ASSERT_EQ(numBlocks, reader.readBits(16));

        const zserio::ObjectArray<BlockHeader>& headers = database.getHeaders();
        uint32_t expectedOffset = FIRST_BYTE_OFFSET;
        for (uint16_t i = 0; i < numBlocks; ++i)
        {
            const uint16_t numItems = static_cast<uint16_t>(reader.readBits(16));
            ASSERT_EQ(headers.at(i).getNumItems(), numItems);
            ASSERT_EQ(expectedOffset, reader.readBits(32));
            expectedOffset += 8 * numItems;
        }

        const zserio::ObjectArray<Block>& blocks = database.getBlocks();
        for (uint16_t i = 0; i < numBlocks; ++i)
        {
            const uint16_t numItems = headers.at(i).getNumItems();
            const zserio::Int64Array& items = blocks.at(i).getItems();
            for (uint16_t j = 0; j < numItems; ++j)
                ASSERT_EQ(items.at(j), reader.readBits64(64));
        }
    }

private:
    static const uint16_t NUM_BLOCKS;
    static const uint32_t FIRST_BYTE_OFFSET;
};

const uint16_t ParameterizedTypesArrayElementParamTest::NUM_BLOCKS = 3;
const uint32_t ParameterizedTypesArrayElementParamTest::FIRST_BYTE_OFFSET =
        2 + ParameterizedTypesArrayElementParamTest::NUM_BLOCKS * (2 + 4);

TEST_F(ParameterizedTypesArrayElementParamTest, write)
{
    Database database;
    fillDatabase(database);

    zserio::BitStreamWriter writer;
    database.write(writer);

    size_t writerBufferByteSize;
    const uint8_t* writerBuffer = writer.getWriteBuffer(writerBufferByteSize);
    zserio::BitStreamReader reader(writerBuffer, writerBufferByteSize);
    checkDatabaseInBitStream(reader, database);
    reader.setBitPosition(0);

    Database readDatabase(reader);
    ASSERT_EQ(database, readDatabase);
}

} // namespace array_element_param
} // namespace parameterized_types
