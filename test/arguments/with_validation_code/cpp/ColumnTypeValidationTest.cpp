#include <memory>

#include "gtest/gtest.h"
#include "test_utils/ValidationObservers.h"
#include "with_validation_code/column_type_validation/ColumnTypeDb.h"
#include "zserio/SerializeUtil.h"
#include "zserio/SqliteFinalizer.h"

using namespace test_utils;

namespace with_validation_code
{

namespace column_type_validation
{

class ColumnTypeValidationTest : public ::testing::Test
{
public:
    ColumnTypeValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new ColumnTypeDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    void populateDb()
    {
        std::vector<ColumnTypeTable::Row> rows(ENTRY_COUNT);
        for (size_t i = 1; i <= ENTRY_COUNT; ++i)
        {
            auto& row = rows.at(i - 1);
            row.setId(static_cast<int8_t>(i));
            row.setInt8Value(static_cast<int8_t>(-1 * static_cast<int8_t>(i)));
            row.setInt16Value(static_cast<int16_t>(-2 * static_cast<int8_t>(i)));
            row.setInt32Value(-3 * static_cast<int8_t>(i));
            row.setInt64Value(-4 * static_cast<int8_t>(i));
            row.setUint8Value(1 * static_cast<uint8_t>(i));
            row.setUint16Value(2 * static_cast<uint16_t>(i));
            row.setUint32Value(3 * static_cast<uint32_t>(i));
            row.setUint64Value(4 * i);
            row.setFloat16Value(1.1F * static_cast<float>(i));
            row.setFloat32Value(1.2F * static_cast<float>(i));
            row.setFloat64Value(1.3 * static_cast<double>(i));
            row.setStringValue("stringValue" + zserio::toString(i, m_database->get_allocator()));
        }

        m_database->getColumnTypeTable().write(rows);
    }

    void populateDbWithNullValues()
    {
        std::vector<ColumnTypeTable::Row> rows(ENTRY_COUNT);
        m_database->getColumnTypeTable().write(rows);
    }

    void executeUpdate(const char* sql)
    {
        m_database->connection().executeUpdate(sql);
    }

    static const char* const DB_FILE_NAME;

    static constexpr size_t ENTRY_COUNT = 2;

    std::unique_ptr<ColumnTypeDb> m_database;

private:
};

const char* const ColumnTypeValidationTest::DB_FILE_NAME =
        "arguments/with_validation_code/column_type_validation_test.sqlite";
constexpr size_t ColumnTypeValidationTest::ENTRY_COUNT;

TEST_F(ColumnTypeValidationTest, validate)
{
    populateDb();

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ColumnTypeValidationTest, validateNullValues)
{
    populateDb();

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ColumnTypeValidationTest, validateFloatWhileIntegerExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET int8Value = 1.3 WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.int8Value type check failed (REAL doesn't match to INTEGER)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateStringWhileIntegerExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET uint8Value = 'STRING' WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.uint8Value type check failed (TEXT doesn't match to INTEGER)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateNumericStringWhileIntegerExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET uint8Value = '13' WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
    // see https://sqlite.org/datatype3.html#affinity - will be converted to INTEGER
}

TEST_F(ColumnTypeValidationTest, validateEmptyStringWhileIntegerExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET int16Value = '' WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.int16Value type check failed (TEXT doesn't match to INTEGER)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateBlobWhileIntegerExpected)
{
    populateDb();

    std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> stmt(m_database->connection().prepareStatement(
            "UPDATE columnTypeTable SET int64Value = ? WHERE id = 2"));
    Blob blob(13);
    auto bitBuffer = zserio::serialize(blob);
    sqlite3_bind_blob(
            stmt.get(), 1, bitBuffer.getBuffer(), static_cast<int>(bitBuffer.getByteSize()), SQLITE_TRANSIENT);
    ASSERT_EQ(SQLITE_DONE, sqlite3_step(stmt.get()));

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"2"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.int64Value type check failed (BLOB doesn't match to INTEGER)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateIntegerWhileFloatExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET float16Value = 13 WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
    // see https://sqlite.org/datatype3.html#affinity - will be converted to REAL
}

TEST_F(ColumnTypeValidationTest, validateStringWhileFloatExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET float32Value = 'STRING' WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.float32Value type check failed (TEXT doesn't match to REAL)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateNumericStringWhileFloatExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET float64Value = '1.3' WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
    // see https://sqlite.org/datatype3.html#affinity - will be converted to REAL
}

TEST_F(ColumnTypeValidationTest, validateEmptyStringWhileFloatExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET float16Value = 'STRING' WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.float16Value type check failed (TEXT doesn't match to REAL)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateBlobWhileFloatExpected)
{
    populateDb();

    std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> stmt(m_database->connection().prepareStatement(
            "UPDATE columnTypeTable SET float16Value = ? WHERE id = 2"));
    Blob blob(13);
    auto bitBuffer = zserio::serialize(blob);
    sqlite3_bind_blob(
            stmt.get(), 1, bitBuffer.getBuffer(), static_cast<int>(bitBuffer.getByteSize()), SQLITE_TRANSIENT);
    ASSERT_EQ(SQLITE_DONE, sqlite3_step(stmt.get()));

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"2"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.float16Value type check failed (BLOB doesn't match to REAL)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateIntegerWhileStringExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET stringValue = 3 WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
    // see https://sqlite.org/datatype3.html#affinity - will be converted to TEXT
}

TEST_F(ColumnTypeValidationTest, validateFlaotWhileStringExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET stringValue = 1.3 WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
    // see https://sqlite.org/datatype3.html#affinity - will be converted to TEXT
}

TEST_F(ColumnTypeValidationTest, validateBlobWhileStringExpected)
{
    populateDb();

    std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> stmt(m_database->connection().prepareStatement(
            "UPDATE columnTypeTable SET stringValue = ? WHERE id = 2"));
    Blob blob(13);
    auto bitBuffer = zserio::serialize(blob);
    sqlite3_bind_blob(
            stmt.get(), 1, bitBuffer.getBuffer(), static_cast<int>(bitBuffer.getByteSize()), SQLITE_TRANSIENT);
    ASSERT_EQ(SQLITE_DONE, sqlite3_step(stmt.get()));

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"2"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.stringValue type check failed (BLOB doesn't match to TEXT)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateIntegerWhileBlobExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET blobValue = 13 WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ("Column ColumnTypeTable.blobValue type check failed (INTEGER doesn't match to BLOB)!",
            error.message);
}

TEST_F(ColumnTypeValidationTest, validateFloatWhileBlobExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET blobValue = 1.3 WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ(
            "Column ColumnTypeTable.blobValue type check failed (REAL doesn't match to BLOB)!", error.message);
}

TEST_F(ColumnTypeValidationTest, validateStringWhileBlobExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET blobValue = 'STRING' WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ(
            "Column ColumnTypeTable.blobValue type check failed (TEXT doesn't match to BLOB)!", error.message);
}

TEST_F(ColumnTypeValidationTest, validateEmptyStringWhileBlobExpected)
{
    populateDb();

    executeUpdate("UPDATE columnTypeTable SET blobValue = '' WHERE id = 1");

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = ColumnTypeDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(1, validationObserver.getErrors().size());
    const auto& error = validationObserver.getErrors()[0];
    ASSERT_EQ(std::vector<std::string>{"1"}, error.primaryKeyValues);
    ASSERT_EQ(
            "Column ColumnTypeTable.blobValue type check failed (TEXT doesn't match to BLOB)!", error.message);
}

} // namespace column_type_validation

} // namespace with_validation_code
