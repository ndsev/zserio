#include <cstdio>
#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_virtual_tables/fts3_virtual_table/Fts3TestDb.h"

namespace sql_virtual_tables
{
namespace fts3_virtual_table
{

class Fts3VirtualTableTest : public ::testing::Test
{
public:
    Fts3VirtualTableTest() : m_database(DB_FILE_NAME)
    {
        m_database.createSchema();
    }

    ~Fts3VirtualTableTest()
    {
        m_database.close();
        std::remove(DB_FILE_NAME);
    }

protected:
    static void fillFts3VirtualTableRow(Fts3VirtualTableRow& row, const std::string& title,
            const std::string& body)
    {
        row.setTitle(title);
        row.setBody(body);
    }

    static void fillFts3VirtualTableRows(std::vector<Fts3VirtualTableRow>& rows)
    {
        rows.clear();
        for (int32_t id = 0; id < NUM_VIRTUAL_TABLE_ROWS; ++id)
        {
            const std::string title = "Title" + zserio::convertToString(id);
            const std::string body = "Body" + zserio::convertToString(id);
            Fts3VirtualTableRow row;
            fillFts3VirtualTableRow(row, title, body);
            rows.push_back(row);
        }
    }

    static void checkFts3VirtualTableRow(const Fts3VirtualTableRow& row1, const Fts3VirtualTableRow& row2)
    {
        ASSERT_EQ(row1.getTitle(), row2.getTitle());
        ASSERT_EQ(row1.getBody(), row2.getBody());
    }

    static void checkFts3VirtualTableRows(const std::vector<Fts3VirtualTableRow>& rows1,
            const std::vector<Fts3VirtualTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkFts3VirtualTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "fts3VirtualTable";
        std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        int result = sqlite3_prepare_v2(m_database.getConnection(), sqlQuery.c_str(), -1, &statement, NULL);
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
    static const int32_t NUM_VIRTUAL_TABLE_ROWS;

    sql_virtual_tables::fts3_virtual_table::Fts3TestDb  m_database;
};

const char Fts3VirtualTableTest::DB_FILE_NAME[] = "fts3_virtual_table_test.sqlite";
const int32_t Fts3VirtualTableTest::NUM_VIRTUAL_TABLE_ROWS = 5;

TEST_F(Fts3VirtualTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    Fts3VirtualTable& testTable = m_database.getFts3VirtualTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(Fts3VirtualTableTest, readWithoutCondition)
{
    Fts3VirtualTable& testTable = m_database.getFts3VirtualTable();

    std::vector<Fts3VirtualTableRow> writtenRows;
    fillFts3VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    std::vector<Fts3VirtualTableRow> readRows;
    testTable.read(readRows);
    checkFts3VirtualTableRows(writtenRows, readRows);
}

TEST_F(Fts3VirtualTableTest, readWithCondition)
{
    Fts3VirtualTable& testTable = m_database.getFts3VirtualTable();

    std::vector<Fts3VirtualTableRow> writtenRows;
    fillFts3VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string condition = "body='Body1'";
    std::vector<Fts3VirtualTableRow> readRows;
    testTable.read(condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkFts3VirtualTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(Fts3VirtualTableTest, update)
{
    Fts3VirtualTable& testTable = m_database.getFts3VirtualTable();

    std::vector<Fts3VirtualTableRow> writtenRows;
    fillFts3VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string updateTitle = "Title3";
    Fts3VirtualTableRow updateRow;
    fillFts3VirtualTableRow(updateRow, updateTitle, "UpdatedName");
    const std::string updateCondition = "title='" + updateTitle + "'";
    testTable.update(updateRow, updateCondition);

    std::vector<Fts3VirtualTableRow> readRows;
    testTable.read(updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkFts3VirtualTableRow(updateRow, readRows[0]);
}

} // namespace fts3_virtual_table
} // namespace sql_virtual_tables
