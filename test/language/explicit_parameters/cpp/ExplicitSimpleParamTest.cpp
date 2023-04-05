#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "explicit_parameters/ExplicitParametersDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"

namespace explicit_parameters
{
namespace explicit_simple_param
{

using allocator_type = ExplicitParametersDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ExplicitSimpleParamTest : public ::testing::Test
{
public:
    ExplicitSimpleParamTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ExplicitSimpleParamTest() override
    {
        delete m_database;
    }

    ExplicitSimpleParamTest(const ExplicitSimpleParamTest&) = delete;
    ExplicitSimpleParamTest& operator=(const ExplicitSimpleParamTest&) = delete;

    ExplicitSimpleParamTest(ExplicitSimpleParamTest&&) = delete;
    ExplicitSimpleParamTest& operator=(ExplicitSimpleParamTest&&) = delete;

protected:
    void fillSimpleParamTableRow(SimpleParamTable::Row& row, uint32_t id, const string_type& name)
    {
        row.setId(id);
        row.setName(name);

        TestBlob testBlob1;
        vector_type<uint8_t>& values1 = testBlob1.getValues();
        for (uint32_t i = 0; i < SIMPLE_PARAM_TABLE_COUNT1; ++i)
            values1.push_back(static_cast<uint8_t>(id));
        row.setBlob1(testBlob1);

        TestBlob testBlob2;
        vector_type<uint8_t>& values2 = testBlob2.getValues();
        for (uint32_t i = 0; i < SIMPLE_PARAM_TABLE_COUNT2; ++i)
            values2.push_back(static_cast<uint8_t>(id + 1));
        row.setBlob2(testBlob2);

        TestBlob testBlob3;
        vector_type<uint8_t>& values3 = testBlob3.getValues();
        for (uint32_t i = 0; i < SIMPLE_PARAM_TABLE_COUNT1; ++i)
            values3.push_back(static_cast<uint8_t>(id + 2));
        row.setBlob3(testBlob3);
    }

    void fillSimpleParamTableRows(vector_type<SimpleParamTable::Row>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_SIMPLE_PARAM_TABLE_ROWS; ++id)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(id);
            SimpleParamTable::Row row;
            fillSimpleParamTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkSimpleParamTableRow(const SimpleParamTable::Row& row1, const SimpleParamTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob1(), row2.getBlob1());
        ASSERT_EQ(row1.getBlob2(), row2.getBlob2());
        ASSERT_EQ(row1.getBlob3(), row2.getBlob3());

        // check reused explicit count parameter
        ASSERT_EQ(row2.getBlob1().getCount(), row2.getBlob3().getCount());
    }

    static void checkSimpleParamTableRows(const vector_type<SimpleParamTable::Row>& rows1,
            const vector_type<SimpleParamTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkSimpleParamTableRow(rows1[i], rows2[i]);
    }

    class SimpleParamTableParameterProvider : public SimpleParamTable::IParameterProvider
    {
    public:
        uint32_t getCount1(SimpleParamTable::Row&) override
        {
            return SIMPLE_PARAM_TABLE_COUNT1;
        }

        uint32_t getCount2(SimpleParamTable::Row&) override
        {
            return SIMPLE_PARAM_TABLE_COUNT2;
        }
    };

    ExplicitParametersDb* m_database;

    static const char* DB_FILE_NAME;

    static const uint32_t NUM_SIMPLE_PARAM_TABLE_ROWS;
    static const uint32_t SIMPLE_PARAM_TABLE_COUNT1;
    static const uint32_t SIMPLE_PARAM_TABLE_COUNT2;
};

const char* ExplicitSimpleParamTest::DB_FILE_NAME =
        "language/explicit_parameters/explicit_simple_param_test.sqlite";

const uint32_t ExplicitSimpleParamTest::NUM_SIMPLE_PARAM_TABLE_ROWS = 5;
const uint32_t ExplicitSimpleParamTest::SIMPLE_PARAM_TABLE_COUNT1 = 10;
const uint32_t ExplicitSimpleParamTest::SIMPLE_PARAM_TABLE_COUNT2 = 11;

TEST_F(ExplicitSimpleParamTest, readWithoutCondition)
{
    SimpleParamTable& simpleParamTable = m_database->getSimpleParamTable();

    SimpleParamTableParameterProvider parameterProvider;
    vector_type<SimpleParamTable::Row> writtenRows;
    fillSimpleParamTableRows(writtenRows);
    simpleParamTable.write(parameterProvider, writtenRows);

    SimpleParamTable::Reader reader = simpleParamTable.createReader(parameterProvider);

    vector_type<SimpleParamTable::Row> readRows;
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkSimpleParamTableRows(writtenRows, readRows);
}

TEST_F(ExplicitSimpleParamTest, readWithCondition)
{
    SimpleParamTable& simpleParamTable = m_database->getSimpleParamTable();

    SimpleParamTableParameterProvider parameterProvider;
    vector_type<SimpleParamTable::Row> writtenRows;
    fillSimpleParamTableRows(writtenRows);
    simpleParamTable.write(parameterProvider, writtenRows);

    const string_type condition = "name='Name1'";
    SimpleParamTable::Reader reader = simpleParamTable.createReader(parameterProvider, condition);

    ASSERT_TRUE(reader.hasNext());
    SimpleParamTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    const size_t expectedRowNum = 1;
    checkSimpleParamTableRow(writtenRows[expectedRowNum], readRow);
}

TEST_F(ExplicitSimpleParamTest, update)
{
    SimpleParamTable& simpleParamTable = m_database->getSimpleParamTable();

    SimpleParamTableParameterProvider parameterProvider;
    vector_type<SimpleParamTable::Row> writtenRows;
    fillSimpleParamTableRows(writtenRows);
    simpleParamTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    SimpleParamTable::Row updateRow;
    fillSimpleParamTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "id=" + zserio::toString<allocator_type>(updateRowId);
    simpleParamTable.update(parameterProvider, updateRow, updateCondition);

    SimpleParamTable::Reader reader = simpleParamTable.createReader(parameterProvider, updateCondition);

    ASSERT_TRUE(reader.hasNext());
    SimpleParamTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());
    checkSimpleParamTableRow(updateRow, readRow);
}

} // namespace explicit_simple_param
} // namespace explicit_parameters
