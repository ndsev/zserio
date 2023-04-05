#include <memory>

#include "gtest/gtest.h"

#include "with_validation_code/virtual_table_validation/VirtualTableValidationDb.h"
#include "test_utils/ValidationObservers.h"

using namespace test_utils;

namespace with_validation_code
{

namespace virtual_table_validation
{

class VirtualTableValidationTest : public ::testing::Test
{
public:
    VirtualTableValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new VirtualTableValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    void populateDb(zserio::SqliteConnection& connection)
    {
        const bool wasTransactionStarted = connection.startTransaction();

        for (int16_t id = 0; id < ENTRY_COUNT; id++)
            insertTestTableRow(connection, id);

        connection.endTransaction(wasTransactionStarted);
    }

    static constexpr int16_t ENTRY_COUNT = 5;

    std::unique_ptr<VirtualTableValidationDb> m_database;

private:
    void insertTestTableRow(zserio::SqliteConnection& connection, int16_t id)
    {
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                "INSERT INTO TestTable (docId, text, anotherId) VALUES (?, ?, ?)"));

        int argIdx = 1;
        sqlite3_bind_int(statement.get(), argIdx++, static_cast<int>(id));
        const std::string text("Test " + std::to_string(id));
        sqlite3_bind_text(statement.get(), argIdx++, text.c_str(), static_cast<int>(text.size()),
                SQLITE_TRANSIENT);
        if (id % 2 == 0)
            sqlite3_bind_int(statement.get(), argIdx++, id / 2);
        else
            sqlite3_bind_null(statement.get(), argIdx++);
        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));
    }

    static const char* DB_FILE_NAME;
};

const char* VirtualTableValidationTest::DB_FILE_NAME =
        "arguments/with_validation_code/virtual_table_validation_test.sqlite";

constexpr int16_t VirtualTableValidationTest::ENTRY_COUNT;

TEST_F(VirtualTableValidationTest, validate)
{
    populateDb(m_database->connection());

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(2, validationObserver.getNumberOfTables());
    ASSERT_EQ(2, validationObserver.getNumberOfValidatedTables());
    ASSERT_EQ(0, validationObserver.getErrors().size()) << validationObserver.getErrorsString();

    const auto& tableName1 = VirtualTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName1));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName1));

    const auto& tableName2 = VirtualTableValidationDb::tableNames()[1];
    ASSERT_EQ(0, validationObserver.getNumberOfTableRows(tableName2));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows(tableName2));
}

} // namespace virtual_table_validation

} // namespace with_validation_code
