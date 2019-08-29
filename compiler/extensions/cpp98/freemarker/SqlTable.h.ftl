<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <vector>
#include <string>
#include <sqlite3.h>
<@system_includes headerSystemIncludes, false/>
#include <zserio/SqliteConnection.h>
<#if withInspectorCode>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/inspector/BlobInspectorTree.h>
</#if>

<#if withInspectorCode>
#include "<@include_path rootPackage.path, "ISqlTableInspector.h"/>"
</#if>
#include "<@include_path package.path, "${rowName}.h"/>"
<@user_includes headerUserIncludes, false/>

<@namespace_begin package.path/>

<#assign needsParameterProvider=explicitParameters?has_content/>
class ${name}<#if withInspectorCode> : public ${rootPackage.name}::ISqlTableInspector</#if>
{
public:
<#if needsParameterProvider>
    class IParameterProvider
    {
    public:
        <#list explicitParameters as parameter>
        virtual <@sql_parameter_provider_return_type parameter/> <@sql_parameter_provider_getter_name parameter/>(sqlite3_stmt& statement) = 0;
        </#list>

        virtual ~IParameterProvider()
        {}
    };

</#if>
    ${name}(zserio::SqliteConnection& db, const std::string& tableName, const std::string& attachedDbName = "");
    ~${name}();
<#if withWriterCode>

    void createTable();
    <#if sql_table_has_non_virtual_field(fields) && isWithoutRowId>
    void createOrdinaryRowIdTable();
    </#if>
    void deleteTable();
</#if>

    void read(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if>std::vector<${rowName}>& rows) const;
    void read(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if>const std::string& condition,
            std::vector<${rowName}>& rows) const;
<#if withWriterCode>
    void write(std::vector<${rowName}>& rows);
    void update(${rowName}& row, const std::string& whereCondition);
</#if>
<#if withInspectorCode>

    virtual bool convertBitStreamToBlobTree(const std::string& blobName, zserio::BitStreamReader& reader,
            ${rootPackage.name}::IInspectorParameterProvider& parameterProvider,
            zserio::BlobInspectorTree& tree) const;
    virtual bool convertBlobTreeToBitStream(const std::string& blobName,
            const zserio::BlobInspectorTree& tree,
            ${rootPackage.name}::IInspectorParameterProvider& parameterProvider,
            zserio::BitStreamWriter& writer) const;
    virtual bool doesBlobExist(const std::string& blobName) const;
</#if>

private:
    static void readRow(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>sqlite3_stmt& statement,
            std::vector<${rowName}>& rows);
<#if withWriterCode>
    static void writeRow(${rowName}& row, sqlite3_stmt& statement);

    void appendCreateTableToQuery(std::string& sqlQuery);
</#if>

    void appendTableNameToQuery(std::string& sqlQuery) const;

    zserio::SqliteConnection& m_db;
    const std::string m_name;
    const std::string m_attachedDbName;
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
