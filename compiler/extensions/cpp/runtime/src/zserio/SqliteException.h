#ifndef ZSERIO_SQLITE_EXCEPTION_H_INC
#define ZSERIO_SQLITE_EXCEPTION_H_INC

#include <string>
#include <sqlite3.h>

#include "zserio/CppRuntimeException.h"

namespace zserio
{

/** Exception thrown when an error in an SQLite operation occurs. */
class SqliteException : public CppRuntimeException
{
public:
    /**
     * Constructor.
     *
     * \param message Description of the SQLite error.
     */
    explicit SqliteException(const std::string& message) : CppRuntimeException(message) {}

    /**
     * Constructor.
     *
     * \param message Description of the SQLite error.
     * \param sqliteCode Concrete SQLite error code.
     */
    SqliteException(const std::string& message, int sqliteCode) :
            CppRuntimeException(message + ": " + sqlite3_errstr(sqliteCode) + "!") {}
};

} // namespace zserio

#endif // ifndef ZSERIO_SQLITE_EXCEPTION_H_INC
