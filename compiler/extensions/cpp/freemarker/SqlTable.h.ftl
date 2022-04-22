<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <memory>
#include <sqlite3.h>
<@type_includes types.vector/>
<@type_includes types.string/>
#include <zserio/AllocatorHolder.h>
#include <zserio/Span.h>
#include <zserio/StringView.h>
#include <zserio/SqliteConnection.h>
#include <zserio/SqliteFinalizer.h>
<#if withValidationCode>
#include <zserio/IValidationObserver.h>
#include <zserio/ValidationSqliteUtil.h>
</#if>
<#if withTypeInfoCode>
#include <zserio/ITypeInfo.h>
</#if>
<@type_includes types.inplaceOptionalHolder/>
<@system_includes headerSystemIncludes/>
<@user_includes headerUserIncludes/>
<@namespace_begin package.path/>

<#assign needsParameterProvider=explicitParameters?has_content/>
<#assign hasBlobField=sql_table_has_blob_field(fields)/>
<#if withValidationCode>
    <#assign hasValidatableField=sql_table_has_validatable_field(fields)/>
    <#assign hasNonVirtualField=sql_table_has_non_virtual_field(fields)/>
</#if>
class ${name} : public ::zserio::AllocatorHolder<${types.allocator.default}>
{
public:
<#if needsParameterProvider>
    class Row;

    class IParameterProvider
    {
    public:
        virtual ~IParameterProvider() = default;

        <#list explicitParameters as parameter>
        virtual <@sql_parameter_provider_return_type parameter/> <@sql_parameter_provider_getter_name parameter/>(Row& currentRow) = 0;
        </#list>
    };

</#if>
    class Row
    {
    public:
<#if hasImplicitParameters>
        Row() = default;
        ~Row() = default;

        Row(const Row& other);
        Row& operator=(const Row& other);

        Row(Row&& other);
        Row& operator=(Row&& other);

</#if>
<#list fields as field>
    <#if !field.typeInfo.isSimple>
        ${field.typeInfo.typeFullName}& ${field.getterName}();
        const ${field.typeInfo.typeFullName}& ${field.getterName}() const;
    <#else>
        ${field.typeInfo.typeFullName} ${field.getterName}() const;
    </#if>
    <#if !field.typeInfo.isSimple>
        void ${field.setterName}(const ${field.typeInfo.typeFullName}& <@sql_field_argument_name field/>);
        void ${field.setterName}(${field.typeInfo.typeFullName}&& <@sql_field_argument_name field/>);
    <#else>
        void ${field.setterName}(${field.typeInfo.typeFullName} <@sql_field_argument_name field/>);
    </#if>
        void ${field.resetterName}();
        bool ${field.isSetIndicatorName}() const;

</#list>
<#if withWriterCode>
    <#if needsChildrenInitialization>
        void initializeChildren(<#if needsParameterProvider>IParameterProvider& parameterProvider</#if>);

    </#if>
    <#if hasBlobField>
        void initializeOffsets();

    </#if>
</#if>
    private:
<#if hasImplicitParameters>
        void reinitializeBlobs();

</#if>
<#list fields as field>
        ${types.inplaceOptionalHolder.name}<${field.typeInfo.typeFullName}> <@sql_field_member_name field/>;
</#list>
    };

    class Reader : public ::zserio::AllocatorHolder<${types.allocator.default}>
    {
    public:
        ~Reader() = default;

        Reader(const Reader&) = delete;
        Reader& operator=(const Reader&) = delete;

        Reader(Reader&&) = default;
        Reader& operator=(Reader&&) = delete;

        bool hasNext() const noexcept;
        Row next();

    private:
        explicit Reader(::zserio::SqliteConnection& db, <#rt>
                <#lt><#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
                <#lt>const ${types.string.name}& sqlQuery, const allocator_type& allocator = allocator_type());
        friend class ${name};

        void makeStep();

<#if needsParameterProvider>
        IParameterProvider& m_parameterProvider;
</#if>
        ::std::unique_ptr<sqlite3_stmt, ::zserio::SqliteFinalizer> m_stmt;
        int m_lastResult;
    };

    ${name}(::zserio::SqliteConnection& db, ::zserio::StringView tableName,
            ::zserio::StringView attachedDbName = ::zserio::StringView(),
            const allocator_type& allocator = allocator_type());
    ${name}(::zserio::SqliteConnection& db, ::zserio::StringView tableName, const allocator_type& allocator);

    ~${name}() = default;

    ${name}(const ${name}&) = delete;
    ${name}& operator=(const ${name}&) = delete;

    ${name}(${name}&&) = delete;
    ${name}& operator=(${name}&&) = delete;
<#if withTypeInfoCode>

    static const ::zserio::ITypeInfo& typeInfo();
</#if>
<#if withWriterCode>

    void createTable();
    <#if sql_table_has_non_virtual_field(fields) && isWithoutRowId>
    void createOrdinaryRowIdTable();
    </#if>
    void deleteTable();
</#if>

    Reader createReader(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>::zserio::StringView condition = ::zserio::StringView()) const;
<#if withWriterCode>
    void write(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>::zserio::Span<Row> rows);
    void update(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>Row& row, ::zserio::StringView whereCondition);
</#if>
<#if withValidationCode>

    bool validate(::zserio::IValidationObserver& validationObserver<#rt>
            <#lt><#if needsParameterProvider>, IParameterProvider& parameterProvider</#if>, bool& continueValidation);
</#if>

private:
<#if withValidationCode>
    bool validateSchema(::zserio::IValidationObserver& validationObserver);
    <#list fields as field>
    bool validateColumn${field.name?cap_first}(::zserio::IValidationObserver& validationObserver,
            ::zserio::ValidationSqliteUtil<${types.allocator.default}>::TableSchema& tableSchema,
            bool& continueValidation);
    </#list>
    <#if hasNonVirtualField>

        <#list fields as field>
            <#if field.sqlTypeData.isBlob>
    bool validateBlob${field.name?cap_first}(::zserio::IValidationObserver& validationObserver,
            sqlite3_stmt* statement, Row& row<#rt>
            <#lt><#if field.hasExplicitParameters>, IParameterProvider& parameterProvider</#if>, bool& continueValidation);
            <#else>
    bool validateField${field.name?cap_first}(::zserio::IValidationObserver& validationObserver,
            sqlite3_stmt* statement, Row& row, bool& continueValidation);
            </#if>
        </#list>
        <#if hasValidatableField>

    <@vector_type_name types.string.name/> getRowKeyValuesHolder(sqlite3_stmt* statement);
    <@vector_type_name "::zserio::StringView"/> getRowKeyValues(
            const <@vector_type_name types.string.name/>& rowKeyValuesHolder);
        </#if>
    </#if>

</#if>
<#if withWriterCode>
    void writeRow(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>Row& row, sqlite3_stmt& statement);

    void appendCreateTableToQuery(${types.string.name}& sqlQuery) const;

</#if>
    void appendTableNameToQuery(${types.string.name}& sqlQuery) const;

    ::zserio::SqliteConnection& m_db;
    ::zserio::StringView m_name;
    ::zserio::StringView m_attachedDbName;
};
<@namespace_end package.path/>

<@include_guard_end package.path, name/>
