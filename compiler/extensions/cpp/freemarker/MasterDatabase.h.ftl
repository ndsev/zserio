<#include "FileHeader.inc.ftl">
<#include "InstantiateTemplate.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "MasterDatabase"/>

<#if withInspectorCode>
#include <map>
</#if>
#include <string>
#include <set>
#include <sqlite3.h>
#include <zserio/SqlDatabase.h>
<#if withWriterCode>
#include <zserio/ISqlDatabaseWriter.h>
</#if>
<#if withInspectorCode>
#include <zserio/BitStreamReader.h>
</#if>
<@system_includes headerSystemIncludes, false/>

<#if withInspectorCode>
#include "<@include_path rootPackage.path, "ISqlDatabaseInspector.h"/>"
#include "<@include_path rootPackage.path, "IInspectorParameterProvider.h"/>"
</#if>
<@user_includes headerUserIncludes, false/>

<@namespace_begin rootPackage.path/>

class MasterDatabase : public zserio::SqlDatabase<#if withWriterCode>, public zserio::ISqlDatabaseWriter</#if>
{
public:
    MasterDatabase();
    explicit MasterDatabase(sqlite3* externalConnection);
    MasterDatabase(const std::string& fileName);
    ~MasterDatabase();

    void open(sqlite3* externalConnection);
    void open(const std::string& fileName);
<#if withWriterCode>

    virtual void createSchema();
    virtual void createSchema(const std::set<std::string>& withoutRowIdTableNamesBlackList);
    virtual void deleteSchema();
</#if>
<#if withInspectorCode>

    bool convertBitStreamToBlobTree(const std::string& tableName, const std::string& blobName,
            zserio::BitStreamReader& reader, IInspectorParameterProvider& parameterProvider,
            zserio::BlobInspectorTree& tree) const;
    bool convertBlobTreeToBitStream(const std::string& tableName, const std::string& blobName,
            const zserio::BlobInspectorTree& tree, IInspectorParameterProvider& parameterProvider,
            zserio::BitStreamWriter& writer) const;
    bool doesBlobExist(const std::string& tableName, const std::string& blobName) const;
</#if>
<#if withValidationCode>

    void validate();
</#if>

private:
    virtual void doOpen(sqlite3* connection);
<#if withInspectorCode>

    void fillTableToDatabaseMap();
    ${rootPackage.name}::ISqlDatabaseInspector* findDatabaseByTableName(const std::string& tableName) const;

    typedef std::map<std::string, ${rootPackage.name}::ISqlDatabaseInspector*> TTableToDatabaseMap;

    TTableToDatabaseMap tableToDatabaseMap;
</#if>

<#list databases as database>
    ${database.typeName} m_${database.name?uncap_first};
</#list>
};

<@namespace_end rootPackage.path/>

<@include_guard_end rootPackage.path, "MasterDatabase"/>
