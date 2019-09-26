#include <cstdio>

#include "gtest/gtest.h"

#include "templates/sql_table_templated_field/SqlTableTemplatedFieldDb.h"

namespace templates
{
namespace sql_table_templated_field
{

class SqlTableTemplatedFieldTest : public ::testing::Test
{
public:
    SqlTableTemplatedFieldTest()
    {
        std::remove(DB_FILE_NAME.c_str());
    }

    static const std::string DB_FILE_NAME;

protected:
    template <typename T>
    void assertEqualRows(const std::vector<T>& rows1, const std::vector<T>& rows2)
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

const std::string SqlTableTemplatedFieldTest::DB_FILE_NAME = "sql_table_templated_field_test.sqlite";

TEST_F(SqlTableTemplatedFieldTest, readWrite)
{
    SqlTableTemplatedFieldDb sqlTableTemplatedFieldDb(DB_FILE_NAME);
    sqlTableTemplatedFieldDb.createSchema();

    TemplatedTable_uint32& uint32Table = sqlTableTemplatedFieldDb.getUint32Table();
    std::vector<TemplatedTable_uint32::Row> uint32TableRows;
    TemplatedTable_uint32::Row uint32Row1;
    uint32Row1.setId(0);
    uint32Row1.setData(Data_uint32{42});
    uint32TableRows.push_back(uint32Row1);
    uint32Table.write(uint32TableRows);

    TemplatedTable_Union& unionTable = sqlTableTemplatedFieldDb.getUnionTable();
    std::vector<TemplatedTable_Union::Row> unionTableRows;
    TemplatedTable_Union::Row unionRow1;
    unionRow1.setId(0);
    unionRow1.setData(Data_Union{Union{std::string{"string"}}});
    unionTableRows.push_back(unionRow1);
    unionTable.write(unionTableRows);

    SqlTableTemplatedFieldDb readSqlTableTemplatedFieldDb(DB_FILE_NAME);
    std::vector<TemplatedTable_uint32::Row> readUint32TableRows;
    auto readerUint32 = readSqlTableTemplatedFieldDb.getUint32Table().createReader();
    while (readerUint32.hasNext())
        readUint32TableRows.push_back(readerUint32.next());
    std::vector<TemplatedTable_Union::Row> readUnionTableRows;
    auto readerUnion = readSqlTableTemplatedFieldDb.getUnionTable().createReader();
    while (readerUnion.hasNext())
        readUnionTableRows.push_back(readerUnion.next());

    assertEqualRows(uint32TableRows, readUint32TableRows);
    assertEqualRows(unionTableRows, readUnionTableRows);
}

} // namespace sql_table_templated_field
} // namespace templates
