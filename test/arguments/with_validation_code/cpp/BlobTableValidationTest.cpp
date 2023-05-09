#include <memory>

#include "gtest/gtest.h"

#include "zserio/FloatUtil.h"
#include "zserio/SqliteFinalizer.h"

#include "with_validation_code/blob_table_validation/BlobTableValidationDb.h"
#include "test_utils/ValidationObservers.h"

using namespace test_utils;

namespace with_validation_code
{

namespace blob_table_validation
{

class BlobTableValidationTest : public ::testing::Test
{
public:
    BlobTableValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new BlobTableValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    void populateDb(zserio::SqliteConnection& connection)
    {
        const bool wasTransactionStarted = connection.startTransaction();

        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                "INSERT INTO blobTable(id, blob, nullableBlob) VALUES (?, ?, ?)"));

        int argIdx = 1;
        sqlite3_bind_int(statement.get(), argIdx++, ROW_ID);

        zserio::BitBuffer bitBuffer(128);
        zserio::BitStreamWriter writer(bitBuffer);
        writer.writeBool(HAS_NAN);
        writer.writeFloat16(zserio::convertUInt32ToFloat(NONE_STANDARD_NAN_VALUE));
        writer.writeBits(ALIGNED_7_BITS, 7);
        writer.writeBits(END_VALUE, 8);
        sqlite3_bind_blob(statement.get(), argIdx++, bitBuffer.getBuffer(),
                static_cast<int>((writer.getBitPosition() + 7) / 8), SQLITE_TRANSIENT);

        sqlite3_bind_null(statement.get(), argIdx++);

        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));

        connection.endTransaction(wasTransactionStarted);
    }

    std::unique_ptr<BlobTableValidationDb> m_database;

private:
    static const char* const DB_FILE_NAME;

    static constexpr uint32_t ROW_ID = 0;
    static constexpr bool HAS_NAN = true;
    static constexpr uint32_t NONE_STANDARD_NAN_VALUE = UINT32_C(0xFFFFFFFF);
    static constexpr uint8_t ALIGNED_7_BITS = 0x7F;
    static constexpr int8_t END_VALUE = 0x23;
};

const char* const BlobTableValidationTest::DB_FILE_NAME =
        "arguments/with_validation_code/blob_table_validation_test.sqlite";

constexpr uint32_t BlobTableValidationTest::ROW_ID;
constexpr bool BlobTableValidationTest::HAS_NAN;
constexpr uint32_t  BlobTableValidationTest::NONE_STANDARD_NAN_VALUE;
constexpr uint8_t  BlobTableValidationTest::ALIGNED_7_BITS;
constexpr int8_t  BlobTableValidationTest::END_VALUE;

TEST_F(BlobTableValidationTest, validate)
{
    populateDb(m_database->connection());

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(0, validationObserver.getErrors().size()) << validationObserver.getErrorsString();

    const auto& tableName = BlobTableValidationDb::tableNames()[0];
    ASSERT_EQ(1, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTableRows(tableName));
}

} // namespace blob_table_validation

} // namespace with_validation_code
