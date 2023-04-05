#include <cstdio>
#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "sql_virtual_columns/hidden_virtual_columns/HiddenVirtualColumnsDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/ValidationSqliteUtil.h"
#include "zserio/SqliteFinalizer.h"

using namespace zserio::literals;

namespace sql_virtual_columns
{
namespace hidden_virtual_columns
{

using allocator_type = HiddenVirtualColumnsDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class HiddenVirtualColumnsTest : public ::testing::Test
{
public:
    HiddenVirtualColumnsTest() : m_tableName("hiddenVirtualColumnsTable")
    {
        std::remove(DB_FILE_NAME);

        m_database = new HiddenVirtualColumnsDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~HiddenVirtualColumnsTest() override
    {
        delete m_database;
    }

    HiddenVirtualColumnsTest(const HiddenVirtualColumnsTest&) = delete;
    HiddenVirtualColumnsTest& operator=(const HiddenVirtualColumnsTest&) = delete;

    HiddenVirtualColumnsTest(HiddenVirtualColumnsTest&&) = delete;
    HiddenVirtualColumnsTest& operator=(HiddenVirtualColumnsTest&&) = delete;

protected:
    static void fillHiddenVirtualColumnsTableRow(HiddenVirtualColumnsTable::Row& row, int64_t docId,
            const string_type& searchTags)
    {
        row.setDocId(docId);
        const uint16_t languageCode = 1;
        row.setLanguageCode(languageCode);
        row.setSearchTags(searchTags);
        const uint32_t frequency = 0xDEAD;
        row.setFrequency(frequency);
    }

    static void fillHiddenVirtualColumnsTableRows(vector_type<HiddenVirtualColumnsTable::Row>& rows)
    {
        rows.clear();
        for (int32_t id = 0; id < NUM_TABLE_ROWS; ++id)
        {
            const string_type searchTags = "Search Tags" + zserio::toString<allocator_type>(id);
            HiddenVirtualColumnsTable::Row row;
            fillHiddenVirtualColumnsTableRow(row, id, searchTags);
            rows.push_back(row);
        }
    }

    static void checkHiddenVirtualColumnsTableRow(const HiddenVirtualColumnsTable::Row& row1,
            const HiddenVirtualColumnsTable::Row& row2)
    {
        ASSERT_EQ(row1.getDocId(), row2.getDocId());
        ASSERT_EQ(row1.getLanguageCode(), row2.getLanguageCode());
        ASSERT_EQ(row1.getSearchTags(), row2.getSearchTags());
        ASSERT_EQ(row1.getFrequency(), row2.getFrequency());
    }

    static void checkHiddenVirtualColumnsTableRows(const vector_type<HiddenVirtualColumnsTable::Row>& rows1,
            const vector_type<HiddenVirtualColumnsTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkHiddenVirtualColumnsTableRow(rows1[i], rows2[i]);
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
        if (readTableName == nullptr || m_tableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
            return false;

        return true;
    }

    bool isHiddenVirtualColumnInTable(const string_type& columnName)
    {
        return zserio::ValidationSqliteUtil<allocator_type>::isColumnInTable(
                m_database->connection(), ""_sv, m_tableName, columnName, allocator_type());
    }

    static const char* DB_FILE_NAME;
    static const int32_t NUM_TABLE_ROWS;

    string_type m_tableName;
    HiddenVirtualColumnsDb* m_database;
};

const char* HiddenVirtualColumnsTest::DB_FILE_NAME =
        "language/sql_virtual_columns/hidden_virtual_columns_test.sqlite";
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

    vector_type<HiddenVirtualColumnsTable::Row> writtenRows;
    fillHiddenVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<HiddenVirtualColumnsTable::Row> readRows;
    auto reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkHiddenVirtualColumnsTableRows(writtenRows, readRows);
}

TEST_F(HiddenVirtualColumnsTest, readWithCondition)
{
    HiddenVirtualColumnsTable& testTable = m_database->getHiddenVirtualColumnsTable();

    vector_type<HiddenVirtualColumnsTable::Row> writtenRows;
    fillHiddenVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "searchTags='Search Tags1'";
    vector_type<HiddenVirtualColumnsTable::Row> readRows;
    auto reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkHiddenVirtualColumnsTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(HiddenVirtualColumnsTest, update)
{
    HiddenVirtualColumnsTable& testTable = m_database->getHiddenVirtualColumnsTable();

    vector_type<HiddenVirtualColumnsTable::Row> writtenRows;
    fillHiddenVirtualColumnsTableRows(writtenRows);
    testTable.write(writtenRows);

    const int64_t updateDocId = 1;
    HiddenVirtualColumnsTable::Row updateRow;
    fillHiddenVirtualColumnsTableRow(updateRow, updateDocId, "Updated Search Tags");
    const string_type updateCondition = "docId='" + zserio::toString<allocator_type>(updateDocId) + "'";
    testTable.update(updateRow, updateCondition);

    vector_type<HiddenVirtualColumnsTable::Row> readRows;
    auto reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
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
