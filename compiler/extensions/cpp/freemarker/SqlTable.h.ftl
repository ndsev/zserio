<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "DocComment.inc.ftl">
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
<@type_includes types.typeInfo/>
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
<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
class ${name} : public ::zserio::AllocatorHolder<${types.allocator.default}>
{
public:
<#if needsParameterProvider>
    class Row;

    <#if withCodeComments>
    /** Interface for class which provides all explicit parameters of the table. */
    </#if>
    class IParameterProvider
    {
    public:
    <#if withCodeComments>
        /** Default destructor. */
    </#if>
        virtual ~IParameterProvider() = default;

    <#list explicitParameters as parameter>
        <#if withCodeComments>
            <#if !parameter?is_first>

            </#if>
        /**
         * Gets the value of the explicit parameter ${parameter.expression}.
         *
         * /param currentRow Current row of the table which can be used during parameter calculation.
         *
         * /return The value of the explicit parameter ${parameter.expression}.
         */
        </#if>
        virtual <@sql_parameter_provider_return_type parameter/> <@sql_parameter_provider_getter_name parameter/>(Row& currentRow) = 0;
    </#list>
    };

</#if>
<#if withCodeComments>
    /** Class which describes one row in the table. */
</#if>
    class Row
    {
    public:
<#if hasImplicitParameters>
    <#if withCodeComments>
        /** Default constructor. */
    </#if>
        Row() = default;
    <#if withCodeComments>
        /** Default destructor. */
    </#if>
        ~Row() = default;

    <#if withCodeComments>
    /** Copy constructor declaration. */
    </#if>
        Row(const Row& other);
    <#if withCodeComments>
    /** Assignment operator declaration. */
    </#if>
        Row& operator=(const Row& other);

    <#if withCodeComments>
    /** Move constructor declaration. */
    </#if>
        Row(Row&& other);
    <#if withCodeComments>
    /** Move assignment operator declaration. */
    </#if>
        Row& operator=(Row&& other);

</#if>
<#list fields as field>
    <#if !field.typeInfo.isSimple>
        <#if withCodeComments>
        /**
         * Gets the reference to the field ${field.name}.
         *
            <#if field.docComments??>
         * \b Description
         *
         <@doc_comments_inner field.docComments, 2/>
         *
            </#if>
         * \return Reference to the field ${field.name}.
         */
        </#if>
        ${field.typeInfo.typeFullName}& ${field.getterName}();
        <#if withCodeComments>

        /**
         * Gets the const reference to the field ${field.name}.
         *
            <#if field.docComments??>
         * \b Description
         *
         <@doc_comments_inner field.docComments, 2/>
         *
            </#if>
         * \return Const reference to the field ${field.name}.
         */
        </#if>
        const ${field.typeInfo.typeFullName}& ${field.getterName}() const;
    <#else>
        <#if withCodeComments>
        /**
         * Gets the value of the field ${field.name}.
         *
            <#if field.docComments??>
         * \b Description
         *
         <@doc_comments_inner field.docComments, 2/>
         *
            </#if>
         * \return Value of the field ${field.name}.
         */
        </#if>
        ${field.typeInfo.typeFullName} ${field.getterName}() const;
    </#if>
    <#if !field.typeInfo.isSimple>
        <#if withCodeComments>

        /**
         * Sets the field ${field.name}.
         *
            <#if field.docComments??>
         * \b Description
         *
         <@doc_comments_inner field.docComments, 2/>
         *
            </#if>
         * \param <@sql_field_argument_name field/> Const reference to the field ${field.name} to set.
         */
        </#if>
        void ${field.setterName}(const ${field.typeInfo.typeFullName}& <@sql_field_argument_name field/>);
        <#if withCodeComments>

        /**
         * Sets the field ${field.name} using r-value.
         *
            <#if field.docComments??>
         * \b Description
         *
         <@doc_comments_inner field.docComments, 2/>
         *
            </#if>
         * \param <@sql_field_argument_name field/> R-value of the field ${field.name} to set.
         */
        </#if>
        void ${field.setterName}(${field.typeInfo.typeFullName}&& <@sql_field_argument_name field/>);
    <#else>
        <#if withCodeComments>

        /**
         * Sets the value of the field ${field.name}.
         *
            <#if field.docComments??>
         * \b Description
         *
         <@doc_comments_inner field.docComments, 2/>
         *
            </#if>
         * \param <@sql_field_argument_name field/> Value of the field ${field.name} to set.
         */
        </#if>
        void ${field.setterName}(${field.typeInfo.typeFullName} <@sql_field_argument_name field/>);
    </#if>
    <#if withCodeComments>

        /**
         * Resets the value of the field ${field.name}.
         */
    </#if>
        void ${field.resetterName}();
    <#if withCodeComments>

        /**
         * Checks if the field ${field.name} is set.
         *
         * \return True if the field ${field.name} is set, otherwise false.
         */
    </#if>
        bool ${field.isSetIndicatorName}() const;

</#list>
<#if withWriterCode>
    <#if needsChildrenInitialization>
        <#if withCodeComments>
        /**
         * Initializes all blob fields of the table.
         *
         * This method sets all parameters for all blob fields recursively.
         *
         * \param parameterProvider Explicit parameter provider to use for initialization.
         */
        </#if>
        void initializeChildren(<#if needsParameterProvider>IParameterProvider& parameterProvider</#if>);

    </#if>
    <#if hasBlobField>
        <#if withCodeComments>
        /**
         * Initializes offsets in all blob fields of the table.
         *
         * This method sets offsets in all blob fields recursively.
         */
        </#if>
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

<#if withCodeComments>
    /** Reader abstraction which is used for reading rows from the table. */
</#if>
    class Reader : public ::zserio::AllocatorHolder<${types.allocator.default}>
    {
    public:
<#if withCodeComments>
        /** Default destructor. */
</#if>
        ~Reader() = default;

<#if withCodeComments>
        /** Disables copy constructor. */
</#if>
        Reader(const Reader&) = delete;
<#if withCodeComments>
        /** Disables assignment operator. */
</#if>
        Reader& operator=(const Reader&) = delete;

<#if withCodeComments>
        /** Disables move constructor. */
</#if>
        Reader(Reader&&) = default;
<#if withCodeComments>
        /** Disables move assignment operator. */
</#if>
        Reader& operator=(Reader&&) = delete;

<#if withCodeComments>
        /**
         * Checks if there is next row available for reading.
         *
         * \return True if row for reading is available, otherwise false.
         */
</#if>
        bool hasNext() const noexcept;
<#if withCodeComments>

        /**
         * Reads next row from the table.
         *
         * \return Read row.
         */
</#if>
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

<#if withCodeComments>
    /**
     * Constructor from database connection, table name and attached database name.
     *
     * \param db Database connection where the table is located.
     * \param tableName Table name.
     * \param attachedDbName Name of the attached database where table has been relocated.
     * \param allocator Allocator to construct from.
     */
</#if>
    ${name}(::zserio::SqliteConnection& db, ::zserio::StringView tableName,
            ::zserio::StringView attachedDbName = ::zserio::StringView(),
            const allocator_type& allocator = allocator_type());
<#if withCodeComments>

    /**
     * Constructor from database connection and table name.
     *
     * \param db Database connection where the table is located.
     * \param tableName Table name.
     * \param allocator Allocator to construct from.
     */
</#if>
    ${name}(::zserio::SqliteConnection& db, ::zserio::StringView tableName, const allocator_type& allocator);

<#if withCodeComments>
    /** Default destructor. */
</#if>
    ~${name}() = default;

<#if withCodeComments>
    /** Disables copy constructor. */
</#if>
    ${name}(const ${name}&) = delete;
<#if withCodeComments>
    /** Disables assignment operator. */
</#if>
    ${name}& operator=(const ${name}&) = delete;

<#if withCodeComments>
    /** Disables move constructor. */
</#if>
    ${name}(${name}&&) = delete;
<#if withCodeComments>
    /** Disables move assignment operator. */
</#if>
    ${name}& operator=(${name}&&) = delete;
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about the table type useful for generic introspection.
     *
     * \return Const reference to Zserio type information.
     */
    </#if>
    static const ${types.typeInfo.name}& typeInfo();
</#if>
<#if withWriterCode>

    <#if withCodeComments>
    /**
     * Creates the table using database connection given by constructor.
     */
    </#if>
    void createTable();
    <#if sql_table_has_non_virtual_field(fields) && isWithoutRowId>
        <#if withCodeComments>

        /**
         * Creates the table as ordinary row id using database connection given by constructor.
         *
         * The method creates the table as ordinary row id even if it is specified as without row id in schema.
         */
        </#if>
    void createOrdinaryRowIdTable();
    </#if>
    <#if withCodeComments>

    /**
     * Deletes the table using database connection given by constructor.
     */
    </#if>
    void deleteTable();
</#if>

<#if withCodeComments>
    /**
     * Creates the table reader for given SQL condition.
     *
        <#if needsParameterProvider>
     * \param parameterProvider Explicit parameter provider to be used during reading.
        </#if>
     * \param condition SQL condition to use.
     *
     * \return Created table reader.
     */
</#if>
    Reader createReader(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>::zserio::StringView condition = ::zserio::StringView()) const;
<#if withWriterCode>
    <#if withCodeComments>

    /**
     * Writes rows to the table.
     *
        <#if needsParameterProvider>
     * \param parameterProvider Explicit parameter provider to be used during reading.
        </#if>
     * \param rows Table rows to write.
     */
    </#if>
    void write(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>::zserio::Span<Row> rows);
    <#if withCodeComments>

    /**
     * Updates row of the table.
     *
        <#if needsParameterProvider>
     * \param parameterProvider Explicit parameter provider to be used during reading.
        </#if>
     * \param row Table row to update.
     * \param whereCondition SQL where condition to use.
     */
    </#if>
    void update(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
            <#lt>Row& row, ::zserio::StringView whereCondition);
</#if>
<#if withValidationCode>

    <#if withCodeComments>
    /**
     * Validates the table.
     *
     * Validation consists of the following validation tests:
     *
     * 1. Table schema validation.
     *
     *    Table schema is read from database and checked according to the schema. It's checked if number of
     *    columns are correct and if each column has expected type and expected 'isNotNull' and 'isPrimaryKey' 
     *    flags.
     *
     * 2. Validation of column values.
     *
     *    Each blob or integer value stored in table is read from database and checked. Blobs are read from
     *    the bit stream and written again. Then read bit stream is binary compared with the written stream.
     *    Integer values are checked according to their boundaries specified in schema.
     *
     * \param validationObserver Validation observer from which users can obtain validation results.
        <#if needsParameterProvider>
     * \param parameterProvider Explicit parameter provider to be used during reading.
        </#if>
     * \param continueValidation True if validation shall continue to validate next tables.
     */
    </#if>
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
