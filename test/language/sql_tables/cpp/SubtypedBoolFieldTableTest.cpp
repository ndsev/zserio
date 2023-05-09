#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"

namespace sql_tables
{
namespace subtyped_bool_field_table
{

using allocator_type = TestDb::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class SubtypedBoolFieldTableTest : public ::testing::Test
{
public:
    SubtypedBoolFieldTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~SubtypedBoolFieldTableTest() override
    {
        delete m_database;
    }

    SubtypedBoolFieldTableTest(const SubtypedBoolFieldTableTest&) = delete;
    SubtypedBoolFieldTableTest& operator=(const SubtypedBoolFieldTableTest&) = delete;

    SubtypedBoolFieldTableTest(SubtypedBoolFieldTableTest&&) = delete;
    SubtypedBoolFieldTableTest& operator=(SubtypedBoolFieldTableTest&&) = delete;

protected:
    static void fillRow(SubtypedBoolFieldTable::Row& row, size_t i)
    {
        row.setId(static_cast<int32_t>(i));
        row.setBoolField(i % 2 == 0);
    }

    static void fillRows(vector_type<SubtypedBoolFieldTable::Row>& rows)
    {
        rows.clear();
        for (size_t i = 0; i < NUM_ROWS; ++i)
        {
            SubtypedBoolFieldTable::Row row;
            fillRow(row, i);
            rows.push_back(row);
        }
    }

    static void checkRow(const SubtypedBoolFieldTable::Row& row1, const SubtypedBoolFieldTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getBoolField(), row2.getBoolField());
    }

    static void checkRows(const vector_type<SubtypedBoolFieldTable::Row>& rows1,
            const vector_type<SubtypedBoolFieldTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkRow(rows1[i], rows2[i]);
    }

    static const char* const DB_FILE_NAME;
    static const size_t NUM_ROWS;

    sql_tables::TestDb* m_database;
};

const char* const SubtypedBoolFieldTableTest::DB_FILE_NAME =
        "language/sql_tables/subtyped_bool_field_table_test.sqlite";
const size_t SubtypedBoolFieldTableTest::NUM_ROWS = 5;

TEST_F(SubtypedBoolFieldTableTest, readWithoutCondition)
{
    SubtypedBoolFieldTable& table = m_database->getSubtypedBoolFieldTable();

    vector_type<SubtypedBoolFieldTable::Row> rows;
    fillRows(rows);
    table.write(rows);

    vector_type<SubtypedBoolFieldTable::Row> readRows;
    auto reader = table.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkRows(rows, readRows);
}

} // namespace subtyped_bool_field_table
} // namespace sql_tables
