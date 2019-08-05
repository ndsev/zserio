<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin package.path, name/>

#include <memory>
#include <vector>
#include <string>
#include <sqlite3.h>
<@system_includes headerSystemIncludes, false/>
#include <zserio/SqliteConnection.h>
#include <zserio/SqliteFinalizer.h>
<#if fields?has_content>
#include <zserio/OptionalHolder.h>
</#if>

<@user_includes headerUserIncludes, false/>

<@namespace_begin package.path/>

<#assign needsParameterProvider=explicitParameters?has_content/>
<#assign needsChildrenInitialization=(hasImplicitParameters || explicitParameters?has_content)/>
<#assign hasBlobField=sql_table_has_blob_field(fields)/>
class ${name}
{
public:
<#if needsParameterProvider>
    class Row;

    class IParameterProvider
    {
    public:
        <#list explicitParameters as parameter>
        virtual <@sql_parameter_provider_return_type parameter/> <@sql_parameter_provider_getter_name parameter/>(Row& currentRow) = 0;
        </#list>

        virtual ~IParameterProvider()
        {}
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
    <#if !field.isSimpleType>
        ${field.optionalCppTypeName}& ${field.getterName}();
    </#if>
        const ${field.optionalCppTypeName}& ${field.getterName}() const;
        void ${field.setterName}(${field.optionalCppArgumentTypeName} <@sql_field_argument_name field/>);
    <#if !field.isSimpleType>
        void ${field.setterName}(${field.optionalCppTypeName}&& <@sql_field_argument_name field/>);
    </#if>

</#list>
<#if needsChildrenInitialization>
        void initializeChildren(<#if needsParameterProvider>IParameterProvider& parameterProvider</#if>);

</#if>
<#if hasBlobField>
        void initializeOffsets();

</#if>
    private:
<#if hasImplicitParameters>
        void reinitializeBlobs();

</#if>
<#list fields as field>
        ${field.optionalCppTypeName} <@sql_field_member_name field/>;
</#list>
    };

    class Reader
    {
    public:
        ~Reader() = default;
        Reader(const Reader& other) = delete;
        Reader& operator=(const Reader& other) = delete;

        Reader(Reader&& other) = default;
        Reader& operator=(Reader&& other) = default;

        bool hasNext() const noexcept;
        Row next();

    private:
        explicit Reader(::zserio::SqliteConnection& db, <#rt>
                <#lt><#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
                <#lt>const ::std::string& sqlQuery);
        friend class ${name};

        void makeStep();

<#if needsParameterProvider>
        IParameterProvider& m_parameterProvider;
</#if>
        ::std::unique_ptr<sqlite3_stmt, ::zserio::SqliteFinalizer> m_stmt;
        int m_lastResult;
    };

    ${name}(::zserio::SqliteConnection& db, const ::std::string& tableName, const ::std::string& attachedDbName = "");
    ~${name}();
<#if withWriterCode>

    void createTable();
    <#if sql_table_has_non_virtual_field(fields) && isWithoutRowId>
    void createOrdinaryRowIdTable();
    </#if>
    void deleteTable();
</#if>

    Reader createReader(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>const ::std::string& condition = "") const;
<#if withWriterCode>
    void write(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>::std::vector<Row>& rows);
    void update(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>Row& row, const ::std::string& whereCondition);
</#if>

private:
<#if withWriterCode>
    static void writeRow(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>Row& row, sqlite3_stmt& statement);

    void appendCreateTableToQuery(::std::string& sqlQuery);

</#if>
    void appendTableNameToQuery(::std::string& sqlQuery) const;

    ::zserio::SqliteConnection& m_db;
    const ::std::string m_name;
    const ::std::string m_attachedDbName;
};

<@namespace_end package.path/>

<@include_guard_end package.path, name/>
