#ifndef ZSERIO_ISQL_DATABASE_READER_H_INC
#define ZSERIO_ISQL_DATABASE_READER_H_INC

#include <string>
#include <set>

#include "zserio/SqliteConnection.h"

namespace zserio
{

/** Generic interface for SQLite database. */
class ISqliteDatabaseReader
{
public:
    /**
     * Virtual destructor.
     */
    virtual ~ISqliteDatabaseReader() = default;

    /**
     * Returns current database connection.
     *
     * \return SQLite database connection.
     */
    virtual SqliteConnection& connection() noexcept = 0;
};

} // namespace zserio

#endif // ZSERIO_ISQL_DATABASE_READER_H_INC
