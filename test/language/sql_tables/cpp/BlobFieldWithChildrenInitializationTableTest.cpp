#include <cstdio>
#include <fstream>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"

namespace sql_tables
{
namespace blob_field_with_children_initialization_table
{

using allocator_type = TestDb::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class BlobFieldWithChildrenInitializationTableTest : public ::testing::Test
{
public:
    BlobFieldWithChildrenInitializationTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~BlobFieldWithChildrenInitializationTableTest() override
    {
        delete m_database;
    }

protected:
    static void fillRow(BlobFieldWithChildrenInitializationTable::Row& row, size_t index)
    {
        row.setId(static_cast<uint32_t>(index));
        const uint32_t arrayLength = static_cast<uint32_t>(index);
        vector_type<uint32_t> array;
        for (uint32_t i = 0; i < arrayLength; ++i)
            array.push_back(i);
        row.setBlob(BlobWithChildrenInitialization{arrayLength, ParameterizedArray{std::move(array)}});
    }

    static void fillRows(vector_type<BlobFieldWithChildrenInitializationTable::Row>& rows)
    {
        rows.clear();
        for (size_t i = 0; i < NUM_ROWS; ++i)
        {
            BlobFieldWithChildrenInitializationTable::Row row;
            fillRow(row, i);
            rows.push_back(row);
        }
    }

    static void checkRow(const BlobFieldWithChildrenInitializationTable::Row& row1,
            const BlobFieldWithChildrenInitializationTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkRows(const vector_type<BlobFieldWithChildrenInitializationTable::Row>& rows1,
            const vector_type<BlobFieldWithChildrenInitializationTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkRow(rows1[i], rows2[i]);
    }

    static const char DB_FILE_NAME[];
    static const size_t NUM_ROWS;

    sql_tables::TestDb* m_database;
};

const char BlobFieldWithChildrenInitializationTableTest::DB_FILE_NAME[] =
        "language/sql_tables/blob_field_with_children_initialization_table_test.sqlite";
const size_t BlobFieldWithChildrenInitializationTableTest::NUM_ROWS = 5;

TEST_F(BlobFieldWithChildrenInitializationTableTest, readWithoutCondition)
{
    BlobFieldWithChildrenInitializationTable& table = m_database->getBlobFieldWithChildrenInitializationTable();

    vector_type<BlobFieldWithChildrenInitializationTable::Row> rows;
    fillRows(rows);
    table.write(rows);

    vector_type<BlobFieldWithChildrenInitializationTable::Row> readRows;
    auto reader = table.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkRows(rows, readRows);
}

} // namespace blob_field_with_children_initialization_table
} // namespace sql_tables
