<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@file_header generatorDescription/>

#include "zserio/SqliteException.h"

#include "<@include_path package.path, "${name}.h"/>"
<@namespace_begin package.path/>

<#if withWriterCode>
    <#assign hasWithoutRowIdTable=sql_db_has_without_rowid_table(fields)/>
</#if>
${name}::${name}(const ::std::string& fileName, const TRelocationMap& tableToDbFileNameRelocationMap)
{
    sqlite3 *internalConnection = NULL;
    const int sqliteOpenMode = SQLITE_OPEN_URI | <#rt>
            <#lt><#if withWriterCode>SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE<#else>SQLITE_OPEN_READONLY</#if>;
    const int result = sqlite3_open_v2(fileName.c_str(), &internalConnection, sqliteOpenMode, NULL);
    if (result != SQLITE_OK)
        throw ::zserio::SqliteException("${name}::open(): can't open DB " + fileName, result);

    m_db.reset(internalConnection, ::zserio::SqliteConnection::INTERNAL_CONNECTION);

    TRelocationMap tableToAttachedDbNameRelocationMap;
    ::std::map<::std::string, ::std::string> dbFileNameToAttachedDbNameMap;
    for (TRelocationMap::const_iterator relocationIt = tableToDbFileNameRelocationMap.begin();
            relocationIt != tableToDbFileNameRelocationMap.end(); ++relocationIt)
    {
        const ::std::string& tableName = relocationIt->first;
        const ::std::string& fileName = relocationIt->second;
        ::std::map<::std::string, ::std::string>::const_iterator attachedDbIt =
                dbFileNameToAttachedDbNameMap.find(fileName);
        if(attachedDbIt == dbFileNameToAttachedDbNameMap.end())
        {
            const ::std::string attachedDbName = ::std::string(databaseName()) + "_" + tableName;
            attachDatabase(fileName, attachedDbName);
            attachedDbIt = dbFileNameToAttachedDbNameMap.insert(::std::make_pair(fileName, attachedDbName)).first;
        }
        tableToAttachedDbNameRelocationMap.insert(::std::make_pair(tableName, attachedDbIt->second));
    }

    initTables(tableToAttachedDbNameRelocationMap);
}

${name}::${name}(sqlite3* externalConnection, const TRelocationMap& tableToAttachedDbNameRelocationMap)
{
    m_db.reset(externalConnection, ::zserio::SqliteConnection::EXTERNAL_CONNECTION);
    initTables(tableToAttachedDbNameRelocationMap);
}

${name}::~${name}()
{
    detachDatabases();
}

sqlite3* ${name}::connection() noexcept
{
    return m_db.getConnection();
}

<#list fields as field>

${field.cppTypeName}& ${name}::${field.getterName}() noexcept
{
    return *<@sql_field_member_name field/>;
}
</#list>
<#if withWriterCode>

void ${name}::createSchema()
{
    const bool wasTransactionStarted = m_db.startTransaction();

    <#list fields as field>
    <@sql_field_member_name field/>->createTable();
    </#list>

    m_db.endTransaction(wasTransactionStarted);
}

void ${name}::createSchema(const ::std::set<::std::string>&<#if hasWithoutRowIdTable> withoutRowIdTableNamesBlackList</#if>)
{
    <#if hasWithoutRowIdTable>
    const bool wasTransactionStarted = m_db.startTransaction();

        <#list fields as field>
            <#if field.isWithoutRowIdTable>
    if (withoutRowIdTableNamesBlackList.find(<@sql_db_table_name_getter field/>) !=
            withoutRowIdTableNamesBlackList.end())
        <@sql_field_member_name field/>->createOrdinaryRowIdTable();
    else
        <@sql_field_member_name field/>->createTable();
            <#else>
    <@sql_field_member_name field/>->createTable();
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
    <@sql_field_member_name field/>->deleteTable();
    </#list>

    m_db.endTransaction(wasTransactionStarted);
}
</#if>

const char* ${name}::databaseName() noexcept
{
    return "${name}";
}

<#list fields as field>
constexpr const char* ${name}::<@sql_db_table_name_getter field/> noexcept
{
    return "${field.name}";
}

</#list>
const ::std::array<const char*, ${fields?size}>& ${name}::tableNames() noexcept
{
    static constexpr ::std::array<const char*, ${fields?size}> names =
    {
<#list fields as field>
        <@sql_db_table_name_getter field/><#if !field?is_last>,</#if>
</#list>
    };

    return names; 
}

void ${name}::initTables(const TRelocationMap& tableToAttachedDbNameRelocationMap)
{
    static const char* EMPTY_STR = "";
<#list fields as field>
    <#if field?is_first>
    TRelocationMap::const_iterator relocationIt =
            tableToAttachedDbNameRelocationMap.find(<@sql_db_table_name_getter field/>);
    <#else>
    relocationIt = tableToAttachedDbNameRelocationMap.find(<@sql_db_table_name_getter field/>);
    </#if>
    <@sql_field_member_name field/>.reset(new ${field.cppTypeName}(
            this->m_db, <@sql_db_table_name_getter field/>,
            relocationIt != tableToAttachedDbNameRelocationMap.end() ? relocationIt->second : EMPTY_STR));
    <#if field?has_next>

    </#if>
</#list>
}

void ${name}::attachDatabase(const ::std::string& fileName, const ::std::string& attachedDbName)
{
    ::std::string sqlQuery = "ATTACH DATABASE '";
    sqlQuery += fileName;
    sqlQuery += "' AS ";
    sqlQuery += attachedDbName;

    m_db.executeUpdate(sqlQuery);

    m_attachedDbList.push_back(attachedDbName);
}

void ${name}::detachDatabases()
{
    for (::std::vector<::std::string>::const_iterator attachedDbIt = m_attachedDbList.begin();
            attachedDbIt != m_attachedDbList.end(); ++attachedDbIt)
    {
        const ::std::string sqlQuery = "DETACH DATABASE " + *attachedDbIt;
        m_db.executeUpdate(sqlQuery);
    }
    m_attachedDbList.clear();
}
<@namespace_end package.path/>
