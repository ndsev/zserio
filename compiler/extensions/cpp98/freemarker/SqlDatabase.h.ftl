<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "InstantiateTemplate.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <string>
#include <vector>
#include <set>
#include <map>
#include <zserio/ISqliteDatabase<#if !withWriterCode>Reader</#if>.h>
#include <zserio/SqliteConnection.h>
<#if withInspectorCode>
#include <zserio/BitStreamReader.h>
</#if>
<@system_includes headerSystemIncludes, false/>

<#if withInspectorCode>
#include "<@include_path rootPackage.path, "ISqlDatabaseInspector.h"/>"
#include "<@include_path rootPackage.path, "ISqlTableInspector.h"/>"
#include "<@include_path rootPackage.path, "IInspectorParameterProvider.h"/>"
</#if>
<@user_includes headerUserIncludes, false/>

<@namespace_begin package.path/>

class ${name} : public zserio::ISqliteDatabase<#if !withWriterCode>Reader</#if><#if withInspectorCode>,
        public ${rootPackage.name}::ISqlDatabaseInspector</#if>
{
public:
    typedef std::map<std::string, std::string> TRelocationMap;

    explicit ${name}(const std::string& fileName,
            const TRelocationMap& tableToDbFileNameRelocationMap = TRelocationMap());
    explicit ${name}(sqlite3* externalConnection,
            const TRelocationMap& tableToAttachedDbNameRelocationMap = TRelocationMap());

    ~${name}();

    sqlite3* connection();

<#list fields as field>
    ${field.cppTypeName}& ${field.getterName}();
</#list>
<#if withWriterCode>

    virtual void createSchema();
    virtual void createSchema(const std::set<std::string>& withoutRowIdTableNamesBlackList);
    virtual void deleteSchema();
</#if>
<#if withInspectorCode>

    virtual bool convertBitStreamToBlobTree(const std::string& tableName, const std::string& blobName,
            zserio::BitStreamReader& reader,
            ${rootPackage.name}::IInspectorParameterProvider& parameterProvider,
            zserio::BlobInspectorTree& tree) const;
    virtual bool convertBlobTreeToBitStream(const std::string& tableName, const std::string& blobName,
            const zserio::BlobInspectorTree& tree,
            ${rootPackage.name}::IInspectorParameterProvider& parameterProvider,
            zserio::BitStreamWriter& writer) const;
    virtual bool doesBlobExist(const std::string& tableName, const std::string& blobName) const;
</#if>

    static const char* databaseName();
    static void fillTableNames(std::vector<std::string>& tableNames);

private:
    void initTables(const TRelocationMap& tableToAttachedDbNameRelocationMap);
    void attachDatabase(const std::string& fileName, const std::string& attachedDbName);
    void detachDatabases();

<#list fields as field>
    static const char* <@sql_db_table_name_getter field/>;
</#list>

    <#if withInspectorCode>
    void fillTableMap();
    ${rootPackage.name}::ISqlTableInspector* findTableByName(const std::string& tableName) const;

    </#if>
    zserio::SqliteConnection m_db;
    std::vector<std::string> m_attachedDbList;

<#list fields as field>
    ${field.cppTypeName}* <@sql_db_field_member_name field/>;
</#list>
    <#if withInspectorCode>

    typedef std::map<std::string, ${rootPackage.name}::ISqlTableInspector*> TTableMap;
    TTableMap m_tableMap;
    </#if>
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
