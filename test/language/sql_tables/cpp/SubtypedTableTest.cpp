#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace subtyped_table
{

TEST(SubtypedTest, TestSubtypedTable)
{
    const std::string dbFileName = "subtyped_table_test.sqlite";
    std::remove(dbFileName.c_str());

    TestDb database(dbFileName);
    database.createSchema();
    ASSERT_TRUE(database.isOpen());

    // check if database does contain the table
    std::string tableName;
    sqlite3_stmt* statement;
    const std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='subtypedTable'";
    int result = sqlite3_prepare_v2(database.getConnection(), sqlQuery.c_str(), -1, &statement, NULL);
    ASSERT_EQ(result, SQLITE_OK);
    result = sqlite3_step(statement);
    if (result == SQLITE_ROW)
    {
        const unsigned char* readTableName = sqlite3_column_text(statement, 0);
        if (readTableName != NULL)
            tableName = reinterpret_cast<const char*>(readTableName);
    }
    sqlite3_finalize(statement);

    ASSERT_EQ("subtypedTable", tableName);

    // check table getter
    TestTable& studentsAsTestTable = database.getSubtypedTable();
    SubtypedTable& studentsAsSubtypedTable = database.getSubtypedTable();
    ASSERT_EQ(&studentsAsTestTable, &studentsAsSubtypedTable);
}

} // namespace subtyped_table
} // namespace sql_tables
