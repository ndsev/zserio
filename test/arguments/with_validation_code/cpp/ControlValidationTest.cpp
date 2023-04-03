#include <memory>
#include <map>

#include "gtest/gtest.h"

#include "zserio/FloatUtil.h"
#include "zserio/SqliteFinalizer.h"
#include "zserio/StringConvertUtil.h"

#include "with_validation_code/control_validation/ControlValidationDb.h"
#include "test_utils/ValidationObservers.h"

using namespace test_utils;

namespace with_validation_code
{

namespace control_validation
{

class ControlValidationTest : public ::testing::Test
{
public:
    ControlValidationTest()
    {
        std::remove(DB_FILE_NAME);

        m_database.reset(new ControlValidationDb(DB_FILE_NAME));
        m_database->createSchema();
    }

protected:
    void populateDb(zserio::SqliteConnection& connection)
    {
        const bool wasTransactionStarted = connection.startTransaction();

        {
            std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                    "INSERT INTO table1(id, field) VALUES (?, ?)"));
            insertRows(statement.get(), TABLE1_NUM_ROWS);
        }

        {
            std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                    "INSERT INTO table2(id, blob) VALUES (?, ?)"));
            insertRowsWithBlob(statement.get(), TABLE2_NUM_ROWS);
        }

        {
            std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                    "INSERT INTO table3(id, field) VALUES (?, ?)"));
            insertRows(statement.get(), TABLE3_NUM_ROWS);
        }

        connection.endTransaction(wasTransactionStarted);
    }

    void insertRows(sqlite3_stmt* statement, uint32_t numRows)
    {
        for (uint32_t id = 0; id < numRows; ++id)
        {
            int argIdx = 1;
            sqlite3_bind_int(statement, argIdx++, static_cast<int>(id));
            sqlite3_bind_int(statement, argIdx++, static_cast<int>(id * numRows));

            ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement));

            sqlite3_reset(statement);
        }
    }

    void insertRowsWithBlob(sqlite3_stmt* statement, uint32_t numRows)
    {
        for (uint32_t id = 0; id < numRows; ++id)
        {
            int argIdx = 1;
            sqlite3_bind_int(statement, argIdx++, static_cast<int>(id));

            Blob blob(static_cast<int32_t>(id * numRows)); // field ctor
            zserio::BitBuffer bitBuffer(blob.bitSizeOf());
            zserio::BitStreamWriter writer(bitBuffer);
            blob.write(writer);
            sqlite3_bind_blob(statement, argIdx++, bitBuffer.getBuffer(),
                    static_cast<int>(bitBuffer.getByteSize()), SQLITE_TRANSIENT);

            ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement));

            sqlite3_reset(statement);
        }
    }

    void writeInvalidBlobToTable2(zserio::SqliteConnection& connection, uint32_t id)
    {
        std::unique_ptr<sqlite3_stmt, zserio::SqliteFinalizer> statement(connection.prepareStatement(
                "UPDATE table2 SET blob = ? where id = ?"));

        zserio::BitBuffer bitBuffer(8);
        zserio::BitStreamWriter writer(bitBuffer);
        writer.writeBits(0xff, 8);
        sqlite3_bind_blob(statement.get(), 1, bitBuffer.getBuffer(), static_cast<int>(bitBuffer.getByteSize()),
                SQLITE_TRANSIENT);
        sqlite3_bind_int(statement.get(), 2, static_cast<int>(id));

        ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));
    }

    std::unique_ptr<ControlValidationDb> m_database;

    static constexpr uint32_t TABLE1_NUM_ROWS = 13;
    static constexpr uint32_t TABLE2_NUM_ROWS = 14;
    static constexpr uint32_t TABLE3_NUM_ROWS = 15;

    class SkipTableObserver : public ValidationObserver
    {
    public:
        explicit SkipTableObserver(const std::set<std::string>& tablesToSkip,
                const std::string& tableToStopAfter = std::string(),
                size_t numberOfErrorsInRowsToSkipRestOfTheTable = SIZE_MAX) :
                m_tablesToSkip(tablesToSkip), m_tableToStopAfter(tableToStopAfter),
                m_numberOfErrorsInRowsToSkipRestOfTheTable(numberOfErrorsInRowsToSkipRestOfTheTable)
        {}

        bool beginTable(zserio::StringView tableName, size_t numberOfTableRows) override
        {
            ValidationObserver::beginTable(tableName, numberOfTableRows);
            if (m_tablesToSkip.count(zserio::toString(tableName)) != 0)
                return false;
            return true;
        }

        bool endTable(zserio::StringView tableName, size_t numberOfValidatedTableRows) override
        {
            ValidationObserver::endTable(tableName, numberOfValidatedTableRows);
            if (zserio::StringView(m_tableToStopAfter) == tableName)
                return false;
            return true;
        }

        bool reportError(zserio::StringView tableName, zserio::StringView fieldName,
            zserio::Span<const zserio::StringView> primaryKeyValues, ErrorType errorType,
            zserio::StringView message) override
        {
            ValidationObserver::reportError(tableName, fieldName, primaryKeyValues, errorType, message);
            if (errorType >= zserio::IValidationObserver::VALUE_OUT_OF_RANGE)
            {
                size_t& numErrors = m_numberOfErrorsInRowsPerTable[zserio::toString(tableName)];
                if (++numErrors >= m_numberOfErrorsInRowsToSkipRestOfTheTable)
                    return false;
            }
            return true;
        }

    private:
        std::set<std::string> m_tablesToSkip;
        std::string m_tableToStopAfter;
        size_t m_numberOfErrorsInRowsToSkipRestOfTheTable;
        std::map<std::string, size_t> m_numberOfErrorsInRowsPerTable;
    };

private:
    static const char DB_FILE_NAME[];
};

constexpr uint32_t ControlValidationTest::TABLE1_NUM_ROWS;
constexpr uint32_t ControlValidationTest::TABLE2_NUM_ROWS;
constexpr uint32_t ControlValidationTest::TABLE3_NUM_ROWS;

const char ControlValidationTest::DB_FILE_NAME[] =
        "arguments/with_validation_code/control_validation_test.sqlite";

TEST_F(ControlValidationTest, validate)
{
    populateDb(m_database->connection());

    ValidationObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(3, validationObserver.getNumberOfValidatedTables());

    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table1"));

    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table2"));

    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table3"));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ControlValidationTest, validateSkipTable1)
{
    populateDb(m_database->connection());

    SkipTableObserver validationObserver({"table1"});
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(2, validationObserver.getNumberOfValidatedTables());

    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table1"));

    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table2"));

    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table3"));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ControlValidationTest, validateSkipTable2)
{
    populateDb(m_database->connection());

    SkipTableObserver validationObserver({"table2"});
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(2, validationObserver.getNumberOfValidatedTables());

    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table1"));

    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table2"));

    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table3"));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ControlValidationTest, validateSkipTable3)
{
    populateDb(m_database->connection());

    SkipTableObserver validationObserver({"table3"});
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(2, validationObserver.getNumberOfValidatedTables());

    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table1"));

    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table2"));

    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table3"));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ControlValidationTest, validateSkipTable12)
{
    populateDb(m_database->connection());

    SkipTableObserver validationObserver({"table1", "table2"});
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table1"));

    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table2"));

    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table3"));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ControlValidationTest, validateStopAfterTable1)
{
    populateDb(m_database->connection());

    SkipTableObserver validationObserver({}, "table1");
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table1"));

    // validation stopped...
    ASSERT_EQ(0, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table2"));

    ASSERT_EQ(0, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table3"));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ControlValidationTest, validateSkipTable1AndStopAfterTable2)
{
    populateDb(m_database->connection());

    SkipTableObserver validationObserver({"table1"}, "table2");
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(1, validationObserver.getNumberOfValidatedTables());

    // skipped
    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table1"));

    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table2"));

    // validation stopped...
    ASSERT_EQ(0, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table3"));

    ASSERT_TRUE(validationObserver.getErrors().empty()) << validationObserver.getErrorsString();
}

TEST_F(ControlValidationTest, validateSkipTableAfter2ErrorsInRows)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();
    populateDb(connection);
    connection.executeUpdate("UPDATE table1 SET field = (16383 + 1) where id = 0");
    connection.executeUpdate("UPDATE table1 SET field = (-16383 - 1) where id = 3");
    connection.endTransaction(wasTransactionStarted);

    writeInvalidBlobToTable2(connection, 1);

    connection.executeUpdate("UPDATE table3 SET field = (-72057594037927935 -1) where id in (0, 1)");

    SkipTableObserver validationObserver({}, "", 2);
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(3, validationObserver.getNumberOfValidatedTables());

    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(4, validationObserver.getNumberOfValidatedTableRows("table1")); // error in 0 and 3

    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table2")); // error in 3

    ASSERT_EQ(TABLE3_NUM_ROWS, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(2, validationObserver.getNumberOfValidatedTableRows("table3")); // error in 0, 1

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(5, errors.size()) << validationObserver.getErrorsString();

    // table1
    ASSERT_EQ("table1", errors.at(0).tableName);
    ASSERT_EQ("field", errors.at(0).fieldName);
    ASSERT_EQ(std::vector<std::string>{"0"}, errors.at(0).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, errors.at(0).errorType);
    ASSERT_EQ("Value 16384 of Table1.field exceeds the range of -16383..16383", errors.at(0).message);

    ASSERT_EQ("table1", errors.at(1).tableName);
    ASSERT_EQ("field", errors.at(1).fieldName);
    ASSERT_EQ(std::vector<std::string>{"3"}, errors.at(1).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, errors.at(1).errorType);
    ASSERT_EQ("Value -16384 of Table1.field exceeds the range of -16383..16383", errors.at(1).message);

    // table2
    ASSERT_EQ("table2", errors.at(2).tableName);
    ASSERT_EQ("blob", errors.at(2).fieldName);
    ASSERT_EQ(std::vector<std::string>{"1"}, errors.at(2).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::BLOB_PARSE_FAILED, errors.at(2).errorType);
    ASSERT_EQ("BitStreamReader: Reached eof(), reading from stream failed!", errors.at(2).message);

    // table3
    ASSERT_EQ("table3", errors.at(3).tableName);
    ASSERT_EQ("field", errors.at(3).fieldName);
    ASSERT_EQ(std::vector<std::string>{"0"}, errors.at(3).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, errors.at(3).errorType);
    ASSERT_EQ("Value -72057594037927936 of Table3.field exceeds the range of "
            "-72057594037927935..72057594037927935", errors.at(3).message);

    ASSERT_EQ("table3", errors.at(4).tableName);
    ASSERT_EQ("field", errors.at(4).fieldName);
    ASSERT_EQ(std::vector<std::string>{"1"}, errors.at(4).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, errors.at(4).errorType);
    ASSERT_EQ("Value -72057594037927936 of Table3.field exceeds the range of "
            "-72057594037927935..72057594037927935", errors.at(4).message);
}


TEST_F(ControlValidationTest, validateSkipTableAndTerminateValidationAfterFirstSchemaError)
{
    zserio::SqliteConnection& connection = m_database->connection();

    const bool wasTransactionStarted = connection.startTransaction();

    populateDb(connection);

    // error in table1 rows shall be normally reported
    connection.executeUpdate("UPDATE table1 SET field = (16383 + 1) where id = 0");
    connection.executeUpdate("UPDATE table1 SET field = (-16383 - 1) where id = 3");

    connection.executeUpdate("CREATE TABLE table2Temp (id INTEGER PRIMARY KEY NOT NULL, "
            "blob BLOB, superfluousField INTEGER NOT NULL)"); // field shall be NOT NULL
    connection.executeUpdate("INSERT INTO table2Temp SELECT id, blob, 0 FROM table2");
    connection.executeUpdate("DROP TABLE table2");
    connection.executeUpdate("ALTER TABLE table2Temp RENAME TO table2");

    connection.endTransaction(wasTransactionStarted);

    class SkipAndTerminateObserver : public ValidationObserver
    {
    public:
        bool endTable(zserio::StringView tableName, size_t numberOfValidatedTableRows) override
        {
            ValidationObserver::endTable(tableName, numberOfValidatedTableRows);
            return !m_wasSchemaError; // terminate if an schema error occurred
        }

        bool reportError(zserio::StringView tableName, zserio::StringView fieldName,
            zserio::Span<const zserio::StringView> primaryKeyValues, ErrorType errorType,
            zserio::StringView message) override
        {
            ValidationObserver::reportError(tableName, fieldName, primaryKeyValues, errorType, message);
            if (errorType < zserio::IValidationObserver::VALUE_OUT_OF_RANGE)
            {
                m_wasSchemaError = true;
                return false;
            }
            return true;
        }

    private:
        bool m_wasSchemaError = false;
    };

    SkipAndTerminateObserver validationObserver;
    m_database->validate(validationObserver);

    ASSERT_EQ(3, validationObserver.getNumberOfTables());
    ASSERT_EQ(2, validationObserver.getNumberOfValidatedTables());

    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfTableRows("table1"));
    ASSERT_EQ(TABLE1_NUM_ROWS, validationObserver.getNumberOfValidatedTableRows("table1"));

    ASSERT_EQ(TABLE2_NUM_ROWS, validationObserver.getNumberOfTableRows("table2"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table2")); // skipped after schema error

    // validation stopped ...
    ASSERT_EQ(0, validationObserver.getNumberOfTableRows("table3"));
    ASSERT_EQ(0, validationObserver.getNumberOfValidatedTableRows("table3")); // error in 0, 1

    const auto& errors = validationObserver.getErrors();
    ASSERT_EQ(3, errors.size()) << validationObserver.getErrorsString();

    // table1
    ASSERT_EQ("table1", errors.at(0).tableName);
    ASSERT_EQ("field", errors.at(0).fieldName);
    ASSERT_EQ(std::vector<std::string>{"0"}, errors.at(0).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, errors.at(0).errorType);
    ASSERT_EQ("Value 16384 of Table1.field exceeds the range of -16383..16383", errors.at(0).message);

    ASSERT_EQ("table1", errors.at(1).tableName);
    ASSERT_EQ("field", errors.at(1).fieldName);
    ASSERT_EQ(std::vector<std::string>{"3"}, errors.at(1).primaryKeyValues);
    ASSERT_EQ(zserio::IValidationObserver::VALUE_OUT_OF_RANGE, errors.at(1).errorType);
    ASSERT_EQ("Value -16384 of Table1.field exceeds the range of -16383..16383", errors.at(1).message);

    // table2
    ASSERT_EQ("table2", errors.at(2).tableName);
    ASSERT_EQ("blob", errors.at(2).fieldName);
    ASSERT_TRUE(errors.at(2).primaryKeyValues.empty());
    ASSERT_EQ(zserio::IValidationObserver::INVALID_COLUMN_CONSTRAINT, errors.at(2).errorType);
    ASSERT_EQ("column Table2.blob is NULL-able, but the column is expected to be NOT NULL-able",
            errors.at(2).message);
}

} // namespace control_table
} // namespace with_validation_code
