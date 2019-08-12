#include <cstdio>
#include <string>
#include <set>

#include "gtest/gtest.h"

#include "sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdDb.h"

namespace sql_without_rowid_tables
{
namespace simple_without_rowid_table
{

class SimpleWithoutRowIdTableTest : public ::testing::Test
{
public:
    SimpleWithoutRowIdTableTest() :
            m_dbFileName("simple_without_rowid_table_test.sqlite"),
            m_tableName("simpleWithoutRowIdTable"),
            m_rowIdColumnName("rowid")
    {
        std::remove(m_dbFileName);
    }

protected:
    bool isColumnInTable(zserio::ISqliteDatabase& database,
            const std::string& columnName, const std::string& tableName)
    {
        sqlite3_stmt* statement;
        const std::string sqlQuery = "SELECT " + columnName + " FROM " + tableName + " LIMIT 0";
        int result = sqlite3_prepare_v2(database.connection(), sqlQuery.c_str(), -1, &statement, NULL);
        sqlite3_finalize(statement);

        return (result == SQLITE_OK) ? true : false;
    }

    const char* m_dbFileName;
    const char* m_tableName;
    const char* m_rowIdColumnName;
};

TEST_F(SimpleWithoutRowIdTableTest, checkRowIdColumn)
{
    SimpleWithoutRowIdDb database(m_dbFileName);
    database.createSchema();
    ASSERT_FALSE(isColumnInTable(database, m_rowIdColumnName, m_tableName));
}

TEST_F(SimpleWithoutRowIdTableTest, createOrdinaryRowIdTable)
{
    SimpleWithoutRowIdDb database(m_dbFileName);
    SimpleWithoutRowIdTable& testTable = database.getSimpleWithoutRowIdTable();
    testTable.createOrdinaryRowIdTable();
    ASSERT_TRUE(isColumnInTable(database, m_rowIdColumnName, m_tableName));
}

TEST_F(SimpleWithoutRowIdTableTest, checkWithoutRowIdTableNamesBlackList)
{
    SimpleWithoutRowIdDb database(m_dbFileName);
    std::set<std::string> withoutRowIdTableNamesBlackList;
    withoutRowIdTableNamesBlackList.insert(m_tableName);
    database.createSchema(withoutRowIdTableNamesBlackList);
    ASSERT_TRUE(isColumnInTable(database, m_rowIdColumnName, m_tableName));
}

} // namespace simple_without_rowid_table

} // namespace sql_without_rowid_tables
