#ifndef ZSERIO_ISQLITE_DATABASE_H_INC
#define ZSERIO_ISQLITE_DATABASE_H_INC

#include "zserio/ISqliteDatabaseReader.h"

namespace zserio
{

/** Writer interface for generated databases. */
class ISqliteDatabase : public ISqliteDatabaseReader
{
public:
    /**
     * Destructor.
     */
    ~ISqliteDatabase() override {}

    /**
     * Creates database schema.
     */
    virtual void createSchema() = 0;

    /**
     * Deletes database schema.
     */
    virtual void deleteSchema() = 0;
};

} // namespace zserio

#endif // ifndef ZSERIO_ISQLITE_DATABASE_H_INC
