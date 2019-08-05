#ifndef ZSERIO_SQLITE_FINALIZER_H_INC
#define ZSERIO_SQLITE_FINALIZER_H_INC

#include <sqlite3.h>

namespace zserio
{

struct SqliteFinalizer
{
    void operator()(sqlite3_stmt* ptr) const
    {
        sqlite3_finalize(ptr);
    }
};

} // namespace zserio

#endif // ZSERIO_SQLITE_FINALIZER_H_INC
