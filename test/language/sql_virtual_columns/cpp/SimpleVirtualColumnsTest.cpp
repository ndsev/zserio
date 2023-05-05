#include <cstdio>
#include <string>
#include <vector>
#include <memory>

#include "gtest/gtest.h"

#include "sql_virtual_columns/simple_virtual_columns/SimpleVirtualColumnsDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/SqliteFinalizer.h"

namespace sql_virtual_columns
{
namespace simple_virtual_columns
{

using allocator_type = SimpleVirtualColumnsDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class SimpleVirtualColumnsTest : public ::testing::Test
{
public:
    SimpleVirtualColumnsTest() : m_tableName("simpleVirtualColumnsTable"), m_virtualColumnName("content")
    {
        std::remove(DB_FILE_NAME);

        m_database = new SimpleVirtualColumnsDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~SimpleVirtualColumnsTest() override
    {
        delete m_database;
    }

    SimpleVirtualColumnsTest(const SimpleVirtualColumnsTest&) = delete;
    SimpleVirtualColumnsTest& operator=(const SimpleVirtualColumnsTest&) = delete;

    SimpleVirtualColumnsTest(SimpleVirtualColumnsTest&&) = delete;
    SimpleVirtualColumnsTest& operator=(SimpleVirtualColumnsTest&&) = delete;

protected:
    static void fillSimpleVirtualColumnsTableRow(SimpleVirtualColumnsTable::Row& row,
            const string_type& content)
    {
        row.setContent(content);
    }

    static void fillSimpleVirtualColumnsTableRows(vector_type<SimpleVirtualColumnsTable::Row>& rows)
    {
        rows.clear();
        for (int32_t id = 0; id < NUM_TABLE_ROWS; ++id)
        {
            const string_type content = "Content" + zserio::toString<allocator_type>(id);
            SimpleVirtualColumnsTable::Row row;
            fillSimpleVirtualColumnsTableRow(row, content);
            rows.push_back(row);
        }
    }

    static void checkSimpleVirtualColumnsTableRow(const SimpleVirtualColumnsTable::Row& row1,
            const SimpleVirtualColumnsTable::Row& row2)
    {
        ASSERT_EQ(row1.getContent(), row2.getContent());
    }

    static void checkSimpleVirtualColumnsTableRows(const vector_type<SimpleVirtualColumnsTable::Row>& rows1,
            const vector_type<SimpleVirtualColumnsTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkSimpleVirtualColumnsTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + m_tableName +
                "'";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                m_database->connection().prepareStatement(sqlQuery));

        int result = sqlite3_step(statement.get());
        if (result == SQLITE_DONE || result != SQLITE_ROW)
            return false;

        const unsigned char* readTableName = sqlite3_column_text(statement.get(), 0);
        return (readTableName != nullptr && m_tableName == reinterpret_cast<const char*>(readTableName));
    }

    bool isVirtualColumnInTable()
    {
        string_type sqlQuery = "PRAGMA table_info(" + m_tableName + ")";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                m_database->connection().prepareStatement(sqlQuery));

        bool isFound = false;
        while (!isFound)
        {
            int result = sqlite3_step(statement.get());
            if (result == SQLITE_DONE || result != SQLITE_ROW)
                break;

            const unsigned char* readColumnName = sqlite3_column_text(statement.get(), 1);
            if (readColumnName != nullptr &&
                    m_virtualColumnName == reinterpret_cast<const char*>(readColumnName))
            {
                isFound = true;
            }
        }

        return isFound;
    }

    static const char* const DB_FILE_NAME;
    static const int32_t NUM_TABLE_ROWS;

    string_type m_tableName;
    string_type m_virtualColumnName;
    SimpleVirtualColumnsDb* m_database;
};

const char* const SimpleVirtualColumnsTest::DB_FILE_NAME =
        "language/sql_virtual_columns/simple_virtual_columns_test.sqlite";
const int32_t SimpleVirtualColumnsTest::NUM_TABLE_ROWS = 5;

TEST_F(SimpleVirtualColumnsTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    SimpleVirtualColumnsTable& testTable = m_database->getSimpleVirtualColumnsTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(SimpleVirtualColumnsTest, readWithoutCondition)
{
    SimpleVirtualColumnsTable& testTable = m_database->getSimpleVirtualColumnsTable();

    vector_type<SimpleVirtualColumnsTable::Row> writtenRows;
    fillSimpleVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<SimpleVirtualColumnsTable::Row> readRows;
    auto reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkSimpleVirtualColumnsTableRows(writtenRows, readRows);
}

TEST_F(SimpleVirtualColumnsTest, readWithCondition)
{
    SimpleVirtualColumnsTable& testTable = m_database->getSimpleVirtualColumnsTable();

    vector_type<SimpleVirtualColumnsTable::Row> writtenRows;
    fillSimpleVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "content='Content1'";
    vector_type<SimpleVirtualColumnsTable::Row> readRows;
    auto reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkSimpleVirtualColumnsTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(SimpleVirtualColumnsTest, update)
{
    SimpleVirtualColumnsTable& testTable = m_database->getSimpleVirtualColumnsTable();

    vector_type<SimpleVirtualColumnsTable::Row> writtenRows;
    fillSimpleVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type updateContent = "UpdatedContent";
    SimpleVirtualColumnsTable::Row updateRow;
    fillSimpleVirtualColumnsTableRow(updateRow, updateContent);
    const string_type updateCondition = "content='Content3'";
    testTable.update(updateRow, updateCondition);

    vector_type<SimpleVirtualColumnsTable::Row> readRows;
    const string_type readCondition = "content='" + updateContent + "'";
    auto reader = testTable.createReader(readCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkSimpleVirtualColumnsTableRow(updateRow, readRows[0]);
}

TEST_F(SimpleVirtualColumnsTest, checkVirtualColumn)
{
    ASSERT_TRUE(isVirtualColumnInTable());
}

} // simple_virtual_columns
} // namespace sql_virtual_columns
