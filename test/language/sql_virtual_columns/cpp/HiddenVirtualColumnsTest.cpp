#include <cstdio>
#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_virtual_columns/hidden_virtual_columns/HiddenVirtualColumnsDb.h"

namespace sql_virtual_columns
{
namespace hidden_virtual_columns
{

class HiddenVirtualColumnsTest : public ::testing::Test
{
public:
    HiddenVirtualColumnsTest() : m_tableName("hiddenVirtualColumnsTable")
    {
        std::remove(DB_FILE_NAME);

        m_database = new HiddenVirtualColumnsDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~HiddenVirtualColumnsTest()
    {
        delete m_database;
    }

protected:
    static void fillHiddenVirtualColumnsTableRow(HiddenVirtualColumnsTableRow& row, int64_t docId,
            const std::string& searchTags)
    {
        row.setDocId(docId);
        const uint16_t languageCode = 1;
        row.setLanguageCode(languageCode);
        row.setSearchTags(searchTags);
        const uint32_t frequency = 0xDEAD;
        row.setFrequency(frequency);
    }

    static void fillHiddenVirtualColumnsTableRows(std::vector<HiddenVirtualColumnsTableRow>& rows)
    {
        rows.clear();
        for (int32_t id = 0; id < NUM_TABLE_ROWS; ++id)
        {
            const std::string searchTags = "Search Tags" + zserio::convertToString(id);
            HiddenVirtualColumnsTableRow row;
            fillHiddenVirtualColumnsTableRow(row, id, searchTags);
            rows.push_back(row);
        }
    }

    static void checkHiddenVirtualColumnsTableRow(const HiddenVirtualColumnsTableRow& row1,
            const HiddenVirtualColumnsTableRow& row2)
    {
        ASSERT_EQ(row1.getDocId(), row2.getDocId());
        ASSERT_EQ(row1.getLanguageCode(), row2.getLanguageCode());
        ASSERT_EQ(row1.getSearchTags(), row2.getSearchTags());
        ASSERT_EQ(row1.getFrequency(), row2.getFrequency());
    }

    static void checkHiddenVirtualColumnsTableRows(const std::vector<HiddenVirtualColumnsTableRow>& rows1,
            const std::vector<HiddenVirtualColumnsTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkHiddenVirtualColumnsTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + m_tableName +
                "'";
        int result = sqlite3_prepare_v2(m_database->connection(), sqlQuery.c_str(), -1, &statement, NULL);
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

    bool isHiddenVirtualColumnInTable(const std::string& columnName)
    {
        sqlite3_stmt* statement;
        const std::string sqlQuery = "SELECT " + columnName + " FROM " + m_tableName + " LIMIT 0";
        int result = sqlite3_prepare_v2(m_database->connection(), sqlQuery.c_str(), -1, &statement, NULL);
        sqlite3_finalize(statement);

        return (result == SQLITE_OK) ? true : false;
    }

    static const char DB_FILE_NAME[];
    static const int32_t NUM_TABLE_ROWS;

    std::string m_tableName;
    HiddenVirtualColumnsDb* m_database;
};

const char HiddenVirtualColumnsTest::DB_FILE_NAME[] = "hidden_virtual_columns_test.sqlite";
const int32_t HiddenVirtualColumnsTest::NUM_TABLE_ROWS = 5;

TEST_F(HiddenVirtualColumnsTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    HiddenVirtualColumnsTable& testTable = m_database->getHiddenVirtualColumnsTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(HiddenVirtualColumnsTest, readWithoutCondition)
{
    HiddenVirtualColumnsTable& testTable = m_database->getHiddenVirtualColumnsTable();

    std::vector<HiddenVirtualColumnsTableRow> writtenRows;
    fillHiddenVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    std::vector<HiddenVirtualColumnsTableRow> readRows;
    testTable.read(readRows);
    checkHiddenVirtualColumnsTableRows(writtenRows, readRows);
}

TEST_F(HiddenVirtualColumnsTest, readWithCondition)
{
    HiddenVirtualColumnsTable& testTable = m_database->getHiddenVirtualColumnsTable();

    std::vector<HiddenVirtualColumnsTableRow> writtenRows;
    fillHiddenVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    const std::string condition = "searchTags='Search Tags1'";
    std::vector<HiddenVirtualColumnsTableRow> readRows;
    testTable.read(condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkHiddenVirtualColumnsTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(HiddenVirtualColumnsTest, update)
{
    HiddenVirtualColumnsTable& testTable = m_database->getHiddenVirtualColumnsTable();

    std::vector<HiddenVirtualColumnsTableRow> writtenRows;
    fillHiddenVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    const int64_t updateDocId = 1;
    HiddenVirtualColumnsTableRow updateRow;
    fillHiddenVirtualColumnsTableRow(updateRow, updateDocId, "Updated Search Tags");
    const std::string updateCondition = "docId='" + zserio::convertToString(updateDocId) + "'";
    testTable.update(updateRow, updateCondition);

    std::vector<HiddenVirtualColumnsTableRow> readRows;
    testTable.read(updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkHiddenVirtualColumnsTableRow(updateRow, readRows[0]);
}

TEST_F(HiddenVirtualColumnsTest, checkVirtualColumn)
{
    ASSERT_TRUE(isHiddenVirtualColumnInTable("docId"));
    ASSERT_TRUE(isHiddenVirtualColumnInTable("languageCode"));
}

} // hidden_virtual_columns
} // namespace sql_virtual_columns
