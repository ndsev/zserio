#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "explicit_parameters/ExplicitParametersDb.h"

namespace explicit_parameters
{
namespace multiple_explicit_params
{

class MultipleExplicitParamsTest : public ::testing::Test
{
public:
    MultipleExplicitParamsTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~MultipleExplicitParamsTest()
    {
        delete m_database;
    }

protected:
    void fillMultipleParamsTableRow(MultipleParamsTableRow& row, uint32_t id, const std::string& name)
    {
        row.setId(id);
        row.setName(name);

        TestBlob testBlob1;
        testBlob1.initialize(MULTIPLE_PARAMS_COUNT1, MULTIPLE_PARAMS_COUNT2);
        {
            zserio::UInt8Array& values8 = testBlob1.getValues8();
            zserio::UInt16Array& values16 = testBlob1.getValues16();
            for (uint32_t i = 0; i < MULTIPLE_PARAMS_COUNT1; ++i)
                values8.push_back(static_cast<uint8_t>(id));
            for (uint32_t i = 0; i < MULTIPLE_PARAMS_COUNT2; ++i)
                values16.push_back(static_cast<uint16_t>(id));
        }
        row.setBlob1(testBlob1);

        TestBlob testBlob2;
        testBlob2.initialize(MULTIPLE_PARAMS_COUNT, MULTIPLE_PARAMS_COUNT);
        {
            zserio::UInt8Array& values8 = testBlob2.getValues8();
            zserio::UInt16Array& values16 = testBlob2.getValues16();
            for (uint32_t i = 0; i < MULTIPLE_PARAMS_COUNT; ++i)
            {
                values8.push_back(static_cast<uint8_t>(id + 1));
                values16.push_back(static_cast<uint16_t>(id + 1));
            }
        }
        row.setBlob2(testBlob2);

        TestBlob testBlob3;
        testBlob3.initialize(MULTIPLE_PARAMS_COUNT1, MULTIPLE_PARAMS_COUNT1);
        {
            zserio::UInt8Array& values8 = testBlob3.getValues8();
            zserio::UInt16Array& values16 = testBlob3.getValues16();
            for (uint32_t i = 0; i < MULTIPLE_PARAMS_COUNT1; ++i)
            {
                values8.push_back(static_cast<uint8_t>(id + 2));
                values16.push_back(static_cast<uint16_t>(id + 2));
            }
        }
        row.setBlob3(testBlob3);
    }

    void fillMultipleParamsTableRows(std::vector<MultipleParamsTableRow>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_MULTIPLE_PARAMS_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            MultipleParamsTableRow row;
            fillMultipleParamsTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkMultipleParamsTableRow(const MultipleParamsTableRow& row1, const MultipleParamsTableRow& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob1(), row2.getBlob1());
        ASSERT_EQ(row1.getBlob2(), row2.getBlob2());
        ASSERT_EQ(row1.getBlob3(), row2.getBlob3());

        // check reused explicit count1 parameter
        ASSERT_EQ(row2.getBlob1().getCount8(), row2.getBlob3().getCount8());
        ASSERT_EQ(row2.getBlob1().getCount8(), row2.getBlob3().getCount16());
    }

    static void checkMultipleParamsTableRows(const std::vector<MultipleParamsTableRow>& rows1,
            const std::vector<MultipleParamsTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkMultipleParamsTableRow(rows1[i], rows2[i]);
    }

    class MultipleParamsTableParameterProvider : public MultipleParamsTable::IParameterProvider
    {
    public:
        virtual uint32_t getCount1(sqlite3_stmt&)
        {
            return MULTIPLE_PARAMS_COUNT1;
        }

        virtual uint32_t getCount2(sqlite3_stmt&)
        {
            return MULTIPLE_PARAMS_COUNT2;
        }

        virtual uint32_t getCount(sqlite3_stmt&)
        {
            return MULTIPLE_PARAMS_COUNT;
        }
    };

    ExplicitParametersDb* m_database;

    static const char DB_FILE_NAME[];

    static const uint32_t NUM_MULTIPLE_PARAMS_ROWS;
    static const uint32_t MULTIPLE_PARAMS_COUNT1;
    static const uint32_t MULTIPLE_PARAMS_COUNT2;
    static const uint32_t MULTIPLE_PARAMS_COUNT;
};

const char MultipleExplicitParamsTest::DB_FILE_NAME[] = "multiple_explicit_param_test.sqlite";

const uint32_t MultipleExplicitParamsTest::NUM_MULTIPLE_PARAMS_ROWS = 5;
const uint32_t MultipleExplicitParamsTest::MULTIPLE_PARAMS_COUNT1 = 10;
const uint32_t MultipleExplicitParamsTest::MULTIPLE_PARAMS_COUNT2 = 11;
const uint32_t MultipleExplicitParamsTest::MULTIPLE_PARAMS_COUNT = 12;

TEST_F(MultipleExplicitParamsTest, readWithoutCondition)
{
    MultipleParamsTable& multipleParamsTable = m_database->getMultipleParamsTable();

    std::vector<MultipleParamsTableRow> writtenRows;
    fillMultipleParamsTableRows(writtenRows);
    multipleParamsTable.write(writtenRows);

    MultipleParamsTableParameterProvider parameterProvider;
    std::vector<MultipleParamsTableRow> readRows;
    multipleParamsTable.read(parameterProvider, readRows);
    checkMultipleParamsTableRows(writtenRows, readRows);
}

TEST_F(MultipleExplicitParamsTest, readWithCondition)
{
    MultipleParamsTable& multipleParamsTable = m_database->getMultipleParamsTable();

    std::vector<MultipleParamsTableRow> writtenRows;
    fillMultipleParamsTableRows(writtenRows);
    multipleParamsTable.write(writtenRows);

    MultipleParamsTableParameterProvider parameterProvider;
    const std::string condition = "name='Name1'";
    std::vector<MultipleParamsTableRow> readRows;
    multipleParamsTable.read(parameterProvider, condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkMultipleParamsTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(MultipleExplicitParamsTest, update)
{
    MultipleParamsTable& multipleParamsTable = m_database->getMultipleParamsTable();

    std::vector<MultipleParamsTableRow> writtenRows;
    fillMultipleParamsTableRows(writtenRows);
    multipleParamsTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    MultipleParamsTableRow updateRow;
    fillMultipleParamsTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    multipleParamsTable.update(updateRow, updateCondition);

    MultipleParamsTableParameterProvider parameterProvider;
    std::vector<MultipleParamsTableRow> readRows;
    multipleParamsTable.read(parameterProvider, updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkMultipleParamsTableRow(updateRow, readRows[0]);
}

} // namespace multiple_explicit_params
} // namespace explicit_parameters
