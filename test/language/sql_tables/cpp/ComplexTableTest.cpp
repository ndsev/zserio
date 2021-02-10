#include <cstdio>
#include <vector>
#include <string>
#include <limits>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "sql_tables/TestDb.h"

namespace sql_tables
{
namespace complex_table
{

class ComplexTableTest : public ::testing::Test
{
public:
    ComplexTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ComplexTableTest()
    {
        delete m_database;
    }

protected:
    static void fillComplexTableRowWithNullValues(ComplexTable::Row& row, uint64_t blobId)
    {
        row.setBlobId(blobId);
        row.resetAge();
        row.resetName();
        row.resetIsValid();
        row.resetSalary();
        row.resetBonus();
        row.resetValue();
        row.resetColor();
        row.resetBlob();
    }

    static void fillComplexTableRowsWithNullValues(std::vector<ComplexTable::Row>& rows)
    {
        rows.clear();
        for (uint64_t blobId = 0; blobId < NUM_COMPLEX_TABLE_ROWS; ++blobId)
        {
            ComplexTable::Row row;
            fillComplexTableRowWithNullValues(row, blobId);
            rows.push_back(row);
        }
    }

    static void fillComplexTableRow(ComplexTable::Row& row, uint64_t blobId, const std::string& name)
    {
        row.setBlobId(blobId);
        row.setAge(std::numeric_limits<int64_t>::max());
        row.setName(name);
        row.setIsValid(true);
        row.setSalary(9.9f);
        row.setBonus(5.5);
        row.setValue(0x34);
        row.setColor(TestEnum::RED);

        TestBlob testBlob;
        std::vector<uint8_t>& values = testBlob.getValues();
        for (size_t i = 0; i < COMPLEX_TABLE_COUNT; ++i)
            values.push_back(static_cast<uint8_t>(blobId));
        testBlob.setOffsetEnd(TEST_BLOB_OFFSET_END);
        testBlob.setEnd(true);
        row.setBlob(testBlob);
    }

    static void fillComplexTableRows(std::vector<ComplexTable::Row>& rows)
    {
        rows.clear();
        for (uint64_t blobId = 0; blobId < NUM_COMPLEX_TABLE_ROWS; ++blobId)
        {
            const std::string name = "Name" + zserio::convertToString(blobId);
            ComplexTable::Row row;
            fillComplexTableRow(row, blobId, name);
            rows.push_back(row);
        }
    }

    static void checkComplexTableRow(const ComplexTable::Row& row1, const ComplexTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());

        ASSERT_EQ(row1.getAge(), row2.getAge());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getIsValid(), row2.getIsValid());
        ASSERT_EQ(row1.getSalary(), row2.getSalary());
        ASSERT_EQ(row1.getBonus(), row2.getBonus());
        ASSERT_EQ(row1.getValue(), row2.getValue());
        ASSERT_EQ(row1.getColor(), row2.getColor());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
    }

    static void checkComplexTableRows(const std::vector<ComplexTable::Row>& rows1,
            const std::vector<ComplexTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkComplexTableRow(rows1[i], rows2[i]);
    }

    static void checkComplexTableRowWithNullValues(const ComplexTable::Row& row1, const ComplexTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());

        if (row1.isAgeUsed() && row2.isAgeUsed())
            ASSERT_EQ(row1.getAge(), row2.getAge());
        else
            ASSERT_EQ(row1.isAgeUsed(), row2.isAgeUsed());

        if (row1.isNameUsed() && row2.isNameUsed())
            ASSERT_EQ(row1.getName(), row2.getName());
        else
            ASSERT_EQ(row1.isNameUsed(), row2.isNameUsed());

        if (row1.isIsValidUsed() && row2.isIsValidUsed())
            ASSERT_EQ(row1.getIsValid(), row2.getIsValid());
        else
            ASSERT_EQ(row1.isIsValidUsed(), row2.isIsValidUsed());

        if (row1.isSalaryUsed() && row2.isSalaryUsed())
            ASSERT_EQ(row1.getSalary(), row2.getSalary());
        else
            ASSERT_EQ(row1.isSalaryUsed(), row2.isSalaryUsed());

        if (row1.isBonusUsed() && row2.isBonusUsed())
            ASSERT_EQ(row1.getBonus(), row2.getBonus());
        else
            ASSERT_EQ(row1.isBonusUsed(), row2.isBonusUsed());

        if (row1.isValueUsed() && row2.isValueUsed())
            ASSERT_EQ(row1.getValue(), row2.getValue());
        else
            ASSERT_EQ(row1.isValueUsed(), row2.isValueUsed());

        if (row1.isColorUsed() && row2.isColorUsed())
            ASSERT_EQ(row1.getColor(), row2.getColor());
        else
            ASSERT_EQ(row1.isColorUsed(), row2.isColorUsed());

        if (row1.isBlobUsed() && row2.isBlobUsed())
            ASSERT_EQ(row1.getBlob(), row2.getBlob());
        else
            ASSERT_EQ(row1.isBlobUsed(), row2.isBlobUsed());
    }

    static void checkComplexTableRowsWithNullValues(const std::vector<ComplexTable::Row>& rows1,
            const std::vector<ComplexTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkComplexTableRowWithNullValues(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        sqlite3_stmt* statement;
        std::string checkTableName = "complexTable";
        std::string sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + checkTableName +
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
        if (readTableName == NULL || checkTableName.compare(reinterpret_cast<const char*>(readTableName)) != 0)
        {
            sqlite3_finalize(statement);
            return false;
        }

        sqlite3_finalize(statement);

        return true;
    }

    class ComplexTableParameterProvider : public ComplexTable::IParameterProvider
    {
        virtual uint32_t getCount(ComplexTable::Row&)
        {
            return static_cast<uint32_t>(COMPLEX_TABLE_COUNT);
        }
    };

    static const char DB_FILE_NAME[];

    static const size_t NUM_COMPLEX_TABLE_ROWS;
    static const size_t COMPLEX_TABLE_COUNT;
    static const uint32_t TEST_BLOB_OFFSET_END;

    sql_tables::TestDb* m_database;
};

const char ComplexTableTest::DB_FILE_NAME[] = "complex_table_test.sqlite";

const size_t ComplexTableTest::NUM_COMPLEX_TABLE_ROWS = 5;
const size_t ComplexTableTest::COMPLEX_TABLE_COUNT = 10;
const uint32_t ComplexTableTest::TEST_BLOB_OFFSET_END = 4 + (COMPLEX_TABLE_COUNT * 3 + 7) / 8;

TEST_F(ComplexTableTest, deleteTable)
{
    ASSERT_TRUE(isTableInDb());

    ComplexTable& testTable = m_database->getComplexTable();
    testTable.deleteTable();
    ASSERT_FALSE(isTableInDb());

    testTable.createTable();
    ASSERT_TRUE(isTableInDb());
}

TEST_F(ComplexTableTest, readWithoutCondition)
{
    ComplexTable& testTable = m_database->getComplexTable();
    ComplexTableParameterProvider parameterProvider;

    std::vector<ComplexTable::Row> writtenRows;
    fillComplexTableRows(writtenRows);
    testTable.write(parameterProvider, writtenRows);

    std::vector<ComplexTable::Row> readRows;
    auto reader = testTable.createReader(parameterProvider);
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkComplexTableRows(writtenRows, readRows);
}

TEST_F(ComplexTableTest, readWithoutConditionWithNullValues)
{
    ComplexTable& testTable = m_database->getComplexTable();
    ComplexTableParameterProvider parameterProvider;

    std::vector<ComplexTable::Row> writtenRows;
    fillComplexTableRowsWithNullValues(writtenRows);
    testTable.write(parameterProvider, writtenRows);

    std::vector<ComplexTable::Row> readRows;
    auto reader = testTable.createReader(parameterProvider);
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkComplexTableRowsWithNullValues(writtenRows, readRows);
}

TEST_F(ComplexTableTest, readWithCondition)
{
    ComplexTable& testTable = m_database->getComplexTable();
    ComplexTableParameterProvider parameterProvider;

    std::vector<ComplexTable::Row> writtenRows;
    fillComplexTableRows(writtenRows);
    testTable.write(parameterProvider, writtenRows);

    const std::string condition = "name='Name1'";
    std::vector<ComplexTable::Row> readRows;
    auto reader = testTable.createReader(parameterProvider, condition);
    while (reader.hasNext())
        readRows.push_back(reader.next());

    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkComplexTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ComplexTableTest, update)
{
    ComplexTable& testTable = m_database->getComplexTable();
    ComplexTableParameterProvider parameterProvider;

    std::vector<ComplexTable::Row> writtenRows;
    fillComplexTableRows(writtenRows);
    testTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    ComplexTable::Row updateRow;
    fillComplexTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "blobId=" + zserio::convertToString(updateRowId);
    testTable.update(parameterProvider, updateRow, updateCondition);

    std::vector<ComplexTable::Row> readRows;
    auto reader = testTable.createReader(parameterProvider, updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());

    ASSERT_EQ(1, readRows.size());

    checkComplexTableRow(updateRow, readRows[0]);
}

} // namespace complex_table
} // namespace sql_tables
