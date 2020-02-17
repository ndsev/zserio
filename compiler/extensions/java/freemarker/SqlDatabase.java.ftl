<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#if withValidationCode>
    <#assign needsParameterProvider = sql_db_needs_parameter_provider(fields)/>
</#if>
<#macro field_member_name field>
    ${field.name}_<#t>
</#macro>

public class ${name} implements zserio.runtime.SqlDatabase<#if !withWriterCode>Reader</#if>
{
<#if withValidationCode && needsParameterProvider>
    public static interface ParameterProvider
    {
        <#list fields as field>
            <#if field.hasExplicitParameters>
        ${field.javaTypeName}.ParameterProvider get${field.name?cap_first}ParameterProvider();
            </#if>
        </#list>
    };

</#if>
    public ${name}(java.lang.String fileName) throws java.sql.SQLException
    {
        this(fileName, new java.util.HashMap<java.lang.String, java.lang.String>());
    }

    public ${name}(java.lang.String fileName,
            java.util.Map<java.lang.String, java.lang.String> tableToDbFileNameRelocationMap)
            throws java.sql.SQLException
    {
        final java.util.Properties connectionProps = new java.util.Properties();
        connectionProps.setProperty("flags", <#if withWriterCode>"CREATE"<#else>"READONLY"</#if>);
        final java.lang.String uriPath = "jdbc:sqlite:" + new java.io.File(fileName).toString();

        __connection = java.sql.DriverManager.getConnection(uriPath, connectionProps);
        __isExternal = false;
        __attachedDbList = new java.util.ArrayList<java.lang.String>();

        final java.util.Map<java.lang.String, java.lang.String> tableToAttachedDbNameRelocationMap =
                new java.util.HashMap<java.lang.String, java.lang.String>();
        final java.util.Map<java.lang.String, java.lang.String> dbFileNameToAttachedDbNameMap =
                new java.util.HashMap<java.lang.String, java.lang.String>();
        for (java.util.Map.Entry<java.lang.String, java.lang.String> entry :
                tableToDbFileNameRelocationMap.entrySet())
        {
            final java.lang.String relocatedTableName = entry.getKey();
            final java.lang.String dbFileName = entry.getValue();

            java.lang.String attachedDbName = dbFileNameToAttachedDbNameMap.get(dbFileName);
            if (attachedDbName == null)
            {
                attachedDbName = "${name}" + "_" + relocatedTableName;
                attachDatabase(dbFileName, attachedDbName);
                dbFileNameToAttachedDbNameMap.put(dbFileName, attachedDbName);
            }

            tableToAttachedDbNameRelocationMap.put(relocatedTableName, attachedDbName);
        }

        initTables(tableToAttachedDbNameRelocationMap);
    }

    public ${name}(java.sql.Connection externalConnection)
    {
        this(externalConnection, new java.util.HashMap<java.lang.String, java.lang.String>());
    }

    public ${name}(java.sql.Connection externalConnection,
            java.util.Map<java.lang.String, java.lang.String> tableToAttachedDbNameRelocationMap)
    {
        __connection = externalConnection;
        __isExternal = true;
        __attachedDbList = null;

        initTables(tableToAttachedDbNameRelocationMap);
    }

    @Override
    public void close() throws java.sql.SQLException
    {
        if (!__isExternal)
        {
            detachDatabases();
            __connection.close();
        }
    }

    @Override
    public java.sql.Connection connection()
    {
        return __connection;
    }

<#list fields as field>

    public ${field.javaTypeName} ${field.getterName}()
    {
        return <@field_member_name field/>;
    }
</#list>
<#if withWriterCode>

    @Override
    public void createSchema() throws java.sql.SQLException
    {
        final boolean wasTransactionStarted = startTransaction();

        <#list fields as field>
        <@field_member_name field/>.createTable();
        </#list>

        endTransaction(wasTransactionStarted);
    }

    <#assign hasWithoutRowIdTable=false/>
    <#list fields as field>
        <#if field.isWithoutRowIdTable>
            <#assign hasWithoutRowIdTable=true/>
            <#break>
        </#if>
    </#list>
    @Override
    public void createSchema(java.util.Set<java.lang.String> withoutRowIdTableNamesBlackList)
            throws java.sql.SQLException
    {
    <#if hasWithoutRowIdTable>
        final boolean wasTransactionStarted = startTransaction();

        <#list fields as field>
            <#if field.isWithoutRowIdTable>
        if (withoutRowIdTableNamesBlackList.contains(${field.name?upper_case}_TABLE_NAME))
            <@field_member_name field/>.createOrdinaryRowIdTable();
        else
            <@field_member_name field/>.createTable();
            <#else>
        <@field_member_name field/>.createTable();
            </#if>
        </#list>

        endTransaction(wasTransactionStarted);
    <#else>
        createSchema();
    </#if>
    }

    @Override
    public void deleteSchema() throws java.sql.SQLException
    {
        final boolean wasTransactionStarted = startTransaction();

    <#list fields as field>
        <@field_member_name field/>.deleteTable();
    </#list>

        endTransaction(wasTransactionStarted);
    }
</#if>
<#if withValidationCode>

    public zserio.runtime.validation.ValidationReport validate(<#if needsParameterProvider>ParameterProvider parameterProvider</#if>)
            throws java.sql.SQLException
    {
        final zserio.runtime.validation.ValidationReport report =
                new zserio.runtime.validation.ValidationReport();
    <#list fields as field>
        report.add(<@field_member_name field/>.validate(<#if field.hasExplicitParameters>parameterProvider.get${field.name?cap_first}ParameterProvider()</#if>));
    </#list>

        return report;
    }
</#if>

    public static java.lang.String databaseName()
    {
        return DATABASE_NAME;
    }

    public static java.lang.String[] tableNames()
    {
        return new java.lang.String[]
        {
<#list fields as field>
            ${field.name?upper_case}_TABLE_NAME<#if field_has_next>,</#if>
</#list>
        };
    }

    private void initTables(java.util.Map<java.lang.String, java.lang.String> tableToAttachedDbNameRelocationMap)
    {
<#list fields as field>
        <@field_member_name field/> = new ${field.javaTypeName}(__connection,
                tableToAttachedDbNameRelocationMap.get(${field.name?upper_case}_TABLE_NAME),
                ${field.name?upper_case}_TABLE_NAME);
</#list>
    }

    private void executeUpdate(java.lang.String sql) throws java.sql.SQLException
    {
        final java.sql.Statement statement = __connection.createStatement();
        try
        {
            statement.executeUpdate(sql);
        }
        finally
        {
            statement.close();
        }
    }

    private void attachDatabase(java.lang.String dbFileName, java.lang.String attachedDbName)
            throws java.sql.SQLException
    {
        final java.lang.StringBuilder sqlQuery = new java.lang.StringBuilder("ATTACH DATABASE '");
        sqlQuery.append(new java.io.File(dbFileName).toString());
        sqlQuery.append("' AS ");
        sqlQuery.append(attachedDbName);
        executeUpdate(sqlQuery.toString());

        __attachedDbList.add(attachedDbName);
    }

    private void detachDatabases() throws java.sql.SQLException
    {
        for (java.lang.String attachedDbName : __attachedDbList)
        {
            final java.lang.String sqlQuery = "DETACH DATABASE " + attachedDbName;
            executeUpdate(sqlQuery);
        }
    }
<#if withWriterCode>

    private boolean startTransaction() throws java.sql.SQLException
    {
        boolean wasTransactionStarted = false;
        if (__connection.getAutoCommit())
        {
            __connection.setAutoCommit(false);
            wasTransactionStarted = true;
        }

        return wasTransactionStarted;
    }

    private void endTransaction(boolean wasTransactionStarted) throws java.sql.SQLException
    {
        if (wasTransactionStarted)
        {
            __connection.commit();
            __connection.setAutoCommit(true);
        }
    }
</#if>

    private static final java.lang.String DATABASE_NAME = "${name}";
<#list fields as field>
    private static final java.lang.String ${field.name?upper_case}_TABLE_NAME = "${field.name}";
</#list>

    private final java.sql.Connection __connection;
    private final boolean __isExternal;
    private final java.util.List<java.lang.String> __attachedDbList;

<#list fields as field>
    private ${field.javaTypeName} <@field_member_name field/>;
</#list>
}
