<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@file_header generatorDescription/>

#include "zserio/SqliteException.h"

#include "<@include_path package.path, "${name}.h"/>"

<@namespace_begin package.path/>

<#if withWriterCode>
    <#assign hasWithoutRowIdTable=sql_db_has_without_rowid_table(fields)/>
</#if>
<#macro db_table_initializer fields>
    <#list fields as field>
        m_${field.name}(NULL)<#if field?has_next>,</#if>
    </#list>
</#macro>
${name}::${name}(const std::string& fileName, const TRelocationMap& tableToDbFileNameRelocationMap) :
        <@db_table_initializer fields/>
{
    sqlite3 *internalConnection = NULL;
    const int sqliteOpenMode = SQLITE_OPEN_URI | <#if withWriterCode>SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE<#else>SQLITE_OPEN_READONLY</#if>;
    const int result = sqlite3_open_v2(fileName.c_str(), &internalConnection, sqliteOpenMode, NULL);
    if (result != SQLITE_OK)
        throw zserio::SqliteException("${name}::open(): can't open DB " + fileName, result);

    m_db.reset(internalConnection, zserio::SqliteConnection::INTERNAL_CONNECTION);

    TRelocationMap tableToAttachedDbNameRelocationMap;
    std::map<std::string, std::string> dbFileNameToAttachedDbNameMap;
    for (TRelocationMap::const_iterator relocationIt = tableToDbFileNameRelocationMap.begin();
            relocationIt != tableToDbFileNameRelocationMap.end(); ++relocationIt)
    {
        const std::string& tableName = relocationIt->first;
        const std::string& fileName = relocationIt->second;
        std::map<std::string, std::string>::const_iterator attachedDbIt =
                dbFileNameToAttachedDbNameMap.find(fileName);
        if(attachedDbIt == dbFileNameToAttachedDbNameMap.end())
        {
            const std::string attachedDbName = std::string(databaseName()) + "_" + tableName;
            attachDatabase(fileName, attachedDbName);
            attachedDbIt = dbFileNameToAttachedDbNameMap.insert(std::make_pair(fileName, attachedDbName)).first;
        }
        tableToAttachedDbNameRelocationMap.insert(std::make_pair(tableName, attachedDbIt->second));
    }

    initTables(tableToAttachedDbNameRelocationMap);
<#if withInspectorCode>
    fillTableMap();
</#if>
}

${name}::${name}(sqlite3* externalConnection, const TRelocationMap& tableToAttachedDbNameRelocationMap) :
        <@db_table_initializer fields/>
{
    m_db.reset(externalConnection, zserio::SqliteConnection::EXTERNAL_CONNECTION);
    initTables(tableToAttachedDbNameRelocationMap);
<#if withInspectorCode>
    fillTableMap();
</#if>
}

${name}::~${name}()
{
<#list fields as field>
    delete m_${field.name};
</#list>
    detachDatabases();
}

sqlite3* ${name}::connection()
{
    return m_db.getConnection();
}

void ${name}::executeUpdate(const std::string& sqlQuery)
{
    m_db.executeUpdate(sqlQuery);
}

sqlite3_stmt* ${name}::prepareStatement(const std::string& sqlQuery)
{
    return m_db.prepareStatement(sqlQuery);
}
<#list fields as field>

${field.cppTypeName}& ${name}::${field.getterName}()
{
    return *m_${field.name};
}
</#list>
<#if withWriterCode>

void ${name}::createSchema()
{
    const bool wasTransactionStarted = m_db.startTransaction();

    <#list fields as field>
    m_${field.name}->createTable();
    </#list>

    m_db.endTransaction(wasTransactionStarted);
}

void ${name}::createSchema(const std::set<std::string>&<#if hasWithoutRowIdTable> withoutRowIdTableNamesBlackList</#if>)
{
    <#if hasWithoutRowIdTable>
    const bool wasTransactionStarted = m_db.startTransaction();

        <#list fields as field>
            <#if field.isWithoutRowIdTable>
    if (withoutRowIdTableNamesBlackList.find(<@sql_db_table_name_getter field/>) !=
            withoutRowIdTableNamesBlackList.end())
        m_${field.name}->createOrdinaryRowIdTable();
    else
        m_${field.name}->createTable();
            <#else>
    m_${field.name}->createTable();
            </#if>
        </#list>

    m_db.endTransaction(wasTransactionStarted);
    <#else>
    createSchema();
    </#if>
}

void ${name}::deleteSchema()
{
    const bool wasTransactionStarted = m_db.startTransaction();

    <#list fields as field>
    m_${field.name}->deleteTable();
    </#list>

    m_db.endTransaction(wasTransactionStarted);
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

const char* ${name}::databaseName()
{
    return "${name}";
}

<#list fields as field>
const char* ${name}::<@sql_db_table_name_getter field/>
{
    return "${field.name}";
}

</#list>
void ${name}::fillTableNames(std::vector<std::string>& tableNames)
{
    tableNames.clear();
    tableNames.resize(${fields?size});
<#list fields as field>
    tableNames[${field_index}] = <@sql_db_table_name_getter field/>;
</#list>
}

void ${name}::initTables(const TRelocationMap& tableToAttachedDbNameRelocationMap)
{
    try
    {
        static const char* EMPTY_STR = "";
<#list fields as field>
    <#if field?is_first>
        TRelocationMap::const_iterator relocationIt =
                tableToAttachedDbNameRelocationMap.find(<@sql_db_table_name_getter field/>);
    <#else>
        relocationIt = tableToAttachedDbNameRelocationMap.find(<@sql_db_table_name_getter field/>);
    </#if>
        m_${field.name} = new ${field.cppTypeName}(
                this->m_db, <@sql_db_table_name_getter field/>,
                relocationIt != tableToAttachedDbNameRelocationMap.end() ? relocationIt->second : EMPTY_STR);
    <#if field?has_next>

    </#if>
</#list>
    }
    catch (...)
    {
<#list fields as field>
        <#-- deleting NULL pointer is ok -->
        delete m_${field.name};
</#list>
        throw;
    }
}

void ${name}::attachDatabase(const std::string& fileName, const std::string& attachedDbName)
{
    std::string sqlQuery = "ATTACH DATABASE '";
    sqlQuery += fileName;
    sqlQuery += "' AS ";
    sqlQuery += attachedDbName;

    m_db.executeUpdate(sqlQuery);

    m_attachedDbList.push_back(attachedDbName);
}

void ${name}::detachDatabases()
{
    for (std::vector<std::string>::const_iterator attachedDbIt = m_attachedDbList.begin();
            attachedDbIt != m_attachedDbList.end(); ++attachedDbIt)
    {
        const std::string sqlQuery = "DETACH DATABASE " + *attachedDbIt;
        m_db.executeUpdate(sqlQuery);
    }
    m_attachedDbList.clear();
}
<#if withInspectorCode>

void ${name}::fillTableMap()
{
    <#list fields as field>
    m_tableMap["${field.name}"] = m_${field.name};
    </#list>
}

${rootPackage.name}::ISqlTableInspector* ${name}::findTableByName(const std::string& tableName) const
{
    const TTableMap::const_iterator itFound = m_tableMap.find(tableName);
    return (itFound == m_tableMap.end()) ? NULL : itFound->second;
}
</#if>

<@namespace_end package.path/>
