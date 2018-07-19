<#include "FileHeader.inc.ftl">
<#include "InstantiateTemplate.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

<#if withInspectorCode>
#include <map>
</#if>
#include <string>
#include <vector>
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
#include "<@include_path rootPackage.path, "ISqlTableInspector.h"/>"
#include "<@include_path rootPackage.path, "IInspectorParameterProvider.h"/>"
</#if>
<@user_includes headerUserIncludes, false/>

<@namespace_begin package.path/>

class ${name} : public zserio::SqlDatabase<#rt>
        <#lt><#if withWriterCode>, public zserio::ISqlDatabaseWriter</#if><#if withInspectorCode>,
        public ${rootPackage.name}::ISqlDatabaseInspector</#if>
{
public:
    ${name}();
    explicit ${name}(sqlite3* externalConnection);
    ${name}(const std::string& fileName);

    void open(sqlite3* externalConnection);
    void open(const std::string& fileName);
<#if fields?has_content>

    <#list fields as field>
    ${field.cppTypeName}& ${field.getterName}();
    </#list>
</#if>
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
<#if withValidationCode>

    void validate(/*TODO*/);
</#if>

    static const char* getDatabaseName();
    static void fillTableNames(std::vector<std::string>& tableNames);
<#if fields?has_content>

private:
    <#if withInspectorCode>
    void fillTableMap();
    ${rootPackage.name}::ISqlTableInspector* findTableByName(const std::string& tableName) const;

    </#if>
    static const char DATABASE_NAME[];
    <#list fields as field>
    static const char ${field.name?upper_case}_TABLE_NAME[];
    </#list>

    <#if withInspectorCode>
    typedef std::map<std::string, ${rootPackage.name}::ISqlTableInspector*> TTableMap;

    TTableMap m_tableMap;

    </#if>
    <#list fields as field>
    ${field.cppTypeName} m_${field.name};
    </#list>
</#if>
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
