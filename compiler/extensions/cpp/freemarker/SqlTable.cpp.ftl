<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#if withInspectorCode>
    <#include "Inspector.inc.ftl">
</#if>
<@file_header generatorDescription/>

#include <zserio/CppRuntimeException.h>
#include <zserio/SqliteException.h>
#include <zserio/BitStreamReader.h>
<#if withWriterCode>
#include <zserio/BitStreamWriter.h>
</#if>
<@system_includes cppSystemIncludes, false/>

#include "<@include_path package.path, "${name}.h"/>"
<#if withInspectorCode>
#include "<@include_path rootPackage.path, "InspectorZserioTypeNames.h"/>"
#include "<@include_path rootPackage.path, "InspectorZserioNames.h"/>"
</#if>
<@user_includes cppUserIncludes, false/>

<@namespace_begin package.path/>

<#assign hasBlobField=sql_table_has_blob_field(fields)/>
<#assign needsParameterProvider=explicitParameters?has_content/>
<#assign needsInspectorParameterProvider=sql_table_needs_inspector_parameter_provider(fields)/>
<#if withWriterCode>
    <#assign hasNonVirtualField=sql_table_has_non_virtual_field(fields)/>
</#if>
${name}::${name}(zserio::SqlDatabase& db, const std::string& tableName) : m_db(db), m_name(tableName)
{
}

${name}::~${name}()
{
}

<#if withWriterCode>
void ${name}::createTable()
{
    std::string sqlQuery;
    appendCreateTableToQuery(sqlQuery);
    <#if hasNonVirtualField && isWithoutRowId>
    sqlQuery += " WITHOUT ROWID";
    </#if>
    m_db.executeUpdate(sqlQuery.c_str());
}

    <#if hasNonVirtualField && isWithoutRowId>
void ${name}::createOrdinaryRowIdTable()
{
    std::string sqlQuery;
    appendCreateTableToQuery(sqlQuery);
    m_db.executeUpdate(sqlQuery.c_str());
}

    </#if>
void ${name}::deleteTable()
{
    std::string sqlQuery = "DROP TABLE ";
    sqlQuery += m_name;
    m_db.executeUpdate(sqlQuery.c_str());
}

</#if>
void ${name}::read(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>std::vector<${rowName}>& rows) const
{
    read(<#if needsParameterProvider>parameterProvider, </#if>"", rows);
}

void ${name}::read(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>const std::string& condition,
        std::vector<${rowName}>& rows) const
{
    // assemble sql query
    std::string sqlQuery;
    sqlQuery +=
            "SELECT "
<#list fields as field>
            "${field.name}<#if field_has_next>, </#if>"
</#list>
            " FROM '";
    sqlQuery += m_name;
    sqlQuery += "'";
    if (!condition.empty())
    {
        sqlQuery += " WHERE ";
        sqlQuery += condition;
    }

    // read rows
    rows.clear();
    sqlite3_stmt* statement = m_db.prepareStatement(sqlQuery);
    int result;
    while (true)
    {
        result = sqlite3_step(statement);
        if (result == SQLITE_DONE || result != SQLITE_ROW)
            break;

        readRow(<#if needsParameterProvider>parameterProvider, </#if>*statement, rows);
    }

    sqlite3_finalize(statement);
    if (result != SQLITE_DONE)
        throw zserio::SqliteException("Read: sqlite3_step() failed", result);
}
<#if withWriterCode>

void ${name}::write(std::vector<${rowName}>& rows)
{
    // assemble sql query
    std::string sqlQuery("INSERT INTO '");
    sqlQuery += m_name;
    sqlQuery +=
            "'("
    <#list fields as field>
            "${field.name}<#if field_has_next>, </#if>"
    </#list>
            ") VALUES (<#rt>
    <#list fields as field>
            ?<#if field_has_next>, </#if><#t>
    </#list>
            );";<#lt>

    // write rows
    m_db.executeUpdate("BEGIN TRANSACTION;");
    sqlite3_stmt* statement = m_db.prepareStatement(sqlQuery);
    int result = SQLITE_OK;
    for (std::vector<${rowName}>::iterator it = rows.begin(); it != rows.end(); ++it)
    {
        writeRow(*it, *statement);
        result = sqlite3_step(statement);
        if (result != SQLITE_DONE)
            break;

        sqlite3_clear_bindings(statement);
        result = sqlite3_reset(statement);
        if (result != SQLITE_OK)
            break;
    }
    sqlite3_finalize(statement);
    if (result != SQLITE_DONE && result != SQLITE_OK)
        throw zserio::SqliteException("Write: sqlite3_step() failed", result);

    m_db.executeUpdate("COMMIT;");
}

void ${name}::update(${rowName}& row, const std::string& whereCondition)
{
    // assemble sql query
    std::string sqlQuery("UPDATE ");
    sqlQuery += m_name;
    sqlQuery +=
            " SET"
    <#list fields as field>
            " ${field.name}=?<#if field_has_next>,</#if>"
    </#list>
            " WHERE ";
    sqlQuery += whereCondition;

    // update row
    sqlite3_stmt* statement = m_db.prepareStatement(sqlQuery);
    writeRow(row, *statement);
    const int result = sqlite3_step(statement);
    sqlite3_finalize(statement);
    if (result != SQLITE_DONE)
        throw zserio::SqliteException("Update: sqlite3_step() failed", result);
}
</#if>
<#macro read_blob field blob_ctor_variable_name="reader" called_by_inspector=false>
        <#list field.typeParameters as parameter>
        <@sql_parameter_variable_type parameter/> _${parameter.definitionName} = <#rt>
            <#if called_by_inspector>
                <#lt>parameterProvider.<@inspector_parameter_provider_getter_name parameter/>();
            <#elseif parameter.isExplicit>
                <#lt>parameterProvider.<@sql_parameter_provider_getter_name parameter/>(<#if !called_by_inspector>statement</#if>);
            <#else>
                <#lt>${parameter.expression};
            </#if>
        </#list>
        <#if !called_by_inspector>const </#if>${field.cppTypeName} blob(${blob_ctor_variable_name}<#rt>
        <#list field.typeParameters as parameter>
                , _${parameter.definitionName}<#t>
        </#list>
        <#lt>);
</#macro>
<#if withInspectorCode>

bool ${name}::convertBitStreamToBlobTree(const std::string&<#if hasBlobField> blobName</#if>,
        zserio::BitStreamReader&<#if hasBlobField> reader</#if>,
        ${rootPackage.name}::IInspectorParameterProvider&<#if needsInspectorParameterProvider> parameterProvider</#if>,
        zserio::BlobInspectorTree&<#if hasBlobField> tree</#if>) const
{
<#list fields as field>
    <#if field.sqlTypeData.isBlob>
    if (blobName == "${field.name}")
    {
        <@read_blob field "reader", true/>
        zserio::BitStreamWriter dummyWriter(NULL, 0);
        tree.setZserioTypeName(${rootPackage.name}::InspectorZserioTypeNames::<@inspector_zserio_type_name field.zserioTypeName/>);
        tree.setZserioName(${rootPackage.name}::InspectorZserioNames::<@inspector_zserio_name field.name/>);
        const size_t startBitPosition = dummyWriter.getBitPosition();
        blob.write(dummyWriter, tree);
        tree.setZserioDescriptor(startBitPosition, dummyWriter.getBitPosition());
        return true;
    }

    </#if>
</#list>
    return false;
}

bool ${name}::convertBlobTreeToBitStream(const std::string&<#if hasBlobField> blobName</#if>,
        const zserio::BlobInspectorTree&<#if hasBlobField> tree</#if>,
        ${rootPackage.name}::IInspectorParameterProvider&<#if needsInspectorParameterProvider> parameterProvider</#if>,
        zserio::BitStreamWriter&<#if hasBlobField> writer</#if>) const
{
<#list fields as field>
    <#if field.sqlTypeData.isBlob>
    if (blobName == "${field.name}")
    {
        <@read_blob field "tree", true/>
        blob.write(writer);
        return true;
    }

    </#if>
</#list>
    return false;
}

bool ${name}::doesBlobExist(const std::string&<#if hasBlobField> blobName</#if>) const
{
<#list fields as field>
    <#if field.sqlTypeData.isBlob>
    if (blobName == "${field.name}")
        return true;

    </#if>
</#list>
    return false;
}
</#if>

void ${name}::readRow(<#if needsParameterProvider>IParameterProvider& parameterProvider, </#if><#rt>
        <#lt>sqlite3_stmt&<#if fields?has_content> statement</#if>,
        std::vector<${rowName}>& rows)
{
    rows.resize(rows.size() + 1);
    ${rowName}& row = rows.back();

<#list fields as field>
    // field ${field.name}
    if (sqlite3_column_type(&statement, ${field_index}) == SQLITE_NULL)
    {
        row.setNull${field.name?cap_first}();
    }
    else
    {
    <#if field.sqlTypeData.isBlob>
        const void* blobData = sqlite3_column_blob(&statement, ${field_index});
        const int blobDataLength = sqlite3_column_bytes(&statement, ${field_index});
        zserio::BitStreamReader reader(reinterpret_cast<const uint8_t*>(blobData),
                static_cast<size_t>(blobDataLength));
        <@read_blob field/>
        row.set${field.name?cap_first}(blob);
    <#elseif field.sqlTypeData.isInteger>
        const int64_t intValue = sqlite3_column_int64(&statement, ${field_index});
        <#if field.enumData??>
        const ${field.cppTypeName} enumValue = ${field.cppTypeName}::toEnum(static_cast<${field.enumData.baseCppTypeName}>(intValue));
        row.set${field.name?cap_first}(enumValue);
        <#elseif field.isBoolean>
        row.set${field.name?cap_first}(intValue != 0);
        <#else>
        row.set${field.name?cap_first}(static_cast<${field.cppTypeName}>(intValue));
        </#if>
    <#elseif field.sqlTypeData.isReal>
        const double doubleValue = sqlite3_column_double(&statement, ${field_index});
        row.set${field.name?cap_first}(static_cast<${field.cppTypeName}>(doubleValue));
    <#else>
        const unsigned char* textValue = sqlite3_column_text(&statement, ${field_index});
        row.set${field.name?cap_first}(reinterpret_cast<const char*>(textValue));
    </#if>
    }
    <#if field_has_next>

    </#if>
</#list>
}
<#if withWriterCode>

void ${name}::writeRow(${rowName}&<#if fields?has_content> row</#if>, sqlite3_stmt&<#if fields?has_content> statement</#if>)
{
    <#if fields?has_content>
    int result;
    </#if>
    <#list fields as field>
    // field ${field.name}
    if (row.isNull${field.name?cap_first}())
    {
        result = sqlite3_bind_null(&statement, ${field_index + 1});
    }
    else
    {
        <#if field.sqlTypeData.isBlob>
        ${field.cppTypeName}& blob = row.get${field.name?cap_first}();
        zserio::BitStreamWriter writer;
        blob.write(writer);
        size_t blobDataLength;
        const uint8_t* blobData = writer.getWriteBuffer(blobDataLength);
        result = sqlite3_bind_blob(&statement, ${field_index + 1}, blobData, static_cast<int>(blobDataLength), SQLITE_TRANSIENT);
        <#elseif field.sqlTypeData.isInteger>
        const int64_t intValue = static_cast<int64_t>(row.get${field.name?cap_first}()<#if field.enumData??>.getValue()</#if>);
        result = sqlite3_bind_int64(&statement, ${field_index + 1}, intValue);
        <#elseif field.sqlTypeData.isReal>
        const ${field.cppTypeName} realValue = row.get${field.name?cap_first}();
        result = sqlite3_bind_double(&statement, ${field_index + 1}, static_cast<double>(realValue));
        <#else>
        const ${field.cppTypeName}& stringValue = row.get${field.name?cap_first}();
        result = sqlite3_bind_text(&statement, ${field_index + 1}, stringValue.c_str(), -1, SQLITE_TRANSIENT);
        </#if>
    }
    if (result != SQLITE_OK)
        throw zserio::SqliteException("WriteRow: sqlite3_bind() for field ${field.name} failed", result);
        <#if field_has_next>

        </#if>
    </#list>
}

void ${name}::appendCreateTableToQuery(std::string& sqlQuery)
{
    sqlQuery += "CREATE <#if virtualTableUsing??>VIRTUAL </#if>TABLE ";
    sqlQuery += m_name;
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
                    <#lt><#if field_has_next>, </#if>"
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

<@namespace_end package.path/>
