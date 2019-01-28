#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "explicit_parameters/TestDb.h"

namespace explicit_parameters
{

class ExplicitParametersTest : public ::testing::Test
{
public:
    ExplicitParametersTest() : m_database(DB_FILE_NAME)
    {
        m_database.createSchema();
    }

    ~ExplicitParametersTest()
    {
        m_database.close();
        std::remove(DB_FILE_NAME);
    }

protected:
    static void fillTestTableRow(TestTableRow& row, uint32_t id, const std::string& name)
    {
        row.setId(id);
        row.setName(name);

        TestBlob testBlob1;
        testBlob1.initialize(TEST_TABLE_COUNT1);
        zserio::UInt8Array& values1 = testBlob1.getValues();
        for (uint32_t i = 0; i < TEST_TABLE_COUNT1; ++i)
            values1.push_back(static_cast<uint8_t>(id));
        row.setBlob1(testBlob1);

        TestBlob testBlob2;
        testBlob2.initialize(TEST_TABLE_COUNT2);
        zserio::UInt8Array& values2 = testBlob2.getValues();
        for (uint32_t i = 0; i < TEST_TABLE_COUNT2; ++i)
            values2.push_back(static_cast<uint8_t>(id + 1));
        row.setBlob2(testBlob2);

        TestBlob testBlob3;
        testBlob3.initialize(TEST_TABLE_COUNT1);
        zserio::UInt8Array& values3 = testBlob3.getValues();
        for (uint32_t i = 0; i < TEST_TABLE_COUNT1; ++i)
            values3.push_back(static_cast<uint8_t>(id + 2));
        row.setBlob3(testBlob3);

        TestBlobMultiParam testBlobMultiParam;
        testBlobMultiParam.initialize(TEST_TABLE_COUNT2, TEST_TABLE_COUNT2);
        zserio::UInt8Array& valuesA = testBlobMultiParam.getValuesA();
        zserio::UInt16Array& valuesB = testBlobMultiParam.getValuesB();
        for (uint32_t i = 0; i < TEST_TABLE_COUNT2; ++i)
        {
            valuesA.push_back(static_cast<uint8_t>(id + 3));
            valuesB.push_back(static_cast<uint16_t>(id + 4));
        }
        row.setBlobMultiParam(testBlobMultiParam);
    }

    static void fillTestTableRows(std::vector<TestTableRow>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_TEST_TABLE_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            TestTableRow row;
            fillTestTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkTestTableRow(const TestTableRow& row1, const TestTableRow& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob1(), row2.getBlob1());
        ASSERT_EQ(row1.getBlob2(), row2.getBlob2());
        ASSERT_EQ(row1.getBlob3(), row2.getBlob3());
        ASSERT_EQ(row1.getBlobMultiParam(), row2.getBlobMultiParam());

        // check reused explicit count1 parameter
        ASSERT_EQ(row2.getBlob1().getCount(), row2.getBlob3().getCount());
    }

    static void checkTestTableRows(const std::vector<TestTableRow>& rows1,
            const std::vector<TestTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkTestTableRow(rows1[i], rows2[i]);
    }

    class TestTableParameterProvider : public TestTable::IParameterProvider
    {
        virtual uint32_t getCount1(sqlite3_stmt&)
        {
            return TEST_TABLE_COUNT1;
        }

        virtual uint32_t getCount2(sqlite3_stmt&)
        {
            return TEST_TABLE_COUNT2;
        }
    };

    TestDb m_database;

    static const char DB_FILE_NAME[];

    static const uint32_t NUM_TEST_TABLE_ROWS;
    static const uint32_t TEST_TABLE_COUNT1;
    static const uint32_t TEST_TABLE_COUNT2;
};

const char ExplicitParametersTest::DB_FILE_NAME[] = "explicit_parameters.sqlite";

const uint32_t ExplicitParametersTest::NUM_TEST_TABLE_ROWS = 5;
const uint32_t ExplicitParametersTest::TEST_TABLE_COUNT1 = 10;
const uint32_t ExplicitParametersTest::TEST_TABLE_COUNT2 = 11;

TEST_F(ExplicitParametersTest, readWithoutCondition)
{
    TestTable& testTable = m_database.getTestTable();

    std::vector<TestTableRow> writtenRows;
    fillTestTableRows(writtenRows);
    testTable.write(writtenRows);

    TestTableParameterProvider parameterProvider;
    std::vector<TestTableRow> readRows;
    testTable.read(parameterProvider, readRows);
    checkTestTableRows(writtenRows, readRows);
}

TEST_F(ExplicitParametersTest, readWithCondition)
{
    TestTable& testTable = m_database.getTestTable();

    std::vector<TestTableRow> writtenRows;
    fillTestTableRows(writtenRows);
    testTable.write(writtenRows);

    TestTableParameterProvider parameterProvider;
    const std::string condition = "name='Name1'";
    std::vector<TestTableRow> readRows;
    testTable.read(parameterProvider, condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkTestTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ExplicitParametersTest, update)
{
    TestTable& testTable = m_database.getTestTable();

    std::vector<TestTableRow> writtenRows;
    fillTestTableRows(writtenRows);
    testTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    TestTableRow updateRow;
    fillTestTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    testTable.update(updateRow, updateCondition);

    TestTableParameterProvider parameterProvider;
    std::vector<TestTableRow> readRows;
    testTable.read(parameterProvider, updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkTestTableRow(updateRow, readRows[0]);
}

} // namespace sql_tables
