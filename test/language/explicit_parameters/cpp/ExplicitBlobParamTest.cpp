#include <cstdio>
#include <string>
#include <vector>

#include "explicit_parameters/ExplicitParametersDb.h"
#include "gtest/gtest.h"
#include "zserio/RebindAlloc.h"
#include "zserio/StringConvertUtil.h"
#include "zserio/Vector.h"

namespace explicit_parameters
{
namespace explicit_blob_param
{

using allocator_type = ExplicitParametersDb::allocator_type;
using string_type = zserio::string<allocator_type>;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class ExplicitBlobParamTest : public ::testing::Test
{
public:
    ExplicitBlobParamTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
    }

    ~ExplicitBlobParamTest() override
    {
        delete m_database;
    }

    ExplicitBlobParamTest(const ExplicitBlobParamTest&) = delete;
    ExplicitBlobParamTest& operator=(const ExplicitBlobParamTest&) = delete;

    ExplicitBlobParamTest(ExplicitBlobParamTest&&) = delete;
    ExplicitBlobParamTest& operator=(ExplicitBlobParamTest&&) = delete;

protected:
    class BlobParamTableParameterProvider : public BlobParamTable::IParameterProvider
    {
    public:
        BlobParamTableParameterProvider()
        {
            m_headerParam.setCount(BLOB_PARAM_TABLE_HEADER_COUNT);
            m_blob.setCount(BLOB_PARAM_TABLE_BLOB_COUNT);
        }

        Header& getHeaderParam(BlobParamTable::Row&) override
        {
            return m_headerParam;
        }

        Header& getBlob(BlobParamTable::Row&) override
        {
            return m_blob;
        }

    private:
        Header m_headerParam;
        Header m_blob;
    };

    void fillBlobParamTableRow(BlobParamTable::Row& row, uint32_t id, const string_type& name)
    {
        row.setId(id);
        row.setName(name);

        TestBlob testBlob1;
        vector_type<uint8_t>& values1 = testBlob1.getValues();
        for (uint32_t i = 0; i < BLOB_PARAM_TABLE_HEADER_COUNT; ++i)
            values1.push_back(static_cast<uint8_t>(id));
        row.setBlob1(testBlob1);

        TestBlob testBlob2;
        vector_type<uint8_t>& values2 = testBlob2.getValues();
        for (uint32_t i = 0; i < BLOB_PARAM_TABLE_BLOB_COUNT; ++i)
            values2.push_back(static_cast<uint8_t>(id + 1));
        row.setBlob2(testBlob2);

        TestBlob testBlob3;
        vector_type<uint8_t>& values3 = testBlob3.getValues();
        for (uint32_t i = 0; i < BLOB_PARAM_TABLE_HEADER_COUNT; ++i)
            values3.push_back(static_cast<uint8_t>(id + 2));
        row.setBlob3(testBlob3);
    }

    void fillBlobParamTableRows(vector_type<BlobParamTable::Row>& rows)
    {
        rows.clear();
        rows.resize(NUM_BLOB_PARAM_TABLE_ROWS);
        for (uint32_t id = 0; id < NUM_BLOB_PARAM_TABLE_ROWS; ++id)
        {
            const string_type name = "Name" + zserio::toString<allocator_type>(id);
            fillBlobParamTableRow(rows[id], id, name);
        }
    }

    static void checkBlobParamTableRow(const BlobParamTable::Row& row1, const BlobParamTable::Row& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob1(), row2.getBlob1());
        ASSERT_EQ(row1.getBlob2(), row2.getBlob2());
        ASSERT_EQ(row1.getBlob3(), row2.getBlob3());

        // check reused explicit parameters
        ASSERT_EQ(row2.getBlob1().getBlob().getCount(), row2.getBlob3().getBlob().getCount());
        // check that even address of the reused explicit header parameter is the same!
        ASSERT_EQ(row2.getBlob1().getBlob(), row2.getBlob3().getBlob());
    }

    static void checkBlobParamTableRows(
            const vector_type<BlobParamTable::Row>& rows1, const vector_type<BlobParamTable::Row>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkBlobParamTableRow(rows1[i], rows2[i]);
    }

    ExplicitParametersDb* m_database;

    static const char* const DB_FILE_NAME;

    static const uint32_t NUM_BLOB_PARAM_TABLE_ROWS;
    static const uint32_t BLOB_PARAM_TABLE_HEADER_COUNT;
    static const uint32_t BLOB_PARAM_TABLE_BLOB_COUNT;
};

const char* const ExplicitBlobParamTest::DB_FILE_NAME =
        "language/explicit_parameters/explicit_blob_param_test.sqlite";

const uint32_t ExplicitBlobParamTest::NUM_BLOB_PARAM_TABLE_ROWS = 5;
const uint32_t ExplicitBlobParamTest::BLOB_PARAM_TABLE_HEADER_COUNT = 10;
const uint32_t ExplicitBlobParamTest::BLOB_PARAM_TABLE_BLOB_COUNT = 11;

TEST_F(ExplicitBlobParamTest, readWithoutCondition)
{
    BlobParamTable& blobParamTable = m_database->getBlobParamTable();

    BlobParamTableParameterProvider parameterProvider;
    vector_type<BlobParamTable::Row> writtenRows;
    fillBlobParamTableRows(writtenRows);
    blobParamTable.write(parameterProvider, writtenRows);

    BlobParamTable::Reader reader = blobParamTable.createReader(parameterProvider);

    vector_type<BlobParamTable::Row> readRows;
    while (reader.hasNext())
        readRows.push_back(reader.next());
    checkBlobParamTableRows(writtenRows, readRows);
}

TEST_F(ExplicitBlobParamTest, readWithCondition)
{
    BlobParamTable& blobParamTable = m_database->getBlobParamTable();

    BlobParamTableParameterProvider parameterProvider;
    vector_type<BlobParamTable::Row> writtenRows;
    fillBlobParamTableRows(writtenRows);
    blobParamTable.write(parameterProvider, writtenRows);

    const string_type condition = "name='Name1'";
    BlobParamTable::Reader reader = blobParamTable.createReader(parameterProvider, condition);

    ASSERT_TRUE(reader.hasNext());
    BlobParamTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());

    const size_t expectedRowNum = 1;
    checkBlobParamTableRow(writtenRows[expectedRowNum], readRow);
}

TEST_F(ExplicitBlobParamTest, update)
{
    BlobParamTable& blobParamTable = m_database->getBlobParamTable();

    BlobParamTableParameterProvider parameterProvider;
    vector_type<BlobParamTable::Row> writtenRows;
    fillBlobParamTableRows(writtenRows);
    blobParamTable.write(parameterProvider, writtenRows);

    const uint64_t updateRowId = 3;
    BlobParamTable::Row updateRow;
    fillBlobParamTableRow(updateRow, updateRowId, "UpdatedName");
    const string_type updateCondition = "id=" + zserio::toString<allocator_type>(updateRowId);
    blobParamTable.update(parameterProvider, updateRow, updateCondition);

    BlobParamTable::Reader reader = blobParamTable.createReader(parameterProvider, updateCondition);
    ASSERT_TRUE(reader.hasNext());
    BlobParamTable::Row readRow = reader.next();
    ASSERT_FALSE(reader.hasNext());
    checkBlobParamTableRow(updateRow, readRow);
}

} // namespace explicit_blob_param
} // namespace explicit_parameters
