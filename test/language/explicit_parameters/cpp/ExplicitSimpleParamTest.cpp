#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "explicit_parameters/ExplicitParametersDb.h"

namespace explicit_parameters
{
namespace explicit_simple_param
{

class ExplicitSimpleParamTest : public ::testing::Test
{
public:
    ExplicitSimpleParamTest() : m_database(DB_FILE_NAME)
    {
        m_database.createSchema();
    }

    ~ExplicitSimpleParamTest()
    {
        m_database.close();
        std::remove(DB_FILE_NAME);
    }

protected:
    void fillSimpleParamTableRow(SimpleParamTableRow& row, uint32_t id, const std::string& name)
    {
        row.setId(id);
        row.setName(name);

        TestBlob testBlob1;
        testBlob1.initialize(SIMPLE_PARAM_TABLE_COUNT1);
        zserio::UInt8Array& values1 = testBlob1.getValues();
        for (uint32_t i = 0; i < SIMPLE_PARAM_TABLE_COUNT1; ++i)
            values1.push_back(static_cast<uint8_t>(id));
        row.setBlob1(testBlob1);

        TestBlob testBlob2;
        testBlob2.initialize(SIMPLE_PARAM_TABLE_COUNT2);
        zserio::UInt8Array& values2 = testBlob2.getValues();
        for (uint32_t i = 0; i < SIMPLE_PARAM_TABLE_COUNT2; ++i)
            values2.push_back(static_cast<uint8_t>(id + 1));
        row.setBlob2(testBlob2);

        TestBlob testBlob3;
        testBlob3.initialize(SIMPLE_PARAM_TABLE_COUNT1);
        zserio::UInt8Array& values3 = testBlob3.getValues();
        for (uint32_t i = 0; i < SIMPLE_PARAM_TABLE_COUNT1; ++i)
            values3.push_back(static_cast<uint8_t>(id + 2));
        row.setBlob3(testBlob3);
    }

    void fillSimpleParamTableRows(std::vector<SimpleParamTableRow>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_SIMPLE_PARAM_TABLE_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            SimpleParamTableRow row;
            fillSimpleParamTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkSimpleParamTableRow(const SimpleParamTableRow& row1, const SimpleParamTableRow& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob1(), row2.getBlob1());
        ASSERT_EQ(row1.getBlob2(), row2.getBlob2());
        ASSERT_EQ(row1.getBlob3(), row2.getBlob3());

        // check reused explicit count1 parameter
        ASSERT_EQ(row2.getBlob1().getCount(), row2.getBlob3().getCount());
    }

    static void checkSimpleParamTableRows(const std::vector<SimpleParamTableRow>& rows1,
            const std::vector<SimpleParamTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkSimpleParamTableRow(rows1[i], rows2[i]);
    }

    class SimpleParamTableParameterProvider : public SimpleParamTable::IParameterProvider
    {
    public:
        virtual uint32_t getCount1(sqlite3_stmt&)
        {
            return SIMPLE_PARAM_TABLE_COUNT1;
        }

        virtual uint32_t getCount2(sqlite3_stmt&)
        {
            return SIMPLE_PARAM_TABLE_COUNT2;
        }
    };

    ExplicitParametersDb m_database;

    static const char DB_FILE_NAME[];

    static const uint32_t NUM_SIMPLE_PARAM_TABLE_ROWS;
    static const uint32_t SIMPLE_PARAM_TABLE_COUNT1;
    static const uint32_t SIMPLE_PARAM_TABLE_COUNT2;
};

const char ExplicitSimpleParamTest::DB_FILE_NAME[] = "explicit_parameters.sqlite";

const uint32_t ExplicitSimpleParamTest::NUM_SIMPLE_PARAM_TABLE_ROWS = 5;
const uint32_t ExplicitSimpleParamTest::SIMPLE_PARAM_TABLE_COUNT1 = 10;
const uint32_t ExplicitSimpleParamTest::SIMPLE_PARAM_TABLE_COUNT2 = 11;

TEST_F(ExplicitSimpleParamTest, readWithoutCondition)
{
    SimpleParamTable& simpleParamTable = m_database.getSimpleParamTable();

    std::vector<SimpleParamTableRow> writtenRows;
    fillSimpleParamTableRows(writtenRows);
    simpleParamTable.write(writtenRows);

    SimpleParamTableParameterProvider parameterProvider;
    std::vector<SimpleParamTableRow> readRows;
    simpleParamTable.read(parameterProvider, readRows);
    checkSimpleParamTableRows(writtenRows, readRows);
}

TEST_F(ExplicitSimpleParamTest, readWithCondition)
{
    SimpleParamTable& simpleParamTable = m_database.getSimpleParamTable();

    std::vector<SimpleParamTableRow> writtenRows;
    fillSimpleParamTableRows(writtenRows);
    simpleParamTable.write(writtenRows);

    SimpleParamTableParameterProvider parameterProvider;
    const std::string condition = "name='Name1'";
    std::vector<SimpleParamTableRow> readRows;
    simpleParamTable.read(parameterProvider, condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkSimpleParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ExplicitSimpleParamTest, update)
{
    SimpleParamTable& simpleParamTable = m_database.getSimpleParamTable();

    std::vector<SimpleParamTableRow> writtenRows;
    fillSimpleParamTableRows(writtenRows);
    simpleParamTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    SimpleParamTableRow updateRow;
    fillSimpleParamTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    simpleParamTable.update(updateRow, updateCondition);

    SimpleParamTableParameterProvider parameterProvider;
    std::vector<SimpleParamTableRow> readRows;
    simpleParamTable.read(parameterProvider, updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkSimpleParamTableRow(updateRow, readRows[0]);
}

} // namespace explicit_simple_param
} // namespace explicit_parameters
