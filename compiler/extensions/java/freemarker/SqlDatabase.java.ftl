<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "GeneratePkgPrefix.inc.ftl">
<@standard_header generatorDescription, packageName, javaMajorVersion, [
        "java.io.File",
        "java.sql.Connection",
        "java.sql.DriverManager",
        "java.sql.Statement",
        "java.sql.SQLException",
        "java.util.ArrayList",
        "java.util.HashMap",
        "java.util.List",
        "java.util.Map",
        "java.util.Properties",
        "java.util.Set"
]/>
<#if withWriterCode>
<@imports ["zserio.runtime.SqlDatabase"]/>
<#else>
<@imports ["zserio.runtime.SqlDatabaseReader"]/>
</#if>
<#if withValidationCode>
<@imports ["zserio.runtime.validation.ValidationReport"]/>
    <#assign needsParameterProvider = sql_db_needs_parameter_provider(fields)/>
</#if>

<#macro field_member_name field>
    ${field.name}_<#t>
</#macro>
<@class_header generatorDescription/>
public class ${name} implements SqlDatabase<#if !withWriterCode>Reader</#if>
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
    public ${name}(String fileName) throws SQLException
    {
        this(fileName, new HashMap<String, String>());
    }

    public ${name}(String fileName, Map<String, String> tableToDbFileNameRelocationMap) throws SQLException
    {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty("flags", <#if withWriterCode>"CREATE"<#else>"READONLY"</#if>);
        final String uriPath = "jdbc:sqlite:" + new File(fileName).toString();

        __connection = DriverManager.getConnection(uriPath, connectionProps);
        __isExternal = false;
        __attachedDbList = new ArrayList<String>();

        final Map<String, String> tableToAttachedDbNameRelocationMap = new HashMap<String, String>();
        final Map<String, String> dbFileNameToAttachedDbNameMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : tableToDbFileNameRelocationMap.entrySet())
        {
            final String relocatedTableName = entry.getKey();
            final String dbFileName = entry.getValue();

            String attachedDbName = dbFileNameToAttachedDbNameMap.get(dbFileName);
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

    public ${name}(Connection externalConnection)
    {
        this(externalConnection, new HashMap<String, String>());
    }

    public ${name}(Connection externalConnection, Map<String, String> tableToAttachedDbNameRelocationMap)
    {
        __connection = externalConnection;
        __isExternal = true;
        __attachedDbList = null;

        initTables(tableToAttachedDbNameRelocationMap);
    }

    @Override
    public void close() throws SQLException
    {
        if (!__isExternal)
        {
            detachDatabases();
            __connection.close();
        }
    }

    @Override
    public Connection connection()
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
    public void createSchema() throws SQLException
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
    public void createSchema(Set<String> withoutRowIdTableNamesBlackList) throws SQLException
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
    public void deleteSchema() throws SQLException
    {
        final boolean wasTransactionStarted = startTransaction();

    <#list fields as field>
        <@field_member_name field/>.deleteTable();
    </#list>

        endTransaction(wasTransactionStarted);
    }
</#if>
<#if withValidationCode>

    public ValidationReport validate(<#if needsParameterProvider>ParameterProvider parameterProvider</#if>)
            throws SQLException
    {
        final ValidationReport report = new ValidationReport();
    <#list fields as field>
        report.add(<@field_member_name field/>.validate(<#if field.hasExplicitParameters>parameterProvider.get${field.name?cap_first}ParameterProvider()</#if>));
    </#list>

        return report;
    }
</#if>

    public static String databaseName()
    {
        return DATABASE_NAME;
    }

    public static String[] tableNames()
    {
        return new String[]
        {
<#list fields as field>
            ${field.name?upper_case}_TABLE_NAME<#if field_has_next>,</#if>
</#list>
        };
    }

    private void initTables(Map<String, String> tableToAttachedDbNameRelocationMap)
    {
<#list fields as field>
        <@field_member_name field/> = new ${field.javaTypeName}(__connection,
                tableToAttachedDbNameRelocationMap.get(${field.name?upper_case}_TABLE_NAME),
                ${field.name?upper_case}_TABLE_NAME);
</#list>
    }

    private void executeUpdate(String sql) throws SQLException
    {
        final Statement statement = __connection.createStatement();
        try
        {
            statement.executeUpdate(sql);
        }
        finally
        {
            statement.close();
        }
    }

    private void attachDatabase(String dbFileName, String attachedDbName) throws SQLException
    {
        final StringBuilder sqlQuery = new StringBuilder("ATTACH DATABASE '");
        sqlQuery.append(new File(dbFileName).toString());
        sqlQuery.append("' AS ");
        sqlQuery.append(attachedDbName);
        executeUpdate(sqlQuery.toString());

        __attachedDbList.add(attachedDbName);
    }

    private void detachDatabases() throws SQLException
    {
        for (String attachedDbName : __attachedDbList)
        {
            final String sqlQuery = "DETACH DATABASE " + attachedDbName;
            executeUpdate(sqlQuery);
        }
    }
<#if withWriterCode>

    private boolean startTransaction() throws SQLException
    {
        boolean wasTransactionStarted = false;
        if (__connection.getAutoCommit())
        {
            __connection.setAutoCommit(false);
            wasTransactionStarted = true;
        }

        return wasTransactionStarted;
    }

    private void endTransaction(boolean wasTransactionStarted) throws SQLException
    {
        if (wasTransactionStarted)
        {
            __connection.commit();
            __connection.setAutoCommit(true);
        }
    }
</#if>

    private static final String DATABASE_NAME = "${name}";
<#list fields as field>
    private static final String ${field.name?upper_case}_TABLE_NAME = "${field.name}";
</#list>

    private final Connection __connection;
    private final boolean __isExternal;
    private final List<String> __attachedDbList;

<#list fields as field>
    private ${field.javaTypeName} <@field_member_name field/>;
</#list>
}
