#ifndef ZSERIO_ISQLITE_DATABASE_H_INC
#define ZSERIO_ISQLITE_DATABASE_H_INC

#include <string>
#include <set>

#include "ISqliteDatabaseReader.h"

namespace zserio
{

/** Writer interface for generated databases. */
class ISqliteDatabase : public ISqliteDatabaseReader
{
public:
    /**
     * Virtual destructor.
     */
    virtual ~ISqliteDatabase() {}

    /**
     * Creates database schema.
     */
    virtual void createSchema() = 0;

    /**
     * Creates database schema with the given black list.
     *
     * \param withoutRowIdTableNamesBlackList Black list with tables which should ignore WITHOUT ROWID.
     */
    virtual void createSchema(const std::set<std::string>& withoutRowIdTableNamesBlackList) = 0;

    /**
     * Deletes database schema.
     */
    virtual void deleteSchema() = 0;
};

} // namespace zserio

#endif // ifndef ZSERIO_ISQLITE_DATABASE_H_INC
