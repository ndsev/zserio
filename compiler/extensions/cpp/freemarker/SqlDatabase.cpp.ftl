<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<#if withValidationCode>
#include <stdexcept>
</#if>
#include <sqlite3.h>

#include "<@include_path package.path, "${name}.h"/>"

<@namespace_begin package.path/>

const char ${name}::DATABASE_NAME[] = "${name}";
<#list fields as field>
const char ${name}::${field.name?upper_case}_TABLE_NAME[] = "${field.name}";
</#list>

<#macro db_table_initializer fields>
    <#lt><#if fields?has_content> :
        <#list fields as field>
    m_${field.name}(*this, ${field.name?upper_case}_TABLE_NAME)<#if field_has_next>,</#if>
        </#list>
    </#if>
</#macro>
${name}::${name}()<@db_table_initializer fields/><#rt>
{
<#if withInspectorCode>
    fillTableMap();
</#if>
}

${name}::${name}(sqlite3* externalConnection)<@db_table_initializer fields/><#rt>
{
    open(externalConnection);
<#if withInspectorCode>
    fillTableMap();
</#if>
}

${name}::${name}(const std::string& fileName)<@db_table_initializer fields/><#rt>
{
    open(fileName);
<#if withInspectorCode>
    fillTableMap();
</#if>
}

void ${name}::open(sqlite3* externalConnection)
{
    SqlDatabase::open(externalConnection);
}

void ${name}::open(const std::string& fileName)
{
    SqlDatabase::open(fileName, zserio::SqlDatabase::<#if withWriterCode>DB_ACCESS_CREATE<#else>DB_ACCESS_READONLY</#if>);
}
<#list fields as field>

${field.cppTypeName}& ${name}::${field.getterName}()
{
    return m_${field.name};
}
</#list>
<#if withWriterCode>

void ${name}::createSchema()
{
    <#if fields?has_content>
    const bool wasTransactionStarted = startTransaction();

        <#list fields as field>
    m_${field.name}.createTable();
        </#list>

    endTransaction(wasTransactionStarted);
    </#if>
}

    <#assign hasWithoutRowIdTable=false/>
    <#list fields as field>
        <#if field.isWithoutRowIdTable>
            <#assign hasWithoutRowIdTable=true/>
            <#break>
        </#if>
    </#list>
void ${name}::createSchema(const std::set<std::string>&<#if hasWithoutRowIdTable> withoutRowIdTableNamesBlackList</#if>)
{
    <#if fields?has_content>
        <#if hasWithoutRowIdTable>
    const bool wasTransactionStarted = startTransaction();

            <#list fields as field>
                <#if field.isWithoutRowIdTable>
    if (withoutRowIdTableNamesBlackList.find(${field.name?upper_case}_TABLE_NAME) !=
            withoutRowIdTableNamesBlackList.end())
        m_${field.name}.createOrdinaryRowIdTable();
    else
        m_${field.name}.createTable();
                <#else>
    m_${field.name}.createTable();
                </#if>
            </#list>

    endTransaction(wasTransactionStarted);
        <#else>
    createSchema();
        </#if>
    </#if>
}

void ${name}::deleteSchema()
{
    <#if fields?has_content>
    const bool wasTransactionStarted = startTransaction();

        <#list fields as field>
    m_${field.name}.deleteTable();
        </#list>

    endTransaction(wasTransactionStarted);
    </#if>
}
</#if>
<#if withInspectorCode>

bool ${name}::convertBitStreamToBlobTree(const std::string& tableName, const std::string& blobName,
        zserio::BitStreamReader& reader,
        ${rootPackage.name}::IInspectorParameterProvider& parameterProvider,
        zserio::BlobInspectorTree& tree) const
{
    const ${rootPackage.name}::ISqlTableInspector* const table = findTableByName(tableName);
    if (table == NULL)
        return false;

    return table->convertBitStreamToBlobTree(blobName, reader, parameterProvider, tree);
}

bool ${name}::convertBlobTreeToBitStream(const std::string& tableName, const std::string& blobName,
        const zserio::BlobInspectorTree& tree,
        ${rootPackage.name}::IInspectorParameterProvider& parameterProvider,
        zserio::BitStreamWriter& writer) const
{
    const ${rootPackage.name}::ISqlTableInspector* const table = findTableByName(tableName);
    if (table == NULL)
        return false;

    return table->convertBlobTreeToBitStream(blobName, tree, parameterProvider, writer);
}

bool ${name}::doesBlobExist(const std::string& tableName, const std::string& blobName) const
{
    const ${rootPackage.name}::ISqlTableInspector* const table = findTableByName(tableName);
    if (table == NULL)
        return false;

    return table->doesBlobExist(blobName);
}
</#if>
<#if withValidationCode>

void ${name}::validate()
{
    // TODO
    throw std::runtime_error("not implemented");
}
</#if>

const char* ${name}::getDatabaseName()
{
    return DATABASE_NAME;
}

void ${name}::fillTableNames(std::vector<std::string>& tableNames)
{
    tableNames.clear();
    tableNames.resize(${fields?size});
<#list fields as field>
    tableNames[${field_index}] = ${field.name?upper_case}_TABLE_NAME;
</#list>
}
<#if withInspectorCode>

void ${name}::fillTableMap()
{
    <#list fields as field>
    m_tableMap["${field.name}"] = &m_${field.name};
    </#list>
}

${rootPackage.name}::ISqlTableInspector* ${name}::findTableByName(const std::string& tableName) const
{
    const TTableMap::const_iterator itFound = m_tableMap.find(tableName);
    return (itFound == m_tableMap.end()) ? NULL : itFound->second;
}
</#if>

<@namespace_end package.path/>
