#ifndef ZSERIO_ISQL_DATABASE_WRITER_H_INC
#define ZSERIO_ISQL_DATABASE_WRITER_H_INC

#include <string>
#include <set>

namespace zserio
{

class ISqlDatabaseWriter
{
public:
    virtual void createSchema() = 0;
    virtual void createSchema(const std::set<std::string>& withoutRowIdTableNamesBlackList) = 0;
    virtual void deleteSchema() = 0;
};

} // namespace zserio

#endif // ifndef ZSERIO_ISQL_DATABASE_WRITER_H_INC
