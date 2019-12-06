#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace dynamic_bit_field_enum_field_table
{

class DynamicBitFieldEnumFieldTableTest : public ::testing::Test
{
public:
    DynamicBitFieldEnumFieldTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~DynamicBitFieldEnumFieldTableTest()
    {
        delete m_database;
    }

protected:
    static void fillRow(DynamicBitFieldEnumFieldTable::Row& row, size_t i)
    {
        row.setId(static_cast<int32_t>(i));
        row.setEnumField(i % 3 == 0
                ? TestEnum::ONE
                : i % 3 == 1
                        ? TestEnum::TWO
                        : TestEnum::THREE);
    }

    static void fillRows(std::vector<DynamicBitFieldEnumFieldTable::Row>& rows)
    {
        rows.clear();
        for (size_t i = 0; i < NUM_ROWS; ++i)
        {
            DynamicBitFieldEnumFieldTable::Row row;
            fillRow(row, i);
            rows.push_back(row);
        }
    }

    static void checkRow(const DynamicBitFieldEnumFieldTable::Row& row1,
            const DynamicBitFieldEnumFieldTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getEnumField(), row2.getEnumField());
    }

    static void checkRows(const std::vector<DynamicBitFieldEnumFieldTable::Row>& rows1,
            const std::vector<DynamicBitFieldEnumFieldTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkRow(rows1[i], rows2[i]);
    }

    static const char DB_FILE_NAME[];
    static const size_t NUM_ROWS;

    sql_tables::TestDb* m_database;
};

const char DynamicBitFieldEnumFieldTableTest::DB_FILE_NAME[] = "dynamic_bit_field_enum_field_table_test.sqlite";
const size_t DynamicBitFieldEnumFieldTableTest::NUM_ROWS = 5;

TEST_F(DynamicBitFieldEnumFieldTableTest, readWithoutCondition)
{
    DynamicBitFieldEnumFieldTable& table = m_database->getDynamicBitFieldEnumFieldTable();

    std::vector<DynamicBitFieldEnumFieldTable::Row> rows;
    fillRows(rows);
    table.write(rows);

    std::vector<DynamicBitFieldEnumFieldTable::Row> readRows;
    auto reader = table.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkRows(rows, readRows);
}

} // namespace dynamic_bit_field_enum_field_table
} // namespace sql_tables
