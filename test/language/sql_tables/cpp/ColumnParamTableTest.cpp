#include <cstdio>
#include <vector>
#include <string>
#include <limits>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace column_param_table
{

class ColumnParamTableTest : public ::testing::Test
{
public:
    ColumnParamTableTest() : m_database(DB_FILE_NAME)
    {
        m_database.createSchema();
    }

    ~ColumnParamTableTest()
    {
        m_database.close();
        std::remove(DB_FILE_NAME);
    }

protected:
    static void fillColumnParamTableRow(ColumnParamTableRow& row, uint32_t id, const std::string& name)
    {
        row.setId(id);
        row.setName(name);

        ParameterizedBlob parameterizedBlob;
        parameterizedBlob.setValue(PARAMETERIZED_BLOB_VALUE);
        parameterizedBlob.initialize(id / 2);
        row.setBlob(parameterizedBlob);
    }

    static void fillColumnParamTableRows(std::vector<ColumnParamTableRow>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_COLUMN_PARAM_TABLE_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            ColumnParamTableRow row;
            fillColumnParamTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkColumnParamTableRow(const ColumnParamTableRow& row1, const ColumnParamTableRow& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkColumnParamTableRows(const std::vector<ColumnParamTableRow>& rows1,
            const std::vector<ColumnParamTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkColumnParamTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "columnParamTable";
        std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
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
        if (readTableName == NULL || checkTableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
        {
            sqlite3_finalize(statement);
            return false;
        }

        sqlite3_finalize(statement);

        return true;
    }

    class ColumnParamTableParameterProvider : public IParameterProvider
    {
        virtual uint32_t getComplexTable_count(sqlite3_stmt&)
        {
            return 0;
        }
    };


    static const char DB_FILE_NAME[];

    static const uint32_t PARAMETERIZED_BLOB_VALUE;
    static const uint32_t NUM_COLUMN_PARAM_TABLE_ROWS;

    sql_tables::TestDb  m_database;
};

const char ColumnParamTableTest::DB_FILE_NAME[] = "column_param_table_test.sqlite";

const uint32_t ColumnParamTableTest::PARAMETERIZED_BLOB_VALUE = 0xABCD;
const uint32_t ColumnParamTableTest::NUM_COLUMN_PARAM_TABLE_ROWS = 5;

TEST_F(ColumnParamTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    ColumnParamTable& testTable = m_database.getColumnParamTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(ColumnParamTableTest, readWithoutCondition)
{
    ColumnParamTable& testTable = m_database.getColumnParamTable();

    std::vector<ColumnParamTableRow> writtenRows;
    fillColumnParamTableRows(writtenRows);
    testTable.write(writtenRows);

    ColumnParamTableParameterProvider parameterProvider;
    std::vector<ColumnParamTableRow> readRows;
    testTable.read(parameterProvider, readRows);
    checkColumnParamTableRows(writtenRows, readRows);
}

TEST_F(ColumnParamTableTest, readWithCondition)
{
    ColumnParamTable& testTable = m_database.getColumnParamTable();

    std::vector<ColumnParamTableRow> writtenRows;
    fillColumnParamTableRows(writtenRows);
    testTable.write(writtenRows);

    ColumnParamTableParameterProvider parameterProvider;
    const std::string condition = "name='Name1'";
    std::vector<ColumnParamTableRow> readRows;
    testTable.read(parameterProvider, condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkColumnParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ColumnParamTableTest, update)
{
    ColumnParamTable& testTable = m_database.getColumnParamTable();

    std::vector<ColumnParamTableRow> writtenRows;
    fillColumnParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    ColumnParamTableRow updateRow;
    fillColumnParamTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    testTable.update(updateRow, updateCondition);

    ColumnParamTableParameterProvider parameterProvider;
    std::vector<ColumnParamTableRow> readRows;
    testTable.read(parameterProvider, updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkColumnParamTableRow(updateRow, readRows[0]);
}

} // namespace column_param_table
} // namespace sql_tables
