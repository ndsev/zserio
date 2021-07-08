#include "gtest/gtest.h"

#include "zserio/SqliteConnection.h"

#include "templates/instantiate_type_as_sql_table_field/Test32Table.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace instantiate_type_as_sql_table_field
{

using allocator_type = Test32Table::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

static const char SQLITE3_MEM_DB[] = ":memory:";

TEST(InstantiateSqlTableTest, instantiationOfTest32Table)
{
    sqlite3 *internalConnection = nullptr;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);
    zserio::SqliteConnection connection(internalConnection, ::zserio::SqliteConnection::INTERNAL_CONNECTION);

    Test32Table test32Table(connection, "test32Table");
    test32Table.createTable();

    Test32Table::Row row;
    row.setId(13);
    row.setTest(Test32{42});
    vector_type<Test32Table::Row> rows{row};
    test32Table.write(rows);

    auto reader = test32Table.createReader();
    ASSERT_TRUE(reader.hasNext());
    Test32Table::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    ASSERT_EQ(13, readRow.getId());
    ASSERT_EQ(42, readRow.getTest().getValue());
}

} // namespace instantiate_type_as_sql_table_field
} // namespace templates
