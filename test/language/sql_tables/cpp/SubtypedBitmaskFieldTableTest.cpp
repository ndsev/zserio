#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"

namespace sql_tables
{
namespace subtyped_bitmask_field_table
{

using allocator_type = TestDb::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

class SubtypedBitmaskFieldTableTest : public ::testing::Test
{
public:
    SubtypedBitmaskFieldTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~SubtypedBitmaskFieldTableTest()
    {
        delete m_database;
    }

protected:
    static void fillRow(SubtypedBitmaskFieldTable::Row& row, size_t i)
    {
        row.setId(static_cast<int32_t>(i));
        row.setBitmaskField(i % 3 == 0
                ? TestBitmask::Values::ONE
                : i % 3 == 1
                        ? TestBitmask::Values::TWO
                        : TestBitmask::Values::THREE);
    }

    static void fillRows(vector_type<SubtypedBitmaskFieldTable::Row>& rows)
    {
        rows.clear();
        for (size_t i = 0; i < NUM_ROWS; ++i)
        {
            SubtypedBitmaskFieldTable::Row row;
            fillRow(row, i);
            rows.push_back(row);
        }
    }

    static void checkRow(const SubtypedBitmaskFieldTable::Row& row1, const SubtypedBitmaskFieldTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getBitmaskField(), row2.getBitmaskField());
    }

    static void checkRows(const vector_type<SubtypedBitmaskFieldTable::Row>& rows1,
            const vector_type<SubtypedBitmaskFieldTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkRow(rows1[i], rows2[i]);
    }

    static const char DB_FILE_NAME[];
    static const size_t NUM_ROWS;

    sql_tables::TestDb* m_database;
};

const char SubtypedBitmaskFieldTableTest::DB_FILE_NAME[] =
        "language/sql_tables/subtyped_bitmask_field_table_test.sqlite";
const size_t SubtypedBitmaskFieldTableTest::NUM_ROWS = 5;

TEST_F(SubtypedBitmaskFieldTableTest, readWithoutCondition)
{
    SubtypedBitmaskFieldTable& table = m_database->getSubtypedBitmaskFieldTable();

    vector_type<SubtypedBitmaskFieldTable::Row> rows;
    fillRows(rows);
    table.write(rows);

    vector_type<SubtypedBitmaskFieldTable::Row> readRows;
    auto reader = table.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkRows(rows, readRows);
}

} // namespace subtyped_bitmask_field_table
} // namespace sql_tables
