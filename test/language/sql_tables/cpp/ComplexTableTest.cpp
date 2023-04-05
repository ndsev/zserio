#include <cstdio>
#include <vector>
#include <string>
#include <limits>
#include <memory>

#include "gtest/gtest.h"

#include "sql_tables/TestDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/SqliteFinalizer.h"

namespace sql_tables
{
namespace complex_table
{

using allocator_type = TestDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ComplexTableTest : public ::testing::Test
{
public:
    ComplexTableTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new sql_tables::TestDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ComplexTableTest() override
    {
        delete m_database;
    }

    ComplexTableTest(const ComplexTableTest&) = delete;
    ComplexTableTest& operator=(const ComplexTableTest&) = delete;

    ComplexTableTest(ComplexTableTest&&) = delete;
    ComplexTableTest& operator=(ComplexTableTest&&) = delete;

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

    static void fillComplexTableRowsWithNullValues(vector_type<ComplexTable::Row>& rows)
    {
        rows.clear();
        for (uint64_t blobId = 0; blobId < NUM_COMPLEX_TABLE_ROWS; ++blobId)
        {
            ComplexTable::Row row;
            fillComplexTableRowWithNullValues(row, blobId);
            rows.push_back(row);
        }
    }

    static void fillComplexTableRow(ComplexTable::Row& row, uint64_t blobId, const string_type& name)
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
        vector_type<uint8_t>& values = testBlob.getValues();
        for (size_t i = 0; i < COMPLEX_TABLE_COUNT; ++i)
            values.push_back(static_cast<uint8_t>(blobId));
        testBlob.setOffsetEnd(TEST_BLOB_OFFSET_END);
        testBlob.setEnd(true);
        row.setBlob(testBlob);
    }

    static void fillComplexTableRows(vector_type<ComplexTable::Row>& rows)
    {
        rows.clear();
        for (uint64_t blobId = 0; blobId < NUM_COMPLEX_TABLE_ROWS; ++blobId)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(blobId);
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

    static void checkComplexTableRows(const vector_type<ComplexTable::Row>& rows1,
            const vector_type<ComplexTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkComplexTableRow(rows1[i], rows2[i]);
    }

    static void checkComplexTableRowWithNullValues(const ComplexTable::Row& row1, const ComplexTable::Row& row2)
    {
        ASSERT_EQ(row1.getBlobId(), row2.getBlobId());

        if (row1.isAgeSet() && row2.isAgeSet())
            ASSERT_EQ(row1.getAge(), row2.getAge());
        else
            ASSERT_EQ(row1.isAgeSet(), row2.isAgeSet());

        if (row1.isNameSet() && row2.isNameSet())
            ASSERT_EQ(row1.getName(), row2.getName());
        else
            ASSERT_EQ(row1.isNameSet(), row2.isNameSet());

        if (row1.isIsValidSet() && row2.isIsValidSet())
            ASSERT_EQ(row1.getIsValid(), row2.getIsValid());
        else
            ASSERT_EQ(row1.isIsValidSet(), row2.isIsValidSet());

        if (row1.isSalarySet() && row2.isSalarySet())
            ASSERT_EQ(row1.getSalary(), row2.getSalary());
        else
            ASSERT_EQ(row1.isSalarySet(), row2.isSalarySet());

        if (row1.isBonusSet() && row2.isBonusSet())
            ASSERT_EQ(row1.getBonus(), row2.getBonus());
        else
            ASSERT_EQ(row1.isBonusSet(), row2.isBonusSet());

        if (row1.isValueSet() && row2.isValueSet())
            ASSERT_EQ(row1.getValue(), row2.getValue());
        else
            ASSERT_EQ(row1.isValueSet(), row2.isValueSet());

        if (row1.isColorSet() && row2.isColorSet())
            ASSERT_EQ(row1.getColor(), row2.getColor());
        else
            ASSERT_EQ(row1.isColorSet(), row2.isColorSet());

        if (row1.isBlobSet() && row2.isBlobSet())
            ASSERT_EQ(row1.getBlob(), row2.getBlob());
        else
            ASSERT_EQ(row1.isBlobSet(), row2.isBlobSet());
    }

    static void checkComplexTableRowsWithNullValues(const vector_type<ComplexTable::Row>& rows1,
            const vector_type<ComplexTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkComplexTableRowWithNullValues(rows1[i], rows2[i]);
    }

    bool isTableInDb()
    {
        string_type checkTableName = "complexTable";
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

    class ComplexTableParameterProvider : public ComplexTable::IParameterProvider
    {
        uint32_t getCount(ComplexTable::Row&) override
        {
            return static_cast<uint32_t>(COMPLEX_TABLE_COUNT);
        }
    };

    static const char* DB_FILE_NAME;

    static const size_t NUM_COMPLEX_TABLE_ROWS;
    static const size_t COMPLEX_TABLE_COUNT;
    static const uint32_t TEST_BLOB_OFFSET_END;

    sql_tables::TestDb* m_database;
};

const char* ComplexTableTest::DB_FILE_NAME = "language/sql_tables/complex_table_test.sqlite";

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

    vector_type<ComplexTable::Row> writtenRows;
    fillComplexTableRows(writtenRows);
    testTable.write(parameterProvider, writtenRows);

    vector_type<ComplexTable::Row> readRows;
    auto reader = testTable.createReader(parameterProvider);
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkComplexTableRows(writtenRows, readRows);
}

TEST_F(ComplexTableTest, readWithoutConditionWithNullValues)
{
    ComplexTable& testTable = m_database->getComplexTable();
    ComplexTableParameterProvider parameterProvider;

    vector_type<ComplexTable::Row> writtenRows;
    fillComplexTableRowsWithNullValues(writtenRows);
    testTable.write(parameterProvider, writtenRows);

    vector_type<ComplexTable::Row> readRows;
    auto reader = testTable.createReader(parameterProvider);
    while (reader.hasNext())
        readRows.push_back(reader.next());

    checkComplexTableRowsWithNullValues(writtenRows, readRows);
}

TEST_F(ComplexTableTest, readWithCondition)
{
    ComplexTable& testTable = m_database->getComplexTable();
    ComplexTableParameterProvider parameterProvider;

    vector_type<ComplexTable::Row> writtenRows;
    fillComplexTableRows(writtenRows);
    testTable.write(parameterProvider, writtenRows);

    const string_type condition = "name='Name1'";
    vector_type<ComplexTable::Row> readRows;
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

    vector_type<ComplexTable::Row> writtenRows;
    fillComplexTableRows(writtenRows);
    testTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    ComplexTable::Row updateRow;
    fillComplexTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "blobId=" + zserio::toString<allocator_type>(updateRowId);
    testTable.update(parameterProvider, updateRow, updateCondition);

    vector_type<ComplexTable::Row> readRows;
    auto reader = testTable.createReader(parameterProvider, updateCondition);
    while (reader.hasNext())
        readRows.push_back(reader.next());

    ASSERT_EQ(1, readRows.size());

    checkComplexTableRow(updateRow, readRows[0]);
}

} // namespace complex_table
} // namespace sql_tables
