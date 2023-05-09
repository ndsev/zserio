#include <memory>

#include "gtest/gtest.h"

#include "with_validation_code/hidden_column_table_validation/HiddenColumnTableValidationDb.h"
#include "test_utils/ValidationObservers.h"

#include "zserio/SqliteFinalizer.h"

using namespace test_utils;

namespace with_validation_code
{

namespace hidden_column_table_validation
{

class HiddenColumnTableValidationTest : public ::testing::Test
{
public:
    HiddenColumnTableValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new HiddenColumnTableValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    void populateDb(zserio::SqliteConnection& connection)
    {
        const bool wasTransactionStarted = connection.startTransaction();

        for (uint16_t i = 0; i < NUM_TABLE_ROWS; i++)
        {
            insertRow(connection, static_cast<int64_t>(i), i, "Some substitution string",
                    "Some search tags string", i);
        }

        connection.endTransaction(wasTransactionStarted);
    }

    std::unique_ptr<HiddenColumnTableValidationDb> m_database;

    static constexpr size_t NUM_TABLE_ROWS = 16;

private:
    void insertRow(zserio::SqliteConnection& connection, int64_t docId, uint16_t languageCode,
            const std::string& substitutionId, const std::string& searchTags, uint32_t frequency)
    {
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                "INSERT INTO HiddenColumnTable(docId, languageCode, substitutionId, searchTags, frequency) "
                "VALUES (?, ?, ?, ?, ?)"));

        int argIdx = 1;
        sqlite3_bind_int64(statement.get(), argIdx++, docId);
        sqlite3_bind_int(statement.get(), argIdx++, languageCode);
        sqlite3_bind_text(statement.get(), argIdx++, substitutionId.c_str(),
                static_cast<int>(substitutionId.size()), SQLITE_TRANSIENT);
        sqlite3_bind_text(statement.get(), argIdx++, searchTags.c_str(), static_cast<int>(searchTags.size()),
                SQLITE_TRANSIENT);
        sqlite3_bind_int(statement.get(), argIdx++, static_cast<int>(frequency));

        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));
    }

    static const char* const DB_FILE_NAME;
};

constexpr size_t HiddenColumnTableValidationTest::NUM_TABLE_ROWS;

const char* const HiddenColumnTableValidationTest::DB_FILE_NAME =
        "arguments/with_validation_code/hidden_column_table_validation_test.sqlite";

TEST_F(HiddenColumnTableValidationTest, validate)
{
    populateDb(m_database->connection());

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(0, validationObserver.getErrors().size()) << validationObserver.getErrorsString();

    const auto& tableName = HiddenColumnTableValidationDb::tableNames()[0];
    ASSERT_EQ(NUM_TABLE_ROWS, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(NUM_TABLE_ROWS, validationObserver.getNumberOfValidatedTableRows(tableName));
}

} // namespace hidden_column_table_validation

} // namespace with_validation_code
