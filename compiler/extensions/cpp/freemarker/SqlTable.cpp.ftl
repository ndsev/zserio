<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/CppRuntimeException.h>
#include <zserio/SqliteException.h>
#include <zserio/BitStreamReader.h>
<#if withWriterCode || withValidationCode>
    <#assign hasNonVirtualField=sql_table_has_non_virtual_field(fields)/>
    <#if hasNonVirtualField>
#include <algorithm>
#include <zserio/BitFieldUtil.h>
    </#if>
#include <zserio/BitStreamWriter.h>
<@type_includes types.bitBuffer/>
</#if>
<#if withTypeInfoCode>
#include <zserio/TypeInfo.h>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

<#assign needsParameterProvider=explicitParameters?has_content/>
<#if withValidationCode>
    <#assign hasPrimaryKeyField=false/>
    <#list fields as field>
        <#if field.isPrimaryKey>
            <#assign hasPrimaryKeyField=true/>
            <#break>
        </#if>
    </#list>
</#if>
${name}::${name}(::zserio::SqliteConnection& db, ::zserio::StringView tableName,
        ::zserio::StringView attachedDbName, const allocator_type& allocator) :
        ::zserio::AllocatorHolder<allocator_type>(allocator),
        m_db(db), m_name(tableName), m_attachedDbName(attachedDbName)
{
}

${name}::${name}(::zserio::SqliteConnection& db, ::zserio::StringView tableName,
        const allocator_type& allocator) :
        ${name}(db, tableName, ::zserio::StringView(), allocator)
{
}

<#if withTypeInfoCode>
const ${types.typeInfo.name}& ${name}::typeInfo()
{
    <@template_info_template_name_var "templateName", templateInstantiation!/>
    <@template_info_template_arguments_var "templateArguments", templateInstantiation!/>

    <#list fields as field>
    <@column_info_type_arguments_var field/>
    </#list>
    static const <@info_array_type "::zserio::BasicColumnInfo<allocator_type>", fields?size/> columns<#rt>
    <#if fields?has_content>
        <#lt> = {
        <#list fields as field>
        <@column_info field field?has_next/>
        </#list>
    };
    <#else>
        <#lt>;
    </#if>

    static const ::zserio::StringView sqlConstraint<#rt>
    <#if sqlConstraint??>
        <#lt> = ${sqlConstraint};
    <#else>
        <#lt>;
    </#if>

    static const ::zserio::StringView virtualTableUsing<#rt>
    <#if virtualTableUsing??>
        <#lt> = ::zserio::makeStringView("${virtualTableUsing}");
    <#else>
        <#lt>;
    </#if>

    static const bool isWithoutRowId = <#if isWithoutRowId>true<#else>false</#if>;

    static const ::zserio::SqlTableTypeInfo<allocator_type> typeInfo = {
        ::zserio::makeStringView("${schemaTypeName}"), templateName, templateArguments,
        columns, sqlConstraint, virtualTableUsing, isWithoutRowId
    };

    return typeInfo;
}

</#if>
<#if withWriterCode>
void ${name}::createTable()
{
    ${types.string.name} sqlQuery(get_allocator_ref());
    appendCreateTableToQuery(sqlQuery);
    <#if hasNonVirtualField && isWithoutRowId>
    sqlQuery += " WITHOUT ROWID";
    </#if>
    m_db.executeUpdate(sqlQuery);
}

    <#if hasNonVirtualField && isWithoutRowId>
void ${name}::createOrdinaryRowIdTable()
{
    ${types.string.name} sqlQuery(get_allocator_ref());
    appendCreateTableToQuery(sqlQuery);
    m_db.executeUpdate(sqlQuery);
}

    </#if>
void ${name}::deleteTable()
{
    ${types.string.name} sqlQuery(get_allocator_ref());
    sqlQuery += "DROP TABLE ";
    appendTableNameToQuery(sqlQuery);
    m_db.executeUpdate(sqlQuery);
}

</#if>
${name}::Reader ${name}::createReader(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>::zserio::StringView condition) const
{
    ${types.string.name} sqlQuery(get_allocator_ref());
    sqlQuery +=
            "SELECT "
<#list fields as field>
            "${field.name}<#if field?has_next>, </#if>"
</#list>
            " FROM ";
    appendTableNameToQuery(sqlQuery);
    if (!condition.empty())
    {
        sqlQuery += " WHERE ";
        sqlQuery += condition;
    }

    return Reader(m_db, <#if needsParameterProvider>parameterProvider, </#if>sqlQuery, get_allocator_ref());
}

${name}::Reader::Reader(::zserio::SqliteConnection& db, <#rt>
        <#lt><#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>const ${types.string.name}& sqlQuery, const allocator_type& allocator) :
        ::zserio::AllocatorHolder<allocator_type>(allocator),
        <#lt><#if needsParameterProvider>m_parameterProvider(parameterProvider),</#if><#rt>
        m_stmt(db.prepareStatement(sqlQuery))
{
    makeStep();
}

bool ${name}::Reader::hasNext() const noexcept
{
    return m_lastResult == SQLITE_ROW;
}

<#macro read_blob field parameterProviderVarName>
        <#list field.typeParameters as parameter>
        <@sql_parameter_variable_type parameter/> _${parameter.name} = <#rt>
            <#if parameter.isExplicit>
                <#lt>${parameterProviderVarName}.<@sql_parameter_provider_getter_name parameter/>(row);
            <#else>
                <#lt>${parameter.expression};
            </#if>
        </#list>
        ${field.typeInfo.typeFullName} blob(reader<#rt>
        <#list field.typeParameters as parameter>
                , _${parameter.name}<#t>
        </#list>
        <#lt>, get_allocator_ref());
</#macro>
${name}::Row ${name}::Reader::next()
{
    if (!hasNext())
    {
        throw ::zserio::SqliteException("Table::Reader::next: next row is not available: ") <<
                ::zserio::SqliteErrorCode(m_lastResult);
    }

    Row row;
<#list fields as field>

    // field ${field.name}
    if (sqlite3_column_type(m_stmt.get(), ${field?index}) != SQLITE_NULL)
    {
    <#if field.sqlTypeData.isBlob>
        const void* blobData = sqlite3_column_blob(m_stmt.get(), ${field?index});
        const int blobDataLength = sqlite3_column_bytes(m_stmt.get(), ${field?index});
        ::zserio::BitStreamReader reader(static_cast<const uint8_t*>(blobData),
                static_cast<size_t>(blobDataLength));
        <@read_blob field, "m_parameterProvider"/>
        row.${field.setterName}(::std::move(blob));
    <#elseif field.sqlTypeData.isInteger>
        const int64_t intValue = sqlite3_column_int64(m_stmt.get(), ${field?index});
        <#if field.typeInfo.isEnum>
        const ${field.typeInfo.typeFullName} enumValue = ::zserio::valueToEnum<${field.typeInfo.typeFullName}>(static_cast<${field.underlyingTypeInfo.typeFullName}>(intValue));
        row.${field.setterName}(enumValue);
        <#elseif field.typeInfo.isBitmask>
        const ${field.typeInfo.typeFullName} bitmaskValue = ${field.typeInfo.typeFullName}(static_cast<${field.underlyingTypeInfo.typeFullName}>(intValue));
        row.${field.setterName}(bitmaskValue);
        <#elseif field.typeInfo.isBoolean>
        row.${field.setterName}(intValue != 0);
        <#else>
        row.${field.setterName}(static_cast<${field.typeInfo.typeFullName}>(intValue));
        </#if>
    <#elseif field.sqlTypeData.isReal>
        const double doubleValue = sqlite3_column_double(m_stmt.get(), ${field?index});
        row.${field.setterName}(static_cast<${field.typeInfo.typeFullName}>(doubleValue));
    <#else>
        const unsigned char* textValue = sqlite3_column_text(m_stmt.get(), ${field?index});
        row.${field.setterName}(${field.typeInfo.typeFullName}(
                reinterpret_cast<const char*>(textValue), get_allocator_ref()));
    </#if>
    }
</#list>

    makeStep();

    return row;
}

void ${name}::Reader::makeStep()
{
    m_lastResult = sqlite3_step(m_stmt.get());
    if (m_lastResult != SQLITE_ROW && m_lastResult != SQLITE_DONE)
    {
        throw ::zserio::SqliteException("${name}::Read: sqlite3_step() failed: ") <<
                ::zserio::SqliteErrorCode(m_lastResult);
    }
}
<#if withWriterCode>

void ${name}::write(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>::zserio::Span<Row> rows)
{
    // assemble sql query
    ${types.string.name} sqlQuery(get_allocator_ref());
    sqlQuery += "INSERT INTO ";
    appendTableNameToQuery(sqlQuery);
    sqlQuery +=
            "("
    <#list fields as field>
            "${field.name}<#if field?has_next>, </#if>"
    </#list>
            ") VALUES (<#rt>
    <#list fields as field>
            ?<#if field?has_next>, </#if><#t>
    </#list>
            );";<#lt>

    // write rows
    const bool wasTransactionStarted = m_db.startTransaction();
    ::std::unique_ptr<sqlite3_stmt, ::zserio::SqliteFinalizer> statement(m_db.prepareStatement(sqlQuery));

    for (Row& row : rows)
    {
        writeRow(<#if needsParameterProvider>parameterProvider, </#if>row, *statement);
        int result = sqlite3_step(statement.get());
        if (result != SQLITE_DONE)
        {
            throw ::zserio::SqliteException("Write: sqlite3_step() failed: ") <<
                    ::zserio::SqliteErrorCode(result);
        }

        sqlite3_clear_bindings(statement.get());
        result = sqlite3_reset(statement.get());
        if (result != SQLITE_OK)
        {
            throw ::zserio::SqliteException("Write: sqlite3_reset() failed: ") <<
                    ::zserio::SqliteErrorCode(result);
        }
    }

    m_db.endTransaction(wasTransactionStarted);
}

void ${name}::update(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>Row& row, ::zserio::StringView whereCondition)
{
    // assemble sql query
    ${types.string.name} sqlQuery(get_allocator_ref());
    sqlQuery += "UPDATE ";
    appendTableNameToQuery(sqlQuery);
    sqlQuery +=
            " SET"
    <#list fields as field>
            " ${field.name}=?<#if field?has_next>,</#if>"
    </#list>
            " WHERE ";
    sqlQuery += whereCondition;

    // update row
    ::std::unique_ptr<sqlite3_stmt, ::zserio::SqliteFinalizer> statement(m_db.prepareStatement(sqlQuery));
    writeRow(<#if needsParameterProvider>parameterProvider, </#if>row, *statement);
    const int result = sqlite3_step(statement.get());
    if (result != SQLITE_DONE)
        throw ::zserio::SqliteException("Update: sqlite3_step() failed: ") << ::zserio::SqliteErrorCode(result);
}
<#if withValidationCode>

bool ${name}::validate(::zserio::IValidationObserver& validationObserver<#rt>
        <#lt><#if needsParameterProvider>, IParameterProvider& parameterProvider</#if>, bool& continueValidation)
{
    const size_t numberOfRows = <#if hasNonVirtualField>::zserio::ValidationSqliteUtil<${types.allocator.default}>::getNumberOfTableRows(
            m_db, m_attachedDbName, m_name, get_allocator_ref());<#else>0;</#if>
    continueValidation = true;
    if (!validationObserver.beginTable(m_name, numberOfRows))
        return false;

    size_t numberOfValidatedRows = 0;
    <#if hasNonVirtualField>
    if (validateSchema(validationObserver))
    {
        ${types.string.name} sqlQuery{get_allocator_ref()};
        sqlQuery += "SELECT ";
        <#list fields as field>
        sqlQuery += "${field.name}<#if field?has_next || !hasPrimaryKeyField>, </#if>";
        </#list>
        <#if !hasPrimaryKeyField><#-- use rowid instead of primary key in getRowKeyValuesHolder -->
        sqlQuery += "rowid";
        </#if>
        sqlQuery += " FROM ";
        appendTableNameToQuery(sqlQuery);
        ::std::unique_ptr<sqlite3_stmt, ::zserio::SqliteFinalizer> statement(m_db.prepareStatement(sqlQuery));
        int result = SQLITE_OK;
        bool continueTableValidation = true;
        while ((result = sqlite3_step(statement.get())) == SQLITE_ROW && continueTableValidation)
        {
            ++numberOfValidatedRows;

        <#list fields as field>
            if (!validateType${field.name?cap_first}(validationObserver, statement.get(), continueValidation))
                continue;
        </#list>

            Row row;
        <#list fields as field>
            <#if field.sqlTypeData.isBlob>
            if (!validateBlob${field.name?cap_first}(validationObserver, statement.get(), row<#rt>
                    <#lt><#if field.hasExplicitParameters>, parameterProvider</#if>, continueTableValidation))
                continue;
            <#else>
            if (!validateField${field.name?cap_first}(validationObserver, statement.get(), row, continueTableValidation))
                continue;
            </#if>
        </#list>
        }
        if (result != SQLITE_DONE && (continueTableValidation || result != SQLITE_ROW))
        {
            throw ::zserio::SqliteException("Validate: sqlite3_step() failed: ") <<
                    ::zserio::SqliteErrorCode(result);
        }
    }
    <#else>
    <#-- If the table has only virtual fields, skip everything except of schema checking. -->
    validateSchema(validationObserver);
    </#if>

    continueValidation = validationObserver.endTable(m_name, numberOfValidatedRows);

    return true;
}

bool ${name}::validateSchema(::zserio::IValidationObserver& validationObserver)
{
    ::zserio::ValidationSqliteUtil<${types.allocator.default}>::TableSchema tableSchema(
            get_allocator_ref());
    ::zserio::ValidationSqliteUtil<${types.allocator.default}>::getTableSchema(
            m_db, m_attachedDbName, m_name, tableSchema, get_allocator_ref());

    bool result = true;
    bool continueValidation = true;
    <#list fields as field>

    if (<#if !field?is_first>continueValidation && </#if>!validateColumn${field.name?cap_first}(
            validationObserver, tableSchema, continueValidation))
    {
        result = false;
    }
    </#list>

    if (!tableSchema.empty())
    {
        for (auto it = tableSchema.begin(); it != tableSchema.end() && continueValidation; ++it)
        {
            const auto& columnName = it->first;
            const auto& columnType = it->second.type;
            ${types.string.name} errorMessage = ${types.string.name}(
                    "superfluous column ", get_allocator_ref());
            errorMessage += m_name;
            errorMessage += ".";
            errorMessage += columnName;
            errorMessage += " of type ";
            errorMessage += columnType;
            errorMessage += " encountered";
            continueValidation = validationObserver.reportError(m_name, columnName,
                    ::zserio::Span<::zserio::StringView>(),
                    ::zserio::IValidationObserver::COLUMN_SUPERFLUOUS,
                    errorMessage);
        }
        result = false;
    }

    return result;
}
    <#list fields as field>

bool ${name}::validateColumn${field.name?cap_first}(::zserio::IValidationObserver& validationObserver,
        ::zserio::ValidationSqliteUtil<${types.allocator.default}>::TableSchema& tableSchema,
        bool& continueValidation)
{
    auto search = tableSchema.find(${types.string.name}("${field.name}", get_allocator_ref()));
    <#-- if column is virtual, it can be hidden -->
    if (search == tableSchema.end()<#if field.isVirtual> &&
            !::zserio::ValidationSqliteUtil<${types.allocator.default}>::isColumnInTable(
                    m_db, m_attachedDbName, m_name, ::zserio::makeStringView("${field.name}"),
                    get_allocator_ref())</#if>)
    {
        continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
                ::zserio::Span<const ::zserio::StringView>(),
                ::zserio::IValidationObserver::COLUMN_MISSING,
                ::zserio::makeStringView("column ${name}.${field.name} is missing"));
        return false;
    }

        <#-- SQLite does not maintain column properties for virtual tables columns or for virtual columns -->
        <#if !virtualTableUsing?? && !field.isVirtual>
    const auto column = search->second;<#-- copy the column since it will be erased from schema -->
    tableSchema.erase(search);

    if (column.type != "${field.sqlTypeData.name}")
    {
        ${types.string.name} errorMessage = ${types.string.name}(
                "column ${name}.${field.name} has type '", get_allocator_ref());
        errorMessage += column.type;
        errorMessage += "' but '${field.sqlTypeData.name}' is expected";
        continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
                ::zserio::Span<const ::zserio::StringView>(),
                ::zserio::IValidationObserver::INVALID_COLUMN_TYPE, errorMessage);
        return false;
    }

    if (<#if field.isNotNull>!</#if>column.isNotNull)
    {
        continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
                ::zserio::Span<::zserio::StringView>(),
                ::zserio::IValidationObserver::INVALID_COLUMN_CONSTRAINT,
                ::zserio::makeStringView(
                        "column ${name}.${field.name} is <#if !field.isNotNull>NOT </#if>NULL-able, "
                        "but the column is expected to be <#if field.isNotNull>NOT </#if>NULL-able"));
        return false;
    }

    if (<#if field.isPrimaryKey>!</#if>column.isPrimaryKey)
    {
        continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
                ::zserio::Span<::zserio::StringView>(),
                ::zserio::IValidationObserver::INVALID_COLUMN_CONSTRAINT,
                ::zserio::makeStringView(
                        "column ${name}.${field.name} is <#if field.isPrimaryKey>not </#if>primary key, "
                        "but the column is expected <#if !field.isPrimaryKey>not </#if>to be primary key"));
        return false;
    }
        <#else>
    if (search != tableSchema.end())
        tableSchema.erase(search);
        </#if>

    return true;
}
    </#list>
    <#if hasNonVirtualField>
        <#list fields as field>

<#macro sqlite_type_field field>
    <#if field.sqlTypeData.isInteger>
        SQLITE_INTEGER<#t>
    <#elseif field.sqlTypeData.isReal>
        SQLITE_FLOAT<#t>
    <#elseif field.sqlTypeData.isBlob>
        SQLITE_BLOB<#t>
    <#else>
        SQLITE_TEXT<#t>
    </#if>
</#macro>
bool ${name}::validateType${field.name?cap_first}(::zserio::IValidationObserver& validationObserver,
        sqlite3_stmt* statement, bool& continueValidation)
{
    const int type = sqlite3_column_type(statement, ${field?index});
    if (type == SQLITE_NULL)
        return true;

    if (type != <@sqlite_type_field field/>)
    {
        const auto rowKeyValuesHolder = getRowKeyValuesHolder(statement);
        ${types.string.name} errorMessage = ${types.string.name}(
                "Column ${name}.${field.name} type check failed (", get_allocator_ref());
        errorMessage += ::zserio::ValidationSqliteUtil<${types.allocator.default}>::sqliteColumnTypeName(type);
        errorMessage += " doesn't match to ";
        errorMessage += ::zserio::ValidationSqliteUtil<${types.allocator.default}>::sqliteColumnTypeName(<@sqlite_type_field field/>);
        errorMessage += ")!";
        continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
                getRowKeyValues(rowKeyValuesHolder), ::zserio::IValidationObserver::INVALID_COLUMN_TYPE,
                errorMessage);
        return false;
    }

    return true;
}
        </#list>
        <#list fields as field>

            <#if field.sqlTypeData.isBlob>
bool ${name}::validateBlob${field.name?cap_first}(::zserio::IValidationObserver& validationObserver,
        sqlite3_stmt* statement, Row& row<#rt>
        <#lt><#if field.hasExplicitParameters>, IParameterProvider& parameterProvider</#if>, bool& continueValidation)
{
    const void* blobData = sqlite3_column_blob(statement, ${field?index});
    if (blobData == nullptr)
        return true;

    try
    {
        const int blobDataLength = sqlite3_column_bytes(statement, ${field?index});
        ::zserio::BitStreamReader reader(static_cast<const uint8_t*>(blobData),
                static_cast<size_t>(blobDataLength));
        <@read_blob field "parameterProvider"/>
        ${types.bitBuffer.name} bitBuffer(reader.getBitPosition(), get_allocator_ref());
        ::zserio::BitStreamWriter writer(bitBuffer);
        blob.write(writer);
        if (reader.getBitPosition() != writer.getBitPosition())
        {
            const auto rowKeyValuesHolder = getRowKeyValuesHolder(statement);
            ${types.string.name} errorMessage = ${types.string.name}(
                    "Blob binary compare failed because of length (", get_allocator_ref());
            errorMessage += ::zserio::toString(reader.getBitPosition(), get_allocator_ref());
            errorMessage += " != ";
            errorMessage += ::zserio::toString(writer.getBitPosition(), get_allocator_ref());
            errorMessage += ")";
            continueValidation = validationObserver.reportError(m_name,
                    ::zserio::makeStringView("${field.name}"), getRowKeyValues(rowKeyValuesHolder),
                    ::zserio::IValidationObserver::BLOB_COMPARE_FAILED, errorMessage);
            return false;
        }
        row.${field.setterName}(::std::move(blob));
    }
    catch (const ::zserio::CppRuntimeException& exception)
    {
        const auto rowKeyValuesHolder = getRowKeyValuesHolder(statement);
        continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
                getRowKeyValues(rowKeyValuesHolder),
                ::zserio::IValidationObserver::BLOB_PARSE_FAILED,
                exception.what());
        return false;
    }

    return true;
}
            <#else>
<#macro range_check_field name field types indent>
    <#local I>${""?left_pad(indent * 4)}</#local>
${I}if (<#if field.sqlRangeCheckData.checkLowerBound>rangeCheckValue < lowerBound || </#if>rangeCheckValue > upperBound)
${I}{
${I}    const auto rowKeyValuesHolder = getRowKeyValuesHolder(statement);
${I}    ${types.string.name} errorMessage = ${types.string.name}("Value ", get_allocator_ref());
${I}    errorMessage += ::zserio::toString(rangeCheckValue, get_allocator_ref());
${I}    errorMessage += " of ${name}.${field.name} exceeds the range of ";
${I}    errorMessage += ::zserio::toString(lowerBound, get_allocator_ref());
${I}    errorMessage += "..";
${I}    errorMessage += ::zserio::toString(upperBound, get_allocator_ref());
${I}    continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
${I}            getRowKeyValues(rowKeyValuesHolder),
${I}            ::zserio::IValidationObserver::VALUE_OUT_OF_RANGE, errorMessage);
${I}    return false;
${I}}
</#macro>
bool ${name}::validateField${field.name?cap_first}(::zserio::IValidationObserver&<#rt>
        <#lt><#if field.sqlRangeCheckData?? || field.typeInfo.isEnum> validationObserver</#if>,
        sqlite3_stmt* statement, Row& row, bool&<#rt>
        <#lt><#if field.sqlRangeCheckData?? || field.typeInfo.isEnum> continueValidation</#if>)
{
    if (sqlite3_column_type(statement, ${field?index}) == SQLITE_NULL)
        return true;

                <#if field.sqlTypeData.isInteger>
    const int64_t intValue = sqlite3_column_int64(statement, ${field?index});
                    <#if field.sqlRangeCheckData??>
    // range check
    const ${field.sqlRangeCheckData.typeInfo.typeFullName} rangeCheckValue = static_cast<${field.sqlRangeCheckData.typeInfo.typeFullName}>(intValue);
                        <#if field.sqlRangeCheckData.bitFieldLength??>
    try
    {
        const size_t bitFieldLength = static_cast<size_t>(${field.sqlRangeCheckData.bitFieldLength});
        const ${field.sqlRangeCheckData.typeInfo.typeFullName} lowerBound = static_cast<${field.sqlRangeCheckData.typeInfo.typeFullName}><#rt>
            <#lt>(::zserio::getBitFieldLowerBound(bitFieldLength, <#if field.sqlRangeCheckData.typeInfo.isSigned>true<#else>false</#if>));
        const ${field.sqlRangeCheckData.typeInfo.typeFullName} upperBound = static_cast<${field.sqlRangeCheckData.typeInfo.typeFullName}><#rt>
            <#lt>(::zserio::getBitFieldUpperBound(bitFieldLength, <#if field.sqlRangeCheckData.typeInfo.isSigned>true<#else>false</#if>));
        <@range_check_field name, field, types, 2/>
    }
    catch (const ::zserio::CppRuntimeException& exception)
    {
        const auto rowKeyValuesHolder = getRowKeyValuesHolder(statement);
        continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
                getRowKeyValues(rowKeyValuesHolder),
                ::zserio::IValidationObserver::INVALID_VALUE,
                exception.what());
        return false;
    }
                        <#else>
    const ${field.sqlRangeCheckData.typeInfo.typeFullName} lowerBound = ${field.sqlRangeCheckData.lowerBound};
    const ${field.sqlRangeCheckData.typeInfo.typeFullName} upperBound = ${field.sqlRangeCheckData.upperBound};
    <@range_check_field name, field, types, 1/>
                        </#if>

                    </#if>
                    <#if field.typeInfo.isEnum>
    try
    {
        const ${field.typeInfo.typeFullName} enumValue = ::zserio::valueToEnum<${field.typeInfo.typeFullName}>(<#rt>
                <#lt>static_cast<${field.underlyingTypeInfo.typeFullName}>(intValue));
        row.${field.setterName}(enumValue);
    }
    catch (const ::zserio::CppRuntimeException&)
    {
        const auto rowKeyValuesHolder = getRowKeyValuesHolder(statement);
        ${types.string.name} errorMessage = ${types.string.name}("Enumeration value ", get_allocator_ref());
        errorMessage += ::zserio::toString(intValue, get_allocator_ref());
        errorMessage += " of ${name}.${field.name} is not valid!";
        continueValidation = validationObserver.reportError(m_name, ::zserio::makeStringView("${field.name}"),
                getRowKeyValues(rowKeyValuesHolder),
                ::zserio::IValidationObserver::INVALID_VALUE, errorMessage);
        return false;
    }
                    <#elseif field.typeInfo.isBitmask>
    const ${field.typeInfo.typeFullName} bitmaskValue = ${field.typeInfo.typeFullName}(static_cast<${field.underlyingTypeInfo.typeFullName}>(intValue));
    row.${field.setterName}(bitmaskValue);
                    <#elseif field.typeInfo.isBoolean>
    row.${field.setterName}(intValue != 0);
                    <#else>
    row.${field.setterName}(static_cast<${field.typeInfo.typeFullName}>(intValue));
                    </#if>
                <#elseif field.sqlTypeData.isReal>
    const double doubleValue = sqlite3_column_double(statement, ${field?index});
    row.${field.setterName}(static_cast<${field.typeInfo.typeFullName}>(doubleValue));
                <#else>
    const unsigned char* textValue = sqlite3_column_text(statement, ${field?index});
    row.${field.setterName}(${field.typeInfo.typeFullName}(
            reinterpret_cast<const char*>(textValue), get_allocator_ref()));
                </#if>

    return true;
}
            </#if>
        </#list>

<#assign needsStatementArgument=false/>
<#if hasPrimaryKeyField>
    <#list fields as field>
        <#if field.isPrimaryKey>
            <#if !field.sqlTypeData.isBlob>
                <#assign needsStatementArgument=true/>
                <#break>
            </#if>
         </#if>
    </#list>
<#else>
    <#assign needsStatementArgument=true/>
</#if>
<@vector_type_name types.string.name/> ${name}::getRowKeyValuesHolder(sqlite3_stmt*<#if needsStatementArgument> statement</#if>)
{
    <@vector_type_name types.string.name/> rowKeyValuesHolder{get_allocator_ref()};

        <#if hasPrimaryKeyField>
            <#list fields as field>
                <#if field.isPrimaryKey>
                    <#if field.sqlTypeData.isBlob>
    rowKeyValuesHolder.emplace_back("BLOB");
                    <#else>
    const unsigned char* strValue${field.name?cap_first} = sqlite3_column_text(statement, ${field?index});
    rowKeyValuesHolder.emplace_back(reinterpret_cast<const char*>(strValue${field.name?cap_first}));
                    </#if>
                </#if>
            </#list>
        <#else>
    const unsigned char* strValueRowId = sqlite3_column_text(statement, ${fields?size});
    rowKeyValuesHolder.emplace_back(reinterpret_cast<const char*>(strValueRowId));
        </#if>

    return rowKeyValuesHolder;
}

<@vector_type_name "::zserio::StringView"/> ${name}::getRowKeyValues(
            const <@vector_type_name types.string.name/>& rowKeyValuesHolder)
{
    <@vector_type_name "::zserio::StringView"/> rowKeyValues{get_allocator_ref()};
    ::std::transform(rowKeyValuesHolder.begin(), rowKeyValuesHolder.end(),
            ::std::back_inserter(rowKeyValues),
            [](const ${types.string.name}& message) -> ::zserio::StringView { return message; });
    return rowKeyValues;
}
    </#if>
</#if>

void ${name}::writeRow(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>Row& row, sqlite3_stmt& statement)
{
<#if needsChildrenInitialization>
    row.initializeChildren(<#if needsParameterProvider>parameterProvider</#if>);

</#if>
    int result = SQLITE_ERROR;

    <#list fields as field>
    // field ${field.name}
    if (!row.${field.isSetIndicatorName}())
    {
        result = sqlite3_bind_null(&statement, ${field?index + 1});
    }
    else
    {
        <#if field.sqlTypeData.isBlob>
        const ${field.typeInfo.typeFullName}& blob = row.${field.getterName}();
        ${types.bitBuffer.name} bitBuffer(blob.bitSizeOf(), get_allocator_ref());
        ::zserio::BitStreamWriter writer(bitBuffer);
        blob.write(writer);
        result = sqlite3_bind_blob(&statement, ${field?index + 1}, bitBuffer.getBuffer(),
                static_cast<int>(bitBuffer.getByteSize()), SQLITE_TRANSIENT);
        <#elseif field.sqlTypeData.isInteger>
        const int64_t intValue = static_cast<int64_t>(row.${field.getterName}()<#if field.typeInfo.isBitmask>.getValue()</#if>);
        result = sqlite3_bind_int64(&statement, ${field?index + 1}, intValue);
        <#elseif field.sqlTypeData.isReal>
        const ${field.typeInfo.typeFullName} realValue = row.${field.getterName}();
        result = sqlite3_bind_double(&statement, ${field?index + 1}, static_cast<double>(realValue));
        <#else>
        const ${field.typeInfo.typeFullName}& stringValue = row.${field.getterName}();
        result = sqlite3_bind_text(&statement, ${field?index + 1}, stringValue.c_str(), -1, SQLITE_TRANSIENT);
        </#if>
    }
    if (result != SQLITE_OK)
    {
        throw ::zserio::SqliteException("${name}::WriteRow: sqlite3_bind() for field ${field.name} failed: ") <<
                ::zserio::SqliteErrorCode(result);
    }
        <#if field?has_next>

        </#if>
    </#list>
}

void ${name}::appendCreateTableToQuery(${types.string.name}& sqlQuery) const
{
    sqlQuery += "CREATE <#if virtualTableUsing??>VIRTUAL </#if>TABLE ";
    appendTableNameToQuery(sqlQuery);
    <#if virtualTableUsing??>
    sqlQuery += " USING ${virtualTableUsing}";
    </#if>
    <#if hasNonVirtualField || sqlConstraint??>
    sqlQuery += '(';
        <#assign firstNonVirtualField=true/>
        <#list fields as field>
            <#if !field.isVirtual>
    sqlQuery += "<#if !firstNonVirtualField>, </#if>${field.name}<#if needsTypesInSchema> ${field.sqlTypeData.name}</#if>";
                <#if field.sqlConstraint??>
    sqlQuery += ' ';
    sqlQuery += ${field.sqlConstraint};
                </#if>
                <#assign firstNonVirtualField=false/>
            </#if>
        </#list>
        <#if hasNonVirtualField && sqlConstraint??>
    sqlQuery += ", ";
        </#if>
        <#if sqlConstraint??>
    sqlQuery += ${sqlConstraint};
        </#if>
    sqlQuery += ')';
    </#if>
}
</#if>

void ${name}::appendTableNameToQuery(${types.string.name}& sqlQuery) const
{
    if (!m_attachedDbName.empty())
    {
        sqlQuery += m_attachedDbName;
        sqlQuery += '.';
    }
    sqlQuery += m_name;
}
<#if hasImplicitParameters>

${name}::Row::Row(const Row& other) :
    <#list fields as field>
        <@sql_field_member_name field/>(other.<@sql_field_member_name field/>)<#if field?has_next>,</#if>
    </#list>
{
    reinitializeBlobs();
}

${name}::Row& ${name}::Row::operator=(const Row& other)
{
    <#list fields as field>
    <@sql_field_member_name field/> = other.<@sql_field_member_name field/>;
    </#list>

    reinitializeBlobs();

    return *this;
}

${name}::Row::Row(Row&& other) :
    <#list fields as field>
        <@sql_field_member_name field/>(::std::move(other.<@sql_field_member_name field/>))<#if field?has_next>,</#if>
    </#list>
{
    reinitializeBlobs();
}

${name}::Row& ${name}::Row::operator=(Row&& other)
{
    <#list fields as field>
    <@sql_field_member_name field/> = ::std::move(other.<@sql_field_member_name field/>);
    </#list>

    reinitializeBlobs();

    return *this;
}
</#if>
<#list fields as field>
<#if !field.typeInfo.isSimple>

${field.typeInfo.typeFullName}& ${name}::Row::${field.getterName}()
{
    return <@sql_field_member_name field/>.value();
}
</#if>

<#if !field.typeInfo.isSimple>
const ${field.typeInfo.typeFullName}& ${name}::Row::${field.getterName}() const
<#else>
${field.typeInfo.typeFullName} ${name}::Row::${field.getterName}() const
</#if>
{
    return <@sql_field_member_name field/>.value();
}

<#if !field.typeInfo.isSimple>
void ${name}::Row::${field.setterName}(const ${field.typeInfo.typeFullName}& <@sql_field_argument_name field/>)
<#else>
void ${name}::Row::${field.setterName}(${field.typeInfo.typeFullName} <@sql_field_argument_name field/>)
</#if>
{
    <@sql_field_member_name field/> = <@sql_field_argument_name field/>;
}
<#if !field.typeInfo.isSimple>

void ${name}::Row::${field.setterName}(${field.typeInfo.typeFullName}&& <@sql_field_argument_name field/>)
{
    <@sql_field_member_name field/> = ::std::move(<@sql_field_argument_name field/>);
}
</#if>

void ${name}::Row::${field.resetterName}()
{
    <@sql_field_member_name field/>.reset();
}

bool ${name}::Row::${field.isSetIndicatorName}() const
{
    return <@sql_field_member_name field/>.hasValue();
}
</#list>
<#if withWriterCode>
    <#if needsChildrenInitialization>

void ${name}::Row::initializeChildren(<#if needsParameterProvider>IParameterProvider& parameterProvider</#if>)
{
        <#if requiresOwnerContext || needsParameterProvider>
    Row& row = *this;

        </#if>
        <#list fields as field>
            <#if field.typeParameters?has_content || field.needsChildrenInitialization>
    if (<@sql_field_member_name field/>)
    {
                <#if field.typeParameters?has_content>
        <@sql_field_member_name field/>->initialize(
                    <#list field.typeParameters as parameter>
                        <#if parameter.isExplicit>
                parameterProvider.<@sql_parameter_provider_getter_name parameter/>(row)<#rt>
                        <#else>
                ${parameter.expression}<#rt>
                        </#if>
                        <#if parameter?has_next>
                <#lt>,
                        </#if>
                    </#list>
                <#lt>);
                <#else>
        <@sql_field_member_name field/>->initializeChildren();
                </#if>
    }
            </#if>
        </#list>
}
    </#if>
</#if>
<#if hasImplicitParameters>

void ${name}::Row::reinitializeBlobs()
{
    <#if requiresOwnerContext>
    Row& row = *this;

    </#if>
        <#list fields as field>
            <#if field.hasImplicitParameters>
    if (<@sql_field_member_name field/> && <@sql_field_member_name field/>->isInitialized())
    {
        <@sql_field_member_name field/>->initialize(
                <#list field.typeParameters as parameter>
                    <#if parameter.isExplicit>
                <@sql_field_member_name field/>->${parameter.getterName}()<#rt>
                    <#else>
                ${parameter.expression}<#rt>
                    </#if>
                    <#if parameter?has_next>
                <#lt>,
                    </#if>
                </#list>
                <#lt>);
    }
            </#if>
        </#list>
}
</#if>
<@namespace_end package.path/>
