#include <cstdio>
#include <string>
#include <vector>
#include <memory>

#include "gtest/gtest.h"

#include "sql_virtual_tables/fts3_virtual_table/Fts3TestDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/SqliteFinalizer.h"

namespace sql_virtual_tables
{
namespace fts3_virtual_table
{

using allocator_type = Fts3TestDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class Fts3VirtualTableTest : public ::testing::Test
{
public:
    Fts3VirtualTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_virtual_tables::fts3_virtual_table::Fts3TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~Fts3VirtualTableTest() override
    {
        delete m_database;
    }

    Fts3VirtualTableTest(const Fts3VirtualTableTest&) = delete;
    Fts3VirtualTableTest& operator=(const Fts3VirtualTableTest&) = delete;

    Fts3VirtualTableTest(Fts3VirtualTableTest&&) = delete;
    Fts3VirtualTableTest& operator=(Fts3VirtualTableTest&&) = delete;

protected:
    static void fillFts3VirtualTableRow(Fts3VirtualTable::Row& row, const string_type& title,
            const string_type& body)
    {
        row.setTitle(title);
        row.setBody(body);
    }

    static void fillFts3VirtualTableRows(vector_type<Fts3VirtualTable::Row>& rows)
    {
        rows.clear();
        for (int32_t id = 0; id < NUM_VIRTUAL_TABLE_ROWS; ++id)
        {
            const string_type title = "Title" + zserio::toString<allocator_type>(id);
            const string_type body = "Body" + zserio::toString<allocator_type>(id);
            Fts3VirtualTable::Row row;
            fillFts3VirtualTableRow(row, title, body);
            rows.push_back(row);
        }
    }

    static void checkFts3VirtualTableRow(const Fts3VirtualTable::Row& row1, const Fts3VirtualTable::Row& row2)
    {
        ASSERT_EQ(row1.getTitle(), row2.getTitle());
        ASSERT_EQ(row1.getBody(), row2.getBody());
    }

    static void checkFts3VirtualTableRows(const vector_type<Fts3VirtualTable::Row>& rows1,
            const vector_type<Fts3VirtualTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkFts3VirtualTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "fts3VirtualTable";
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
    static const int32_t NUM_VIRTUAL_TABLE_ROWS;

    sql_virtual_tables::fts3_virtual_table::Fts3TestDb* m_database;
};

const char* Fts3VirtualTableTest::DB_FILE_NAME = "language/sql_virtual_tables/fts3_virtual_table_test.sqlite";
const int32_t Fts3VirtualTableTest::NUM_VIRTUAL_TABLE_ROWS = 5;

TEST_F(Fts3VirtualTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    Fts3VirtualTable& testTable = m_database->getFts3VirtualTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(Fts3VirtualTableTest, readWithoutCondition)
{
    Fts3VirtualTable& testTable = m_database->getFts3VirtualTable();

    vector_type<Fts3VirtualTable::Row> writtenRows;
    fillFts3VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<Fts3VirtualTable::Row> readRows;
    auto reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkFts3VirtualTableRows(writtenRows, readRows);
}

TEST_F(Fts3VirtualTableTest, readWithCondition)
{
    Fts3VirtualTable& testTable = m_database->getFts3VirtualTable();

    vector_type<Fts3VirtualTable::Row> writtenRows;
    fillFts3VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "body='Body1'";
    vector_type<Fts3VirtualTable::Row> readRows;
    auto reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkFts3VirtualTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(Fts3VirtualTableTest, update)
{
    Fts3VirtualTable& testTable = m_database->getFts3VirtualTable();

    vector_type<Fts3VirtualTable::Row> writtenRows;
    fillFts3VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type updateTitle = "Title3";
    Fts3VirtualTable::Row updateRow;
    fillFts3VirtualTableRow(updateRow, updateTitle, "UpdatedName");
    const string_type updateCondition = "title='" + updateTitle + "'";
    testTable.update(updateRow, updateCondition);

    vector_type<Fts3VirtualTable::Row> readRows;
    auto reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkFts3VirtualTableRow(updateRow, readRows[0]);
}

} // namespace fts3_virtual_table
} // namespace sql_virtual_tables
