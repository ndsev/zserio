#ifndef ZSERIO_SQLITE_EXCEPTION_H_INC
#define ZSERIO_SQLITE_EXCEPTION_H_INC

#include <string>
#include <sqlite3.h>

#include "CppRuntimeException.h"

namespace zserio
{

class SqliteException : public CppRuntimeException
{
public:
    explicit SqliteException(const std::string& message) : CppRuntimeException(message) {}

    SqliteException(const std::string& message, int sqliteCode) :
            CppRuntimeException(message + ": " + sqlite3_errstr(sqliteCode) + "!") {}
};

} // namespace zserio

#endif // ifndef ZSERIO_SQLITE_EXCEPTION_H_INC
