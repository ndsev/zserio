#include <cstdio>
#include <fstream>
#include <memory>
#include <string>

#include "gtest/gtest.h"
#include "sql_tables/TestDb.h"
#include "zserio/SqliteFinalizer.h"

namespace sql_tables
{
namespace bad_name_table
{

using allocator_type = TestDb::allocator_type;
using string_type = zserio::string<allocator_type>;

class BadNameTableTest : public ::testing::Test
{
public:
    BadNameTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new sql_tables::TestDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    bool isTableInDb(zserio::StringView tableName)
    {
        string_type sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='";
        sqlQuery += tableName;
        sqlQuery += "'";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                m_database->connection().prepareStatement(sqlQuery));

        int result = sqlite3_step(statement.get());
        if (result == SQLITE_DONE || result != SQLITE_ROW)
        {
            return false;
        }

        const char* readTableName = reinterpret_cast<const char*>(sqlite3_column_text(statement.get(), 0));
        return readTableName != nullptr && tableName == zserio::StringView(readTableName);
    }

    static const char* const DB_FILE_NAME;

    std::unique_ptr<sql_tables::TestDb> m_database;
};

const char* const BadNameTableTest::DB_FILE_NAME = "language/sql_tables/bad_name_table_test.sqlite";

TEST_F(BadNameTableTest, tableCreated)
{
    ASSERT_TRUE(isTableInDb("select"));
}

TEST_F(BadNameTableTest, tableOperations)
{
    BadNameTable& testTable = m_database->getSelect();
    BadNameTable::Row row;
    row.setId(1);
    row.setSelect(35);
    row.setWhere(11);
    row.setOrder(5);
    std::vector<BadNameTable::Row> rows{row};
    testTable.write(rows);
    row.setOrder(99);
    testTable.update(row, "TRUE");
    auto reader = testTable.createReader();
    ASSERT_TRUE(reader.hasNext());
    auto read = reader.next();
    ASSERT_EQ(read.getSelect(), 35);
    ASSERT_EQ(read.getWhere(), 11);
    ASSERT_EQ(read.getOrder(), 99);
}

} // namespace bad_name_table
} // namespace sql_tables
