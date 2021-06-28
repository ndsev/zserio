#ifndef ZSERIO_SQLITE_FINALIZER_H_INC
#define ZSERIO_SQLITE_FINALIZER_H_INC

#include "sqlite3.h"

namespace zserio
{

/**
 * Helper class intended to be used as a Deleter in std::unique_ptr.
 */
struct SqliteFinalizer
{
    /**
     * Function call operator which finalizes the given SQLite statement.
     *
     * \param ptr SQLite statement.
     */
    void operator()(sqlite3_stmt* ptr) const
    {
        sqlite3_finalize(ptr);
    }
};

} // namespace zserio

#endif // ZSERIO_SQLITE_FINALIZER_H_INC
