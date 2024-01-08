#include <cstdio>

#include "gtest/gtest.h"
#include "templates/sql_table_templated_field/SqlTableTemplatedFieldDb.h"
#include "zserio/RebindAlloc.h"

namespace templates
{
namespace sql_table_templated_field
{

using allocator_type = SqlTableTemplatedFieldDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class SqlTableTemplatedFieldTest : public ::testing::Test
{
public:
    SqlTableTemplatedFieldTest()
    {
        std::remove(DB_FILE_NAME.c_str());
    }

    static const string_type DB_FILE_NAME;

protected:
    template <typename T>
    void assertEqualRows(const vector_type<T>& rows1, const vector_type<T>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
        {
            assertEqualRow(rows1.at(i), rows2.at(i));
        }
    }

    template <typename T>
    void assertEqualRow(const T& row1, const T& row2)
    {
        ASSERT_TRUE(row1.getId() == row2.getId());
        ASSERT_TRUE(row1.getData() == row2.getData());
    }
};

const string_type SqlTableTemplatedFieldTest::DB_FILE_NAME =
        "language/templates/sql_table_templated_field_test.sqlite";

TEST_F(SqlTableTemplatedFieldTest, readWrite)
{
    SqlTableTemplatedFieldDb sqlTableTemplatedFieldDb(DB_FILE_NAME);
    sqlTableTemplatedFieldDb.createSchema();

    TemplatedTable_uint32& uint32Table = sqlTableTemplatedFieldDb.getUint32Table();
    vector_type<TemplatedTable_uint32::Row> uint32TableRows;
    TemplatedTable_uint32::Row uint32Row1;
    uint32Row1.setId(0);
    uint32Row1.setData(Data_uint32{42});
    uint32TableRows.push_back(uint32Row1);
    uint32Table.write(uint32TableRows);

    TemplatedTable_Union& unionTable = sqlTableTemplatedFieldDb.getUnionTable();
    vector_type<TemplatedTable_Union::Row> unionTableRows;
    TemplatedTable_Union::Row unionRow1;
    unionRow1.setId(0);
    {
        Union u;
        u.setValueString("string");
        unionRow1.setData(Data_Union{std::move(u)});
    }
    unionTableRows.push_back(unionRow1);
    unionTable.write(unionTableRows);

    SqlTableTemplatedFieldDb readSqlTableTemplatedFieldDb(DB_FILE_NAME);
    vector_type<TemplatedTable_uint32::Row> readUint32TableRows;
    auto readerUint32 = readSqlTableTemplatedFieldDb.getUint32Table().createReader();
    while (readerUint32.hasNext())
        readUint32TableRows.push_back(readerUint32.next());
    vector_type<TemplatedTable_Union::Row> readUnionTableRows;
    auto readerUnion = readSqlTableTemplatedFieldDb.getUnionTable().createReader();
    while (readerUnion.hasNext())
        readUnionTableRows.push_back(readerUnion.next());

    assertEqualRows(uint32TableRows, readUint32TableRows);
    assertEqualRows(unionTableRows, readUnionTableRows);
}

} // namespace sql_table_templated_field
} // namespace templates
