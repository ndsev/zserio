#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"

namespace sql_tables
{
namespace table_with_implicit_array
{

using allocator_type = TestDb::allocator_type;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

class TableWithImplicitArrayTest : public ::testing::Test
{
public:
    TableWithImplicitArrayTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~TableWithImplicitArrayTest()
    {
        delete m_database;
    }

protected:
    static void fillRow(TableWithImplicitArray::Row& row, size_t i)
    {
        row.setId(static_cast<int32_t>(i));
        row.setStructWithImplicit(StructWithImplicit{vector_type<uint32_t>{1, 2, 3, 4, 5}});
        row.setText("test" + zserio::toString<allocator_type>(i));
    }

    static void fillRows(vector_type<TableWithImplicitArray::Row>& rows)
    {
        rows.clear();
        for (size_t i = 0; i < NUM_ROWS; ++i)
        {
            TableWithImplicitArray::Row row;
            fillRow(row, i);
            rows.push_back(row);
        }
    }

    static void checkRow(const TableWithImplicitArray::Row& row1, const TableWithImplicitArray::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getStructWithImplicit(), row2.getStructWithImplicit());
        ASSERT_EQ(row1.getText(), row2.getText());
    }

    static void checkRows(const vector_type<TableWithImplicitArray::Row>& rows1,
            const vector_type<TableWithImplicitArray::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkRow(rows1[i], rows2[i]);
    }

    static const char DB_FILE_NAME[];
    static const size_t NUM_ROWS;

    sql_tables::TestDb* m_database;
};

const char TableWithImplicitArrayTest::DB_FILE_NAME[] = "table_with_implicit_array_test.sqlite";
const size_t TableWithImplicitArrayTest::NUM_ROWS = 5;

TEST_F(TableWithImplicitArrayTest, readWithoutCondition)
{
    TableWithImplicitArray& table = m_database->getTableWithImplicitArray();

    vector_type<TableWithImplicitArray::Row> rows;
    fillRows(rows);
    table.write(rows);

    vector_type<TableWithImplicitArray::Row> readRows;
    auto reader = table.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkRows(rows, readRows);
}

} // namespace table_with_implicit_array
} // namespace sql_tables
