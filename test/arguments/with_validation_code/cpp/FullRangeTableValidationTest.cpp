#include <memory>

#include "gtest/gtest.h"

#include "with_validation_code/full_range_table_validation/FullRangeTableValidationDb.h"
#include "ValidationObservers.h"

#include "zserio/SqliteFinalizer.h"

using namespace utils;

namespace with_validation_code
{

namespace full_range_table_validation
{

class FullRangeTableValidationTest : public ::testing::Test
{
public:
    FullRangeTableValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new FullRangeTableValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    void populateDb(zserio::SqliteConnection& connection)
    {
        const bool wasTransactionStarted = connection.startTransaction();

        for (size_t i = 0; i < NUM_TABLE_ROWS; i++)
        {
            insertRow(connection, static_cast<int64_t>(i), static_cast<uint64_t>(i), static_cast<int64_t>(i),
                    static_cast<uint64_t>(i), "This is very long long long message");
        }

        connection.endTransaction(wasTransactionStarted);
    }

    std::unique_ptr<FullRangeTableValidationDb> m_database;

    static constexpr size_t NUM_TABLE_ROWS = 16;

private:
    void insertRow(zserio::SqliteConnection& connection, int64_t fullSigned, uint64_t fullUnsigned,
            int64_t fullVarSigned, uint64_t fullVarUnsigned, const std::string& message)
    {
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                "INSERT INTO FullRangeTable(fullSigned, fullUnsigned, fullVarSigned, fullVarUnsigned, message) "
                "VALUES (?, ?, ?, ?, ?)"));

        int argIdx = 1;
        sqlite3_bind_int64(statement.get(), argIdx++, fullSigned);
        sqlite3_bind_int64(statement.get(), argIdx++, static_cast<int64_t>(fullUnsigned));
        sqlite3_bind_int64(statement.get(), argIdx++, fullVarSigned);
        sqlite3_bind_int64(statement.get(), argIdx++, static_cast<int64_t>(fullVarUnsigned));
        sqlite3_bind_text(statement.get(), argIdx++, message.c_str(), static_cast<int>(message.size()),
                SQLITE_TRANSIENT);

        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));
    }

    static const char DB_FILE_NAME[];
};

constexpr size_t FullRangeTableValidationTest::NUM_TABLE_ROWS;

const char FullRangeTableValidationTest::DB_FILE_NAME[] =
        "arguments/with_validation_code/full_range_table_validation_test.sqlite";

TEST_F(FullRangeTableValidationTest, validate)
{
    populateDb(m_database->connection());

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(0, validationObserver.getErrors().size()) << validationObserver.getErrorsString();

    const auto& tableName = FullRangeTableValidationDb::tableNames()[0];
    ASSERT_EQ(NUM_TABLE_ROWS, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(NUM_TABLE_ROWS, validationObserver.getNumberOfValidatedTableRows(tableName));
}

} // namespace full_range_table_validation

} // namespace with_validation_code
