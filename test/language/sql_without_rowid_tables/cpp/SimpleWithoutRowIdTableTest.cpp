#include <cstdio>
#include <string>
#include <set>

#include "gtest/gtest.h"

#include "sql_without_rowid_tables/simple_without_rowid_table/SimpleWithoutRowIdDb.h"

#include "zserio/ValidationSqliteUtil.h"

using namespace zserio::literals;

namespace sql_without_rowid_tables
{
namespace simple_without_rowid_table
{

using allocator_type = SimpleWithoutRowIdDb::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T, typename COMPARE = std::less<T>>
using set_type = std::set<T, COMPARE, zserio::RebindAlloc<allocator_type, T>>;

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
            const string_type& columnName, const string_type& tableName)
    {
        return zserio::ValidationSqliteUtil<allocator_type>::isColumnInTable(
                database.connection(), ""_sv, tableName, columnName, allocator_type());
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
    set_type<string_type> withoutRowIdTableNamesBlackList;
    withoutRowIdTableNamesBlackList.insert(m_tableName);
    database.createSchema(withoutRowIdTableNamesBlackList);
    ASSERT_TRUE(isColumnInTable(database, m_rowIdColumnName, m_tableName));
}

} // namespace simple_without_rowid_table

} // namespace sql_without_rowid_tables
