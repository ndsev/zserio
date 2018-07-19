#include "SqliteException.h"
#include "SqlDatabase.h"

namespace zserio
{

SqlDatabase::SqliteConnection::SqliteConnection(sqlite3* connection, ConnectionType connectionType) :
        m_connection(connection), m_connectionType(connectionType)
{
}

SqlDatabase::SqliteConnection::~SqliteConnection()
{
    reset();
}

void SqlDatabase::SqliteConnection::reset(sqlite3* connection, ConnectionType connectionType)
{
    // close connection only if it is internal
    if (m_connectionType == INTERNAL_CONNECTION)
        sqlite3_close_v2(m_connection);     // sqlite3_close_v2(NULL) is a harmless no-op

    m_connection = connection;
    m_connectionType = connectionType;
}

bool SqlDatabase::SqliteConnection::isOpen() const
{
    return m_connection != NULL;
}

sqlite3* SqlDatabase::SqliteConnection::get()
{
    return m_connection;
}

SqlDatabase::SqlDatabase()
{
}

SqlDatabase::~SqlDatabase()
{
}

void SqlDatabase::open(sqlite3* externalConnection)
{
    if (isOpen())
        throw SqliteException("SqlDatabase::open(): database is already open");

    m_db.reset(externalConnection, SqliteConnection::EXTERNAL_CONNECTION);
    doOpen(externalConnection);
}

void SqlDatabase::open(const std::string& fileName, DbAccessMode dbAccessMode)
{
    if (isOpen())
        throw SqliteException("SqlDatabase::open(): database is already open");

    sqlite3 *internalConnection = NULL;
    const int sqliteOpenMode = convertDbAccessModeToSqliteOpenMode(dbAccessMode);
    const int result = sqlite3_open_v2(fileName.c_str(), &internalConnection, sqliteOpenMode, NULL);
    if (result != SQLITE_OK)
        throw SqliteException("SqlDatabase::open(): can't open DB " + fileName, result);

    m_db.reset(internalConnection, SqliteConnection::INTERNAL_CONNECTION);
    doOpen(internalConnection);
}

bool SqlDatabase::isOpen() const
{
    return m_db.isOpen();
}

void SqlDatabase::close()
{
    if (!isOpen())
        throw SqliteException("SqlDatabase::close(): database is not open");

    m_db.reset();
}

sqlite3* SqlDatabase::getConnection()
{
    return m_db.get();
}

void SqlDatabase::executeUpdate(const std::string& sqlQuery)
{
    const int result = sqlite3_exec(m_db.get(), sqlQuery.c_str(), NULL, NULL, NULL);
    if (result != SQLITE_OK)
    {
        throw SqliteException("SqlDatabase::executeUpdate(): SQLite query failed", result);
    }
}

sqlite3_stmt* SqlDatabase::prepareStatement(const std::string& sqlQuery)
{
    sqlite3_stmt* statement = NULL;
    const int result = sqlite3_prepare_v2(m_db.get(), sqlQuery.c_str(), -1, &statement, NULL);
    if (result != SQLITE_OK)
    {
        throw SqliteException("SqlDatabase::prepareStatement(): sqlite3_prepare_v2() failed", result);
    }

    return statement;
}

bool SqlDatabase::startTransaction()
{
    bool wasTransactionStarted = false;
    if (sqlite3_get_autocommit(m_db.get()))
    {
        executeUpdate("BEGIN;");
        wasTransactionStarted = true;
    }

    return wasTransactionStarted;
}

void SqlDatabase::endTransaction(bool wasTransactionStarted)
{
    if (wasTransactionStarted)
        executeUpdate("COMMIT;");
}

int SqlDatabase::convertDbAccessModeToSqliteOpenMode(DbAccessMode dbAccessMode)
{
    switch (dbAccessMode)
    {
    case SqlDatabase::DB_ACCESS_READONLY:
        return SQLITE_OPEN_READONLY;

    case SqlDatabase::DB_ACCESS_WRITE:
        return SQLITE_OPEN_READWRITE;

    default:
        return SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE;
    }
}

} // namespace zserio
