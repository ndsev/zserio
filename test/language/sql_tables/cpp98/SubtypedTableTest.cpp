#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace subtyped_table
{

class SubtypedTableTest : public ::testing::Test
{
public:
    SubtypedTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~SubtypedTableTest()
    {
        delete m_database;
    }

protected:
    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "subtypedTable";
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

    sql_tables::TestDb* m_database;
};

const char SubtypedTableTest::DB_FILE_NAME[] = "subtyped_table_test.sqlite";

TEST_F(SubtypedTableTest, testSubtypedTable)
{
    ASSERT_TRUE(isTableInDb());

    TestTable& studentsAsTestTable = m_database->getSubtypedTable();
    SubtypedTable& studentsAsSubtypedTable = m_database->getSubtypedTable();
    ASSERT_EQ(&studentsAsTestTable, &studentsAsSubtypedTable);
}

} // namespace subtyped_table
} // namespace sql_tables
