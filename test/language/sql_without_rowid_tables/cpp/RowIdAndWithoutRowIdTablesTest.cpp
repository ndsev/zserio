#include <cstdio>
#include <string>
#include <set>

#include "gtest/gtest.h"

#include "sql_without_rowid_tables/rowid_and_without_rowid_tables/RowIdAndWithoutRowIdDb.h"

#include "zserio/ValidationSqliteUtil.h"

using namespace zserio::literals;

namespace sql_without_rowid_tables
{
namespace rowid_and_without_rowid_tables
{

using allocator_type = RowIdAndWithoutRowIdDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T, typename COMPARE = std::less<T>>
using set_type = std::set<T, COMPARE, zserio::RebindAlloc<allocator_type, T>>;

class RowIdAndWithoutRowIdTablesTest : public ::testing::Test
{
public:
    RowIdAndWithoutRowIdTablesTest() :
            m_dbFileName("language/sql_without_rowid_tables/rowid_and_without_rowid_tables_test.sqlite"),
            m_withoutRowIdTableName("withoutRowIdTable"),
            m_ordinaryRowIdTableName("ordinaryRowIdTable"),
            m_rowIdColumnName("rowid")
    {
        std::remove(m_dbFileName);
    }

protected:
    bool isColumnInTable(zserio::ISqliteDatabase& database,
            const string_type& columnName, const string_type& tableName)
    {
        return zserio::ValidationSqliteUtil<allocator_type>::isColumnInTable(
                database.connection(), ""_sv, tableName, columnName, allocator_type());
    }

    const char* m_dbFileName;
    const char* m_withoutRowIdTableName;
    const char* m_ordinaryRowIdTableName;
    const char* m_rowIdColumnName;
};

TEST_F(RowIdAndWithoutRowIdTablesTest, checkRowIdColumn)
{
    RowIdAndWithoutRowIdDb database(m_dbFileName);
    database.createSchema();
    ASSERT_FALSE(isColumnInTable(database, m_rowIdColumnName, m_withoutRowIdTableName));
    ASSERT_TRUE(isColumnInTable(database, m_rowIdColumnName, m_ordinaryRowIdTableName));
}

TEST_F(RowIdAndWithoutRowIdTablesTest, createOrdinaryRowIdTable)
{
    RowIdAndWithoutRowIdDb database(m_dbFileName);
    WithoutRowIdTable& testTable = database.getWithoutRowIdTable();
    testTable.createOrdinaryRowIdTable();
    ASSERT_TRUE(isColumnInTable(database, m_rowIdColumnName, m_withoutRowIdTableName));
}

TEST_F(RowIdAndWithoutRowIdTablesTest, checkWithoutRowIdTableNamesBlackList)
{
    RowIdAndWithoutRowIdDb database(m_dbFileName);
    set_type<string_type> withoutRowIdTableNamesBlackList;
    withoutRowIdTableNamesBlackList.insert(m_withoutRowIdTableName);
    database.createSchema(withoutRowIdTableNamesBlackList);
    ASSERT_TRUE(isColumnInTable(database, m_rowIdColumnName, m_withoutRowIdTableName));
    ASSERT_TRUE(isColumnInTable(database, m_rowIdColumnName, m_ordinaryRowIdTableName));
}

} // namespace rowid_and_without_rowid_tables

} // namespace sql_without_rowid_tables
