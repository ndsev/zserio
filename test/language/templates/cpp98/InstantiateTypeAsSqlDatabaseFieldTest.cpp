#include <cstdio>

#include "gtest/gtest.h"

#include "templates/instantiate_type_as_sql_database_field/InstantiateTypeAsSqlDatabaseFieldDb.h"

namespace templates
{
namespace instantiate_type_as_sql_database_field
{

class InstantiateTypeAsSqlDatabaseFieldTest : public ::testing::Test
{
public:
    InstantiateTypeAsSqlDatabaseFieldTest()
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

const std::string InstantiateTypeAsSqlDatabaseFieldTest::DB_FILE_NAME = "instantiate_type_as_sql_database_field_test.sqlite";

TEST_F(InstantiateTypeAsSqlDatabaseFieldTest, readWrite)
{
    InstantiateTypeAsSqlDatabaseFieldDb instantiateTypeAsSqlDatabaseFieldDb(DB_FILE_NAME);
    instantiateTypeAsSqlDatabaseFieldDb.createSchema();

    StringTable& stringTable = instantiateTypeAsSqlDatabaseFieldDb.getStringTable();
    std::vector<StringTableRow> stringTableRows;
    StringTableRow stringRow1;
    stringRow1.setId(0);
    stringRow1.setData("test");
    stringTableRows.push_back(stringRow1);
    stringTable.write(stringTableRows);

    StringTable& otherStringTable = instantiateTypeAsSqlDatabaseFieldDb.getOtherStringTable();
    std::vector<StringTableRow> otherStringTableRows;
    StringTableRow otherStringRow1;
    otherStringRow1.setId(0);
    otherStringRow1.setData("other test");
    otherStringTableRows.push_back(otherStringRow1);
    otherStringTable.write(otherStringTableRows);

    InstantiateTypeAsSqlDatabaseFieldDb readInstantiateTypeAsSqlDatabaseFieldDb(DB_FILE_NAME);
    std::vector<StringTableRow> readStringTableRows;
    readInstantiateTypeAsSqlDatabaseFieldDb.getStringTable().read(readStringTableRows);
    std::vector<StringTableRow> readOtherStringTableRows;
    readInstantiateTypeAsSqlDatabaseFieldDb.getOtherStringTable().read(readOtherStringTableRows);

    assertEqualRows(stringTableRows, readStringTableRows);
    assertEqualRows(otherStringTableRows, readOtherStringTableRows);
}

} // namespace instantiate_type_as_sql_database_field
} // namespace templates
