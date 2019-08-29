#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "explicit_parameters/ExplicitParametersDb.h"

namespace explicit_parameters
{
namespace explicit_same_as_field
{

class ExplicitSameAsFieldTest : public ::testing::Test
{
public:
    ExplicitSameAsFieldTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ExplicitSameAsFieldTest()
    {
        delete m_database;
    }

protected:
    void fillSameAsFieldTableRow(SameAsFieldTable::Row& row, uint32_t id, const std::string& name)
    {
        row.setId(id);
        row.setName(name);
        row.setCount(SAME_AS_FIELD_TABLE_COUNT);

        TestBlob testBlob;
        std::vector<uint8_t>& values = testBlob.getValues();
        for (uint32_t i = 0; i < SAME_AS_FIELD_TABLE_COUNT; ++i)
            values.push_back(static_cast<uint8_t>(id));
        row.setBlob(testBlob);

        TestBlob testBlobExplicit;
        std::vector<uint8_t>& valuesExplicit = testBlobExplicit.getValues();
        for (uint32_t i = 0; i < SAME_AS_FIELD_TABLE_COUNT_EXPLICIT; ++i)
            valuesExplicit.push_back(static_cast<uint8_t>(id + 1));
        row.setBlobExplicit(testBlobExplicit);
    }

    void fillSameAsFieldTableRows(std::vector<SameAsFieldTable::Row>& rows)
    {
        rows.clear();
        rows.reserve(NUM_SAME_AS_FIELD_TABLE_ROWS);
        for (uint32_t id = 0; id < NUM_SAME_AS_FIELD_TABLE_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            SameAsFieldTable::Row row;
            fillSameAsFieldTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkSameAsFieldTableRow(const SameAsFieldTable::Row& row1, const SameAsFieldTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getCount(), row2.getCount());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
        ASSERT_EQ(row1.getBlobExplicit(), row2.getBlobExplicit());
    }

    static void checkSameAsFieldTableRows(const std::vector<SameAsFieldTable::Row>& rows1,
            const std::vector<SameAsFieldTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkSameAsFieldTableRow(rows1[i], rows2[i]);
    }

    class SameAsFieldTableParameterProvider : public SameAsFieldTable::IParameterProvider
    {
    public:
        virtual uint32_t getCount(SameAsFieldTable::Row&)
        {
            return SAME_AS_FIELD_TABLE_COUNT_EXPLICIT;
        }
    };

    ExplicitParametersDb* m_database;

    static const char DB_FILE_NAME[];

    static const uint32_t NUM_SAME_AS_FIELD_TABLE_ROWS;
    static const uint32_t SAME_AS_FIELD_TABLE_COUNT;
    static const uint32_t SAME_AS_FIELD_TABLE_COUNT_EXPLICIT;
};

const char ExplicitSameAsFieldTest::DB_FILE_NAME[] = "explicit_same_as_field_test.sqlite";

const uint32_t ExplicitSameAsFieldTest::NUM_SAME_AS_FIELD_TABLE_ROWS = 5;
const uint32_t ExplicitSameAsFieldTest::SAME_AS_FIELD_TABLE_COUNT = 10;
const uint32_t ExplicitSameAsFieldTest::SAME_AS_FIELD_TABLE_COUNT_EXPLICIT = 11;

TEST_F(ExplicitSameAsFieldTest, readWithoutCondition)
{
    SameAsFieldTable& sameAsFieldTable = m_database->getSameAsFieldTable();

    SameAsFieldTableParameterProvider parameterProvider;
    std::vector<SameAsFieldTable::Row> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(parameterProvider, writtenRows);

    SameAsFieldTable::Reader reader = sameAsFieldTable.createReader(parameterProvider);

    std::vector<SameAsFieldTable::Row> readRows;
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkSameAsFieldTableRows(writtenRows, readRows);
}

TEST_F(ExplicitSameAsFieldTest, readWithCondition)
{
    SameAsFieldTable& sameAsFieldTable = m_database->getSameAsFieldTable();

    SameAsFieldTableParameterProvider parameterProvider;
    std::vector<SameAsFieldTable::Row> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(parameterProvider, writtenRows);

    const std::string condition = "name='Name1'";
    SameAsFieldTable::Reader reader = sameAsFieldTable.createReader(parameterProvider, condition);

    ASSERT_TRUE(reader.hasNext());
    SameAsFieldTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    const size_t expectedRowNum = 1;
    checkSameAsFieldTableRow(writtenRows[expectedRowNum], readRow);
}

TEST_F(ExplicitSameAsFieldTest, update)
{
    SameAsFieldTable& sameAsFieldTable = m_database->getSameAsFieldTable();

    SameAsFieldTableParameterProvider parameterProvider;
    std::vector<SameAsFieldTable::Row> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    SameAsFieldTable::Row updateRow;
    fillSameAsFieldTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    sameAsFieldTable.update(parameterProvider, updateRow, updateCondition);

    SameAsFieldTable::Reader reader = sameAsFieldTable.createReader(parameterProvider, updateCondition);
    ASSERT_TRUE(reader.hasNext());
    SameAsFieldTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());
    checkSameAsFieldTableRow(updateRow, readRow);
}

} // namespace explicit_same_as_field
} // namespace explicit_parameters
