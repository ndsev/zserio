#ifndef ZSERIO_SQL_DATABASE_H_INC
#define ZSERIO_SQL_DATABASE_H_INC

#include <string>
#include <set>
#include <sqlite3.h>

#include "BitStreamReader.h"

namespace zserio
{
class SqlDatabase
{
public:
    enum DbAccessMode
    {
        DB_ACCESS_READONLY,
        DB_ACCESS_WRITE,
        DB_ACCESS_CREATE
    };

    SqlDatabase();
    virtual ~SqlDatabase();

    void open(sqlite3* externalConnection);
    void open(const std::string& fileName, DbAccessMode mode);
    bool isOpen() const;
    void close();

    sqlite3* getConnection();

    void executeUpdate(const std::string& sqlQuery);
    sqlite3_stmt* prepareStatement(const std::string& sqlQuery);

protected:
    virtual void doOpen(sqlite3*) {}

    bool startTransaction();
    void endTransaction(bool wasTransactionStarted);

private:
    // disable copy constructor and assignment operator
    SqlDatabase(const SqlDatabase&);
    SqlDatabase& operator=(const SqlDatabase&);

    static int convertDbAccessModeToSqliteOpenMode(DbAccessMode dbAccessMode);

    class SqliteConnection
    {
    public:
        enum ConnectionType
        {
            INTERNAL_CONNECTION,
            EXTERNAL_CONNECTION
        };

        SqliteConnection(sqlite3* connection = NULL, ConnectionType connectionType = INTERNAL_CONNECTION);
        ~SqliteConnection();
        bool isOpen() const;
        void reset(sqlite3* connection = NULL, ConnectionType connectionType = INTERNAL_CONNECTION);

        sqlite3* get();

    private:
        sqlite3* m_connection;
        ConnectionType m_connectionType;
    };

    SqliteConnection m_db;
};

} // namespace zserio

#endif // ifndef ZSERIO_SQL_DATABASE_H_INC
