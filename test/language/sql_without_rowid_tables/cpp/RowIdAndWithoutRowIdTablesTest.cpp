#include <cstdio>
#include <string>
#include <set>

#include "gtest/gtest.h"

#include "sql_without_rowid_tables/rowid_and_without_rowid_tables/RowIdAndWithoutRowIdDb.h"

namespace sql_without_rowid_tables
{
namespace rowid_and_without_rowid_tables
{

class RowIdAndWithoutRowIdTablesTest : public ::testing::Test
{
public:
    RowIdAndWithoutRowIdTablesTest()
    {
        m_dbFileName = "rowid_and_without_rowid_tables_test.sqlite";
        m_withoutRowIdTableName = "withoutRowIdTable";
        m_ordinaryRowIdTableName = "ordinaryRowIdTable";
        m_rowIdColumnName = "rowid";
    }

protected:
    bool isColumnInTable(const std::string& columnName, const std::string& tableName)
    {
        sqlite3_stmt* statement;
        const std::string sqlQuery = "SELECT " + columnName + " FROM " + tableName + " LIMIT 0";
        int result = sqlite3_prepare_v2(m_database.getConnection(), sqlQuery.c_str(), -1, &statement, NULL);
        sqlite3_finalize(statement);

        return (result == SQLITE_OK) ? true : false;
    }

    sql_without_rowid_tables::rowid_and_without_rowid_tables::RowIdAndWithoutRowIdDb m_database;
    const char* m_dbFileName;
    const char* m_withoutRowIdTableName;
    const char* m_ordinaryRowIdTableName;
    const char* m_rowIdColumnName;
};

TEST_F(RowIdAndWithoutRowIdTablesTest, checkRowIdColumn)
{
    std::remove(m_dbFileName);
    m_database.open(m_dbFileName);
    m_database.createSchema();
    ASSERT_FALSE(isColumnInTable(m_rowIdColumnName, m_withoutRowIdTableName));
    ASSERT_TRUE(isColumnInTable(m_rowIdColumnName, m_ordinaryRowIdTableName));
}

TEST_F(RowIdAndWithoutRowIdTablesTest, createOrdinaryRowIdTable)
{
    std::remove(m_dbFileName);
    m_database.open(m_dbFileName);
    WithoutRowIdTable& testTable = m_database.getWithoutRowIdTable();
    testTable.createOrdinaryRowIdTable();
    ASSERT_TRUE(isColumnInTable(m_rowIdColumnName, m_withoutRowIdTableName));
}

TEST_F(RowIdAndWithoutRowIdTablesTest, checkWithoutRowIdTableNamesBlackList)
{
    std::remove(m_dbFileName);
    std::set<std::string> withoutRowIdTableNamesBlackList;
    withoutRowIdTableNamesBlackList.insert(m_withoutRowIdTableName);
    m_database.open(m_dbFileName);
    m_database.createSchema(withoutRowIdTableNamesBlackList);
    ASSERT_TRUE(isColumnInTable(m_rowIdColumnName, m_withoutRowIdTableName));
    ASSERT_TRUE(isColumnInTable(m_rowIdColumnName, m_ordinaryRowIdTableName));
}

} // namespace rowid_and_without_rowid_tables

} // namespace sql_without_rowid_tables
