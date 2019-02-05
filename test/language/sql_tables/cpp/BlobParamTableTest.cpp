#include <cstdio>
#include <vector>
#include <string>
#include <limits>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace blob_param_table
{

class BlobParamTableTest : public ::testing::Test
{
public:
    BlobParamTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~BlobParamTableTest()
    {
        delete m_database;
    }

protected:
    static void fillBlobParamTableRow(BlobParamTableRow& row, uint32_t blobId, const std::string& name)
    {
        row.setBlobId(blobId);
        row.setName(name);

        Parameters parameters;
        parameters.setCount(PARAMETERS_COUNT);
        row.setParameters(parameters);

        ParameterizedBlob parameterizedBlob;
        parameterizedBlob.initialize(row.getParameters());
        zserio::UInt32Array& array = parameterizedBlob.getArray();
        for (uint32_t i = 0; i < PARAMETERS_COUNT; ++i)
            array.push_back(i);
        row.setBlob(parameterizedBlob);
    }

    static void fillBlobParamTableRows(std::vector<BlobParamTableRow>& rows)
    {
        rows.clear();
        rows.resize(NUM_BLOB_PARAM_TABLE_ROWS);
        for (uint32_t blobId = 0; blobId < NUM_BLOB_PARAM_TABLE_ROWS; ++blobId)
        {
            const std::string name = "Name" + zserio::convertToString(blobId);
            fillBlobParamTableRow(rows[blobId], blobId, name);
        }
    }

    static void checkBlobParamTableRow(const BlobParamTableRow& row1, const BlobParamTableRow& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getParameters(), row2.getParameters());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkBlobParamTableRows(const std::vector<BlobParamTableRow>& rows1,
            const std::vector<BlobParamTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkBlobParamTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "blobParamTable";
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

    static const uint32_t PARAMETERS_COUNT;
    static const uint32_t NUM_BLOB_PARAM_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char BlobParamTableTest::DB_FILE_NAME[] = "blob_param_table_test.sqlite";

const uint32_t BlobParamTableTest::PARAMETERS_COUNT = 10;
const uint32_t BlobParamTableTest::NUM_BLOB_PARAM_TABLE_ROWS = 5;

TEST_F(BlobParamTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    BlobParamTable& testTable = m_database->getBlobParamTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(BlobParamTableTest, readWithoutCondition)
{
    BlobParamTable& testTable = m_database->getBlobParamTable();

    std::vector<BlobParamTableRow> writtenRows;
    fillBlobParamTableRows(writtenRows);
    testTable.write(writtenRows);

    std::vector<BlobParamTableRow> readRows;
    // we must use reserve to prevent dangling pointer to parameters in parameterizedBlob
    // once std::vector is reallocated!
    testTable.read(readRows);
    checkBlobParamTableRows(writtenRows, readRows);
}

TEST_F(BlobParamTableTest, readWithCondition)
{
    BlobParamTable& testTable = m_database->getBlobParamTable();

    std::vector<BlobParamTableRow> writtenRows;
    fillBlobParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string condition = "name='Name1'";
    std::vector<BlobParamTableRow> readRows;
    // reserve not needed since we expect only a single row
    testTable.read(condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkBlobParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(BlobParamTableTest, update)
{
    BlobParamTable& testTable = m_database->getBlobParamTable();

    std::vector<BlobParamTableRow> writtenRows;
    fillBlobParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    BlobParamTableRow updateRow;
    fillBlobParamTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "blobId=" + zserio::convertToString(updateRowId);
    testTable.update(updateRow, updateCondition);

    std::vector<BlobParamTableRow> readRows;
    // reserve not needed since we expect only a single row
    testTable.read(updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkBlobParamTableRow(updateRow, readRows[0]);
}

} // namespace blob_param_table
} // namespace sql_tables
