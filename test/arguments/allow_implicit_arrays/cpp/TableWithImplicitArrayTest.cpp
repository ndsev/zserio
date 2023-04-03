#include <cstdio>
#include <string>
#include <fstream>

#include "gtest/gtest.h"

#include "allow_implicit_arrays/table_with_implicit_array/DbWithImplicitArray.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"

namespace allow_implicit_arrays
{
namespace table_with_implicit_array
{

using allocator_type = DbWithImplicitArray::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class TableWithImplicitArrayTest : public ::testing::Test
{
public:
    TableWithImplicitArrayTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new allow_implicit_arrays::table_with_implicit_array::DbWithImplicitArray(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~TableWithImplicitArrayTest() override
    {
        delete m_database;
    }

    TableWithImplicitArrayTest(const TableWithImplicitArrayTest&) = delete;
    TableWithImplicitArrayTest& operator=(const TableWithImplicitArrayTest&) = delete;

    TableWithImplicitArrayTest(TableWithImplicitArrayTest&&) = delete;
    TableWithImplicitArrayTest& operator=(TableWithImplicitArrayTest&&) = delete;

protected:
    static void fillRow(TableWithImplicitArray::Row& row, size_t i)
    {
        row.setId(static_cast<uint32_t>(i));
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

    allow_implicit_arrays::table_with_implicit_array::DbWithImplicitArray* m_database;
};

const char TableWithImplicitArrayTest::DB_FILE_NAME[] =
        "arguments/allow_implicit_arrays/table_with_implicit_array_test.sqlite";
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
} // namespace allow_implicit_arrays
