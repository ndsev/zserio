<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<#if withValidationCode>
#include <stdexcept>

</#if>
#include "<@include_path rootPackage.path, "MasterDatabase.h"/>"

<@namespace_begin rootPackage.path/>

MasterDatabase::MasterDatabase()
{
<#if withInspectorCode>
    fillTableToDatabaseMap();
</#if>
}

MasterDatabase::MasterDatabase(sqlite3* externalConnection)
{
    open(externalConnection);
<#if withInspectorCode>
    fillTableToDatabaseMap();
</#if>
}

MasterDatabase::MasterDatabase(const std::string& fileName)
{
    open(fileName);
<#if withInspectorCode>
    fillTableToDatabaseMap();
</#if>
}

MasterDatabase::~MasterDatabase()
{
}

void MasterDatabase::open(sqlite3* externalConnection)
{
    SqlDatabase::open(externalConnection);
}

void MasterDatabase::open(const std::string& fileName)
{
    SqlDatabase::open(fileName, zserio::SqlDatabase::<#if withWriterCode>DB_ACCESS_CREATE<#else>DB_ACCESS_READONLY</#if>);
}
<#if withWriterCode>

void MasterDatabase::createSchema()
{
    <#if databases?has_content>
    const bool wasTransactionStarted = startTransaction();

        <#list databases as database>
    m_${database.name?uncap_first}.createSchema();
        </#list>

    endTransaction(wasTransactionStarted);
    </#if>
}

void MasterDatabase::createSchema(const std::set<std::string>& withoutRowIdTableNamesBlackList)
{
    <#if databases?has_content>
    const bool wasTransactionStarted = startTransaction();

        <#list databases as database>
    m_${database.name?uncap_first}.createSchema(withoutRowIdTableNamesBlackList);
        </#list>

    endTransaction(wasTransactionStarted);
    </#if>
}

void MasterDatabase::deleteSchema()
{
    <#if databases?has_content>
    const bool wasTransactionStarted = startTransaction();

        <#list databases as database>
    m_${database.name?uncap_first}.deleteSchema();
        </#list>

    endTransaction(wasTransactionStarted);
    </#if>
}
</#if>
<#if withInspectorCode>

bool MasterDatabase::convertBitStreamToBlobTree(const std::string& tableName, const std::string& blobName,
        zserio::BitStreamReader& reader, IInspectorParameterProvider& parameterProvider,
        zserio::BlobInspectorTree& tree) const
{
    const ISqlDatabaseInspector* const db = findDatabaseByTableName(tableName);
    if (db == NULL)
        return false;

    return db->convertBitStreamToBlobTree(tableName, blobName, reader, parameterProvider, tree);
}

bool MasterDatabase::convertBlobTreeToBitStream(const std::string& tableName, const std::string& blobName,
        const zserio::BlobInspectorTree& tree, IInspectorParameterProvider& parameterProvider,
        zserio::BitStreamWriter& writer) const
{
    const ISqlDatabaseInspector* const db = findDatabaseByTableName(tableName);
    if (db == NULL)
        return false;

    return db->convertBlobTreeToBitStream(tableName, blobName, tree, parameterProvider, writer);
}

bool MasterDatabase::doesBlobExist(const std::string& tableName, const std::string& blobName) const
{
    const ISqlDatabaseInspector* const db = findDatabaseByTableName(tableName);
    if (db == NULL)
        return false;

    return db->doesBlobExist(tableName, blobName);
}
</#if>
<#if withValidationCode>

void MasterDatabase::validate()
{
    // TODO
    throw std::runtime_error("not implemented");
}
</#if>

void MasterDatabase::doOpen(sqlite3* connection)
{
<#list databases as database>
    m_${database.name?uncap_first}.open(connection);
</#list>
}
<#if withInspectorCode>

void MasterDatabase::fillTableToDatabaseMap()
{
    <#list databases as database>
        <#list database.tableNames as tableName>
    tableToDatabaseMap["${tableName}"] = &m_${database.name?uncap_first};
        </#list>
        <#if database_has_next>

        </#if>
    </#list>
}

${rootPackage.name}::ISqlDatabaseInspector* MasterDatabase::findDatabaseByTableName(const std::string& tableName) const
{
    const TTableToDatabaseMap::const_iterator itFound = tableToDatabaseMap.find(tableName);
    return (itFound == tableToDatabaseMap.end()) ? NULL : itFound->second;
}
</#if>

<@namespace_end rootPackage.path/>
