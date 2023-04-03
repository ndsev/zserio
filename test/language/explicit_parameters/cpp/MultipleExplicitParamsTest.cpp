#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "explicit_parameters/ExplicitParametersDb.h"

#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"

namespace explicit_parameters
{
namespace multiple_explicit_params
{

using allocator_type = ExplicitParametersDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class MultipleExplicitParamsTest : public ::testing::Test
{
public:
    MultipleExplicitParamsTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~MultipleExplicitParamsTest() override
    {
        delete m_database;
    }

    MultipleExplicitParamsTest(const MultipleExplicitParamsTest&) = delete;
    MultipleExplicitParamsTest& operator=(const MultipleExplicitParamsTest&) = delete;

    MultipleExplicitParamsTest(MultipleExplicitParamsTest&&) = delete;
    MultipleExplicitParamsTest& operator=(MultipleExplicitParamsTest&&) = delete;

protected:
    void fillMultipleParamsTableRow(MultipleParamsTable::Row& row, uint32_t id, const string_type& name)
    {
        row.setId(id);
        row.setName(name);

        TestBlob testBlob1;
        {
            vector_type<uint8_t>& values8 = testBlob1.getValues8();
            vector_type<uint16_t>& values16 = testBlob1.getValues16();
            for (uint32_t i = 0; i < MULTIPLE_PARAMS_COUNT1; ++i)
                values8.push_back(static_cast<uint8_t>(id));
            for (uint32_t i = 0; i < MULTIPLE_PARAMS_COUNT2; ++i)
                values16.push_back(static_cast<uint16_t>(id));
        }
        row.setBlob1(testBlob1);

        TestBlob testBlob2;
        {
            vector_type<uint8_t>& values8 = testBlob2.getValues8();
            vector_type<uint16_t>& values16 = testBlob2.getValues16();
            for (uint32_t i = 0; i < MULTIPLE_PARAMS_COUNT; ++i)
            {
                values8.push_back(static_cast<uint8_t>(id + 1));
                values16.push_back(static_cast<uint16_t>(id + 1));
            }
        }
        row.setBlob2(testBlob2);

        TestBlob testBlob3;
        {
            vector_type<uint8_t>& values8 = testBlob3.getValues8();
            vector_type<uint16_t>& values16 = testBlob3.getValues16();
            for (uint32_t i = 0; i < MULTIPLE_PARAMS_COUNT1; ++i)
            {
                values8.push_back(static_cast<uint8_t>(id + 2));
                values16.push_back(static_cast<uint16_t>(id + 2));
            }
        }
        row.setBlob3(testBlob3);
    }

    void fillMultipleParamsTableRows(vector_type<MultipleParamsTable::Row>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_MULTIPLE_PARAMS_ROWS; ++id)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(id);
            MultipleParamsTable::Row row;
            fillMultipleParamsTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkMultipleParamsTableRow(const MultipleParamsTable::Row& row1,
            const MultipleParamsTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob1(), row2.getBlob1());
        ASSERT_EQ(row1.getBlob2(), row2.getBlob2());
        ASSERT_EQ(row1.getBlob3(), row2.getBlob3());

        // check reused explicit parameters
        ASSERT_EQ(row2.getBlob1().getCount8(), row2.getBlob3().getCount8());
        ASSERT_EQ(row2.getBlob1().getCount8(), row2.getBlob3().getCount16());
        ASSERT_EQ(row2.getBlob2().getCount8(), row2.getBlob2().getCount16());
    }

    static void checkMultipleParamsTableRows(const vector_type<MultipleParamsTable::Row>& rows1,
            const vector_type<MultipleParamsTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkMultipleParamsTableRow(rows1[i], rows2[i]);
    }

    class MultipleParamsTableParameterProvider : public MultipleParamsTable::IParameterProvider
    {
    public:
        virtual uint32_t getCount1(MultipleParamsTable::Row&) override
        {
            return MULTIPLE_PARAMS_COUNT1;
        }

        virtual uint32_t getCount2(MultipleParamsTable::Row&) override
        {
            return MULTIPLE_PARAMS_COUNT2;
        }

        virtual uint32_t getCount(MultipleParamsTable::Row&) override
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

const char MultipleExplicitParamsTest::DB_FILE_NAME[] =
        "language/explicit_parameters/multiple_explicit_param_test.sqlite";

const uint32_t MultipleExplicitParamsTest::NUM_MULTIPLE_PARAMS_ROWS = 5;
const uint32_t MultipleExplicitParamsTest::MULTIPLE_PARAMS_COUNT1 = 10;
const uint32_t MultipleExplicitParamsTest::MULTIPLE_PARAMS_COUNT2 = 11;
const uint32_t MultipleExplicitParamsTest::MULTIPLE_PARAMS_COUNT = 12;

TEST_F(MultipleExplicitParamsTest, readWithoutCondition)
{
    MultipleParamsTable& multipleParamsTable = m_database->getMultipleParamsTable();

    MultipleParamsTableParameterProvider parameterProvider;
    vector_type<MultipleParamsTable::Row> writtenRows;
    fillMultipleParamsTableRows(writtenRows);
    multipleParamsTable.write(parameterProvider, writtenRows);

    MultipleParamsTable::Reader reader = multipleParamsTable.createReader(parameterProvider);

    vector_type<MultipleParamsTable::Row> readRows;
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkMultipleParamsTableRows(writtenRows, readRows);
}

TEST_F(MultipleExplicitParamsTest, readWithCondition)
{
    MultipleParamsTable& multipleParamsTable = m_database->getMultipleParamsTable();

    MultipleParamsTableParameterProvider parameterProvider;
    vector_type<MultipleParamsTable::Row> writtenRows;
    fillMultipleParamsTableRows(writtenRows);
    multipleParamsTable.write(parameterProvider, writtenRows);

    const string_type condition = "name='Name1'";
    MultipleParamsTable::Reader reader = multipleParamsTable.createReader(parameterProvider, condition);

    ASSERT_TRUE(reader.hasNext());
    MultipleParamsTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    const size_t expectedRowNum = 1;
    checkMultipleParamsTableRow(writtenRows[expectedRowNum], readRow);
}

TEST_F(MultipleExplicitParamsTest, update)
{
    MultipleParamsTable& multipleParamsTable = m_database->getMultipleParamsTable();

    MultipleParamsTableParameterProvider parameterProvider;
    vector_type<MultipleParamsTable::Row> writtenRows;
    fillMultipleParamsTableRows(writtenRows);
    multipleParamsTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    MultipleParamsTable::Row updateRow;
    fillMultipleParamsTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "id=" + zserio::toString<allocator_type>(updateRowId);
    multipleParamsTable.update(parameterProvider, updateRow, updateCondition);

    MultipleParamsTable::Reader reader = multipleParamsTable.createReader(parameterProvider, updateCondition);
    ASSERT_TRUE(reader.hasNext());
    MultipleParamsTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());
    checkMultipleParamsTableRow(updateRow, readRow);
}

} // namespace multiple_explicit_params
} // namespace explicit_parameters
