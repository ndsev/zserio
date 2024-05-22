#include <cstdio>
#include <string>
#include <vector>

#include "explicit_parameters/ExplicitParametersDb.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"

namespace explicit_parameters
{
namespace explicit_same_as_field
{

using allocator_type = ExplicitParametersDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ExplicitSameAsFieldTest : public ::testing::Test
{
public:
    ExplicitSameAsFieldTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ExplicitSameAsFieldTest() override
    {
        delete m_database;
    }

    ExplicitSameAsFieldTest(const ExplicitSameAsFieldTest&) = delete;
    ExplicitSameAsFieldTest& operator=(const ExplicitSameAsFieldTest&) = delete;

    ExplicitSameAsFieldTest(ExplicitSameAsFieldTest&&) = delete;
    ExplicitSameAsFieldTest& operator=(ExplicitSameAsFieldTest&&) = delete;

protected:
    void fillSameAsFieldTableRow(SameAsFieldTable::Row& row, uint32_t id, const string_type& name)
    {
        row.setId(id);
        row.setName(name);
        row.setCount(SAME_AS_FIELD_TABLE_COUNT);

        TestBlob testBlob;
        vector_type<uint8_t>& values = testBlob.getValues();
        for (uint32_t i = 0; i < SAME_AS_FIELD_TABLE_COUNT; ++i)
        {
            values.push_back(static_cast<uint8_t>(id));
        }
        row.setBlob(testBlob);

        TestBlob testBlobExplicit;
        vector_type<uint8_t>& valuesExplicit = testBlobExplicit.getValues();
        for (uint32_t i = 0; i < SAME_AS_FIELD_TABLE_COUNT_EXPLICIT; ++i)
        {
            valuesExplicit.push_back(static_cast<uint8_t>(id + 1));
        }
        row.setBlobExplicit(testBlobExplicit);
    }

    void fillSameAsFieldTableRows(vector_type<SameAsFieldTable::Row>& rows)
    {
        rows.clear();
        rows.reserve(NUM_SAME_AS_FIELD_TABLE_ROWS);
        for (uint32_t id = 0; id < NUM_SAME_AS_FIELD_TABLE_ROWS; ++id)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(id);
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

    static void checkSameAsFieldTableRows(
            const vector_type<SameAsFieldTable::Row>& rows1, const vector_type<SameAsFieldTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
        {
            checkSameAsFieldTableRow(rows1[i], rows2[i]);
        }
    }

    class SameAsFieldTableParameterProvider : public SameAsFieldTable::IParameterProvider
    {
    public:
        uint32_t getCount(SameAsFieldTable::Row&) override
        {
            return SAME_AS_FIELD_TABLE_COUNT_EXPLICIT;
        }
    };

    ExplicitParametersDb* m_database;

    static const char* const DB_FILE_NAME;

    static const uint32_t NUM_SAME_AS_FIELD_TABLE_ROWS;
    static const uint32_t SAME_AS_FIELD_TABLE_COUNT;
    static const uint32_t SAME_AS_FIELD_TABLE_COUNT_EXPLICIT;
};

const char* const ExplicitSameAsFieldTest::DB_FILE_NAME =
        "language/explicit_parameters/explicit_same_as_field_test.sqlite";

const uint32_t ExplicitSameAsFieldTest::NUM_SAME_AS_FIELD_TABLE_ROWS = 5;
const uint32_t ExplicitSameAsFieldTest::SAME_AS_FIELD_TABLE_COUNT = 10;
const uint32_t ExplicitSameAsFieldTest::SAME_AS_FIELD_TABLE_COUNT_EXPLICIT = 11;

TEST_F(ExplicitSameAsFieldTest, readWithoutCondition)
{
    SameAsFieldTable& sameAsFieldTable = m_database->getSameAsFieldTable();

    SameAsFieldTableParameterProvider parameterProvider;
    vector_type<SameAsFieldTable::Row> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(parameterProvider, writtenRows);

    SameAsFieldTable::Reader reader = sameAsFieldTable.createReader(parameterProvider);

    vector_type<SameAsFieldTable::Row> readRows;
    while (reader.hasNext())
    {
        readRows.push_back(reader.next());
    }
    checkSameAsFieldTableRows(writtenRows, readRows);
}

TEST_F(ExplicitSameAsFieldTest, readWithCondition)
{
    SameAsFieldTable& sameAsFieldTable = m_database->getSameAsFieldTable();

    SameAsFieldTableParameterProvider parameterProvider;
    vector_type<SameAsFieldTable::Row> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(parameterProvider, writtenRows);

    const string_type condition = "name='Name1'";
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
    vector_type<SameAsFieldTable::Row> writtenRows;
    fillSameAsFieldTableRows(writtenRows);
    sameAsFieldTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    SameAsFieldTable::Row updateRow;
    fillSameAsFieldTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "id=" + zserio::toString<allocator_type>(updateRowId);
    sameAsFieldTable.update(parameterProvider, updateRow, updateCondition);

    SameAsFieldTable::Reader reader = sameAsFieldTable.createReader(parameterProvider, updateCondition);
    ASSERT_TRUE(reader.hasNext());
    SameAsFieldTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());
    checkSameAsFieldTableRow(updateRow, readRow);
}

} // namespace explicit_same_as_field
} // namespace explicit_parameters
