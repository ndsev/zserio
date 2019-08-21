<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <memory>
#include <string>
#include <vector>
#include <array>
#include <set>
#include <map>
#include <zserio/ISqliteDatabase<#if !withWriterCode>Reader</#if>.h>
#include <zserio/SqliteConnection.h>
<@system_includes headerSystemIncludes, false/>

<@user_includes headerUserIncludes, false/>

<@namespace_begin package.path/>

class ${name} : public zserio::ISqliteDatabase<#if !withWriterCode>Reader</#if>
{
public:
    typedef std::map<std::string, std::string> TRelocationMap;

    explicit ${name}(const std::string& fileName,
            const TRelocationMap& tableToDbFileNameRelocationMap = TRelocationMap());
    explicit ${name}(sqlite3* externalConnection,
            const TRelocationMap& tableToAttachedDbNameRelocationMap = TRelocationMap());

    ~${name}();

    ${name}(const ${name}&) = delete;
    ${name}& operator=(const ${name}&) = delete;

    ${name}(${name}&&) = delete;
    ${name}& operator=(${name}&&) = delete;

    sqlite3* connection() noexcept;

<#list fields as field>
    ${field.cppTypeName}& ${field.getterName}() noexcept;
</#list>
<#if withWriterCode>

    virtual void createSchema();
    virtual void createSchema(const std::set<std::string>& withoutRowIdTableNamesBlackList);
    virtual void deleteSchema();
</#if>

    <#-- cannot be constexpr since consexpr must be defined inline -->
    static const char* databaseName() noexcept;
    static const std::array<const char*, ${fields?size}>& tableNames() noexcept;

private:
    void initTables(const TRelocationMap& tableToAttachedDbNameRelocationMap);
    void attachDatabase(const std::string& fileName, const std::string& attachedDbName);
    void detachDatabases();

<#list fields as field>
    <#-- can be constexpr since it is privated and therefore used only from a single cpp unit -->
    static constexpr const char* <@sql_db_table_name_getter field/> noexcept;
</#list>

    zserio::SqliteConnection m_db;
    std::vector<std::string> m_attachedDbList;

<#list fields as field>
    std::unique_ptr<${field.cppTypeName}> <@sql_field_member_name field/>;
</#list>
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
