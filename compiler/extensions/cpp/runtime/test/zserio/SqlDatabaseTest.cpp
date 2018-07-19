#include "zserio/SqlDatabase.h"
#include "zserio/SqliteException.h"

#include <string>
#include <vector>

#include <sqlite3.h>

#include "gtest/gtest.h"

namespace zserio
{

namespace
{
    extern "C" int sqliteResultAccumulatorCallback(void *data, int nColumns, char** colValues, char** colNames);

    class SqliteResultAccumulator
    {
    public:
        typedef std::vector<std::string> TRow;
        typedef std::vector<TRow> TResult;

        TResult const& getResult() const
        {
            return result;
        }

        int callback(int nColumns, char** colValues, char**)
        {
            TRow row;
            row.reserve(nColumns);
            for (int i = 0; i < nColumns; ++i)
                row.push_back(std::string(colValues[i]));
            result.push_back(row);
            return 0; // continue
        }

        TResult result;
    };

    int sqliteResultAccumulatorCallback(void *data, int nColumns, char** colValues, char** colNames)
    {
        SqliteResultAccumulator *self = static_cast<SqliteResultAccumulator*>(data);
        return self->callback(nColumns, colValues, colNames);
    }
} // namespace

static const char SQLITE3_MEM_DB[] = ":memory:";

TEST(SqlDatabaseTest, Ctor)
{
    SqlDatabase db;
    ASSERT_FALSE(db.isOpen());
}

TEST(SqlDatabaseTest, OpenExternal)
{
    sqlite3 *externalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqlDatabase db;
    db.open(externalConnection);
    ASSERT_TRUE(db.isOpen());

    db.close();

    result = sqlite3_close(externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqlDatabaseTest, OpenInternal)
{
    SqlDatabase db;
    db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_READONLY);
    ASSERT_TRUE(db.isOpen());
    db.close();
}

TEST(SqlDatabaseTest, DoubleOpenExternal)
{
    sqlite3 *externalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqlDatabase db;
    db.open(externalConnection);
    ASSERT_THROW(db.open(externalConnection), std::runtime_error);

    result = sqlite3_close(externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqlDatabaseTest, DoubleOpenInternal)
{
    SqlDatabase db;
    db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_READONLY);
    ASSERT_THROW(db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_READONLY), std::runtime_error);
    db.close();

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqlDatabaseTest, IsOpen)
{
    SqlDatabase db;
    db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_READONLY);
    ASSERT_TRUE(db.isOpen());

    db.close();
    ASSERT_FALSE(db.isOpen());

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqlDatabaseTest, Close)
{
    SqlDatabase db;
    ASSERT_THROW(db.close(), std::runtime_error);
    db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_READONLY);
    db.close();

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqlDatabaseTest, GetConnection)
{
    SqlDatabase db;
    db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_READONLY);
    ASSERT_TRUE(db.getConnection() != NULL);

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqlDatabaseTest, ExecuteUpdate)
{
    SqlDatabase db;
    db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_CREATE);
    db.executeUpdate("CREATE TABLE Foo AS SELECT 1");

    sqlite3* const dbConnection = db.getConnection();
    SqliteResultAccumulator resultAcc;
    sqlite3_exec(dbConnection, "SELECT * FROM Foo", sqliteResultAccumulatorCallback, &resultAcc, NULL);

    SqliteResultAccumulator::TResult const& result = resultAcc.getResult();
    ASSERT_EQ(1, result.size());
    SqliteResultAccumulator::TRow const& row = result.front();
    ASSERT_EQ(1, row.size());
    ASSERT_EQ("1", row.front());

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqlDatabaseTest, ExecuteUpdateOnReadOnlyDatabase)
{
    SqlDatabase db;
    db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_READONLY);
    ASSERT_THROW(db.executeUpdate("CREATE TABLE Foo AS SELECT 1"), SqliteException);
}

TEST(SqlDatabaseTest, PrepareStatement)
{
    SqlDatabase db;
    db.open(SQLITE3_MEM_DB, SqlDatabase::DB_ACCESS_CREATE);
    db.executeUpdate("CREATE TABLE Foo AS SELECT 1");

    sqlite3_stmt* const statement = db.prepareStatement("SELECT 1");
    ASSERT_TRUE(statement != NULL);

    int result = sqlite3_step(statement);
    ASSERT_EQ(SQLITE_ROW, result);

    ASSERT_EQ(1, sqlite3_column_count(statement));
    const std::string resultString(reinterpret_cast<char const*>(sqlite3_column_text(statement, 0)));
    ASSERT_EQ("1", resultString);

    result = sqlite3_step(statement);
    ASSERT_EQ(SQLITE_DONE, result);

    result = sqlite3_finalize(statement);
    ASSERT_EQ(SQLITE_OK, result);

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

} // namespace zserio
