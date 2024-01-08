#include <memory>

#include "gtest/gtest.h"
#include "test_utils/ValidationObservers.h"
#include "with_validation_code/depend_table_validation/DependTableValidationDb.h"
#include "zserio/FloatUtil.h"
#include "zserio/SqliteFinalizer.h"

using namespace test_utils;

namespace with_validation_code
{

namespace depend_table_validation
{

class DependTableValidationTest : public ::testing::Test
{
public:
    DependTableValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new DependTableValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    void populateDb(zserio::SqliteConnection& connection)
    {
        const bool wasTransactionStarted = connection.startTransaction();

        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                "INSERT INTO dependTable(id, numBits, value, size, blob) VALUES (?, ?, ?, ?, ?)"));

        int argIdx = 1;
        sqlite3_bind_int(statement.get(), argIdx++, 0);
        sqlite3_bind_null(statement.get(), argIdx++); // numBits null
        sqlite3_bind_int(statement.get(), argIdx++, 0x0F); // value, but numBits null -> exception
        sqlite3_bind_null(statement.get(), argIdx++); // size null
        sqlite3_bind_null(statement.get(), argIdx++); // blob null
        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));

        ASSERT_EQ(SQLITE_OK, sqlite3_reset(statement.get()));
        ASSERT_EQ(SQLITE_OK, sqlite3_clear_bindings(statement.get()));

        argIdx = 1;
        sqlite3_bind_int(statement.get(), argIdx++, 1);
        sqlite3_bind_int(statement.get(), argIdx++, 7); // numBits
        sqlite3_bind_int(statement.get(), argIdx++, 0x0F); // value
        sqlite3_bind_null(statement.get(), argIdx++); // size is null
        zserio::BitBuffer bitBuffer(32);
        zserio::BitStreamWriter writer(bitBuffer);
        writer.writeBits(1, 32);
        sqlite3_bind_blob(statement.get(), argIdx++, bitBuffer.getBuffer(),
                static_cast<int>((writer.getBitPosition() + 7) / 8), SQLITE_TRANSIENT); // array[0] = 0
        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));

        connection.endTransaction(wasTransactionStarted);
    }

    std::unique_ptr<DependTableValidationDb> m_database;

private:
    static const char* const DB_FILE_NAME;
};

const char* const DependTableValidationTest::DB_FILE_NAME =
        "arguments/with_validation_code/depend_table_validation_test.sqlite";

TEST_F(DependTableValidationTest, validate)
{
    populateDb(m_database->connection());

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(2, errors.size()) << validationObserver.getErrorsString();

    ASSERT_EQ("dependTable", errors.at(0).tableName);
    ASSERT_EQ("value", errors.at(0).fieldName);
    ASSERT_EQ(std::vector<std::string>{"0"}, errors.at(0).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::INVALID_VALUE, errors.at(0).errorType);
    ASSERT_EQ("Trying to access value of non-present optional field!", errors.at(0).message);

    ASSERT_EQ("dependTable", errors.at(1).tableName);
    ASSERT_EQ("blob", errors.at(1).fieldName);
    ASSERT_EQ(std::vector<std::string>{"1"}, errors.at(1).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::BLOB_PARSE_FAILED, errors.at(1).errorType);
    ASSERT_EQ("Trying to access value of non-present optional field!", errors.at(1).message);

    const auto& tableName = DependTableValidationDb::tableNames()[0];
    ASSERT_EQ(2, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(2, validationObserver.getNumberOfValidatedTableRows(tableName));
}

} // namespace depend_table_validation

} // namespace with_validation_code
