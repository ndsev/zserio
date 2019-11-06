#include "gtest/gtest.h"

#include "zserio/SqliteConnection.h"

#include "templates/instantiate_type_as_sql_table_field/Test32Table.h"

namespace templates
{
namespace instantiate_type_as_sql_table_field
{

static const char SQLITE3_MEM_DB[] = ":memory:";

TEST(InstantiateSqlTableTest, instantiationOfTest32Table)
{
    sqlite3 *internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);
    zserio::SqliteConnection connection(internalConnection, ::zserio::SqliteConnection::INTERNAL_CONNECTION);

    Test32Table test32Table(connection, "test32Table");
    test32Table.createTable();

    std::vector<Test32TableRow> rows;
    Test32TableRow row;
    row.setId(13);
    Test32 test32;
    test32.setValue(42);
    row.setTest(test32);
    rows.push_back(row);
    test32Table.write(rows);

    std::vector<Test32TableRow> readRows;
    test32Table.read(readRows);
    ASSERT_EQ(1, readRows.size());

    ASSERT_EQ(13, readRows.at(0).getId());
    ASSERT_EQ(42, readRows.at(0).getTest().getValue());
}

} // namespace instantiate_type_as_sql_table_field
} // namespace templates
