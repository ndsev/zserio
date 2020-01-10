#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "explicit_parameters/ExplicitParametersDb.h"

namespace explicit_parameters
{
namespace explicit_bitmask_param
{

class ExplicitBitmaskParamTest : public ::testing::Test
{
public:
    ExplicitBitmaskParamTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ExplicitBitmaskParamTest()
    {
        delete m_database;
    }

protected:
    void fillBitmaskParamTableRow(BitmaskParamTable::Row& row, uint32_t id, const std::string& name)
    {
        row.setId(id);
        row.setName(name);

        TestBlob testBlob1;
        std::vector<uint8_t>& values1 = testBlob1.getValues();
        for (uint32_t i = 0; i < BITMASK_PARAM_TABLE_COUNT1.getValue(); ++i)
            values1.push_back(static_cast<uint8_t>(id));
        row.setBlob1(testBlob1);

        TestBlob testBlob2;
        std::vector<uint8_t>& values2 = testBlob2.getValues();
        for (uint32_t i = 0; i < BITMASK_PARAM_TABLE_COUNT2.getValue(); ++i)
            values2.push_back(static_cast<uint8_t>(id + 1));
        row.setBlob2(testBlob2);

        TestBlob testBlob3;
        std::vector<uint8_t>& values3 = testBlob3.getValues();
        for (uint32_t i = 0; i < BITMASK_PARAM_TABLE_COUNT1.getValue(); ++i)
            values3.push_back(static_cast<uint8_t>(id + 2));
        row.setBlob3(testBlob3);
    }

    void fillBitmaskParamTableRows(std::vector<BitmaskParamTable::Row>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_BITMASK_PARAM_TABLE_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            BitmaskParamTable::Row row;
            fillBitmaskParamTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkBitmaskParamTableRow(const BitmaskParamTable::Row& row1, const BitmaskParamTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob1(), row2.getBlob1());
        ASSERT_EQ(row1.getBlob2(), row2.getBlob2());
        ASSERT_EQ(row1.getBlob3(), row2.getBlob3());

        // check reused explicit count parameter
        ASSERT_EQ(row2.getBlob1().getCount(), row2.getBlob3().getCount());
    }

    static void checkBitmaskParamTableRows(const std::vector<BitmaskParamTable::Row>& rows1,
            const std::vector<BitmaskParamTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkBitmaskParamTableRow(rows1[i], rows2[i]);
    }

    class BitmaskParamTableParameterProvider : public BitmaskParamTable::IParameterProvider
    {
    public:
        virtual TestBitmask getCount1(BitmaskParamTable::Row&)
        {
            return BITMASK_PARAM_TABLE_COUNT1;
        }

        virtual TestBitmask getCount2(BitmaskParamTable::Row&)
        {
            return BITMASK_PARAM_TABLE_COUNT2;
        }
    };

    ExplicitParametersDb* m_database;

    static const char DB_FILE_NAME[];

    static const uint32_t NUM_BITMASK_PARAM_TABLE_ROWS;
    static const TestBitmask BITMASK_PARAM_TABLE_COUNT1;
    static const TestBitmask BITMASK_PARAM_TABLE_COUNT2;
};

const char ExplicitBitmaskParamTest::DB_FILE_NAME[] = "explicit_bitmask_param_test.sqlite";

const uint32_t ExplicitBitmaskParamTest::NUM_BITMASK_PARAM_TABLE_ROWS = 5;
const TestBitmask ExplicitBitmaskParamTest::BITMASK_PARAM_TABLE_COUNT1 = TestBitmask::Values::TEN;
const TestBitmask ExplicitBitmaskParamTest::BITMASK_PARAM_TABLE_COUNT2 = TestBitmask::Values::ELEVEN;

TEST_F(ExplicitBitmaskParamTest, readWithoutCondition)
{
    BitmaskParamTable& bitmaskParamTable = m_database->getBitmaskParamTable();

    BitmaskParamTableParameterProvider parameterProvider;
    std::vector<BitmaskParamTable::Row> writtenRows;
    fillBitmaskParamTableRows(writtenRows);
    bitmaskParamTable.write(parameterProvider, writtenRows);

    BitmaskParamTable::Reader reader = bitmaskParamTable.createReader(parameterProvider);

    std::vector<BitmaskParamTable::Row> readRows;
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkBitmaskParamTableRows(writtenRows, readRows);
}

TEST_F(ExplicitBitmaskParamTest, readWithCondition)
{
    BitmaskParamTable& bitmaskParamTable = m_database->getBitmaskParamTable();

    BitmaskParamTableParameterProvider parameterProvider;
    std::vector<BitmaskParamTable::Row> writtenRows;
    fillBitmaskParamTableRows(writtenRows);
    bitmaskParamTable.write(parameterProvider, writtenRows);

    const std::string condition = "name='Name1'";
    BitmaskParamTable::Reader reader = bitmaskParamTable.createReader(parameterProvider, condition);

    ASSERT_TRUE(reader.hasNext());
    BitmaskParamTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    const size_t expectedRowNum = 1;
    checkBitmaskParamTableRow(writtenRows[expectedRowNum], readRow);
}

TEST_F(ExplicitBitmaskParamTest, update)
{
    BitmaskParamTable& bitmaskParamTable = m_database->getBitmaskParamTable();

    BitmaskParamTableParameterProvider parameterProvider;
    std::vector<BitmaskParamTable::Row> writtenRows;
    fillBitmaskParamTableRows(writtenRows);
    bitmaskParamTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    BitmaskParamTable::Row updateRow;
    fillBitmaskParamTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    bitmaskParamTable.update(parameterProvider, updateRow, updateCondition);

    BitmaskParamTable::Reader reader = bitmaskParamTable.createReader(parameterProvider, updateCondition);

    ASSERT_TRUE(reader.hasNext());
    BitmaskParamTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());
    checkBitmaskParamTableRow(updateRow, readRow);
}

} // namespace explicit_bitmask_param
} // namespace explicit_parameters
