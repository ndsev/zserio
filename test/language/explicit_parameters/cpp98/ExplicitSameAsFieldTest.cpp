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
    void fillSameAsFieldTableRow(SameAsFieldTableRow& row, uint32_t id, const std::string& name)
    {
        row.setId(id);
        row.setName(name);
        row.setCount(SAME_AS_FIELD_TABLE_COUNT);

        TestBlob testBlob;
        testBlob.initialize(SAME_AS_FIELD_TABLE_COUNT);
        zserio::UInt8Array& values = testBlob.getValues();
        for (uint32_t i = 0; i < SAME_AS_FIELD_TABLE_COUNT; ++i)
            values.push_back(static_cast<uint8_t>(id));
        row.setBlob(testBlob);

        TestBlob testBlobExplicit;
        testBlobExplicit.initialize(SAME_AS_FIELD_TABLE_COUNT_EXPLICIT);
        zserio::UInt8Array& valuesExplicit = testBlobExplicit.getValues();
        for (uint32_t i = 0; i < SAME_AS_FIELD_TABLE_COUNT_EXPLICIT; ++i)
            valuesExplicit.push_back(static_cast<uint8_t>(id + 1));
        row.setBlobExplicit(testBlobExplicit);
    }

    void fillSameAsFieldTableRows(std::vector<SameAsFieldTableRow>& rows)
    {
        rows.clear();
        for (uint32_t id = 0; id < NUM_SAME_AS_FIELD_TABLE_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            SameAsFieldTableRow row;
            fillSameAsFieldTableRow(row, id, name);
            rows.push_back(row);
        }
    }

    static void checkSameAsFieldTableRow(const SameAsFieldTableRow& row1, const SameAsFieldTableRow& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getCount(), row2.getCount());
        ASSERT_EQ(row1.getBlob(), row2.getBlob());
        ASSERT_EQ(row1.getBlobExplicit(), row2.getBlobExplicit());
    }

    static void checkSameAsFieldTableRows(const std::vector<SameAsFieldTableRow>& rows1,
            const std::vector<SameAsFieldTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkSameAsFieldTableRow(rows1[i], rows2[i]);
    }

    class SameAsFieldTableParameterProvider : public SameAsFieldTable::IParameterProvider
    {
    public:
        virtual uint32_t getCount(sqlite3_stmt&)
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

    std::vector<SameAsFieldTableRow> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(writtenRows);

    SameAsFieldTableParameterProvider parameterProvider;
    std::vector<SameAsFieldTableRow> readRows;
    sameAsFieldTable.read(parameterProvider, readRows);
    checkSameAsFieldTableRows(writtenRows, readRows);
}

TEST_F(ExplicitSameAsFieldTest, readWithCondition)
{
    SameAsFieldTable& sameAsFieldTable = m_database->getSameAsFieldTable();

    std::vector<SameAsFieldTableRow> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(writtenRows);

    SameAsFieldTableParameterProvider parameterProvider;
    const std::string condition = "name='Name1'";
    std::vector<SameAsFieldTableRow> readRows;
    sameAsFieldTable.read(parameterProvider, condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkSameAsFieldTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ExplicitSameAsFieldTest, update)
{
    SameAsFieldTable& sameAsFieldTable = m_database->getSameAsFieldTable();

    std::vector<SameAsFieldTableRow> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    SameAsFieldTableRow updateRow;
    fillSameAsFieldTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    sameAsFieldTable.update(updateRow, updateCondition);

    SameAsFieldTableParameterProvider parameterProvider;
    std::vector<SameAsFieldTableRow> readRows;
    sameAsFieldTable.read(parameterProvider, updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkSameAsFieldTableRow(updateRow, readRows[0]);
}

} // namespace explicit_same_as_field
} // namespace explicit_parameters
