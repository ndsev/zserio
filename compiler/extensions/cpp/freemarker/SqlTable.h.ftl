<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <vector>
#include <string>
#include <sqlite3.h>
<@system_includes headerSystemIncludes, false/>
#include <zserio/SqlDatabase.h>
<#if withInspectorCode>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/inspector/BlobInspectorTree.h>
</#if>

#include "<@include_path rootPackage.path, "IParameterProvider.h"/>"
<#if withInspectorCode>
#include "<@include_path rootPackage.path, "ISqlTableInspector.h"/>"
</#if>
#include "<@include_path package.path, "${rowName}.h"/>"
<@user_includes headerUserIncludes, false/>

<@namespace_begin package.path/>

<#assign hasNonVirtualField=false/>
<#list fields as field>
    <#if !field.isVirtual>
        <#assign hasNonVirtualField=true/>
        <#break>
    </#if>
</#list>
class ${name}<#if withInspectorCode> : public ${rootPackage.name}::ISqlTableInspector</#if>
{
public:
    ${name}(zserio::SqlDatabase& db, const std::string& tableName);
    ~${name}();
<#if withWriterCode>

    void createTable();
    <#if hasNonVirtualField && isWithoutRowId>
    void createOrdinaryRowIdTable();
    </#if>
    void deleteTable();
</#if>

    void read(${rootPackage.name}::IParameterProvider& parameterProvider, std::vector<${rowName}>& rows) const;
    void read(${rootPackage.name}::IParameterProvider& parameterProvider, const std::string& condition,
            std::vector<${rowName}>& rows) const;
<#if withWriterCode>
    void write(const std::vector<${rowName}>& rows);
    void update(const ${rowName}& row, const std::string& whereCondition);
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
<#if withValidationCode>

    void validate();
</#if>

private:
    static void readRow(${rootPackage.name}::IParameterProvider& parameterProvider,
            sqlite3_stmt& statement, std::vector<${rowName}>& rows);
<#if withWriterCode>
    static void writeRow(const ${rowName}& row, sqlite3_stmt& statement);

    void appendCreateTableToQuery(std::string& sqlQuery);
</#if>

    zserio::SqlDatabase& m_db;
    const std::string m_name;
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
