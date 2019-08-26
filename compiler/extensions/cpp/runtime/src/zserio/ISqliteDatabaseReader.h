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
    /**
     * Virtual destructor.
     */
    virtual ~ISqliteDatabaseReader() {}

    /**
     * Returns current database connection.
     *
     * \return SQLite database connection or NULL if the database is not open.
     */
    virtual sqlite3* connection() = 0;
};

} // namespace zserio

#endif // ZSERIO_ISQL_DATABASE_READER_H_INC
