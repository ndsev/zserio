#include <cstdio>
#include <vector>
#include <string>
#include <limits>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"

namespace sql_tables
{
namespace column_param_table
{

using allocator_type = TestDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ColumnParamTableTest : public ::testing::Test
{
public:
    ColumnParamTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ColumnParamTableTest()
    {
        delete m_database;
    }

protected:
    static void fillColumnParamTableRow(ColumnParamTable::Row& row, uint32_t blobId, const string_type& name)
    {
        row.setBlobId(blobId);
        row.setName(name);

        ParameterizedBlob parameterizedBlob;
        parameterizedBlob.setValue(PARAMETERIZED_BLOB_VALUE);
        row.setBlob(parameterizedBlob);
    }

    static void fillColumnParamTableRows(vector_type<ColumnParamTable::Row>& rows)
    {
        rows.clear();
        for (uint32_t blobId = 0; blobId < NUM_COLUMN_PARAM_TABLE_ROWS; ++blobId)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(blobId);
            ColumnParamTable::Row row;
            fillColumnParamTableRow(row, blobId, name);
            rows.push_back(row);
        }
    }

    static void checkColumnParamTableRow(const ColumnParamTable::Row& row1, const ColumnParamTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkColumnParamTableRows(const vector_type<ColumnParamTable::Row>& rows1,
            const vector_type<ColumnParamTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkColumnParamTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "columnParamTable";
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

    static const uint32_t PARAMETERIZED_BLOB_VALUE;
    static const uint32_t NUM_COLUMN_PARAM_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char ColumnParamTableTest::DB_FILE_NAME[] = "language/sql_tables/column_param_table_test.sqlite";

const uint32_t ColumnParamTableTest::PARAMETERIZED_BLOB_VALUE = 0xABCD;
const uint32_t ColumnParamTableTest::NUM_COLUMN_PARAM_TABLE_ROWS = 5;

TEST_F(ColumnParamTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    ColumnParamTable& testTable = m_database->getColumnParamTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(ColumnParamTableTest, readWithoutCondition)
{
    ColumnParamTable& testTable = m_database->getColumnParamTable();

    vector_type<ColumnParamTable::Row> writtenRows;
    fillColumnParamTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<ColumnParamTable::Row> readRows;
    auto reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkColumnParamTableRows(writtenRows, readRows);
}

TEST_F(ColumnParamTableTest, readWithCondition)
{
    ColumnParamTable& testTable = m_database->getColumnParamTable();

    vector_type<ColumnParamTable::Row> writtenRows;
    fillColumnParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "name='Name1'";
    vector_type<ColumnParamTable::Row> readRows;
    auto reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkColumnParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ColumnParamTableTest, update)
{
    ColumnParamTable& testTable = m_database->getColumnParamTable();

    vector_type<ColumnParamTable::Row> writtenRows;
    fillColumnParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    ColumnParamTable::Row updateRow;
    fillColumnParamTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "blobId=" + zserio::toString<allocator_type>(updateRowId);
    testTable.update(updateRow, updateCondition);

    vector_type<ColumnParamTable::Row> readRows;
    auto reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkColumnParamTableRow(updateRow, readRows[0]);
}

} // namespace column_param_table
} // namespace sql_tables
