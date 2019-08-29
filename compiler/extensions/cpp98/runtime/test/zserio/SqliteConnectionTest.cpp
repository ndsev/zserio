#include "zserio/SqliteConnection.h"
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

TEST(SqliteConnectionTest, CtorEmpty)
{
    SqliteConnection db;
    ASSERT_EQ(NULL, db.getConnection());
}

TEST(SqliteConnectionTest, CtorExternal)
{
    sqlite3 *externalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db(externalConnection, SqliteConnection::EXTERNAL_CONNECTION);
    ASSERT_EQ(externalConnection, db.getConnection());
    ASSERT_EQ(SqliteConnection::EXTERNAL_CONNECTION, db.getConnectionType());

    db.reset();

    result = sqlite3_close(externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, CtorInternal)
{
    sqlite3* internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);

    SqliteConnection db(internalConnection, SqliteConnection::INTERNAL_CONNECTION);
    ASSERT_EQ(internalConnection, db.getConnection());
    ASSERT_EQ(SqliteConnection::INTERNAL_CONNECTION, db.getConnectionType());

    db.reset();
    ASSERT_EQ(NULL, db.getConnection());

    result = sqlite3_close(internalConnection);
    ASSERT_NE(SQLITE_OK, result); // shall be already closed!

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, CtorDefaultInternal)
{
    sqlite3* internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db(internalConnection, SqliteConnection::INTERNAL_CONNECTION);
    ASSERT_EQ(internalConnection, db.getConnection());
    ASSERT_EQ(SqliteConnection::INTERNAL_CONNECTION, db.getConnectionType());

    db.reset();
    ASSERT_EQ(NULL, db.getConnection());

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, ResetExternal)
{
    sqlite3 *externalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db;
    db.reset(externalConnection, SqliteConnection::EXTERNAL_CONNECTION);
    ASSERT_EQ(externalConnection, db.getConnection());
    ASSERT_EQ(SqliteConnection::EXTERNAL_CONNECTION, db.getConnectionType());

    db.reset();
    ASSERT_EQ(NULL, db.getConnection());

    result = sqlite3_close(externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, ResetInternal)
{
    sqlite3* internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db;
    db.reset(internalConnection, SqliteConnection::INTERNAL_CONNECTION);
    ASSERT_EQ(internalConnection, db.getConnection());
    ASSERT_EQ(SqliteConnection::INTERNAL_CONNECTION, db.getConnectionType());

    db.reset();
    ASSERT_EQ(NULL, db.getConnection());

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, ResetDefaultInternal)
{
    sqlite3* internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db;
    db.reset(internalConnection);
    ASSERT_EQ(internalConnection, db.getConnection());
    ASSERT_EQ(SqliteConnection::INTERNAL_CONNECTION, db.getConnectionType());

    db.reset();
    ASSERT_EQ(NULL, db.getConnection());

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, DoubleResetExternal)
{
    sqlite3 *externalConnection1 = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &externalConnection1);
    ASSERT_EQ(SQLITE_OK, result);

    sqlite3 *externalConnection2 = NULL;
    result = sqlite3_open(SQLITE3_MEM_DB, &externalConnection2);
    ASSERT_EQ(SQLITE_OK, result);

    {
        SqliteConnection db;
        db.reset(externalConnection1, SqliteConnection::EXTERNAL_CONNECTION);
        ASSERT_EQ(externalConnection1, db.getConnection());
        ASSERT_EQ(SqliteConnection::EXTERNAL_CONNECTION, db.getConnectionType());

        db.reset(externalConnection2, SqliteConnection::EXTERNAL_CONNECTION);
        ASSERT_EQ(externalConnection2, db.getConnection());
        ASSERT_EQ(SqliteConnection::EXTERNAL_CONNECTION, db.getConnectionType());
    } // db dtor

    result = sqlite3_close(externalConnection1);
    ASSERT_EQ(SQLITE_OK, result);

    result = sqlite3_close(externalConnection2);
    ASSERT_EQ(SQLITE_OK, result);

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, DoubleResetInternal)
{
    sqlite3 *internalConnection1 = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection1);
    ASSERT_EQ(SQLITE_OK, result);

    sqlite3 *internalConnection2 = NULL;
    result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection2);
    ASSERT_EQ(SQLITE_OK, result);

    {
        SqliteConnection db;
        db.reset(internalConnection1);
        ASSERT_EQ(internalConnection1, db.getConnection());
        ASSERT_EQ(SqliteConnection::INTERNAL_CONNECTION, db.getConnectionType());

        db.reset(internalConnection2);
        ASSERT_EQ(internalConnection2, db.getConnection());
        ASSERT_EQ(SqliteConnection::INTERNAL_CONNECTION, db.getConnectionType());
    } // db dtor

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, GetConnection)
{
    sqlite3 *internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db(internalConnection);
    ASSERT_EQ(internalConnection, db.getConnection());

    db.reset();
    ASSERT_EQ(NULL, db.getConnection());

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, GetConnectionType)
{
    sqlite3 *internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db(internalConnection);
    ASSERT_EQ(SqliteConnection::INTERNAL_CONNECTION, db.getConnectionType());

    sqlite3 *externalConnection = NULL;
    result = sqlite3_open(SQLITE3_MEM_DB, &externalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    db.reset(externalConnection, SqliteConnection::EXTERNAL_CONNECTION);
    ASSERT_EQ(SqliteConnection::EXTERNAL_CONNECTION, db.getConnectionType());
}

TEST(SqliteConnectionTest, Reset)
{
    sqlite3 *internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db;
    db.reset(internalConnection);
    ASSERT_EQ(internalConnection, db.getConnection());

    db.reset();
    ASSERT_EQ(NULL, db.getConnection());

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, ExecuteUpdate)
{
    sqlite3 *internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db(internalConnection);

    db.executeUpdate("CREATE TABLE Foo AS SELECT 1");

    sqlite3* const dbConnection = db.getConnection();
    SqliteResultAccumulator resultAcc;
    sqlite3_exec(dbConnection, "SELECT * FROM Foo", sqliteResultAccumulatorCallback, &resultAcc, NULL);

    SqliteResultAccumulator::TResult const& accResult = resultAcc.getResult();
    ASSERT_EQ(1, accResult.size());
    SqliteResultAccumulator::TRow const& row = accResult.front();
    ASSERT_EQ(1, row.size());
    ASSERT_EQ("1", row.front());

    db.reset();

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

TEST(SqliteConnectionTest, ExecuteUpdateOnReadOnlyDatabase)
{
    sqlite3 *internalConnection = NULL;
    int result = sqlite3_open_v2(SQLITE3_MEM_DB, &internalConnection, SQLITE_OPEN_READONLY, NULL);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db(internalConnection);

    ASSERT_THROW(db.executeUpdate("CREATE TABLE Foo AS SELECT 1"), SqliteException);
}

TEST(SqliteConnectionTest, PrepareStatement)
{
    sqlite3 *internalConnection = NULL;
    int result = sqlite3_open(SQLITE3_MEM_DB, &internalConnection);
    ASSERT_EQ(SQLITE_OK, result);

    SqliteConnection db(internalConnection);
    db.executeUpdate("CREATE TABLE Foo AS SELECT 1");

    sqlite3_stmt* const statement = db.prepareStatement("SELECT 1");
    ASSERT_TRUE(statement != NULL);

    result = sqlite3_step(statement);
    ASSERT_EQ(SQLITE_ROW, result);

    ASSERT_EQ(1, sqlite3_column_count(statement));
    const std::string resultString(reinterpret_cast<char const*>(sqlite3_column_text(statement, 0)));
    ASSERT_EQ("1", resultString);

    result = sqlite3_step(statement);
    ASSERT_EQ(SQLITE_DONE, result);

    result = sqlite3_finalize(statement);
    ASSERT_EQ(SQLITE_OK, result);

    db.reset();

    ASSERT_EQ(SQLITE_OK, sqlite3_shutdown());
}

} // namespace zserio
