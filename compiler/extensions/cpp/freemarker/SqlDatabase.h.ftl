<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@file_header generatorDescription/>
<#if withValidationCode>
    <#assign needsParameterProvider = sql_db_needs_parameter_provider(fields)/>
</#if>

<@include_guard_begin package.path, name/>

#include <memory>
#include <array>
<@type_includes types.set/>
<@type_includes types.vector/>
<@type_includes types.map/>
<@type_includes types.vector/>
<@type_includes types.string/>
<@type_includes types.uniquePtr/>
#include <zserio/AllocatorHolder.h>
#include <zserio/StringView.h>
#include <zserio/ISqliteDatabase<#if !withWriterCode>Reader</#if>.h>
#include <zserio/SqliteConnection.h>
<#if withValidationCode>
#include <zserio/IValidationObserver.h>
</#if>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

class ${name} : public ::zserio::ISqliteDatabase<#if !withWriterCode>Reader</#if>,
        public ::zserio::AllocatorHolder<${types.allocator.default}>
{
public:
<#if withValidationCode && needsParameterProvider>
    class IParameterProvider
    {
    public:
        virtual ~IParameterProvider() = default;

        <#list fields as field>
            <#if field.hasExplicitParameters>
        virtual ${field.cppTypeName}::IParameterProvider& <@sql_db_table_parameter_provider_getter field/> = 0;
            </#if>
        </#list>
    };

</#if>
    typedef <@map_type_name types.string.name, types.string.name/> TRelocationMap;

    explicit ${name}(const ${types.string.name}& dbFileName,
            const TRelocationMap& tableToDbFileNameRelocationMap = TRelocationMap(),
            const allocator_type& allocator = allocator_type());
    explicit ${name}(const ${types.string.name}& fileName, const allocator_type& allocator);
    explicit ${name}(sqlite3* externalConnection,
            const TRelocationMap& tableToAttachedDbNameRelocationMap = TRelocationMap(),
            const allocator_type& allocator = allocator_type());
    explicit ${name}(sqlite3* externalConnection, const allocator_type& allocator);

    virtual ~${name}() override;

    ${name}(const ${name}&) = delete;
    ${name}& operator=(const ${name}&) = delete;

    ${name}(${name}&&) = delete;
    ${name}& operator=(${name}&&) = delete;

    ::zserio::SqliteConnection& connection() noexcept override;

<#list fields as field>
    ${field.cppTypeName}& ${field.getterName}() noexcept;
</#list>
<#if withWriterCode>

    virtual void createSchema() override;
    virtual void createSchema(const <@set_type_name types.string.name/>& withoutRowIdTableNamesBlackList);
    virtual void deleteSchema() override;
</#if>
<#if withValidationCode>

    void validate(::zserio::IValidationObserver& validationObserver<#rt>
            <#lt><#if needsParameterProvider>, IParameterProvider& parameterProvider</#if>);
</#if>

    <#-- cannot be constexpr since constexpr must be defined inline -->
    static ::zserio::StringView databaseName() noexcept;
    static const ::std::array<::zserio::StringView, ${fields?size}>& tableNames() noexcept;

private:
    void initTables();
    void attachDatabase(::zserio::StringView fileName, ::zserio::StringView attachedDbName);
    void detachDatabases();

<#list fields as field>
    static ::zserio::StringView <@sql_db_table_name_getter field/> noexcept;
</#list>

    ::zserio::SqliteConnection m_db;
    <@vector_type_name types.string.name/> m_attachedDbList;
    TRelocationMap m_tableToAttachedDbNameRelocationMap;

<#list fields as field>
    <@unique_ptr_type_name field.cppTypeName/> <@sql_field_member_name field/>;
</#list>
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
