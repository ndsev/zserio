#ifndef ZSERIO_SQL_CONNECTION_H_INC
#define ZSERIO_SQL_CONNECTION_H_INC

#include "sqlite3.h"
#include "SqliteException.h"

namespace zserio
{

class SqliteConnection
{
public:
    enum ConnectionType
    {
        INTERNAL_CONNECTION,
        EXTERNAL_CONNECTION
    };

    explicit SqliteConnection(sqlite3* connection = NULL, ConnectionType connectionType = INTERNAL_CONNECTION);
    ~SqliteConnection();
    void reset(sqlite3* connection = NULL, ConnectionType connectionType = INTERNAL_CONNECTION);

    ConnectionType getConnectionType() const;
    sqlite3* getConnection();
    void executeUpdate(const std::string& query);
    sqlite3_stmt* prepareStatement(const std::string& query);

    bool startTransaction();
    void endTransaction(bool wasTransactionStarted);

private:
    // disable copy constructor and assignment operator
    SqliteConnection(const SqliteConnection&);
    SqliteConnection& operator=(const SqliteConnection&);

    sqlite3* m_connection;
    ConnectionType m_connectionType;
};

inline SqliteConnection::SqliteConnection(sqlite3* connection, ConnectionType connectionType)
:   m_connection(connection), m_connectionType(connectionType)
{}

inline SqliteConnection::~SqliteConnection()
{
    reset();
}

inline void SqliteConnection::reset(sqlite3* connection, ConnectionType connectionType)
{
    // close connection only if it is internal
    if (m_connectionType == INTERNAL_CONNECTION)
        sqlite3_close_v2(m_connection); // sqlite3_close_v2(NULL) is a harmless no-op

    m_connection = connection;
    m_connectionType = connectionType;
}

inline SqliteConnection::ConnectionType SqliteConnection::getConnectionType() const
{
    return m_connectionType;
}

inline sqlite3* SqliteConnection::getConnection()
{
    return m_connection;
}

inline void SqliteConnection::executeUpdate(const std::string& sqlQuery)
{
    const int result = sqlite3_exec(m_connection, sqlQuery.c_str(), NULL, NULL, NULL);
    if (result != SQLITE_OK)
        throw SqliteException("SqliteConnection::executeUpdate(): sqlite3_exec failed", result);
}

inline sqlite3_stmt* SqliteConnection::prepareStatement(const std::string& sqlQuery)
{
    sqlite3_stmt* statement = NULL;
    const int result = sqlite3_prepare_v2(m_connection, sqlQuery.c_str(), -1, &statement, NULL);
    if (result != SQLITE_OK)
        throw SqliteException("SqliteConnection::prepareStatement(): sqlite3_prepare_v2() failed", result);

    return statement;
}

inline bool SqliteConnection::startTransaction()
{
    bool wasTransactionStarted = false;
    if (sqlite3_get_autocommit(m_connection))
    {
        executeUpdate("BEGIN;");
        wasTransactionStarted = true;
    }

    return wasTransactionStarted;
}

inline void SqliteConnection::endTransaction(bool wasTransactionStarted)
{
    if (wasTransactionStarted)
        executeUpdate("COMMIT;");
}

} // namespace zserio

#endif // ZSERIO_SQL_CONNECTION_H_INC
