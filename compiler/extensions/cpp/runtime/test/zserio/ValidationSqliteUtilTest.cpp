#include "gtest/gtest.h"

#include <memory>

#include "zserio/ValidationSqliteUtil.h"
#include "zserio/SqliteConnection.h"
#include "zserio/SqliteFinalizer.h"

namespace zserio
{

class ValidationSqliteUtilTest : public ::testing::Test
{
public:
    ValidationSqliteUtilTest() :
            connection(createConnection())
    {}

protected:
    using allocator_type = std::allocator<uint8_t>;
    using Util = ValidationSqliteUtil<allocator_type>;

    void insertRows(const std::string& tableName, uint32_t startId, uint32_t numRows)
    {
        Util::Statement statement(
                connection.prepareStatement("INSERT INTO " + tableName + " VALUES (?, ?)"));

        for (uint32_t i = 0; i < numRows; ++i)
        {
            sqlite3_bind_int(statement.get(), 1, static_cast<int>(startId + i));
            sqlite3_bind_int(statement.get(), 2, static_cast<int>(i * i));
            ASSERT_EQ(SQLITE_DONE, sqlite3_step(statement.get()));
            sqlite3_reset(statement.get());
        }
    }

    using TiedColumn = std::tuple<Util::string_type, Util::string_type, bool, bool>;

    TiedColumn tieColumn(const Util::ColumnDescription& columnDescription)
    {
        return std::tie(
                columnDescription.name,
                columnDescription.type,
                columnDescription.isNotNull,
                columnDescription.isPrimaryKey);
    }

    SqliteConnection connection;

private:
    sqlite3* createConnection()
    {
        sqlite3* db = nullptr;
        int result = sqlite3_open(IN_MEMORY_DATABASE, &db);
        EXPECT_EQ(SQLITE_OK, result);
        return db;
    }

    static const char* const IN_MEMORY_DATABASE;
};

const char* const ValidationSqliteUtilTest::IN_MEMORY_DATABASE = ":memory:";

TEST_F(ValidationSqliteUtilTest, getNumberOfTableRows)
{
    ASSERT_THROW(Util::getNumberOfTableRows(connection, ""_sv, "test"_sv, allocator_type()),
            SqliteException);

    ASSERT_THROW(Util::getNumberOfTableRows(connection, "NONEXISTING"_sv, "test"_sv, allocator_type()),
            SqliteException);

    connection.executeUpdate("CREATE TABLE test(id INTEGER PRIMARY KEY NOT NULL, value INTEGER NOT NULL)");
    ASSERT_EQ(0, Util::getNumberOfTableRows(connection, ""_sv, "test"_sv, allocator_type()));

    insertRows("test", 0, 1);
    ASSERT_EQ(1, Util::getNumberOfTableRows(connection, ""_sv, "test"_sv, allocator_type()));

    insertRows("test", 1, 10);
    ASSERT_EQ(11, Util::getNumberOfTableRows(connection, ""_sv, "test"_sv, allocator_type()));

    insertRows("test", 11, 10);
    ASSERT_EQ(21, Util::getNumberOfTableRows(connection, ""_sv, "test"_sv, allocator_type()));
}

TEST_F(ValidationSqliteUtilTest, getTableSchema)
{
    Util::TableSchema schema;
    Util::getTableSchema(connection, ""_sv, "test"_sv, schema, allocator_type());
    ASSERT_TRUE(schema.empty());

    ASSERT_THROW(Util::getTableSchema(connection, "NONEXISTING"_sv, "test"_sv, schema, allocator_type()),
            SqliteException);

    connection.executeUpdate("CREATE TABLE test1(id INTEGER PRIMARY KEY NOT NULL, value INTEGER NOT NULL)");
    Util::getTableSchema(connection, ""_sv, "test1"_sv, schema, allocator_type());
    ASSERT_EQ(2, schema.size());
    auto search = schema.find("id");
    ASSERT_TRUE(search != schema.end());
    ASSERT_EQ("id", search->first);
    ASSERT_EQ(std::make_tuple("id", "INTEGER", true, true), tieColumn(search->second));
    search = schema.find("value");
    ASSERT_TRUE(search != schema.end());
    ASSERT_EQ("value", search->first);
    ASSERT_EQ(std::make_tuple("value", "INTEGER", true, false), tieColumn(search->second));

    schema.clear();
    connection.executeUpdate("CREATE TABLE test2(id INTEGER PRIMARY KEY NOT NULL, text TEXT NOT NULL, "
            "field BLOB)");
    Util::getTableSchema(connection, ""_sv, "test2"_sv, schema, allocator_type());
    ASSERT_EQ(3, schema.size());
    search = schema.find("id");
    ASSERT_TRUE(search != schema.end());
    ASSERT_EQ("id", search->first);
    ASSERT_EQ(std::make_tuple("id", "INTEGER", true, true), tieColumn(search->second));
    search = schema.find("text");
    ASSERT_TRUE(search != schema.end());
    ASSERT_EQ("text", search->first);
    ASSERT_EQ(std::make_tuple("text", "TEXT", true, false), tieColumn(search->second));
    search = schema.find("field");
    ASSERT_TRUE(search != schema.end());
    ASSERT_EQ("field", search->first);
    ASSERT_EQ(std::make_tuple("field", "BLOB", false, false), tieColumn(search->second));

    // multiple primary keys
    schema.clear();
    connection.executeUpdate("CREATE TABLE test3(name TEXT, surname TEXT NOT NULL, field BLOB, "
            "PRIMARY KEY(name, surname))");
    Util::getTableSchema(connection, ""_sv, "test3"_sv, schema, allocator_type());
    ASSERT_EQ(3, schema.size());
    search = schema.find("name");
    ASSERT_TRUE(search != schema.end());
    ASSERT_EQ("name", search->first);
    ASSERT_EQ(std::make_tuple("name", "TEXT", false, true), tieColumn(search->second));
    search = schema.find("surname");
    ASSERT_TRUE(search != schema.end());
    ASSERT_EQ("surname", search->first);
    ASSERT_EQ(std::make_tuple("surname", "TEXT", true, true), tieColumn(search->second));
    search = schema.find("field");
    ASSERT_TRUE(search != schema.end());
    ASSERT_EQ("field", search->first);
    ASSERT_EQ(std::make_tuple("field", "BLOB", false, false), tieColumn(search->second));
}

TEST_F(ValidationSqliteUtilTest, isColumnInTable)
{
    ASSERT_FALSE(Util::isColumnInTable(connection, ""_sv, "test"_sv, "hidden"_sv, allocator_type()));

    ASSERT_FALSE(Util::isColumnInTable(connection, "NONEXISTING"_sv, "test"_sv, "hidden"_sv, allocator_type()));

    connection.executeUpdate("CREATE TABLE test1(id INTEGER PRIMARY KEY NOT NULL, value INTEGER NOT NULL)");
    ASSERT_FALSE(Util::isColumnInTable(connection, ""_sv, "test1"_sv, "hidden"_sv, allocator_type()));
    ASSERT_TRUE(Util::isColumnInTable(connection, ""_sv, "test1"_sv, "id"_sv, allocator_type()));
    ASSERT_TRUE(Util::isColumnInTable(connection, ""_sv, "test1"_sv, "value"_sv, allocator_type()));

    connection.executeUpdate("CREATE TABLE test2(id INTEGER PRIMARY KEY NOT NULL, text HIDDEN TEXT)");
    ASSERT_TRUE(Util::isColumnInTable(connection, ""_sv, "test2"_sv, "id"_sv, allocator_type()));
    ASSERT_TRUE(Util::isColumnInTable(connection, ""_sv, "test2"_sv, "text"_sv, allocator_type()));
}

TEST_F(ValidationSqliteUtilTest, sqliteColumnTypeName)
{
    // this test also verifies that sqlite3_column_type works as we except since we need to use it
    // in validateType* in generated sources for zserio SqlTables

    const char* tableName = "sqliteColumnTypeTable";
    connection.executeUpdate(std::string("CREATE TABLE ") + tableName + "(id INTEGER PRIMARY KEY, "
            "integerCol INTEGER, realCol REAL, textCol TEXT, blobCol BLOB)");

    connection.executeUpdate(std::string("INSERT INTO ") + tableName + " VALUES (0, NULL, NULL, NULL, NULL)");
    connection.executeUpdate(std::string("INSERT INTO ") + tableName + " VALUES (1, 13, 1.3, 'STRING', x'00')");
    connection.executeUpdate(std::string("INSERT INTO ") + tableName + " VALUES (2, 1.3, 'STRING', x'00', 13)");

    Util::Statement stmt(connection.prepareStatement(std::string("SELECT * from ") + tableName));

    // first row check NULL values
    ASSERT_EQ(SQLITE_ROW, sqlite3_step(stmt.get()));
    int type = sqlite3_column_type(stmt.get(), 0);
    ASSERT_EQ(SQLITE_INTEGER, type);
    ASSERT_STREQ("INTEGER", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 1);
    ASSERT_EQ(SQLITE_NULL, type);
    ASSERT_STREQ("NULL", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 2);
    ASSERT_EQ(SQLITE_NULL, type);
    ASSERT_STREQ("NULL", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 3);
    ASSERT_EQ(SQLITE_NULL, type);
    ASSERT_STREQ("NULL", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 4);
    ASSERT_EQ(SQLITE_NULL, type);
    ASSERT_STREQ("NULL", Util::sqliteColumnTypeName(type));

    // second row checks correct values
    ASSERT_EQ(SQLITE_ROW, sqlite3_step(stmt.get()));
    type = sqlite3_column_type(stmt.get(), 0);
    ASSERT_EQ(SQLITE_INTEGER, type);
    ASSERT_STREQ("INTEGER", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 1);
    ASSERT_EQ(SQLITE_INTEGER, type);
    ASSERT_STREQ("INTEGER", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 2);
    ASSERT_EQ(SQLITE_FLOAT, type);
    ASSERT_STREQ("REAL", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 3);
    ASSERT_EQ(SQLITE_TEXT, type);
    ASSERT_STREQ("TEXT", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 4);
    ASSERT_EQ(SQLITE_BLOB, type);
    ASSERT_STREQ("BLOB", Util::sqliteColumnTypeName(type));

    // third row checks types mismatch - i.e. checks dynamic typing in SQLite
    ASSERT_EQ(SQLITE_ROW, sqlite3_step(stmt.get()));
    type = sqlite3_column_type(stmt.get(), 0);
    ASSERT_EQ(SQLITE_INTEGER, type);
    ASSERT_STREQ("INTEGER", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 1);
    ASSERT_EQ(SQLITE_FLOAT, type);
    ASSERT_STREQ("REAL", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 2);
    ASSERT_EQ(SQLITE_TEXT, type);
    ASSERT_STREQ("TEXT", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 3);
    ASSERT_EQ(SQLITE_BLOB, type);
    ASSERT_STREQ("BLOB", Util::sqliteColumnTypeName(type));
    type = sqlite3_column_type(stmt.get(), 4);
    ASSERT_EQ(SQLITE_INTEGER, type);
    ASSERT_STREQ("INTEGER", Util::sqliteColumnTypeName(type));
}

} // namespace zserio
