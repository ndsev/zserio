#include <cstdio>
#include <vector>
#include <string>
#include <limits>
#include <memory>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/SqliteFinalizer.h"

namespace sql_tables
{
namespace blob_offsets_param_table
{

using allocator_type = TestDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class BlobOffsetsParamTableTest : public ::testing::Test
{
public:
    BlobOffsetsParamTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~BlobOffsetsParamTableTest()
    {
        delete m_database;
    }

protected:
    static void fillBlobOffsetsParamTableRow(BlobOffsetsParamTable::Row& row, uint32_t blobId,
            const string_type& name)
    {
        row.setBlobId(blobId);
        row.setName(name);

        const uint32_t array_size = 1 + blobId;
        row.setOffsetsHolder(OffsetsHolder(vector_type<uint32_t>(array_size)));

        row.setBlob(ParameterizedBlob());
        ParameterizedBlob& parameterizedBlob = row.getBlob();
        vector_type<uint32_t>& array = parameterizedBlob.getArray();
        for (uint32_t i = 0; i < array_size; ++i)
            array.push_back(i);
    }

    static void fillBlobOffsetsParamTableRows(vector_type<BlobOffsetsParamTable::Row>& rows)
    {
        rows.clear();
        rows.resize(NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS);
        for (uint32_t blobId = 0; blobId < NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS; ++blobId)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(blobId);
            fillBlobOffsetsParamTableRow(rows[blobId], blobId, name);
        }
    }

    static void checkBlobOffsetsParamTableRow(const BlobOffsetsParamTable::Row& row1,
            const BlobOffsetsParamTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getOffsetsHolder(), row2.getOffsetsHolder());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkBlobOffsetsParamTableRows(const vector_type<BlobOffsetsParamTable::Row>& rows1,
            const vector_type<BlobOffsetsParamTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkBlobOffsetsParamTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "blobOffsetsParamTable";
        string_type sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                m_database->connection().prepareStatement(sqlQuery));

        int result = sqlite3_step(statement.get());
        if (result == SQLITE_DONE || result != SQLITE_ROW)
            return false;

        const unsigned char* readTableName = sqlite3_column_text(statement.get(), 0);
        if (readTableName == nullptr ||
                checkTableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
        {
            return false;
        }

        return true;
    }

    static const char DB_FILE_NAME[];

    static const uint32_t NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char BlobOffsetsParamTableTest::DB_FILE_NAME[] =
        "language/sql_tables/blob_offsets_param_table_test.sqlite";

const uint32_t BlobOffsetsParamTableTest::NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS = 20;

TEST_F(BlobOffsetsParamTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    BlobOffsetsParamTable& testTable = m_database->getBlobOffsetsParamTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(BlobOffsetsParamTableTest, readWithoutCondition)
{
    BlobOffsetsParamTable& testTable = m_database->getBlobOffsetsParamTable();

    vector_type<BlobOffsetsParamTable::Row> writtenRows;
    fillBlobOffsetsParamTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<BlobOffsetsParamTable::Row> readRows;
    BlobOffsetsParamTable::Reader reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkBlobOffsetsParamTableRows(writtenRows, readRows);
}

TEST_F(BlobOffsetsParamTableTest, readWithCondition)
{
    BlobOffsetsParamTable& testTable = m_database->getBlobOffsetsParamTable();

    vector_type<BlobOffsetsParamTable::Row> writtenRows;
    fillBlobOffsetsParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "name='Name1'";
    vector_type<BlobOffsetsParamTable::Row> readRows;
    BlobOffsetsParamTable::Reader reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkBlobOffsetsParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(BlobOffsetsParamTableTest, update)
{
    BlobOffsetsParamTable& testTable = m_database->getBlobOffsetsParamTable();

    vector_type<BlobOffsetsParamTable::Row> writtenRows;
    fillBlobOffsetsParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    BlobOffsetsParamTable::Row updateRow;
    fillBlobOffsetsParamTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "blobId=" + zserio::toString<allocator_type>(updateRowId);
    testTable.update(updateRow, updateCondition);

    vector_type<BlobOffsetsParamTable::Row> readRows;
    BlobOffsetsParamTable::Reader reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkBlobOffsetsParamTableRow(updateRow, readRows[0]);
}

} // namespace blob_offsets_param_table
} // namespace sql_tables
