#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

#include "parameterized_types/array_element_param/Database.h"

namespace parameterized_types
{
namespace array_element_param
{

class ArrayElementParamTest : public ::testing::Test
{
protected:
    void fillDatabase(Database& database)
    {
        database.setNumBlocks(NUM_BLOCKS);
        auto& headers = database.getHeaders();
        for (uint16_t i = 0; i < NUM_BLOCKS; ++i)
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

        database.initializeChildren();
        database.initializeOffsets();
    }

    void checkDatabaseInBitStream(zserio::BitStreamReader& reader, const Database& database)
    {
        const uint16_t numBlocks = database.getNumBlocks();

        ASSERT_EQ(numBlocks, reader.readBits(16));

        const auto& headers = database.getHeaders();
        uint32_t expectedOffset = FIRST_BYTE_OFFSET;
        for (uint16_t i = 0; i < numBlocks; ++i)
        {
            const uint16_t numItems = static_cast<uint16_t>(reader.readBits(16));
            ASSERT_EQ(headers.at(i).getNumItems(), numItems);
            ASSERT_EQ(expectedOffset, reader.readBits(32));
            expectedOffset += 8 * numItems;
        }

        const auto& blocks = database.getBlocks();
        for (uint16_t i = 0; i < numBlocks; ++i)
        {
            const uint16_t numItems = headers.at(i).getNumItems();
            const auto& items = blocks.at(i).getItems();
            for (uint16_t j = 0; j < numItems; ++j)
                ASSERT_EQ(items.at(j), reader.readBits64(64));
        }
    }

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);

private:
    static const uint16_t NUM_BLOCKS;
    static const uint32_t FIRST_BYTE_OFFSET;
};

const uint16_t ArrayElementParamTest::NUM_BLOCKS = 3;
const uint32_t ArrayElementParamTest::FIRST_BYTE_OFFSET =
        2 + ArrayElementParamTest::NUM_BLOCKS * (2 + 4);

TEST_F(ArrayElementParamTest, fieldConstructor)
{
    Database database;
    fillDatabase(database);
    // initialize because Block::operator== touches header parameter (see last assert)
    database.initializeChildren();

    auto headers = database.getHeaders();
    auto blocks = database.getBlocks();

    void* headersPtr = headers.data();
    void* blocksPtr = blocks.data();

    // headers are moved, blocks copied
    Database newDatabase(database.getNumBlocks(), std::move(headers), blocks);
    ASSERT_EQ(headersPtr, newDatabase.getHeaders().data());
    ASSERT_NE(blocksPtr, newDatabase.getBlocks().data());
    ASSERT_EQ(blocks, newDatabase.getBlocks());
}

TEST_F(ArrayElementParamTest, moveConstructor)
{
    Database database;
    fillDatabase(database);

    void* headersPtr = database.getHeaders().data();
    void* blocksPtr = database.getBlocks().data();

    Database movedDatabase(std::move(database));
    ASSERT_EQ(headersPtr, movedDatabase.getHeaders().data());
    ASSERT_EQ(blocksPtr, movedDatabase.getBlocks().data());
}

TEST_F(ArrayElementParamTest, moveAssignmentOperator)
{
    Database database;
    fillDatabase(database);

    void* headersPtr = database.getHeaders().data();
    void* blocksPtr = database.getBlocks().data();

    Database movedDatabase;
    movedDatabase = std::move(database);
    ASSERT_EQ(headersPtr, movedDatabase.getHeaders().data());
    ASSERT_EQ(blocksPtr, movedDatabase.getBlocks().data());
}

TEST_F(ArrayElementParamTest, write)
{
    Database database;
    fillDatabase(database);

    zserio::BitStreamWriter writer(bitBuffer);
    database.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkDatabaseInBitStream(reader, database);
    reader.setBitPosition(0);

    Database readDatabase(reader);
    ASSERT_EQ(database, readDatabase);
}

} // namespace array_element_param
} // namespace parameterized_types
