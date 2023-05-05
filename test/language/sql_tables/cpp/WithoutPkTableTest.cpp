#include <cstdio>
#include <vector>
#include <string>
#include <memory>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/SqliteFinalizer.h"

namespace sql_tables
{
namespace without_pk_table
{

using allocator_type = TestDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class WithoutPkTableTest : public ::testing::Test
{
public:
    WithoutPkTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~WithoutPkTableTest() override
    {
        delete m_database;
    }

    WithoutPkTableTest(const WithoutPkTableTest&) = delete;
    WithoutPkTableTest& operator=(const WithoutPkTableTest&) = delete;

    WithoutPkTableTest(WithoutPkTableTest&&) = delete;
    WithoutPkTableTest& operator=(WithoutPkTableTest&&) = delete;

protected:
    static void fillWithoutPkTableRow(WithoutPkTable::Row& row, int32_t identifier, const string_type& name)
    {
        row.setIdentifier(identifier);
        row.setName(name);
    }

    static void fillWithoutPkTableRows(vector_type<WithoutPkTable::Row>& rows)
    {
        rows.clear();
        for (int32_t identifier = 0; identifier < NUM_WITHOUT_PK_TABLE_ROWS; ++identifier)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(identifier);
            WithoutPkTable::Row row;
            fillWithoutPkTableRow(row, identifier, name);
            rows.push_back(row);
        }
    }

    static void checkWithoutPkTableRow(const WithoutPkTable::Row& row1, const WithoutPkTable::Row& row2)
    {
        ASSERT_EQ(row1.getIdentifier(), row2.getIdentifier());
        ASSERT_EQ(row1.getName(), row2.getName());
    }

    static void checkWithoutPkTableRows(const vector_type<WithoutPkTable::Row>& rows1,
            const vector_type<WithoutPkTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkWithoutPkTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "withoutPkTable";
        string_type sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                m_database->connection().prepareStatement(sqlQuery));

        int result = sqlite3_step(statement.get());
        if (result == SQLITE_DONE || result != SQLITE_ROW)
            return false;

        const unsigned char* readTableName = sqlite3_column_text(statement.get(), 0);
        return (readTableName != nullptr && checkTableName == reinterpret_cast<const char*>(readTableName));
    }

    static const char* const DB_FILE_NAME;
    static const int32_t NUM_WITHOUT_PK_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char* const WithoutPkTableTest::DB_FILE_NAME = "language/sql_tables/without_pk_table_test.sqlite";
const int32_t WithoutPkTableTest::NUM_WITHOUT_PK_TABLE_ROWS = 5;

TEST_F(WithoutPkTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    WithoutPkTable& testTable = m_database->getWithoutPkTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(WithoutPkTableTest, readWithoutCondition)
{
    WithoutPkTable& testTable = m_database->getWithoutPkTable();

    vector_type<WithoutPkTable::Row> writtenRows;
    fillWithoutPkTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<WithoutPkTable::Row> readRows;
    auto reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkWithoutPkTableRows(writtenRows, readRows);
}

TEST_F(WithoutPkTableTest, readWithCondition)
{
    WithoutPkTable& testTable = m_database->getWithoutPkTable();

    vector_type<WithoutPkTable::Row> writtenRows;
    fillWithoutPkTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "name='Name1'";
    vector_type<WithoutPkTable::Row> readRows;
    auto reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkWithoutPkTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(WithoutPkTableTest, update)
{
    WithoutPkTable& testTable = m_database->getWithoutPkTable();

    vector_type<WithoutPkTable::Row> writtenRows;
    fillWithoutPkTableRows(writtenRows);
    testTable.write(writtenRows);

    const int32_t updateRowId = 3;
    WithoutPkTable::Row updateRow;
    fillWithoutPkTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "identifier=" + zserio::toString<allocator_type>(updateRowId);
    testTable.update(updateRow, updateCondition);

    vector_type<WithoutPkTable::Row> readRows;
    auto reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkWithoutPkTableRow(updateRow, readRows[0]);
}

} // namespace without_pk_table
} // namespace sql_tables
