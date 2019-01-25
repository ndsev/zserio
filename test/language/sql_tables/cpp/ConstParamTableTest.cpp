#include <cstdio>
#include <vector>
#include <string>
#include <limits>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace const_param_table
{

class ConstParamTableTest : public ::testing::Test
{
public:
    ConstParamTableTest() : m_database(DB_FILE_NAME)
    {
        m_database.createSchema();
    }

    ~ConstParamTableTest()
    {
        m_database.close();
        std::remove(DB_FILE_NAME);
    }

protected:
    static void fillConstParamTableRow(ConstParamTableRow& row, uint32_t blobId, const std::string& name)
    {
        row.setBlobId(blobId);
        row.setName(name);

        ParameterizedBlob parameterizedBlob;
        parameterizedBlob.setValue(PARAMETERIZED_BLOB_VALUE);
        parameterizedBlob.initialize(PARAMETERIZED_BLOB_PARAM);
        row.setBlob(parameterizedBlob);
    }

    static void fillConstParamTableRows(std::vector<ConstParamTableRow>& rows)
    {
        rows.clear();
        for (uint32_t blobId = 0; blobId < NUM_CONST_PARAM_TABLE_ROWS; ++blobId)
        {
            const std::string name = "Name" + zserio::convertToString(blobId);
            ConstParamTableRow row;
            fillConstParamTableRow(row, blobId, name);
            rows.push_back(row);
        }
    }

    static void checkConstParamTableRow(const ConstParamTableRow& row1, const ConstParamTableRow& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkConstParamTableRows(const std::vector<ConstParamTableRow>& rows1,
            const std::vector<ConstParamTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkConstParamTableRow(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "constParamTable";
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

    class ConstParamTableParameterProvider : public IParameterProvider
    {
        virtual uint32_t getComplexTable_count(sqlite3_stmt&)
        {
            return 0;
        }
    };

    static const char DB_FILE_NAME[];

    static const uint32_t PARAMETERIZED_BLOB_VALUE;
    static const uint32_t PARAMETERIZED_BLOB_PARAM;
    static const uint32_t NUM_CONST_PARAM_TABLE_ROWS;

    sql_tables::TestDb  m_database;
};

const char ConstParamTableTest::DB_FILE_NAME[] = "const_param_table_test.sqlite";

const uint32_t ConstParamTableTest::PARAMETERIZED_BLOB_VALUE = 0xABCD;
const uint32_t ConstParamTableTest::PARAMETERIZED_BLOB_PARAM = 2;
const uint32_t ConstParamTableTest::NUM_CONST_PARAM_TABLE_ROWS = 5;

TEST_F(ConstParamTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    ConstParamTable& testTable = m_database.getConstParamTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(ConstParamTableTest, readWithoutCondition)
{
    ConstParamTable& testTable = m_database.getConstParamTable();

    std::vector<ConstParamTableRow> writtenRows;
    fillConstParamTableRows(writtenRows);
    testTable.write(writtenRows);

    ConstParamTableParameterProvider parameterProvider;
    std::vector<ConstParamTableRow> readRows;
    testTable.read(parameterProvider, readRows);
    checkConstParamTableRows(writtenRows, readRows);
}

TEST_F(ConstParamTableTest, readWithCondition)
{
    ConstParamTable& testTable = m_database.getConstParamTable();

    std::vector<ConstParamTableRow> writtenRows;
    fillConstParamTableRows(writtenRows);
    testTable.write(writtenRows);

    ConstParamTableParameterProvider parameterProvider;
    const std::string condition = "name='Name1'";
    std::vector<ConstParamTableRow> readRows;
    testTable.read(parameterProvider, condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkConstParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ConstParamTableTest, update)
{
    ConstParamTable& testTable = m_database.getConstParamTable();

    std::vector<ConstParamTableRow> writtenRows;
    fillConstParamTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    ConstParamTableRow updateRow;
    fillConstParamTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "blobId=" + zserio::convertToString(updateRowId);
    testTable.update(updateRow, updateCondition);

    ConstParamTableParameterProvider parameterProvider;
    std::vector<ConstParamTableRow> readRows;
    testTable.read(parameterProvider, updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkConstParamTableRow(updateRow, readRows[0]);
}

} // namespace const_param_table
} // namespace sql_tables
