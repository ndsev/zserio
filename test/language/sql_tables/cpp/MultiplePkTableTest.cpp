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
namespace multiple_pk_table
{

using allocator_type = TestDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class MultiplePkTableTest : public ::testing::Test
{
public:
    MultiplePkTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~MultiplePkTableTest() override
    {
        delete m_database;
    }

protected:
    static void fillMultiplePkTableRow(MultiplePkTable::Row& row, int32_t blobId, const string_type& name)
    {
        row.setBlobId(blobId);
        row.setAge(10);
        row.setName(name);
    }

    static void fillMultiplePkTableRows(vector_type<MultiplePkTable::Row>& rows)
    {
        rows.clear();
        for (int32_t blobId = 0; blobId < NUM_MULTIPLE_PK_TABLE_ROWS; ++blobId)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(blobId);
            MultiplePkTable::Row row;
            fillMultiplePkTableRow(row, blobId, name);
            rows.push_back(row);
        }
    }

    static void checkMultiplePkTableRow(const MultiplePkTable::Row& row1, const MultiplePkTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());
        ASSERT_EQ(row1.getAge(), row2.getAge());
        ASSERT_EQ(row1.getName(), row2.getName());
    }

    static void checkMultiplePkTableRows(const vector_type<MultiplePkTable::Row>& rows1,
            const vector_type<MultiplePkTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkMultiplePkTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "multiplePkTable";
        string_type sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
                "'";
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                m_database->connection().prepareStatement(sqlQuery));

        int result = sqlite3_step(statement.get());
        if (result == SQLITE_DONE || result != SQLITE_ROW)
            return false;

        const unsigned char* readTableName = sqlite3_column_text(statement.get(), 0);
        if (readTableName == nullptr ||
                checkTableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
        {
            return false;
        }

        return true;
    }

    static const char DB_FILE_NAME[];

    static const int32_t NUM_MULTIPLE_PK_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char MultiplePkTableTest::DB_FILE_NAME[] = "language/sql_tables/multiple_pk_table_test.sqlite";

const int32_t MultiplePkTableTest::NUM_MULTIPLE_PK_TABLE_ROWS = 5;

TEST_F(MultiplePkTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    MultiplePkTable& testTable = m_database->getMultiplePkTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(MultiplePkTableTest, readWithoutCondition)
{
    MultiplePkTable& testTable = m_database->getMultiplePkTable();

    vector_type<MultiplePkTable::Row> writtenRows;
    fillMultiplePkTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<MultiplePkTable::Row> readRows;
    MultiplePkTable::Reader reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkMultiplePkTableRows(writtenRows, readRows);
}

TEST_F(MultiplePkTableTest, readWithCondition)
{
    MultiplePkTable& testTable = m_database->getMultiplePkTable();

    vector_type<MultiplePkTable::Row> writtenRows;
    fillMultiplePkTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "name='Name1'";
    vector_type<MultiplePkTable::Row> readRows;
    MultiplePkTable::Reader reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkMultiplePkTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(MultiplePkTableTest, update)
{
    MultiplePkTable& testTable = m_database->getMultiplePkTable();

    vector_type<MultiplePkTable::Row> writtenRows;
    fillMultiplePkTableRows(writtenRows);
    testTable.write(writtenRows);

    const int32_t updateRowId = 3;
    MultiplePkTable::Row updateRow;
    fillMultiplePkTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "blobId=" + zserio::toString<allocator_type>(updateRowId);
    testTable.update(updateRow, updateCondition);

    vector_type<MultiplePkTable::Row> readRows;
    MultiplePkTable::Reader reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkMultiplePkTableRow(updateRow, readRows[0]);
}

} // namespace multiple_pk_table
} // namespace sql_tables
