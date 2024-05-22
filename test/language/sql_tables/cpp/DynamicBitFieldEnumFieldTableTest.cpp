#include <cstdio>
#include <fstream>
#include <string>

#include "gtest/gtest.h"
#include "sql_tables/TestDb.h"
#include "zserio/RebindAlloc.h"

namespace sql_tables
{
namespace dynamic_bit_field_enum_field_table
{

using allocator_type = TestDb::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class DynamicBitFieldEnumFieldTableTest : public ::testing::Test
{
public:
    DynamicBitFieldEnumFieldTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~DynamicBitFieldEnumFieldTableTest() override
    {
        delete m_database;
    }

    DynamicBitFieldEnumFieldTableTest(const DynamicBitFieldEnumFieldTableTest&) = delete;
    DynamicBitFieldEnumFieldTableTest& operator=(const DynamicBitFieldEnumFieldTableTest&) = delete;

    DynamicBitFieldEnumFieldTableTest(DynamicBitFieldEnumFieldTableTest&&) = delete;
    DynamicBitFieldEnumFieldTableTest& operator=(DynamicBitFieldEnumFieldTableTest&&) = delete;

protected:
    static void fillRow(DynamicBitFieldEnumFieldTable::Row& row, size_t i)
    {
        row.setId(static_cast<uint32_t>(i));
        row.setEnumField(i % 3 == 0 ? TestEnum::ONE : i % 3 == 1 ? TestEnum::TWO : TestEnum::THREE);
    }

    static void fillRows(vector_type<DynamicBitFieldEnumFieldTable::Row>& rows)
    {
        rows.clear();
        for (size_t i = 0; i < NUM_ROWS; ++i)
        {
            DynamicBitFieldEnumFieldTable::Row row;
            fillRow(row, i);
            rows.push_back(row);
        }
    }

    static void checkRow(
            const DynamicBitFieldEnumFieldTable::Row& row1, const DynamicBitFieldEnumFieldTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getEnumField(), row2.getEnumField());
    }

    static void checkRows(const vector_type<DynamicBitFieldEnumFieldTable::Row>& rows1,
            const vector_type<DynamicBitFieldEnumFieldTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
        {
            checkRow(rows1[i], rows2[i]);
        }
    }

    static const char* const DB_FILE_NAME;
    static const size_t NUM_ROWS;

    sql_tables::TestDb* m_database;
};

const char* const DynamicBitFieldEnumFieldTableTest::DB_FILE_NAME =
        "language/sql_tables/dynamic_bit_field_enum_field_table_test.sqlite";
const size_t DynamicBitFieldEnumFieldTableTest::NUM_ROWS = 5;

TEST_F(DynamicBitFieldEnumFieldTableTest, readWithoutCondition)
{
    DynamicBitFieldEnumFieldTable& table = m_database->getDynamicBitFieldEnumFieldTable();

    vector_type<DynamicBitFieldEnumFieldTable::Row> rows;
    fillRows(rows);
    table.write(rows);

    vector_type<DynamicBitFieldEnumFieldTable::Row> readRows;
    auto reader = table.createReader();
    while (reader.hasNext())
    {
        readRows.push_back(reader.next());
    }

    checkRows(rows, readRows);
}

} // namespace dynamic_bit_field_enum_field_table
} // namespace sql_tables
