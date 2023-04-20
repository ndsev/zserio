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
namespace blob_param_table
{

using allocator_type = TestDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class BlobParamTableTest : public ::testing::Test
{
public:
    BlobParamTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~BlobParamTableTest() override
    {
        delete m_database;
    }

    BlobParamTableTest(const BlobParamTableTest&) = delete;
    BlobParamTableTest& operator=(const BlobParamTableTest&) = delete;

    BlobParamTableTest(BlobParamTableTest&&) = delete;
    BlobParamTableTest& operator=(BlobParamTableTest&&) = delete;

protected:
    static void fillBlobParamTableRowWithNullValues(BlobParamTable::Row& row, uint32_t blobId)
    {
        row.setBlobId(blobId);
        row.resetName();
        row.resetParameters();
        row.resetBlob();
    }

    static void fillBlobParamTableRowsWithNullValues(vector_type<BlobParamTable::Row>& rows)
    {
        rows.clear();
        rows.resize(NUM_BLOB_PARAM_TABLE_ROWS);
        for (uint32_t blobId = 0; blobId < NUM_BLOB_PARAM_TABLE_ROWS; ++blobId)
            fillBlobParamTableRowWithNullValues(rows[blobId], blobId);
    }

    static void fillBlobParamTableRow(BlobParamTable::Row& row, uint32_t blobId, const string_type& name)
    {
        row.setBlobId(blobId);
        row.setName(name);

        // count must be changing because we need to catch error
        // caused by copying of ParameterizedBlob and its wrong initialization
        // - When the Row is read by a generated Reader, it is created on stack and ParameterizedBlob
        //   stores reference to Parameters which are created also on stack. Since the Row in the Reader::next()
        //   method is created still on the same place, the value stored in Parameters would be still the same
        //   if we used a constant and the test doesn't catch the error.
        // - This test tries to check that the generated Row is able to "reinitialize" its fields after
        //   move or copy.
        const uint32_t count = 1 + blobId;
        Parameters parameters(count);
        row.setParameters(parameters);

        ParameterizedBlob parameterizedBlob;
        vector_type<uint32_t>& array = parameterizedBlob.getArray();
        for (uint32_t i = 0; i < count; ++i)
            array.push_back(i);
        row.setBlob(parameterizedBlob);
    }

    static void fillBlobParamTableRows(vector_type<BlobParamTable::Row>& rows)
    {
        rows.clear();
        rows.resize(NUM_BLOB_PARAM_TABLE_ROWS);
        for (uint32_t blobId = 0; blobId < NUM_BLOB_PARAM_TABLE_ROWS; ++blobId)
            fillBlobParamTableRow(rows[blobId], blobId, "Name" + zserio::toString<allocator_type>(blobId));
    }

    static void checkBlobParamTableRow(const BlobParamTable::Row& row1, const BlobParamTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());

        if (row1.isNameSet() && row2.isNameSet())
            ASSERT_EQ(row1.getName(), row2.getName());
        else
            ASSERT_EQ(row1.isNameSet(), row2.isNameSet());

        if (row1.isParametersSet() && row2.isParametersSet())
            ASSERT_EQ(row1.getParameters(), row2.getParameters());
        else
            ASSERT_EQ(row1.isParametersSet(), row2.isParametersSet());

        if (row1.isBlobSet() && row2.isBlobSet())
            ASSERT_EQ(row1.getBlob(), row2.getBlob());
        else
            ASSERT_EQ(row1.isBlobSet(), row2.isBlobSet());
    }

    static void checkBlobParamTableRows(const vector_type<BlobParamTable::Row>& rows1,
            const vector_type<BlobParamTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkBlobParamTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "blobParamTable";
        string_type sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                m_database->connection().prepareStatement(sqlQuery));

        int result = sqlite3_step(statement.get());
        if (result == SQLITE_DONE || result != SQLITE_ROW)
            return false;

        const unsigned char* readTableName = sqlite3_column_text(statement.get(), 0);
        if (readTableName == nullptr || checkTableName != reinterpret_cast<const char*>(readTableName))
            return false;

        return true;
    }

    static const char* DB_FILE_NAME;

    static const uint32_t NUM_BLOB_PARAM_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char* BlobParamTableTest::DB_FILE_NAME = "language/sql_tables/blob_param_table_test.sqlite";

const uint32_t BlobParamTableTest::NUM_BLOB_PARAM_TABLE_ROWS = 20;

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

    vector_type<BlobParamTable::Row> writtenRows;
    fillBlobParamTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<BlobParamTable::Row> readRows;
    BlobParamTable::Reader reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkBlobParamTableRows(writtenRows, readRows);
}


TEST_F(BlobParamTableTest, readWithoutConditionWithNullValues)
{
    BlobParamTable& testTable = m_database->getBlobParamTable();

    vector_type<BlobParamTable::Row> writtenRows;
    fillBlobParamTableRowsWithNullValues(writtenRows);
    testTable.write(writtenRows);

    vector_type<BlobParamTable::Row> readRows;
    BlobParamTable::Reader reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkBlobParamTableRows(writtenRows, readRows);
}

TEST_F(BlobParamTableTest, readWithCondition)
{
    BlobParamTable& testTable = m_database->getBlobParamTable();

    vector_type<BlobParamTable::Row> writtenRows;
    fillBlobParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "name='Name1'";
    vector_type<BlobParamTable::Row> readRows;
    BlobParamTable::Reader reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkBlobParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(BlobParamTableTest, update)
{
    BlobParamTable& testTable = m_database->getBlobParamTable();

    vector_type<BlobParamTable::Row> writtenRows;
    fillBlobParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    BlobParamTable::Row updateRow;
    fillBlobParamTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "blobId=" + zserio::toString<allocator_type>(updateRowId);
    testTable.update(updateRow, updateCondition);

    vector_type<BlobParamTable::Row> readRows;
    BlobParamTable::Reader reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkBlobParamTableRow(updateRow, readRows[0]);
}

} // namespace blob_param_table
} // namespace sql_tables
