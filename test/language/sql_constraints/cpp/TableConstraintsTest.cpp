#include <cstdio>
#include <vector>

#include "gtest/gtest.h"

#include "sql_constraints/TestDb.h"
#include "sql_constraints/table_constraints/TableConstraintsTable.h"

#include "zserio/SqliteException.h"
#include "zserio/RebindAlloc.h"

namespace sql_constraints
{
namespace table_constraints
{

using allocator_type = TableConstraintsTable::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class TableConstraintsTest : public ::testing::Test
{
public:
    TableConstraintsTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_constraints::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~TableConstraintsTest() override
    {
        delete m_database;
    }

protected:
    static const char DB_FILE_NAME[];

    sql_constraints::TestDb* m_database;
};

const char TableConstraintsTest::DB_FILE_NAME[] =
        "language/sql_constraints/table_constraints_test.sqlite";

TEST_F(TableConstraintsTest, primaryKey)
{
    TableConstraintsTable& tableConstraintsTable = m_database->getTableConstraintsTable();
    TableConstraintsTable::Row row;
    row.setPrimaryKey1(1);
    row.setPrimaryKey2(1);
    row.resetUniqueValue1();
    row.resetUniqueValue2();
    vector_type<TableConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_NO_THROW(tableConstraintsTable.write(rows));
}

TEST_F(TableConstraintsTest, primaryKeyWrong)
{
    TableConstraintsTable& tableConstraintsTable = m_database->getTableConstraintsTable();
    TableConstraintsTable::Row row;
    row.setPrimaryKey1(1);
    row.setPrimaryKey2(1);
    row.setUniqueValue1(1);
    row.setUniqueValue2(1);
    vector_type<TableConstraintsTable::Row> rows;
    rows.push_back(row);
    row.setUniqueValue1(2);
    rows.push_back(row);
    ASSERT_THROW(tableConstraintsTable.write(rows), zserio::SqliteException);
}

TEST_F(TableConstraintsTest, unique)
{
    TableConstraintsTable& tableConstraintsTable = m_database->getTableConstraintsTable();
    TableConstraintsTable::Row row;
    row.setPrimaryKey1(1);
    row.setPrimaryKey2(1);
    row.setUniqueValue1(1);
    row.setUniqueValue2(1);
    vector_type<TableConstraintsTable::Row> rows;
    rows.push_back(row);
    ASSERT_NO_THROW(tableConstraintsTable.write(rows));
}

TEST_F(TableConstraintsTest, uniqueWrong)
{
    TableConstraintsTable& tableConstraintsTable = m_database->getTableConstraintsTable();
    TableConstraintsTable::Row row;
    row.setPrimaryKey1(1);
    row.setPrimaryKey2(1);
    row.setUniqueValue1(1);
    row.setUniqueValue2(1);
    vector_type<TableConstraintsTable::Row> rows;
    rows.push_back(row);
    row.setPrimaryKey1(2);
    rows.push_back(row);
    ASSERT_THROW(tableConstraintsTable.write(rows), zserio::SqliteException);
}

} // namespace table_constraints
} // namespace sql_constraints
