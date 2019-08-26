#ifndef ZSERIO_SQL_CONNECTION_H_INC
#define ZSERIO_SQL_CONNECTION_H_INC

#include "sqlite3.h"
#include "SqliteException.h"

namespace zserio
{

/**
 * Helper class to keep sqlite3 connection and ensure its safe destruction.
 *
 * The class also provides simple interface to execute SQLite queries.
 */
class SqliteConnection
{
public:
    /**
     * Connection type.
     */
    enum ConnectionType
    {
        INTERNAL_CONNECTION, /**< Internal connection which must be released in destructor. */
        EXTERNAL_CONNECTION /**< External connection managed from outside. */
    };

    /**
     * Constructor.
     *
     * \param connection Pointer to the SQLite connection.
     * \parm connectionType Type of the connection. Default is INTERNAL_CONNECTION.
     */
    explicit SqliteConnection(sqlite3* connection = NULL, ConnectionType connectionType = INTERNAL_CONNECTION);

    /**
     * Destructor.
     */
    ~SqliteConnection();

    /**
     * Copying and moving is disallowed!
     * \{
     */
    SqliteConnection(const SqliteConnection&) = delete;
    SqliteConnection& operator=(const SqliteConnection&) = delete;

    SqliteConnection(SqliteConnection&&) = delete;
    SqliteConnection& operator=(SqliteConnection&&) = delete;
    /** \} */

    /**
     * Resets the connection.
     *
     * \param connection New connection to set. Default is NULL - i.e. unset.
     * \param connectionType Type of the new connection.
     */
    void reset(sqlite3* connection = NULL, ConnectionType connectionType = INTERNAL_CONNECTION);

    /**
     * Gets the current connection type.
     *
     * When connection is NULL, the connection type is insignificant.
     *
     * \return Connection type.
     */
    ConnectionType getConnectionType() const;

    /**
     * Gets the current connection.
     *
     * \return SQLite connection.
     */
    sqlite3* getConnection();

    /**
     * Executes a query which doesn't need to return anything - e.g. DML.
     *
     * \param query The query string.
     */
    void executeUpdate(const std::string& query);

    /**
     * Prepares the SQLite statement for the given query.
     *
     * Note that the user is responsible to proper statement finalization using sqlite3_finalize!
     *
     * \param query The query string.
     *
     * \return Prepared SQLite statement.
     */
    sqlite3_stmt* prepareStatement(const std::string& query);

    /**
     * Starts a new transaction if a transaction is not already started.
     *
     * \return True when the new transaction was started. False when a transaction is already started.
     */
    bool startTransaction();

    /**
     * Terminates the current transaction.
     *
     * The parameter wasTransactionStarted is used for convenience since it's then easier to write code
     * which uses transactions.
     *
     * \code{.cpp}
     * bool wasTransactionStarted = connection.startTransaction(); // transaction may be already started
     * // execute queries
     * // ...
     * // terminates the transaction only if it was started by the corresponding startTransaction call.
     * connection.endTransaction(wasTransactionStarted);
     * \endcode
     *
     * \param wasTransactionStarted When false, the call does actually nothing.
     */
    void endTransaction(bool wasTransactionStarted);

private:
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
