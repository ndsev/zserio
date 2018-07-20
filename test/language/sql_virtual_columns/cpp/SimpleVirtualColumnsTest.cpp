#include <cstdio>
#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_virtual_columns/simple_virtual_columns/SimpleVirtualColumnsDb.h"

namespace sql_virtual_columns
{
namespace simple_virtual_columns
{


class SimpleVirtualColumnsTest : public ::testing::Test
{
public:
    SimpleVirtualColumnsTest() : m_tableName("simpleVirtualColumnsTable"), m_virtualColumnName("content"),
                                 m_database(DB_FILE_NAME)
    {
        m_database.createSchema();
    }

    ~SimpleVirtualColumnsTest()
    {
        m_database.close();
        std::remove(DB_FILE_NAME);
    }

protected:
    static void fillSimpleVirtualColumnsTableRow(SimpleVirtualColumnsTableRow& row, const std::string& content)
    {
        row.setContent(content);
    }

    static void fillSimpleVirtualColumnsTableRows(std::vector<SimpleVirtualColumnsTableRow>& rows)
    {
        rows.clear();
        for (int32_t id = 0; id < NUM_TABLE_ROWS; ++id)
        {
            const std::string content = "Content" + zserio::convertToString(id);
            SimpleVirtualColumnsTableRow row;
            fillSimpleVirtualColumnsTableRow(row, content);
            rows.push_back(row);
        }
    }

    static void checkSimpleVirtualColumnsTableRow(const SimpleVirtualColumnsTableRow& row1,
            const SimpleVirtualColumnsTableRow& row2)
    {
        ASSERT_EQ(row1.getContent(), row2.getContent());
    }

    static void checkSimpleVirtualColumnsTableRows(const std::vector<SimpleVirtualColumnsTableRow>& rows1,
            const std::vector<SimpleVirtualColumnsTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkSimpleVirtualColumnsTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + m_tableName +
                "'";
        int result = sqlite3_prepare_v2(m_database.getConnection(), sqlQuery.c_str(), -1, &statement, NULL);
        if (result != SQLITE_OK)
            return false;

        result = sqlite3_step(statement);
        if (result == SQLITE_DONE || result != SQLITE_ROW)
        {
            sqlite3_finalize(statement);
            return false;
        }

        const unsigned char* readTableName = sqlite3_column_text(statement, 0);
        if (readTableName == NULL || m_tableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
        {
            sqlite3_finalize(statement);
            return false;
        }

        sqlite3_finalize(statement);

        return true;
    }

    bool isVirtualColumnInTable()
    {
        sqlite3_stmt* statement;
        std::string sqlQuery = "PRAGMA table_info(" + m_tableName + ")";
        int result = sqlite3_prepare_v2(m_database.getConnection(), sqlQuery.c_str(), -1, &statement, NULL);
        if (result != SQLITE_OK)
            return false;

        bool isFound = false;
        while (!isFound)
        {
            result = sqlite3_step(statement);
            if (result == SQLITE_DONE || result != SQLITE_ROW)
                break;

            const unsigned char* readColumnName = sqlite3_column_text(statement, 1);
            if (readColumnName != NULL &&
                    m_virtualColumnName.compare(reinterpret_cast<const char*>(readColumnName)) == 0)
                isFound = true;
        }

        sqlite3_finalize(statement);

        return isFound;
    }

    class SimpleVirtualColumnsTableParameterProvider : public IParameterProvider
    {
    };

    static const char DB_FILE_NAME[];
    static const int32_t NUM_TABLE_ROWS;

    std::string m_tableName;
    std::string m_virtualColumnName;
    SimpleVirtualColumnsDb m_database;
};

const char SimpleVirtualColumnsTest::DB_FILE_NAME[] = "simple_virtual_columns_test.sqlite";
const int32_t SimpleVirtualColumnsTest::NUM_TABLE_ROWS = 5;

TEST_F(SimpleVirtualColumnsTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    SimpleVirtualColumnsTable& testTable = m_database.getSimpleVirtualColumnsTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(SimpleVirtualColumnsTest, readWithoutCondition)
{
    SimpleVirtualColumnsTable& testTable = m_database.getSimpleVirtualColumnsTable();

    std::vector<SimpleVirtualColumnsTableRow> writtenRows;
    fillSimpleVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    SimpleVirtualColumnsTableParameterProvider parameterProvider;
    std::vector<SimpleVirtualColumnsTableRow> readRows;
    testTable.read(parameterProvider, readRows);
    checkSimpleVirtualColumnsTableRows(writtenRows, readRows);
}

TEST_F(SimpleVirtualColumnsTest, readWithCondition)
{
    SimpleVirtualColumnsTable& testTable = m_database.getSimpleVirtualColumnsTable();

    std::vector<SimpleVirtualColumnsTableRow> writtenRows;
    fillSimpleVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    SimpleVirtualColumnsTableParameterProvider parameterProvider;
    const std::string condition = "content='Content1'";
    std::vector<SimpleVirtualColumnsTableRow> readRows;
    testTable.read(parameterProvider, condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkSimpleVirtualColumnsTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(SimpleVirtualColumnsTest, update)
{
    SimpleVirtualColumnsTable& testTable = m_database.getSimpleVirtualColumnsTable();

    std::vector<SimpleVirtualColumnsTableRow> writtenRows;
    fillSimpleVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string updateContent = "UpdatedContent";
    SimpleVirtualColumnsTableRow updateRow;
    fillSimpleVirtualColumnsTableRow(updateRow, updateContent);
    const std::string updateCondition = "content='Content3'";
    testTable.update(updateRow, updateCondition);

    SimpleVirtualColumnsTableParameterProvider parameterProvider;
    std::vector<SimpleVirtualColumnsTableRow> readRows;
    const std::string readCondition = "content='" + updateContent + "'";
    testTable.read(parameterProvider, readCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkSimpleVirtualColumnsTableRow(updateRow, readRows[0]);
}

TEST_F(SimpleVirtualColumnsTest, checkVirtualColumn)
{
    ASSERT_TRUE(isVirtualColumnInTable());
}

} // simple_virtual_columns
} // namespace sql_virtual_columns
