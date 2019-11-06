#include "gtest/gtest.h"

#include "zserio/SqliteConnection.h"

#include "templates/instantiate_sql_table/U32Table.h"

namespace templates
{
namespace instantiate_sql_table
{

static const char SQLITE3_MEM_DB[] = ":memory:";

TEST(InstantiateSqlTableTest, instantiationOfU32Table)
{
    sqlite3 *internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);
    zserio::SqliteConnection connection(internalConnection, ::zserio::SqliteConnection::INTERNAL_CONNECTION);

    U32Table u32Table(connection, "u32Table");
    u32Table.createTable();

    U32TableRow row;
    row.setId(13);
    row.setInfo("info");
    std::vector<U32TableRow> rows;
    rows.push_back(row);
    u32Table.write(rows);

    std::vector<U32TableRow> readRows;
    u32Table.read(readRows);
    ASSERT_EQ(1, readRows.size());

    ASSERT_EQ(13, readRows.at(0).getId());
    ASSERT_EQ(std::string("info"), readRows.at(0).getInfo());
}

} // namespace instantiate_sql_table
} // namespace templates
