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
    virtual ~ISqliteDatabase() {}

    virtual void createSchema() = 0;
    virtual void createSchema(const std::set<std::string>& withoutRowIdTableNamesBlackList) = 0;
    virtual void deleteSchema() = 0;
};

} // namespace zserio

#endif // ifndef ZSERIO_ISQLITE_DATABASE_H_INC
