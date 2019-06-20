#include <cstdio>
#include <vector>
#include <string>

#include "gtest/gtest.h"

#include "zserio/StringConvertUtil.h"

#include "explicit_parameters/ExplicitParametersDb.h"

namespace explicit_parameters
{
namespace explicit_blob_param
{

class ExplicitBlobParamTest : public ::testing::Test
{
public:
    ExplicitBlobParamTest()
    {
        std::remove(DB_FILE_NAME);

        m_database = new ExplicitParametersDb(DB_FILE_NAME);
        m_database->createSchema();
        m_header.setCount(BLOB_PARAM_TABLE_HEADER_COUNT);
        m_blob.setCount(BLOB_PARAM_TABLE_BLOB_COUNT);
    }

    ~ExplicitBlobParamTest()
    {
        delete m_database;
    }

protected:
    void fillBlobParamTableRow(BlobParamTableRow& row, uint32_t id, const std::string& name)
    {
        row.setId(id);
        row.setName(name);

        TestBlob testBlob1;
        testBlob1.initialize(m_header);
        zserio::UInt8Array& values1 = testBlob1.getValues();
        for (uint32_t i = 0; i < BLOB_PARAM_TABLE_HEADER_COUNT; ++i)
            values1.push_back(static_cast<uint8_t>(id));
        row.setBlob1(testBlob1);

        TestBlob testBlob2;
        testBlob2.initialize(m_blob);
        zserio::UInt8Array& values2 = testBlob2.getValues();
        for (uint32_t i = 0; i < BLOB_PARAM_TABLE_BLOB_COUNT; ++i)
            values2.push_back(static_cast<uint8_t>(id + 1));
        row.setBlob2(testBlob2);

        TestBlob testBlob3;
        testBlob3.initialize(m_header);
        zserio::UInt8Array& values3 = testBlob3.getValues();
        for (uint32_t i = 0; i < BLOB_PARAM_TABLE_HEADER_COUNT; ++i)
            values3.push_back(static_cast<uint8_t>(id + 2));
        row.setBlob3(testBlob3);
    }

    void fillBlobParamTableRows(std::vector<BlobParamTableRow>& rows)
    {
        rows.clear();
        rows.resize(NUM_BLOB_PARAM_TABLE_ROWS);
        for (uint32_t id = 0; id < NUM_BLOB_PARAM_TABLE_ROWS; ++id)
        {
            const std::string name = "Name" + zserio::convertToString(id);
            fillBlobParamTableRow(rows[id], id, name);
        }
    }

    static void checkBlobParamTableRow(const BlobParamTableRow& row1, const BlobParamTableRow& row2)
    {
        ASSERT_EQ(row1.getId(), row2.getId());
        ASSERT_EQ(row1.getName(), row2.getName());
        ASSERT_EQ(row1.getBlob1(), row2.getBlob1());
        ASSERT_EQ(row1.getBlob2(), row2.getBlob2());
        ASSERT_EQ(row1.getBlob3(), row2.getBlob3());

        // check reused explicit header parameter
        ASSERT_EQ(row2.getBlob1().getBlob(), row2.getBlob3().getBlob());
        // check that even address of the reused explicit header parameter is the same!
        ASSERT_EQ(&row2.getBlob1().getBlob(), &row2.getBlob3().getBlob());
    }

    static void checkBlobParamTableRows(const std::vector<BlobParamTableRow>& rows1,
            const std::vector<BlobParamTableRow>& rows2)
    {
        ASSERT_EQ(rows1.size(), rows2.size());
        for (size_t i = 0; i < rows1.size(); ++i)
            checkBlobParamTableRow(rows1[i], rows2[i]);
    }

    class BlobParamTableParameterProvider : public BlobParamTable::IParameterProvider
    {
    public:
        BlobParamTableParameterProvider()
        {
            m_header.setCount(BLOB_PARAM_TABLE_HEADER_COUNT);
            m_blob.setCount(BLOB_PARAM_TABLE_BLOB_COUNT);
        }

        virtual Header& getHeader(sqlite3_stmt&)
        {
            return m_header;
        }

        virtual Header& getBlob(sqlite3_stmt&)
        {
            return m_blob;
        }

    private:
        Header m_header;
        Header m_blob;
    };

    ExplicitParametersDb* m_database;
    Header m_header;
    Header m_blob;

    static const char DB_FILE_NAME[];

    static const uint32_t NUM_BLOB_PARAM_TABLE_ROWS;
    static const uint32_t BLOB_PARAM_TABLE_HEADER_COUNT;
    static const uint32_t BLOB_PARAM_TABLE_BLOB_COUNT;
};

const char ExplicitBlobParamTest::DB_FILE_NAME[] = "explicit_blob_param_test.sqlite";

const uint32_t ExplicitBlobParamTest::NUM_BLOB_PARAM_TABLE_ROWS = 5;
const uint32_t ExplicitBlobParamTest::BLOB_PARAM_TABLE_HEADER_COUNT = 10;
const uint32_t ExplicitBlobParamTest::BLOB_PARAM_TABLE_BLOB_COUNT = 11;

TEST_F(ExplicitBlobParamTest, readWithoutCondition)
{
    BlobParamTable& blobParamTable = m_database->getBlobParamTable();

    std::vector<BlobParamTableRow> writtenRows;
    fillBlobParamTableRows(writtenRows);
    blobParamTable.write(writtenRows);

    BlobParamTableParameterProvider parameterProvider;
    std::vector<BlobParamTableRow> readRows;
    blobParamTable.read(parameterProvider, readRows);
    checkBlobParamTableRows(writtenRows, readRows);
}

TEST_F(ExplicitBlobParamTest, readWithCondition)
{
    BlobParamTable& blobParamTable = m_database->getBlobParamTable();

    std::vector<BlobParamTableRow> writtenRows;
    fillBlobParamTableRows(writtenRows);
    blobParamTable.write(writtenRows);

    BlobParamTableParameterProvider parameterProvider;
    const std::string condition = "name='Name1'";
    std::vector<BlobParamTableRow> readRows;
    blobParamTable.read(parameterProvider, condition, readRows);
    ASSERT_EQ(1, readRows.size());

    const size_t expectedRowNum = 1;
    checkBlobParamTableRow(writtenRows[expectedRowNum], readRows[0]);
}

TEST_F(ExplicitBlobParamTest, update)
{
    BlobParamTable& blobParamTable = m_database->getBlobParamTable();

    std::vector<BlobParamTableRow> writtenRows;
    fillBlobParamTableRows(writtenRows);
    blobParamTable.write(writtenRows);

    const uint64_t updateRowId = 3;
    BlobParamTableRow updateRow;
    fillBlobParamTableRow(updateRow, updateRowId, "UpdatedName");
    const std::string updateCondition = "id=" + zserio::convertToString(updateRowId);
    blobParamTable.update(updateRow, updateCondition);

    BlobParamTableParameterProvider parameterProvider;
    std::vector<BlobParamTableRow> readRows;
    blobParamTable.read(parameterProvider, updateCondition, readRows);
    ASSERT_EQ(1, readRows.size());

    checkBlobParamTableRow(updateRow, readRows[0]);
}

} // namespace explicit_blob_param
} // namespace explicit_parameters
