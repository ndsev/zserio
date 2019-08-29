#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace multiple_pk_table
{

class MultiplePkTableTest : public ::testing::Test
{
public:
    MultiplePkTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~MultiplePkTableTest()
    {
        delete m_database;
    }

protected:
    static void fillMultiplePkTableRow(MultiplePkTable::Row& row, int32_t blobId, const std::string& name)
    {
        row.setBlobId(blobId);
        row.setAge(10);
        row.setName(name);
    }

    static void fillMultiplePkTableRows(std::vector<MultiplePkTable::Row>& rows)
    {
        rows.clear();
        for (int32_t blobId = 0; blobId < NUM_MULTIPLE_PK_TABLE_ROWS; ++blobId)
        {
            const std::string name = "Name" + zserio::convertToString(blobId);
            MultiplePkTable::Row row;
            fillMultiplePkTableRow(row, blobId, name);
            rows.push_back(row);
        }
    }

    static void checkMultiplePkTableRow(const MultiplePkTable::Row& row1, const MultiplePkTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());
        ASSERT_EQ(row1.getAge(), row2.getAge());
        ASSERT_EQ(row1.getName(), row2.getName());
    }

    static void checkMultiplePkTableRows(const std::vector<MultiplePkTable::Row>& rows1,
            const std::vector<MultiplePkTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkMultiplePkTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "multiplePkTable";
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

    static const int32_t NUM_MULTIPLE_PK_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char MultiplePkTableTest::DB_FILE_NAME[] = "multiple_pk_table_test.sqlite";

const int32_t MultiplePkTableTest::NUM_MULTIPLE_PK_TABLE_ROWS = 5;

TEST_F(MultiplePkTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    MultiplePkTable& testTable = m_database->getMultiplePkTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(MultiplePkTableTest, readWithoutCondition)
{
    MultiplePkTable& testTable = m_database->getMultiplePkTable();

    std::vector<MultiplePkTable::Row> writtenRows;
    fillMultiplePkTableRows(writtenRows);
    testTable.write(writtenRows);

    std::vector<MultiplePkTable::Row> readRows;
    MultiplePkTable::Reader reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkMultiplePkTableRows(writtenRows, readRows);
}

TEST_F(MultiplePkTableTest, readWithCondition)
{
    MultiplePkTable& testTable = m_database->getMultiplePkTable();

    std::vector<MultiplePkTable::Row> writtenRows;
    fillMultiplePkTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string condition = "name='Name1'";
    std::vector<MultiplePkTable::Row> readRows;
    MultiplePkTable::Reader reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkMultiplePkTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(MultiplePkTableTest, update)
{
    MultiplePkTable& testTable = m_database->getMultiplePkTable();

    std::vector<MultiplePkTable::Row> writtenRows;
    fillMultiplePkTableRows(writtenRows);
    testTable.write(writtenRows);

    const int32_t updateRowId = 3;
    MultiplePkTable::Row updateRow;
    fillMultiplePkTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "blobId=" + zserio::convertToString(updateRowId);
    testTable.update(updateRow, updateCondition);

    std::vector<MultiplePkTable::Row> readRows;
    MultiplePkTable::Reader reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkMultiplePkTableRow(updateRow, readRows[0]);
}

} // namespace multiple_pk_table
} // namespace sql_tables
