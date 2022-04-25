#include <cstdio>

#include "gtest/gtest.h"

#include "templates/instantiate_type_as_sql_database_field/InstantiateTypeAsSqlDatabaseFieldDb.h"

#include "zserio/RebindAlloc.h"

namespace templates
{
namespace instantiate_type_as_sql_database_field
{

using allocator_type = InstantiateTypeAsSqlDatabaseFieldDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class InstantiateTypeAsSqlDatabaseFieldTest : public ::testing::Test
{
public:
    InstantiateTypeAsSqlDatabaseFieldTest()
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

const string_type InstantiateTypeAsSqlDatabaseFieldTest::DB_FILE_NAME =
        "language/templates/instantiate_type_as_sql_database_field_test.sqlite";

TEST_F(InstantiateTypeAsSqlDatabaseFieldTest, readWrite)
{
    InstantiateTypeAsSqlDatabaseFieldDb instantiateTypeAsSqlDatabaseFieldDb(DB_FILE_NAME);
    instantiateTypeAsSqlDatabaseFieldDb.createSchema();

    StringTable& stringTable = instantiateTypeAsSqlDatabaseFieldDb.getStringTable();
    vector_type<StringTable::Row> stringTableRows;
    StringTable::Row stringRow1;
    stringRow1.setId(0);
    stringRow1.setData("test");
    stringTableRows.push_back(stringRow1);
    stringTable.write(stringTableRows);

    StringTable& otherStringTable = instantiateTypeAsSqlDatabaseFieldDb.getOtherStringTable();
    vector_type<StringTable::Row> otherStringTableRows;
    StringTable::Row otherStringRow1;
    otherStringRow1.setId(0);
    otherStringRow1.setData("other test");
    otherStringTableRows.push_back(otherStringRow1);
    otherStringTable.write(otherStringTableRows);

    InstantiateTypeAsSqlDatabaseFieldDb readInstantiateTypeAsSqlDatabaseFieldDb(DB_FILE_NAME);
    vector_type<StringTable::Row> readStringTableRows;
    auto readerString = readInstantiateTypeAsSqlDatabaseFieldDb.getStringTable().createReader();
    while (readerString.hasNext())
        readStringTableRows.push_back(readerString.next());
    vector_type<StringTable::Row> readOtherStringTableRows;
    auto readerOtherString = readInstantiateTypeAsSqlDatabaseFieldDb.getOtherStringTable().createReader();
    while (readerOtherString.hasNext())
        readOtherStringTableRows.push_back(readerOtherString.next());

    assertEqualRows(stringTableRows, readStringTableRows);
    assertEqualRows(otherStringTableRows, readOtherStringTableRows);
}

} // namespace instantiate_type_as_sql_database_field
} // namespace templates
