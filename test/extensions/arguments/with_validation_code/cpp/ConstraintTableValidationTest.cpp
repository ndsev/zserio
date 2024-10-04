#include <memory>

#include "gtest/gtest.h"
#include "test_utils/ValidationObservers.h"
#include "with_validation_code/constraint_table_validation/ConstraintTableValidationDb.h"
#include "zserio/FloatUtil.h"
#include "zserio/SqliteFinalizer.h"

using namespace test_utils;

namespace with_validation_code
{

namespace constraint_table_validation
{

class ConstraintTableValidationTest : public ::testing::Test
{
public:
    ConstraintTableValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new ConstraintTableValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    void populateDb(zserio::SqliteConnection& connection)
    {
        const bool wasTransactionStarted = connection.startTransaction();

        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(
                connection.prepareStatement("INSERT INTO constraintTable(id, blob) VALUES (?, ?)"));

        int argIdx = 1;
        sqlite3_bind_int(statement.get(), argIdx++, ROW_ID);

        zserio::BitBuffer bitBuffer(128);
        zserio::BitStreamWriter writer(bitBuffer);
        writer.writeBits(VALUE, 8);
        sqlite3_bind_blob(statement.get(), argIdx++, bitBuffer.getBuffer(),
                static_cast<int>((writer.getBitPosition() + 7) / 8), SQLITE_TRANSIENT);

        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));

        connection.endTransaction(wasTransactionStarted);
    }

    std::unique_ptr<ConstraintTableValidationDb> m_database;

private:
    static const char* const DB_FILE_NAME;

    static constexpr uint32_t ROW_ID = 0;
    static constexpr uint8_t VALUE = 0x7F;
};

const char* const ConstraintTableValidationTest::DB_FILE_NAME =
        "arguments/with_validation_code/constraint_table_validation_test.sqlite";

constexpr uint32_t ConstraintTableValidationTest::ROW_ID;
constexpr uint8_t ConstraintTableValidationTest::VALUE;

TEST_F(ConstraintTableValidationTest, validate)
{
    populateDb(m_database->connection());

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(1, validationObserver.getErrors().size()) << validationObserver.getErrorsString();
    ASSERT_EQ("constraintTable, blob, [0], 6, Read: Constraint violated at Blob.value!\n",
            validationObserver.getErrorsString());

    const auto& tableName = ConstraintTableValidationDb::tableNames()[0];
    ASSERT_EQ(1, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTableRows(tableName));
}

} // namespace constraint_table_validation

} // namespace with_validation_code
