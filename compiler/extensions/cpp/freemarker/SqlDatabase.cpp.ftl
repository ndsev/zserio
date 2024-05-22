<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@file_header generatorDescription/>
<#if withValidationCode>
    <#assign needsParameterProvider = sql_db_needs_parameter_provider(fields)/>
</#if>

#include <zserio/SqliteException.h>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>
</#if>
<#if withValidationCode>
#include <zserio/ValidationSqliteUtil.h>
</#if>

<@user_include package.path, "${name}.h"/>
<@namespace_begin package.path/>

<#if withWriterCode>
    <#assign hasWithoutRowIdTable=sql_db_has_without_rowid_table(fields)/>
</#if>
${name}::${name}(const ${types.string.name}& dbFileName, const TRelocationMap& tableToDbFileNameRelocationMap,
        const allocator_type& allocator) :
        ::zserio::AllocatorHolder<allocator_type>(allocator),
        m_tableToAttachedDbNameRelocationMap(allocator)
{
    sqlite3 *internalConnection = nullptr;
    const int sqliteOpenMode = SQLITE_OPEN_URI | <#rt>
            <#lt><#if withWriterCode>SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE<#else>SQLITE_OPEN_READONLY</#if>;
    const int result = sqlite3_open_v2(dbFileName.c_str(), &internalConnection, sqliteOpenMode, nullptr);
    m_db.reset(internalConnection, ::zserio::SqliteConnection::INTERNAL_CONNECTION);
    if (result != SQLITE_OK)
    {
        throw ::zserio::SqliteException("${name}::open(): can't open DB ") << dbFileName.c_str() << ": " <<
                ::zserio::SqliteErrorCode(result);
    }

    <@map_type_name "::zserio::StringView" types.string.name/> dbFileNameToAttachedDbNameMap(
            get_allocator_ref());
    for (const auto& relocation : tableToDbFileNameRelocationMap)
    {
        const ${types.string.name}& tableName = relocation.first;
        const ${types.string.name}& fileName = relocation.second;
        auto attachedDbIt = dbFileNameToAttachedDbNameMap.find(fileName);
        if (attachedDbIt == dbFileNameToAttachedDbNameMap.end())
        {
            ${types.string.name} attachedDbName =
                    ::zserio::stringViewToString(databaseName(), get_allocator_ref()) + "_" + tableName;
            attachDatabase(fileName, attachedDbName);
            attachedDbIt = dbFileNameToAttachedDbNameMap.emplace(fileName, ::std::move(attachedDbName)).first;
        }
        m_tableToAttachedDbNameRelocationMap.emplace(
                ${types.string.name}(tableName, get_allocator_ref()),
                ${types.string.name}(attachedDbIt->second, get_allocator_ref()));
    }

    initTables();
}

${name}::${name}(const ${types.string.name}& dbFileName, const allocator_type& allocator) :
        ${name}(dbFileName, TRelocationMap(allocator), allocator)
{}

${name}::${name}(sqlite3* externalConnection, const TRelocationMap& tableToAttachedDbNameRelocationMap,
        const allocator_type& allocator) :
        ::zserio::AllocatorHolder<allocator_type>(allocator),
        m_tableToAttachedDbNameRelocationMap(tableToAttachedDbNameRelocationMap, allocator)
{
    m_db.reset(externalConnection, ::zserio::SqliteConnection::EXTERNAL_CONNECTION);
    initTables();
}

${name}::${name}(sqlite3* externalConnection, const allocator_type& allocator) :
        ${name}(externalConnection, TRelocationMap(allocator), allocator)
{}

${name}::~${name}()
{
    detachDatabases();
}
<#if withTypeInfoCode>

const ${types.typeInfo.name}& ${name}::typeInfo()
{
    static const std::array<::zserio::BasicTableInfo<allocator_type>, ${fields?size}> tables = {
    <#list fields as field>
        <@table_info field field?has_next/>
    </#list>
    };

    static const ::zserio::SqlDatabaseTypeInfo<allocator_type> typeInfo = {
        ::zserio::makeStringView("${schemaTypeName}"), tables
    };

    return typeInfo;
}
</#if>

::zserio::SqliteConnection& ${name}::connection() noexcept
{
    return m_db;
}
<#list fields as field>

${field.typeInfo.typeFullName}& ${name}::${field.getterName}() noexcept
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

void ${name}::createSchema(const <@set_type_name types.string.name/>&<#if hasWithoutRowIdTable> withoutRowIdTableNamesBlackList</#if>)
{
    <#if hasWithoutRowIdTable>
    const bool wasTransactionStarted = m_db.startTransaction();

        <#list fields as field>
            <#if field.isWithoutRowIdTable>
    if (withoutRowIdTableNamesBlackList.find(::zserio::stringViewToString(
            <@sql_db_table_name_getter field/>, get_allocator_ref())) != withoutRowIdTableNamesBlackList.end())
    {
        <@sql_field_member_name field/>->createOrdinaryRowIdTable();
    }
    else
    {
        <@sql_field_member_name field/>->createTable();
    }
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
<#if withValidationCode>

void ${name}::validate(::zserio::IValidationObserver& validationObserver<#rt>
        <#lt><#if needsParameterProvider>, IParameterProvider& parameterProvider</#if>)
{
    validationObserver.beginDatabase(${fields?size});
    bool continueValidation = true;
    size_t numberOfValidatedTables = 0;

    <#list fields as field>
    if (<#if !field?is_first>continueValidation && </#if><@sql_field_member_name field/>->validate(validationObserver<#rt>
        <#if field.hasExplicitParameters>
            <#lt>,
            parameterProvider.<@sql_db_table_parameter_provider_getter field/><#rt>
        </#if>
        <#lt>, continueValidation))
    {
        ++numberOfValidatedTables;
    }
    </#list>

    validationObserver.endDatabase(numberOfValidatedTables);
}
</#if>

::zserio::StringView ${name}::databaseName() noexcept
{
    return ::zserio::makeStringView("${name}");
}

<#list fields as field>
::zserio::StringView ${name}::<@sql_db_table_name_getter field/> noexcept
{
    return ::zserio::makeStringView("${field.name}");
}

</#list>
const ::std::array<::zserio::StringView, ${fields?size}>& ${name}::tableNames() noexcept
{
    static const ::std::array<::zserio::StringView, ${fields?size}> names =
    {
<#list fields as field>
        <@sql_db_table_name_getter field/><#if !field?is_last>,</#if>
</#list>
    };

    return names;
}

void ${name}::initTables()
{
    static ::zserio::StringView EMPTY_STR = ::zserio::StringView();
<#list fields as field>
    <#if field?is_first>
    auto relocationIt = m_tableToAttachedDbNameRelocationMap.find(
            ::zserio::stringViewToString(<@sql_db_table_name_getter field/>, get_allocator_ref()));
    <#else>
    relocationIt = m_tableToAttachedDbNameRelocationMap.find(
            ::zserio::stringViewToString(<@sql_db_table_name_getter field/>, get_allocator_ref()));
    </#if>
    <@sql_field_member_name field/> = ::zserio::allocate_unique<${field.typeInfo.typeFullName}>(
            get_allocator_ref(), this->m_db, <@sql_db_table_name_getter field/>,
            relocationIt != m_tableToAttachedDbNameRelocationMap.end() ? relocationIt->second : EMPTY_STR,
            get_allocator_ref());
    <#if field?has_next>

    </#if>
</#list>
}

void ${name}::attachDatabase(::zserio::StringView fileName, ::zserio::StringView attachedDbName)
{
    ${types.string.name} sqlQuery(get_allocator_ref());
    sqlQuery += "ATTACH DATABASE '";
    sqlQuery += fileName;
    sqlQuery += "' AS ";
    sqlQuery += attachedDbName;

    m_db.executeUpdate(sqlQuery);

    m_attachedDbList.push_back(::zserio::stringViewToString(attachedDbName, get_allocator_ref()));
}

void ${name}::detachDatabases()
{
    for (const auto& attachedDb : m_attachedDbList)
    {
        try
        {
            ${types.string.name} sqlQuery(get_allocator_ref());
            sqlQuery += "DETACH DATABASE ";
            sqlQuery += attachedDb;
            m_db.executeUpdate(sqlQuery);
        }
        catch (const ::zserio::SqliteException&)
        {
            // ignore since we have no logging sub-system and we need to prevent exception in SQLDatabase destructor
        }
    }
    m_attachedDbList.clear();
}
<@namespace_end package.path/>
