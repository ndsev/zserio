<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "GeneratePkgPrefix.inc.ftl">
<@standard_header generatorDescription, packageName, javaMajorVersion, [
        "java.net.URISyntaxException",
        "java.sql.Connection",
        "java.sql.SQLException",
        "java.util.Map",
        "java.util.HashMap",
        "java.util.Set"
]/>
<@imports ["zserio.runtime.SqlDatabase"]/>
<#if withWriterCode>
<@imports ["zserio.runtime.SqlDatabaseWriter"]/>
</#if>
<#if withValidationCode>
<@imports ["zserio.runtime.validation.ValidationReport"]/>
    <#assign needsParameterProvider = sql_db_needs_parameter_provider(fields)/>
</#if>

<@class_header generatorDescription/>
public class ${name} extends SqlDatabase<#if withWriterCode> implements SqlDatabaseWriter</#if>
{
<#if withValidationCode && needsParameterProvider>
    public static interface IParameterProvider
    {
        <#list fields as field>
            <#if field.hasExplicitParameters>
        ${field.javaTypeName}.IParameterProvider get${field.name?cap_first}ParameterProvider();
            </#if>
        </#list>
    };

</#if>
    public ${name}(String fileName) throws SQLException, URISyntaxException
    {
        this(fileName, new HashMap<String, String>());
    }

    public ${name}(String fileName, Map<String, String> tableToDbFileNameRelocationMap)
        throws SQLException, URISyntaxException
    {
        super(fileName, Mode.<#if withWriterCode>CREATE<#else>READONLY</#if>, tableToDbFileNameRelocationMap);
        initTables();
    }

    public ${name}(Connection externalConnection)
    {
        this(externalConnection, new HashMap<String, String>());
    }

    public ${name}(Connection externalConnection, Map<String, String> tableToDbNameRelocationMap)
    {
        super(externalConnection, tableToDbNameRelocationMap);
        initTables();
    }
<#list fields as field>

    public ${field.javaTypeName} ${field.getterName}()
    {
        return ${field.name};
    }
</#list>
<#if withWriterCode>

    @Override
    public void createSchema() throws SQLException
    {
    <#if fields?has_content>
        final boolean wasTransactionStarted = startTransaction();

        <#list fields as field>
        ${field.name}.createTable();
        </#list>

        endTransaction(wasTransactionStarted);
    </#if>
    }

    <#assign hasWithoutRowIdTable=false/>
    <#list fields as field>
        <#if field.isWithoutRowIdTable>
            <#assign hasWithoutRowIdTable=true/>
            <#break>
        </#if>
    </#list>
    @Override
    public void createSchema(Set<String> withoutRowIdTableNamesBlackList) throws SQLException
    {
    <#if fields?has_content>
        <#if hasWithoutRowIdTable>
        final boolean wasTransactionStarted = startTransaction();

            <#list fields as field>
                <#if field.isWithoutRowIdTable>
        if (withoutRowIdTableNamesBlackList.contains(${field.name?upper_case}_TABLE_NAME))
            ${field.name}.createOrdinaryRowIdTable();
        else
            ${field.name}.createTable();
                <#else>
        ${field.name}.createTable();
                </#if>
            </#list>

        endTransaction(wasTransactionStarted);
        <#else>
        createSchema();
        </#if>
    </#if>
    }

    @Override
    public void deleteSchema() throws SQLException
    {
    <#if fields?has_content>
        final boolean wasTransactionStarted = startTransaction();

        <#list fields as field>
        ${field.name}.deleteTable();
        </#list>

        endTransaction(wasTransactionStarted);
    </#if>
    }
</#if>
<#if withValidationCode>

    public ValidationReport validate(<#if needsParameterProvider>IParameterProvider parameterProvider</#if>)
            throws SQLException
    {
        final ValidationReport report = new ValidationReport();
    <#list fields as field>
        report.add(${field.name}.validate(<#if field.hasExplicitParameters>parameterProvider.get${field.name?cap_first}ParameterProvider()</#if>));
    </#list>

        return report;
    }
</#if>

    public static String getDatabaseName()
    {
        return DATABASE_NAME;
    }

    public static String[] getTableNames()
    {
        return new String[]
        {
<#list fields as field>
            ${field.name?upper_case}_TABLE_NAME<#if field_has_next>,</#if>
</#list>
        };
    }

    private void initTables()
    {
<#list fields as field>
        ${field.name} = new ${field.javaTypeName}(
                this, getAttachedDbName(${field.name?upper_case}_TABLE_NAME), ${field.name?upper_case}_TABLE_NAME);
</#list>
    }

    private static final String DATABASE_NAME = "${name}";
<#list fields as field>
    private static final String ${field.name?upper_case}_TABLE_NAME = "${field.name}";
</#list>
<#if fields?has_content>

</#if>
<#list fields as field>
    private ${field.javaTypeName} ${field.name};
</#list>
}
