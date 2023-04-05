#include "gtest/gtest.h"


#include "templates/instantiate_sql_table/U32Table.h"

#include "zserio/SqliteConnection.h"
#include "zserio/RebindAlloc.h"

namespace templates
{
namespace instantiate_sql_table
{

using allocator_type = U32Table::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

static const char* SQLITE3_MEM_DB = ":memory:";

TEST(InstantiateSqlTableTest, instantiationOfU32Table)
{
    sqlite3 *internalConnection = nullptr;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);
    zserio::SqliteConnection connection(internalConnection, ::zserio::SqliteConnection::INTERNAL_CONNECTION);

    U32Table u32Table(connection, "u32Table");
    u32Table.createTable();

    U32Table::Row row;
    row.setId(13);
    row.setInfo("info");
    vector_type<U32Table::Row> rows{row};
    u32Table.write(rows);

    auto reader = u32Table.createReader();
    ASSERT_TRUE(reader.hasNext());
    U32Table::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    ASSERT_EQ(13, readRow.getId());
    ASSERT_EQ(std::string("info"), readRow.getInfo().c_str());
}

} // namespace instantiate_sql_table
} // namespace templates
