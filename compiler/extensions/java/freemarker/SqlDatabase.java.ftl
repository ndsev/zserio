<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "TypeInfo.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#if withValidationCode>
    <#assign needsParameterProvider = sql_db_needs_parameter_provider(fields)/>
</#if>

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

        connection = java.sql.DriverManager.getConnection(uriPath, connectionProps);
        isExternal = false;
        attachedDbList = new java.util.ArrayList<java.lang.String>();

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
        connection = externalConnection;
        isExternal = true;
        attachedDbList = null;

        initTables(tableToAttachedDbNameRelocationMap);
    }

    @Override
    public void close() throws java.sql.SQLException
    {
        if (!isExternal)
        {
            detachDatabases();
            connection.close();
        }
    }

    @Override
    public java.sql.Connection connection()
    {
        return connection;
    }
<#if withTypeInfoCode>

    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        final java.util.List<zserio.runtime.typeinfo.TableInfo> tableList =
                <@tables_info fields/>

        return new zserio.runtime.typeinfo.TypeInfo.SqlDatabaseTypeInfo(
            "${schemaTypeName}", tableList
        );
    }
</#if>
<#list fields as field>

    public ${field.javaTypeName} ${field.getterName}()
    {
        return this.<@sql_field_member_name field/>;
    }
</#list>
<#if withWriterCode>

    @Override
    public void createSchema() throws java.sql.SQLException
    {
        final boolean wasTransactionStarted = startTransaction();

        <#list fields as field>
        this.<@sql_field_member_name field/>.createTable();
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
        if (withoutRowIdTableNamesBlackList.contains(${field.name}_TABLE_NAME))
            this.<@sql_field_member_name field/>.createOrdinaryRowIdTable();
        else
            this.<@sql_field_member_name field/>.createTable();
            <#else>
        this.<@sql_field_member_name field/>.createTable();
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
        this.<@sql_field_member_name field/>.deleteTable();
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
        report.add(this.<@sql_field_member_name field/>.validate(<#if field.hasExplicitParameters>parameterProvider.get${field.name?cap_first}ParameterProvider()</#if>));
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
            ${field.name}_TABLE_NAME<#if field_has_next>,</#if>
</#list>
        };
    }

    private void initTables(java.util.Map<java.lang.String, java.lang.String> tableToAttachedDbNameRelocationMap)
    {
<#list fields as field>
        this.<@sql_field_member_name field/> = new ${field.javaTypeName}(connection,
                tableToAttachedDbNameRelocationMap.get(${field.name}_TABLE_NAME),
                ${field.name}_TABLE_NAME);
</#list>
    }

    private void executeUpdate(java.lang.String sql) throws java.sql.SQLException
    {
        final java.sql.Statement statement = connection.createStatement();
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

        attachedDbList.add(attachedDbName);
    }

    private void detachDatabases() throws java.sql.SQLException
    {
        for (java.lang.String attachedDbName : attachedDbList)
        {
            final java.lang.String sqlQuery = "DETACH DATABASE " + attachedDbName;
            executeUpdate(sqlQuery);
        }
    }
<#if withWriterCode>

    private boolean startTransaction() throws java.sql.SQLException
    {
        boolean wasTransactionStarted = false;
        if (connection.getAutoCommit())
        {
            connection.setAutoCommit(false);
            wasTransactionStarted = true;
        }

        return wasTransactionStarted;
    }

    private void endTransaction(boolean wasTransactionStarted) throws java.sql.SQLException
    {
        if (wasTransactionStarted)
        {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }
</#if>

    private static final java.lang.String DATABASE_NAME = "${name}";
<#list fields as field>
    private static final java.lang.String ${field.name}_TABLE_NAME = "${field.name}";
</#list>

    private final java.sql.Connection connection;
    private final boolean isExternal;
    private final java.util.List<java.lang.String> attachedDbList;

<#list fields as field>
    private ${field.javaTypeName} <@sql_field_member_name field/>;
</#list>
}
