#ifndef ZSERIO_ISQL_DATABASE_READER_H_INC
#define ZSERIO_ISQL_DATABASE_READER_H_INC

#include <string>
#include <set>

#include <sqlite3.h>

namespace zserio
{

/** Generic interface for SQLite database. */
class ISqliteDatabaseReader
{
public:
    virtual ~ISqliteDatabaseReader() {}

    /**
     * Returns current database connection.
     *
     * \return SQLite database connection or NULL if the database is not open.
     */
    virtual sqlite3* connection() = 0;

    /**
     * Executes an update query.
     *
     * \param[in] Query to execute.
     */
    virtual void executeUpdate(const std::string& query) = 0;

    /**
     * Prepares SQLite statement for the given query.
     *
     * \param[in] query Query to prepare statement for.
     *
     * \return Perpared SQLite statement.
     */
    virtual sqlite3_stmt* prepareStatement(const std::string& query) = 0;
};

} // namespace zserio

#endif // ZSERIO_ISQL_DATABASE_READER_H_INC
