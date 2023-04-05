#include <memory>

#include "gtest/gtest.h"

#include "with_validation_code/simple_table_validation/SimpleTableValidationDb.h"
#include "test_utils/ValidationObservers.h"

#include "zserio/SqliteFinalizer.h"

using namespace test_utils;

namespace with_validation_code
{

namespace simple_table_validation
{

class SimpleTableValidationTest : public ::testing::Test
{
public:
    SimpleTableValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new SimpleTableValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    class SimpleTableParameterProvider : public SimpleTable::IParameterProvider
    {
    public:
        uint32_t getLocalCount1(SimpleTable::Row&) override
        {
            ++m_localCount1CallCount;
            return SIMPLE_TABLE_LOCAL_COUNT;
        }

        size_t getLocalCount1CallCount()
        {
            return m_localCount1CallCount;
        }

    private:
        size_t m_localCount1CallCount = 0;
    };

    class TestParameterProvider : public SimpleTableValidationDb::IParameterProvider
    {
    public:
        SimpleTable::IParameterProvider& getSimpleTableParameterProvider() override
        {
            return simpleTableParameterProvider;
        }

        size_t getLocalCount1CallCount()
        {
            return simpleTableParameterProvider.getLocalCount1CallCount();
        }

    private:
        SimpleTableParameterProvider simpleTableParameterProvider;
    };

    void populateDb(zserio::SqliteConnection& connection, bool wrongOffset)
    {
        const bool wasTransactionStarted = connection.startTransaction();

        for (uint32_t id = 0; id < ENTRY_COUNT; id++)
        {
            // make the first entry have wrong offset - if requested by caller
            // first is used to check that the validation continues past the erroneous blob
            insertTestRow(connection, id, wrongOffset && id == 0);
        }

        connection.endTransaction(wasTransactionStarted);
    }

    static const char* DB_FILE_NAME;

    static constexpr uint64_t ENTRY_COUNT = 5;
    static constexpr uint32_t SIMPLE_TABLE_LOCAL_COUNT = 10;

    static constexpr uint32_t FIELD_BIT5_OUT_OF_RANGE_ROW_ID = 0;
    static constexpr uint32_t FIELD_DYNAMIC_BIT_OUT_OF_RANGE_ROW_ID = 1;
    static constexpr uint32_t FIELD_VARINT16_OUT_OF_RANGE_ROW_ID = 2;
    static constexpr uint32_t FIELD_BITMASK_OUT_OF_RANGE_ROW_ID = 3;
    static constexpr uint32_t FIELD_ENUM_RED_ROW_ID = 1;
    static constexpr uint32_t FIELD_ENUM_NULL_ROW_ID = 2;

    std::unique_ptr<SimpleTableValidationDb> m_database;

private:
    void insertTestRow(zserio::SqliteConnection& connection, uint32_t id, bool wrongOffset)
    {
        uint8_t fieldBit5 = static_cast<uint8_t>(id + 1);
        uint32_t fieldDynamicBit = id;
        int16_t fieldVarInt16 = static_cast<int16_t>(id);
        std::string fieldString = "Test " + std::to_string(id);
        RootStruct fieldBlob = createTestRootStruct(SIMPLE_TABLE_LOCAL_COUNT, id);
        TestEnum fieldEnum = (id == FIELD_ENUM_RED_ROW_ID) ? TestEnum::RED : TestEnum::BLUE;
        TestBitmask fieldBitmask = (id % 2 == 0) ? TestBitmask::Values::READ : TestBitmask::Values::WRITE;

        insertRow(connection, id, id == 0, fieldBit5, fieldDynamicBit, fieldVarInt16, fieldString, fieldBlob,
                wrongOffset, fieldEnum, fieldBitmask);
    }

    RootStruct createTestRootStruct(uint32_t count, uint32_t id)
    {
        RootStruct rootStruct;
        rootStruct.initialize(count);
        auto& filler = rootStruct.getFiller();
        filler.resize(rootStruct.getCount());
        for (size_t i = 0; i < filler.size(); ++i)
            filler[i] = static_cast<uint8_t>(id);
        rootStruct.initializeOffsets();

        return rootStruct;
    }

    void insertRow(zserio::SqliteConnection& connection, uint32_t id, bool fieldBool, uint8_t fieldBit5,
            uint32_t fieldDynamicBit, int16_t fieldVarInt16, const std::string& fieldString,
            RootStruct& rootStruct, bool wrongOffset, TestEnum fieldEnum, TestBitmask fieldBitmask)
    {
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                "INSERT INTO simpleTable (rowid, fieldBool, fieldBit5, fieldDynamicBit, fieldVarInt16, "
                        "fieldString, fieldBlob, fieldEnum, fieldBitmask) "
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"));

        int argIdx = 1;
        sqlite3_bind_int(statement.get(), argIdx++, static_cast<int>(id));
        sqlite3_bind_int(statement.get(), argIdx++, fieldBool ? 1 : 0);
        sqlite3_bind_int(statement.get(), argIdx++, fieldBit5);
        sqlite3_bind_int(statement.get(), argIdx++, static_cast<int>(fieldDynamicBit));
        sqlite3_bind_int(statement.get(), argIdx++, fieldVarInt16);
        sqlite3_bind_text(statement.get(), argIdx++, fieldString.c_str(), static_cast<int>(fieldString.size()),
                SQLITE_TRANSIENT);

        zserio::BitBuffer bitBuffer(rootStruct.bitSizeOf());
        zserio::BitStreamWriter writer(bitBuffer);
        rootStruct.write(writer);
        uint8_t* buffer = bitBuffer.getBuffer();
        if (wrongOffset)
            corruptOffsetInFieldBlob(buffer);
        sqlite3_bind_blob(statement.get(), argIdx++, buffer, static_cast<int>(bitBuffer.getByteSize()),
                SQLITE_TRANSIENT);

        if (id == FIELD_ENUM_NULL_ROW_ID)
            sqlite3_bind_null(statement.get(), argIdx++);
        else
            sqlite3_bind_int(statement.get(), argIdx++, static_cast<int>(zserio::enumToValue(fieldEnum)));

        sqlite3_bind_int(statement.get(), argIdx++, fieldBitmask.getValue());

        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));
    }

    void corruptOffsetInFieldBlob(uint8_t* buffer)
    {
        buffer[0] = 0xff;
    }
};

const char* SimpleTableValidationTest::DB_FILE_NAME =
        "arguments/with_validation_code/simple_table_validation_test.sqlite";
constexpr uint64_t SimpleTableValidationTest::ENTRY_COUNT;
constexpr uint32_t SimpleTableValidationTest::SIMPLE_TABLE_LOCAL_COUNT;
constexpr uint32_t SimpleTableValidationTest::FIELD_BIT5_OUT_OF_RANGE_ROW_ID;
constexpr uint32_t SimpleTableValidationTest::FIELD_DYNAMIC_BIT_OUT_OF_RANGE_ROW_ID;
constexpr uint32_t SimpleTableValidationTest::FIELD_VARINT16_OUT_OF_RANGE_ROW_ID;
constexpr uint32_t SimpleTableValidationTest::FIELD_BITMASK_OUT_OF_RANGE_ROW_ID;
constexpr uint32_t SimpleTableValidationTest::FIELD_ENUM_NULL_ROW_ID;
constexpr uint32_t SimpleTableValidationTest::FIELD_ENUM_RED_ROW_ID;

TEST_F(SimpleTableValidationTest, validate)
{
    populateDb(m_database->connection(), false);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    ASSERT_EQ(ENTRY_COUNT, parameterProvider.getLocalCount1CallCount());

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(SimpleTableValidationTest, validateBlobFailure)
{
    populateDb(m_database->connection(), true);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(1, errors.size()) << validationObserver.getErrorsString();

    const auto& error = errors.at(0);
    ASSERT_EQ("simpleTable", error.tableName);
    ASSERT_EQ("fieldBlob", error.fieldName);
    ASSERT_EQ(std::vector<std::string>{"0"}, error.primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::BLOB_PARSE_FAILED, error.errorType);
    ASSERT_EQ("Read: Wrong offset for field RootStruct.end: 14 != 4278190094!", error.message);
}

TEST_F(SimpleTableValidationTest, validateSingleTable)
{
    populateDb(m_database->connection(), false);

    SimpleTable& simpleTable = m_database->getSimpleTable();

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    bool continueValidation;
    const bool isValidated = simpleTable.validate(validationObserver,
            parameterProvider.getSimpleTableParameterProvider(), continueValidation);

    ASSERT_TRUE(isValidated);
    ASSERT_TRUE(continueValidation);
    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));
    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(SimpleTableValidationTest, validateExtraColumn)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    populateDb(connection, false);
    connection.executeUpdate("ALTER TABLE simpleTable ADD COLUMN extraColumn TEXT");
    connection.endTransaction(wasTransactionStarted);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows(tableName));

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(1, errors.size()) << validationObserver.getErrorsString();

    const auto& error = errors.at(0);
    ASSERT_EQ("simpleTable", error.tableName);
    ASSERT_EQ("extraColumn", error.fieldName);
    ASSERT_TRUE(error.primaryKeyValues.empty());
    ASSERT_EQ(zserio::IValidationObserver::COLUMN_SUPERFLUOUS, error.errorType);
    ASSERT_EQ("superfluous column simpleTable.extraColumn of type TEXT encountered", error.message);
}

TEST_F(SimpleTableValidationTest, validateMissingColumn)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    populateDb(connection, false);
    connection.executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, "
            "fieldBool INTEGER NOT NULL, fieldDynamicBit INTEGER NOT NULL, fieldVarInt16 INTEGER NOT NULL,"
            "fieldString TEXT NOT NULL, fieldBlob BLOB NOT NULL, fieldEnum INTEGER, "
            "fieldBitmask INTEGER NOT NULL)");
    connection.executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldDynamicBit, "
            "fieldVarInt16, fieldString, fieldBlob, fieldEnum, fieldBitmask FROM simpleTable");
    connection.executeUpdate("DROP TABLE simpleTable");
    connection.executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");
    connection.endTransaction(wasTransactionStarted);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows(tableName));

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(1, errors.size()) << validationObserver.getErrorsString();

    const auto& error = errors.at(0);
    ASSERT_EQ("simpleTable", error.tableName);
    ASSERT_EQ("fieldBit5", error.fieldName);
    ASSERT_TRUE(error.primaryKeyValues.empty());
    ASSERT_EQ(zserio::IValidationObserver::COLUMN_MISSING, error.errorType);
    ASSERT_EQ("column SimpleTable.fieldBit5 is missing", error.message);
}

TEST_F(SimpleTableValidationTest, validateWrongColumnType)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    populateDb(connection, false);
    connection.executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, "
            "fieldBool INTEGER NOT NULL, fieldBit5 TEXT, fieldDynamicBit INTEGER NOT NULL, "
            "fieldVarInt16 INTEGER NOT NULL, fieldString TEXT NOT NULL, fieldBlob BLOB NOT NULL, "
            "fieldEnum INTEGER, fieldBitmask INTEGER NOT NULL)");
    connection.executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldBit5, fieldDynamicBit, "
            "fieldVarInt16, fieldString, fieldBlob, fieldEnum, fieldBitmask FROM simpleTable");
    connection.executeUpdate("DROP TABLE simpleTable");
    connection.executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");
    connection.endTransaction(wasTransactionStarted);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows(tableName));

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(1, errors.size()) << validationObserver.getErrorsString();

    const auto& error = errors.at(0);
    ASSERT_EQ("simpleTable", error.tableName);
    ASSERT_EQ("fieldBit5", error.fieldName);
    ASSERT_TRUE(error.primaryKeyValues.empty());
    ASSERT_EQ(zserio::IValidationObserver::INVALID_COLUMN_TYPE, error.errorType);
    ASSERT_EQ("column SimpleTable.fieldBit5 has type 'TEXT' but 'INTEGER' is expected", error.message);
}

TEST_F(SimpleTableValidationTest, validateWrongColumnNotNullConstraint)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    populateDb(connection, false);
    connection.executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, "
            "fieldBool INTEGER NOT NULL, fieldBit5 INTEGER NOT NULL, fieldDynamicBit INTEGER NOT NULL, "
            "fieldVarInt16 INTEGER NOT NULL, fieldString TEXT NOT NULL, fieldBlob BLOB, "
            "fieldEnum INTEGER, fieldBitmask INTEGER NOT NULL)");
    connection.executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldBit5, fieldDynamicBit, "
            "fieldVarInt16, fieldString, fieldBlob, fieldEnum, fieldBitmask FROM simpleTable");
    connection.executeUpdate("DROP TABLE simpleTable");
    connection.executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");
    connection.endTransaction(wasTransactionStarted);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows(tableName));

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(2, errors.size()) << validationObserver.getErrorsString();

    const auto& error1 = errors.at(0);
    ASSERT_EQ("simpleTable", error1.tableName);
    ASSERT_EQ("fieldBit5", error1.fieldName);
    ASSERT_TRUE(error1.primaryKeyValues.empty());
    ASSERT_EQ(zserio::IValidationObserver::INVALID_COLUMN_CONSTRAINT, error1.errorType);
    ASSERT_EQ("column SimpleTable.fieldBit5 is NOT NULL-able, but the column is expected to be NULL-able",
            error1.message);

    const auto& error2 = errors.at(1);
    ASSERT_EQ("simpleTable", error2.tableName);
    ASSERT_EQ("fieldBlob", error2.fieldName);
    ASSERT_TRUE(error2.primaryKeyValues.empty());
    ASSERT_EQ(zserio::IValidationObserver::INVALID_COLUMN_CONSTRAINT, error2.errorType);
    ASSERT_EQ("column SimpleTable.fieldBlob is NULL-able, but the column is expected to be NOT NULL-able",
            error2.message);
}

TEST_F(SimpleTableValidationTest, validateWrongColumnPrimaryKeyConstraint)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    populateDb(connection, false);
    connection.executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER NOT NULL, "
            "fieldBool INTEGER NOT NULL, fieldBit5 INTEGER, fieldDynamicBit INTEGER NOT NULL, "
            "fieldVarInt16 INTEGER NOT NULL, fieldString TEXT NOT NULL, fieldBlob BLOB NOT NULL, "
            "fieldEnum INTEGER, fieldBitmask INTEGER NOT NULL)");
    connection.executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldBit5, fieldDynamicBit, "
            "fieldVarInt16, fieldString, fieldBlob, fieldEnum, fieldBitmask from simpleTable");
    connection.executeUpdate("DROP TABLE simpleTable");
    connection.executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");
    connection.endTransaction(wasTransactionStarted);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows(tableName));

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(1, errors.size()) << validationObserver.getErrorsString();

    const auto& error = errors.at(0);
    ASSERT_EQ("simpleTable", error.tableName);
    ASSERT_EQ("rowid", error.fieldName);
    ASSERT_TRUE(error.primaryKeyValues.empty());
    ASSERT_EQ(zserio::IValidationObserver::INVALID_COLUMN_CONSTRAINT, error.errorType);
    ASSERT_EQ("column SimpleTable.rowid is not primary key, but the column is expected to be primary key",
            error.message);
}

TEST_F(SimpleTableValidationTest, validateOutOfRange)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    populateDb(connection, false);
    connection.executeUpdate("UPDATE simpleTable SET fieldBit5 = 32 WHERE rowid = " +
            std::to_string(FIELD_BIT5_OUT_OF_RANGE_ROW_ID));
    connection.executeUpdate("UPDATE simpleTable SET fieldDynamicBit = 4 WHERE rowid = " +
            std::to_string(FIELD_DYNAMIC_BIT_OUT_OF_RANGE_ROW_ID));
    connection.executeUpdate("UPDATE simpleTable SET fieldVarInt16 = 16384 WHERE rowid = " +
            std::to_string(FIELD_VARINT16_OUT_OF_RANGE_ROW_ID));
    connection.executeUpdate("UPDATE simpleTable SET fieldBitmask = -1 WHERE rowid = " +
            std::to_string(FIELD_BITMASK_OUT_OF_RANGE_ROW_ID));
    connection.endTransaction(wasTransactionStarted);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(4, errors.size()) << validationObserver.getErrorsString();

    const auto& error1 = errors.at(0);
    ASSERT_EQ("simpleTable", error1.tableName);
    ASSERT_EQ("fieldBit5", error1.fieldName);
    ASSERT_EQ(std::vector<std::string>{std::to_string(FIELD_BIT5_OUT_OF_RANGE_ROW_ID)},
            error1.primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, error1.errorType);
    ASSERT_EQ("Value 32 of SimpleTable.fieldBit5 exceeds the range of 0..31", error1.message);

    const auto& error2 = errors.at(1);
    ASSERT_EQ("simpleTable", error2.tableName);
    ASSERT_EQ("fieldDynamicBit", error2.fieldName);
    ASSERT_EQ(std::vector<std::string>{std::to_string(FIELD_DYNAMIC_BIT_OUT_OF_RANGE_ROW_ID)},
            error2.primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, error2.errorType);
    ASSERT_EQ("Value 4 of SimpleTable.fieldDynamicBit exceeds the range of 0..3", error2.message);

    const auto& error3 = errors.at(2);
    ASSERT_EQ("simpleTable", error3.tableName);
    ASSERT_EQ("fieldVarInt16", error3.fieldName);
    ASSERT_EQ(std::vector<std::string>{std::to_string(FIELD_VARINT16_OUT_OF_RANGE_ROW_ID)},
            error3.primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, error3.errorType);
    ASSERT_EQ("Value 16384 of SimpleTable.fieldVarInt16 exceeds the range of -16383..16383", error3.message);

    const auto& error4 = errors.at(3);
    ASSERT_EQ("simpleTable", error4.tableName);
    ASSERT_EQ("fieldBitmask", error4.fieldName);
    ASSERT_EQ(std::vector<std::string>{std::to_string(FIELD_BITMASK_OUT_OF_RANGE_ROW_ID)},
            error4.primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, error4.errorType);
    ASSERT_EQ("Value 18446744073709551615 of SimpleTable.fieldBitmask exceeds the range of 0..255",
            error4.message);
}

TEST_F(SimpleTableValidationTest, validateInvalidEnumValue)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    populateDb(connection, false);
    // set fieldEnum to a value outside its Zserio type
    connection.executeUpdate("UPDATE simpleTable SET fieldEnum = 0 WHERE fieldEnum = " +
            std::to_string(zserio::enumToValue(TestEnum::RED)));
    connection.endTransaction(wasTransactionStarted);

    TestParameterProvider parameterProvider;
    ValidationObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);

    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    const auto& tableName = SimpleTableValidationDb::tableNames()[0];
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfTableRows(tableName));
    ASSERT_EQ(ENTRY_COUNT, validationObserver.getNumberOfValidatedTableRows(tableName));

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(1, errors.size()) << validationObserver.getErrorsString();

    const auto& error = errors.at(0);
    ASSERT_EQ("simpleTable", error.tableName);
    ASSERT_EQ("fieldEnum", error.fieldName);
    ASSERT_EQ(std::vector<std::string>{std::to_string(FIELD_ENUM_RED_ROW_ID)},
            error.primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::INVALID_VALUE, error.errorType);
    ASSERT_EQ("Enumeration value 0 of SimpleTable.fieldEnum is not valid!", error.message);
}

TEST_F(SimpleTableValidationTest, validateSkipTable)
{
    zserio::SqliteConnection& connection = m_database->connection();
    populateDb(connection, false);

    TestParameterProvider parameterProvider;
    class SkippingObserver : public ValidationObserver
    {
    public:
        bool beginTable(zserio::StringView tableName, size_t numberOfTableRows) override
        {
            ValidationObserver::beginTable(tableName, numberOfTableRows);
            return false;
        }
    };
    SkippingObserver validationObserver;
    m_database->validate(validationObserver, parameterProvider);
    ASSERT_EQ(1, validationObserver.getNumberOfTables());
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTables());

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

} // namespace simple_table_validation

} // namespace with_validation_code
