<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@file_header generatorDescription/>

#include <zserio/CppRuntimeException.h>
#include <zserio/SqliteException.h>
#include <zserio/BitStreamReader.h>
<#if withWriterCode>
#include <zserio/BitStreamWriter.h>
</#if>
<@system_includes cppSystemIncludes/>

<@user_include package.path, "${name}.h"/>
<@user_includes cppUserIncludes, false/>
<@namespace_begin package.path/>

<#assign needsParameterProvider=explicitParameters?has_content/>
<#assign hasBlobField=sql_table_has_blob_field(fields)/>
<#if withWriterCode>
    <#assign hasNonVirtualField=sql_table_has_non_virtual_field(fields)/>
</#if>
${name}::${name}(::zserio::SqliteConnection& db, const ::std::string& tableName,
        const ::std::string& attachedDbName) :
        m_db(db), m_name(tableName), m_attachedDbName(attachedDbName)
{
}

<#if withWriterCode>
void ${name}::createTable()
{
    ::std::string sqlQuery;
    appendCreateTableToQuery(sqlQuery);
    <#if hasNonVirtualField && isWithoutRowId>
    sqlQuery += " WITHOUT ROWID";
    </#if>
    m_db.executeUpdate(sqlQuery.c_str());
}

    <#if hasNonVirtualField && isWithoutRowId>
void ${name}::createOrdinaryRowIdTable()
{
    ::std::string sqlQuery;
    appendCreateTableToQuery(sqlQuery);
    m_db.executeUpdate(sqlQuery.c_str());
}

    </#if>
void ${name}::deleteTable()
{
    ::std::string sqlQuery = "DROP TABLE ";
    appendTableNameToQuery(sqlQuery);
    m_db.executeUpdate(sqlQuery.c_str());
}

</#if>
${name}::Reader ${name}::createReader(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>const ::std::string& condition) const
{
    // assemble sql query
    ::std::string sqlQuery;
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

    return Reader(m_db, <#if needsParameterProvider>parameterProvider, </#if>sqlQuery);
}

${name}::Reader::Reader(::zserio::SqliteConnection& db, <#rt>
        <#lt><#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>const ::std::string& sqlQuery) :
        <#if needsParameterProvider>m_parameterProvider(parameterProvider),</#if>
        m_stmt(db.prepareStatement(sqlQuery))
{
    makeStep();
}

bool ${name}::Reader::hasNext() const noexcept
{
    return m_lastResult == SQLITE_ROW;
}

<#macro read_blob field blob_ctor_variable_name="reader">
        <#list field.typeParameters as parameter>
        <@sql_parameter_variable_type parameter/> _${parameter.definitionName} = <#rt>
            <#if parameter.isExplicit>
                <#lt>m_parameterProvider.<@sql_parameter_provider_getter_name parameter/>(row);
            <#else>
                <#lt>${parameter.expression};
            </#if>
        </#list>
        const ${field.cppTypeName} blob(${blob_ctor_variable_name}<#rt>
        <#list field.typeParameters as parameter>
                , _${parameter.definitionName}<#t>
        </#list>
        <#lt>);
</#macro>
${name}::Row ${name}::Reader::next()
{
    if (!hasNext())
        throw ::zserio::SqliteException("Table::Reader::next: next row is not available", m_lastResult);

    Row row;
<#list fields as field>

    // field ${field.name}
    if (sqlite3_column_type(m_stmt.get(), ${field?index}) != SQLITE_NULL)
    {
    <#if field.sqlTypeData.isBlob>
        const void* blobData = sqlite3_column_blob(m_stmt.get(), ${field?index});
        const int blobDataLength = sqlite3_column_bytes(m_stmt.get(), ${field?index});
        ::zserio::BitStreamReader reader(reinterpret_cast<const uint8_t*>(blobData),
                static_cast<size_t>(blobDataLength));
        <@read_blob field/>
        row.${field.setterName}(::std::move(blob));
    <#elseif field.sqlTypeData.isInteger>
        const int64_t intValue = sqlite3_column_int64(m_stmt.get(), ${field?index});
        <#if field.enumData??>
        const ${field.cppTypeName} enumValue = ::zserio::valueToEnum<${field.cppTypeName}>(static_cast<${field.enumData.baseCppTypeName}>(intValue));
        row.${field.setterName}(enumValue);
        <#elseif field.isBoolean>
        row.${field.setterName}(intValue != 0);
        <#else>
        row.${field.setterName}(static_cast<${field.cppTypeName}>(intValue));
        </#if>
    <#elseif field.sqlTypeData.isReal>
        const double doubleValue = sqlite3_column_double(m_stmt.get(), ${field?index});
        row.${field.setterName}(static_cast<${field.cppTypeName}>(doubleValue));
    <#else>
        const unsigned char* textValue = sqlite3_column_text(m_stmt.get(), ${field?index});
        row.${field.setterName}(${field.cppTypeName}(reinterpret_cast<const char*>(textValue)));
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
        throw ::zserio::SqliteException("${name}::Read: sqlite3_step() failed", m_lastResult);
}
<#if withWriterCode>

void ${name}::write(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>::std::vector<Row>& rows)
{
    // assemble sql query
    ::std::string sqlQuery("INSERT INTO ");
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
    int result = SQLITE_OK;
    for (::std::vector<Row>::iterator it = rows.begin(); it != rows.end(); ++it)
    {
        writeRow(<#if needsParameterProvider>parameterProvider, </#if>*it, *statement);
        result = sqlite3_step(statement.get());
        if (result != SQLITE_DONE)
            throw ::zserio::SqliteException("Write: sqlite3_step() failed", result);

        sqlite3_clear_bindings(statement.get());
        result = sqlite3_reset(statement.get());
        if (result != SQLITE_OK)
            throw ::zserio::SqliteException("Write: sqlite3_reset() failed", result);
    }

    m_db.endTransaction(wasTransactionStarted);
}

void ${name}::update(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>Row& row, const ::std::string& whereCondition)
{
    // assemble sql query
    ::std::string sqlQuery("UPDATE ");
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
        throw ::zserio::SqliteException("Update: sqlite3_step() failed", result);
}

void ${name}::writeRow(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>Row& row, sqlite3_stmt& statement)
{
<#if needsChildrenInitialization>
    row.initializeChildren(<#if needsParameterProvider>parameterProvider</#if>);

</#if>
<#if hasBlobField>
    row.initializeOffsets();

</#if>
    int result;

    <#list fields as field>
    // field ${field.name}
    if (!row.${field.getterName}())
    {
        result = sqlite3_bind_null(&statement, ${field?index + 1});
    }
    else
    {
        <#if field.sqlTypeData.isBlob>
        ${field.cppTypeName}& blob = *row.${field.getterName}();
        ::zserio::BitStreamWriter writer;
        blob.write(writer, ::zserio::NO_PRE_WRITE_ACTION);
        size_t blobDataLength;
        const uint8_t* blobData = writer.getWriteBuffer(blobDataLength);
        result = sqlite3_bind_blob(&statement, ${field?index + 1}, blobData, static_cast<int>(blobDataLength), SQLITE_TRANSIENT);
        <#elseif field.sqlTypeData.isInteger>
        const int64_t intValue = static_cast<int64_t>(*row.${field.getterName}());
        result = sqlite3_bind_int64(&statement, ${field?index + 1}, intValue);
        <#elseif field.sqlTypeData.isReal>
        const ${field.cppTypeName} realValue = *row.${field.getterName}();
        result = sqlite3_bind_double(&statement, ${field?index + 1}, static_cast<double>(realValue));
        <#else>
        const ${field.cppTypeName}& stringValue = *row.${field.getterName}();
        result = sqlite3_bind_text(&statement, ${field?index + 1}, stringValue.c_str(), -1, SQLITE_TRANSIENT);
        </#if>
    }
    if (result != SQLITE_OK)
        throw ::zserio::SqliteException("${name}::WriteRow: sqlite3_bind() for field ${field.name} failed", result);
        <#if field?has_next>

        </#if>
    </#list>
}

void ${name}::appendCreateTableToQuery(::std::string& sqlQuery)
{
    sqlQuery += "CREATE <#if virtualTableUsing??>VIRTUAL </#if>TABLE ";
    appendTableNameToQuery(sqlQuery);
    sqlQuery +=
    <#if virtualTableUsing??>
            " USING ${virtualTableUsing}"<#if !hasNonVirtualField && !sqlConstraint??>;</#if>
    </#if>
    <#if hasNonVirtualField || sqlConstraint??>
            "("
        <#list fields as field>
            <#if !field.isVirtual>
            "${field.name}<#if needsTypesInSchema> ${field.sqlTypeData.name}</#if><#rt>
                    <#lt><#if field.sqlConstraint??> ${sql_strip_quotes(field.sqlConstraint)}</#if><#rt>
                    <#lt><#if field?has_next>, </#if>"
            </#if>
        </#list>
        <#if hasNonVirtualField && sqlConstraint??>
            ", "
        </#if>
        <#if sqlConstraint??>
            ${sqlConstraint}
        </#if>
            ")";
    </#if>
}
</#if>

void ${name}::appendTableNameToQuery(::std::string& sqlQuery) const
{
    sqlQuery.append(m_attachedDbName.empty() ? m_name : (m_attachedDbName + "." + m_name));
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
<#if !field.isSimpleType>

${field.optionalCppTypeName}& ${name}::Row::${field.getterName}()
{
    return <@sql_field_member_name field/>;
}
</#if>

const ${field.optionalCppTypeName}& ${name}::Row::${field.getterName}() const
{
    return <@sql_field_member_name field/>;
}

void ${name}::Row::${field.setterName}(${field.optionalCppArgumentTypeName} <@sql_field_argument_name field/>)
{
    <@sql_field_member_name field/> = <@sql_field_argument_name field/>;
}
<#if !field.isSimpleType>

void ${name}::Row::${field.setterName}(${field.optionalCppTypeName}&& <@sql_field_argument_name field/>)
{
    <@sql_field_member_name field/> = ::std::move(<@sql_field_argument_name field/>);
}
</#if>
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
    <#if hasBlobField>

void ${name}::Row::initializeOffsets()
{
        <#list fields as field>
            <#if field.sqlTypeData.isBlob>
    if (<@sql_field_member_name field/>)
        <@sql_field_member_name field/>->initializeOffsets(0);
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
