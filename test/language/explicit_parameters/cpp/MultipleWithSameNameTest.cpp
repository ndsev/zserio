#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "explicit_parameters/ExplicitParametersDb.h"

namespace explicit_parameters
{
namespace multiple_with_same_name
{

class MultipleWithSameNameTest : public ::testing::Test
{
public:
    MultipleWithSameNameTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~MultipleWithSameNameTest()
    {
        delete m_database;
    }

protected:
    void fillMultipleWithSameNameTableRow(MultipleWithSameNameTable::Row& row, uint32_t id,
            const std::string& name)
    {
        row.setId(id);
        row.setName(name);

        row.setParameterized1(Parameterized1(id * 10));
        row.setParameterized2(Parameterized2(id * 1.5f));
    }

    void fillMultipleWithSameNameTableRows(std::vector<MultipleWithSameNameTable::Row>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            MultipleWithSameNameTable::Row row;
            fillMultipleWithSameNameTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkMultipleWithSameNameTableRow(const MultipleWithSameNameTable::Row& row1,
            const MultipleWithSameNameTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getParameterized1(), row2.getParameterized1());
        ASSERT_EQ(row1.getParameterized2(), row2.getParameterized2());
    }

    static void checkMultipleWithSameNameTableRows(const std::vector<MultipleWithSameNameTable::Row>& rows1,
            const std::vector<MultipleWithSameNameTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkMultipleWithSameNameTableRow(rows1[i], rows2[i]);
    }

    class MultipleParamsTableParameterProvider : public MultipleWithSameNameTable::IParameterProvider
    {
    public:
        virtual uint32_t getParam1(MultipleWithSameNameTable::Row&)
        {
            return PARAM1;
        }

        virtual float getParam2(MultipleWithSameNameTable::Row&)
        {
            return PARAM2;
        }
    };

    ExplicitParametersDb* m_database;

    static const char DB_FILE_NAME[];

    static const uint32_t NUM_ROWS;
    static const uint32_t PARAM1;
    static const float PARAM2;
};

const char MultipleWithSameNameTest::DB_FILE_NAME[] = "multiple_with_same_name_test.sqlite";

const uint32_t MultipleWithSameNameTest::NUM_ROWS = 5;
const uint32_t MultipleWithSameNameTest::PARAM1 = 100;
const float MultipleWithSameNameTest::PARAM2 = 10.0f;

TEST_F(MultipleWithSameNameTest, readWithoutCondition)
{
    MultipleWithSameNameTable& multipleWithSameNameTable = m_database->getMultipleWithSameNameTable();

    MultipleParamsTableParameterProvider parameterProvider;
    std::vector<MultipleWithSameNameTable::Row> writtenRows;
    fillMultipleWithSameNameTableRows(writtenRows);
    multipleWithSameNameTable.write(parameterProvider, writtenRows);

    MultipleWithSameNameTable::Reader reader = multipleWithSameNameTable.createReader(parameterProvider);

    std::vector<MultipleWithSameNameTable::Row> readRows;
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkMultipleWithSameNameTableRows(writtenRows, readRows);
}

TEST_F(MultipleWithSameNameTest, readWithCondition)
{
    MultipleWithSameNameTable& multipleWithSameNameTable = m_database->getMultipleWithSameNameTable();

    MultipleParamsTableParameterProvider parameterProvider;
    std::vector<MultipleWithSameNameTable::Row> writtenRows;
    fillMultipleWithSameNameTableRows(writtenRows);
    multipleWithSameNameTable.write(parameterProvider, writtenRows);

    const std::string condition = "name='Name1'";
    MultipleWithSameNameTable::Reader reader =
            multipleWithSameNameTable.createReader(parameterProvider, condition);

    ASSERT_TRUE(reader.hasNext());
    MultipleWithSameNameTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    const size_t expectedRowNum = 1;
    checkMultipleWithSameNameTableRow(writtenRows[expectedRowNum], readRow);
}

TEST_F(MultipleWithSameNameTest, update)
{
    MultipleWithSameNameTable& multipleWithSameNameTable = m_database->getMultipleWithSameNameTable();

    MultipleParamsTableParameterProvider parameterProvider;
    std::vector<MultipleWithSameNameTable::Row> writtenRows;
    fillMultipleWithSameNameTableRows(writtenRows);
    multipleWithSameNameTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    MultipleWithSameNameTable::Row updateRow;
    fillMultipleWithSameNameTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    multipleWithSameNameTable.update(parameterProvider, updateRow, updateCondition);

    MultipleWithSameNameTable::Reader reader =
        multipleWithSameNameTable.createReader(parameterProvider, updateCondition);
    ASSERT_TRUE(reader.hasNext());
    MultipleWithSameNameTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());
    checkMultipleWithSameNameTableRow(updateRow, readRow);
}

} // namespace multiple_with_same_name
} // namespace explicit_parameters
