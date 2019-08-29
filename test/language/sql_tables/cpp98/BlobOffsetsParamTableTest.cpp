#include <cstdio>
#include <vector>
#include <string>
#include <limits>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace blob_offsets_param_table
{

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
    static void fillBlobOffsetsParamTableRow(BlobOffsetsParamTableRow& row, uint32_t blobId,
            const std::string& name)
    {
        row.setBlobId(blobId);
        row.setName(name);

        row.setOffsetsHolder(OffsetsHolder()); // row does not have its columns initialized
        row.getOffsetsHolder().getOffsets().resize(ARRAY_SIZE);

        row.setBlob(ParameterizedBlob());
        ParameterizedBlob& parameterizedBlob = row.getBlob();
        parameterizedBlob.initialize(row.getOffsetsHolder());
        zserio::UInt32Array& array = parameterizedBlob.getArray();
        for (uint32_t i = 0; i < ARRAY_SIZE; ++i)
            array.push_back(i);

        // we must initialize offsets manually since offsetsHolder is written first to the sqlite table
        // note that we must use the blob instance which is already in the row!
        parameterizedBlob.initializeOffsets(0);
    }

    static void fillBlobOffsetsParamTableRows(std::vector<BlobOffsetsParamTableRow>& rows)
    {
        rows.clear();
        rows.resize(NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS);
        for (uint32_t blobId = 0; blobId < NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS; ++blobId)
        {
            const std::string name = "Name" + zserio::convertToString(blobId);
            fillBlobOffsetsParamTableRow(rows[blobId], blobId, name);
        }
    }

    static void checkBlobOffsetsParamTableRow(const BlobOffsetsParamTableRow& row1, const BlobOffsetsParamTableRow& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getOffsetsHolder(), row2.getOffsetsHolder());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkBlobOffsetsParamTableRows(const std::vector<BlobOffsetsParamTableRow>& rows1,
            const std::vector<BlobOffsetsParamTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkBlobOffsetsParamTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "blobOffsetsParamTable";
        std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        int result = sqlite3_prepare_v2(m_database->connection(), sqlQuery.c_str(), -1, &statement, NULL);
        if (result != SQLITE_OK)
            return false;

        result = sqlite3_step(statement);
        if (result == SQLITE_DONE || result != SQLITE_ROW)
        {
            sqlite3_finalize(statement);
            return false;
        }

        const unsigned char* readTableName = sqlite3_column_text(statement, 0);
        if (readTableName == NULL || checkTableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
        {
            sqlite3_finalize(statement);
            return false;
        }

        sqlite3_finalize(statement);

        return true;
    }

    static const char DB_FILE_NAME[];

    static const uint32_t ARRAY_SIZE;
    static const uint32_t NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char BlobOffsetsParamTableTest::DB_FILE_NAME[] = "blob_offsets_param_table_test.sqlite";

const uint32_t BlobOffsetsParamTableTest::ARRAY_SIZE = 10;
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

    std::vector<BlobOffsetsParamTableRow> writtenRows;
    fillBlobOffsetsParamTableRows(writtenRows);
    testTable.write(writtenRows);

    std::vector<BlobOffsetsParamTableRow> readRows;
    // we must use reserve to prevent dangling pointer to offsetsHolder in parameterizedBlob
    // once std::vector is reallocated!
    readRows.reserve(NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS);
    testTable.read(readRows);
    checkBlobOffsetsParamTableRows(writtenRows, readRows);
}

TEST_F(BlobOffsetsParamTableTest, readWithCondition)
{
    BlobOffsetsParamTable& testTable = m_database->getBlobOffsetsParamTable();

    std::vector<BlobOffsetsParamTableRow> writtenRows;
    fillBlobOffsetsParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string condition = "name='Name1'";
    std::vector<BlobOffsetsParamTableRow> readRows;
    // reserve not needed since we expect only a single row
    testTable.read(condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkBlobOffsetsParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(BlobOffsetsParamTableTest, update)
{
    BlobOffsetsParamTable& testTable = m_database->getBlobOffsetsParamTable();

    std::vector<BlobOffsetsParamTableRow> writtenRows;
    fillBlobOffsetsParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    BlobOffsetsParamTableRow updateRow;
    fillBlobOffsetsParamTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "blobId=" + zserio::convertToString(updateRowId);
    testTable.update(updateRow, updateCondition);

    std::vector<BlobOffsetsParamTableRow> readRows;
    // reserve not needed since we expect only a single row
    testTable.read(updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkBlobOffsetsParamTableRow(updateRow, readRows[0]);
}

} // namespace blob_offsets_param_table
} // namespace sql_tables
