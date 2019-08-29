#include <cstdio>
#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_virtual_tables/fts5_virtual_table/Fts5TestDb.h"

namespace sql_virtual_tables
{
namespace fts5_virtual_table
{

class Fts5VirtualTableTest : public ::testing::Test
{
public:
    Fts5VirtualTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_virtual_tables::fts5_virtual_table::Fts5TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~Fts5VirtualTableTest()
    {
        delete m_database;
    }

protected:
    static void fillFts5VirtualTableRow(Fts5VirtualTable::Row& row, const std::string& title,
            const std::string& body)
    {
        row.setTitle(title);
        row.setBody(body);
    }

    static void fillFts5VirtualTableRows(std::vector<Fts5VirtualTable::Row>& rows)
    {
        rows.clear();
        for (int32_t id = 0; id < NUM_VIRTUAL_TABLE_ROWS; ++id)
        {
            const std::string title = "Title" + zserio::convertToString(id);
            const std::string body = "Body" + zserio::convertToString(id);
            Fts5VirtualTable::Row row;
            fillFts5VirtualTableRow(row, title, body);
            rows.push_back(row);
        }
    }

    static void checkFts5VirtualTableRow(const Fts5VirtualTable::Row& row1, const Fts5VirtualTable::Row& row2)
    {
        ASSERT_EQ(row1.getTitle(), row2.getTitle());
        ASSERT_EQ(row1.getBody(), row2.getBody());
    }

    static void checkFts5VirtualTableRows(const std::vector<Fts5VirtualTable::Row>& rows1,
            const std::vector<Fts5VirtualTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkFts5VirtualTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "fts5VirtualTable";
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
    static const int32_t NUM_VIRTUAL_TABLE_ROWS;

    sql_virtual_tables::fts5_virtual_table::Fts5TestDb* m_database;

};

const char Fts5VirtualTableTest::DB_FILE_NAME[] = "fts5_virtual_table_test.sqlite";
const int32_t Fts5VirtualTableTest::NUM_VIRTUAL_TABLE_ROWS = 5;

TEST_F(Fts5VirtualTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    Fts5VirtualTable& testTable = m_database->getFts5VirtualTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(Fts5VirtualTableTest, readWithoutCondition)
{
    Fts5VirtualTable& testTable = m_database->getFts5VirtualTable();

    std::vector<Fts5VirtualTable::Row> writtenRows;
    fillFts5VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    std::vector<Fts5VirtualTable::Row> readRows;
    auto reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkFts5VirtualTableRows(writtenRows, readRows);
}

TEST_F(Fts5VirtualTableTest, readWithCondition)
{
    Fts5VirtualTable& testTable = m_database->getFts5VirtualTable();

    std::vector<Fts5VirtualTable::Row> writtenRows;
    fillFts5VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string condition = "body='Body1'";
    std::vector<Fts5VirtualTable::Row> readRows;
    auto reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkFts5VirtualTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(Fts5VirtualTableTest, update)
{
    Fts5VirtualTable& testTable = m_database->getFts5VirtualTable();

    std::vector<Fts5VirtualTable::Row> writtenRows;
    fillFts5VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string updateTitle = "Title3";
    Fts5VirtualTable::Row updateRow;
    fillFts5VirtualTableRow(updateRow, updateTitle, "UpdatedName");
    const std::string updateCondition = "title='" + updateTitle + "'";
    testTable.update(updateRow, updateCondition);

    std::vector<Fts5VirtualTable::Row> readRows;
    auto reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkFts5VirtualTableRow(updateRow, readRows[0]);
}

} // namespace fts5_virtual_table
} // namespace sql_virtual_tables
