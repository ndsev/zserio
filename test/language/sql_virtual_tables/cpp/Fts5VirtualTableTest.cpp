#include <cstdio>
#include <string>
#include <vector>
#include <memory>

#include "gtest/gtest.h"

#include "sql_virtual_tables/fts5_virtual_table/Fts5TestDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/SqliteFinalizer.h"

namespace sql_virtual_tables
{
namespace fts5_virtual_table
{

using allocator_type = Fts5TestDb::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

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
    static void fillFts5VirtualTableRow(Fts5VirtualTable::Row& row, const string_type& title,
            const string_type& body)
    {
        row.setTitle(title);
        row.setBody(body);
    }

    static void fillFts5VirtualTableRows(vector_type<Fts5VirtualTable::Row>& rows)
    {
        rows.clear();
        for (int32_t id = 0; id < NUM_VIRTUAL_TABLE_ROWS; ++id)
        {
            const string_type title = "Title" + zserio::toString<allocator_type>(id);
            const string_type body = "Body" + zserio::toString<allocator_type>(id);
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

    static void checkFts5VirtualTableRows(const vector_type<Fts5VirtualTable::Row>& rows1,
            const vector_type<Fts5VirtualTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkFts5VirtualTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "fts5VirtualTable";
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

    vector_type<Fts5VirtualTable::Row> writtenRows;
    fillFts5VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<Fts5VirtualTable::Row> readRows;
    auto reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkFts5VirtualTableRows(writtenRows, readRows);
}

TEST_F(Fts5VirtualTableTest, readWithCondition)
{
    Fts5VirtualTable& testTable = m_database->getFts5VirtualTable();

    vector_type<Fts5VirtualTable::Row> writtenRows;
    fillFts5VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "body='Body1'";
    vector_type<Fts5VirtualTable::Row> readRows;
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

    vector_type<Fts5VirtualTable::Row> writtenRows;
    fillFts5VirtualTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type updateTitle = "Title3";
    Fts5VirtualTable::Row updateRow;
    fillFts5VirtualTableRow(updateRow, updateTitle, "UpdatedName");
    const string_type updateCondition = "title='" + updateTitle + "'";
    testTable.update(updateRow, updateCondition);

    vector_type<Fts5VirtualTable::Row> readRows;
    auto reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkFts5VirtualTableRow(updateRow, readRows[0]);
}

} // namespace fts5_virtual_table
} // namespace sql_virtual_tables
