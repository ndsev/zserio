#ifndef ZSERIO_SQLITE_EXCEPTION_H_INC
#define ZSERIO_SQLITE_EXCEPTION_H_INC

#include "zserio/CppRuntimeException.h"

#include "sqlite3.h"

namespace zserio
{

/** Wrapper class to work with SQLite error code. */
class SqliteErrorCode
{
public:
    /**
     * Constructor.
     *
     * \param sqliteCode SQLite error code.
     */
    explicit SqliteErrorCode(int sqliteCode) :
            m_code(sqliteCode)
    {}

    /**
     * Gets SQLite error string appropriate to the error code.
     *
     * \return English language text that describes the error code. Memory to hold the error message string is
     *         managed by SQLite.
     */
    const char* getErrorString() const
    {
        return sqlite3_errstr(m_code);
    }

private:
    int m_code;
};

/** Exception thrown when an error in an SQLite operation occurs. */
class SqliteException : public CppRuntimeException
{
public:
    using CppRuntimeException::CppRuntimeException;
};

/**
 * Allow to append SqliteErrorCode to CppRuntimeException.
 *
 * \param exception Exception to modify.
 * \param code SQLite error code.
 */
inline CppRuntimeException& operator<<(CppRuntimeException& exception, SqliteErrorCode code)
{
    return exception << code.getErrorString();
}

} // namespace zserio

#endif // ifndef ZSERIO_SQLITE_EXCEPTION_H_INC
