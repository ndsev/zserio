#include <cstdio>
#include <vector>
#include <string>
#include <limits>
#include <memory>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/SqliteFinalizer.h"

namespace sql_tables
{
namespace const_param_table
{

using allocator_type = TestDb::allocator_type;
using string_type = zserio::string<zserio::RebindAlloc<allocator_type, char>>;
template <typename T>
using vector_type = std::vector<T, zserio::RebindAlloc<allocator_type, T>>;

class ConstParamTableTest : public ::testing::Test
{
public:
    ConstParamTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ConstParamTableTest()
    {
        delete m_database;
    }

protected:
    static void fillConstParamTableRow(ConstParamTable::Row& row, uint32_t blobId, const string_type& name)
    {
        row.setBlobId(blobId);
        row.setName(name);

        ParameterizedBlob parameterizedBlob;
        parameterizedBlob.setValue(PARAMETERIZED_BLOB_VALUE);
        parameterizedBlob.initialize(PARAMETERIZED_BLOB_PARAM);
        row.setBlob(parameterizedBlob);
    }

    static void fillConstParamTableRows(vector_type<ConstParamTable::Row>& rows)
    {
        rows.clear();
        for (uint32_t blobId = 0; blobId < NUM_CONST_PARAM_TABLE_ROWS; ++blobId)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(blobId);
            ConstParamTable::Row row;
            fillConstParamTableRow(row, blobId, name);
            rows.push_back(row);
        }
    }

    static void checkConstParamTableRow(const ConstParamTable::Row& row1, const ConstParamTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkConstParamTableRows(const vector_type<ConstParamTable::Row>& rows1,
            const vector_type<ConstParamTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkConstParamTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "constParamTable";
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

    static const uint32_t PARAMETERIZED_BLOB_VALUE;
    static const uint32_t PARAMETERIZED_BLOB_PARAM;
    static const uint32_t NUM_CONST_PARAM_TABLE_ROWS;

    sql_tables::TestDb* m_database;
};

const char ConstParamTableTest::DB_FILE_NAME[] = "language/sql_tables/const_param_table_test.sqlite";

const uint32_t ConstParamTableTest::PARAMETERIZED_BLOB_VALUE = 0xABCD;
const uint32_t ConstParamTableTest::PARAMETERIZED_BLOB_PARAM = 2;
const uint32_t ConstParamTableTest::NUM_CONST_PARAM_TABLE_ROWS = 5;

TEST_F(ConstParamTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    ConstParamTable& testTable = m_database->getConstParamTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(ConstParamTableTest, readWithoutCondition)
{
    ConstParamTable& testTable = m_database->getConstParamTable();

    vector_type<ConstParamTable::Row> writtenRows;
    fillConstParamTableRows(writtenRows);
    testTable.write(writtenRows);

    vector_type<ConstParamTable::Row> readRows;
    auto reader = testTable.createReader();
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkConstParamTableRows(writtenRows, readRows);
}

TEST_F(ConstParamTableTest, readWithCondition)
{
    ConstParamTable& testTable = m_database->getConstParamTable();

    vector_type<ConstParamTable::Row> writtenRows;
    fillConstParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const string_type condition = "name='Name1'";
    vector_type<ConstParamTable::Row> readRows;
    auto reader = testTable.createReader(condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkConstParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ConstParamTableTest, update)
{
    ConstParamTable& testTable = m_database->getConstParamTable();

    vector_type<ConstParamTable::Row> writtenRows;
    fillConstParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    ConstParamTable::Row updateRow;
    fillConstParamTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "blobId=" + zserio::toString<allocator_type>(updateRowId);
    testTable.update(updateRow, updateCondition);

    vector_type<ConstParamTable::Row> readRows;
    auto reader = testTable.createReader(updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());
    ASSERT_EQ(1, readRows.size());

    checkConstParamTableRow(updateRow, readRows[0]);
}

} // namespace const_param_table
} // namespace sql_tables
