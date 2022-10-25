<#include "FileHeader.inc.ftl">
<#include "Sql.inc.ftl">
<#include "TypeInfo.inc.ftl">
<#include "DocComment.inc.ftl">
<@standard_header generatorDescription, packageName/>
<#if withValidationCode>
    <#assign needsParameterProvider = sql_db_needs_parameter_provider(fields)/>
</#if>

<#if withCodeComments && docComments??>
<@doc_comments docComments/>
</#if>
public class ${name} implements zserio.runtime.SqlDatabase<#if !withWriterCode>Reader</#if>
{
<#if withValidationCode && needsParameterProvider>
    <#if withCodeComments>
    /** Interface for class which provides all explicit parameter providers of all tables in the database. */
    </#if>
    public static interface ParameterProvider
    {
    <#assign isTheFirst=true>
    <#list fields as field>
        <#if field.hasExplicitParameters>
            <#if withCodeComments>
                <#if !isTheFirst>

                <#else>
                    <#assign isTheFirst=false>
                </#if>
        /**
         * Gets the explicit parameter provider for the table ${field.name}.
         *
         * @return The explicit parameter provider for the table ${field.name}.
         */
            </#if>
        ${field.typeInfo.typeFullName}.ParameterProvider get${field.name?cap_first}ParameterProvider();
        </#if>
    </#list>
    };

</#if>
<#if withCodeComments>
    /**
     * Constructor from database file name.
     *
     * The method opens the database of given file name.
     *
     * @param fileName Database file name to use.
     *
     * @throws SQLException If database opening fails.
     */
</#if>
    public ${name}(java.lang.String fileName) throws java.sql.SQLException
    {
        this(fileName, new java.util.HashMap<java.lang.String, java.lang.String>());
    }

<#if withCodeComments>
    /**
     * Constructor from database file name and table relocation map.
     *
     * The method opens the database of given file name and for each relocated table attaches database to
     * which relocated table should be mapped.
     *
     * @param fileName Database file name to use.
     * @param tableToDbFileNameRelocationMap Mapping of relocated table name to database file name.
     *
     * @throws SQLException If database opening fails.
     */
</#if>
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

<#if withCodeComments>
    /**
     * Constructor from external connection.
     *
     * The method uses given external connection if database is already open.
     *
     * @param externalConnection Database connection of already open database.
     */
</#if>
    public ${name}(java.sql.Connection externalConnection)
    {
        this(externalConnection, new java.util.HashMap<java.lang.String, java.lang.String>());
    }

<#if withCodeComments>
    /**
     * Constructor from external connection and table relocation map.
     *
     * The method uses given external connection if database is already open and for each relocated table uses
     * provided already attached database names.
     *
     * @param externalConnection Database connection of already open database.
     * @param tableToAttachedDbNameRelocationMap Mapping of relocated table name to attached database file name.
     */
</#if>
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
            try
            {
                detachDatabases();
            }
            finally
            {
                connection.close();
            }
        }
    }

    @Override
    public java.sql.Connection connection()
    {
        return connection;
    }
<#if withTypeInfoCode>

    <#if withCodeComments>
    /**
     * Gets static information about the database type useful for generic introspection.
     *
     * @return Zserio type information.
     */
    </#if>
    public static zserio.runtime.typeinfo.TypeInfo typeInfo()
    {
        final java.util.List<zserio.runtime.typeinfo.TableInfo> tableList =
                <@tables_info fields/>

        return new zserio.runtime.typeinfo.TypeInfo.SqlDatabaseTypeInfo(
            "${schemaTypeName}", ${name}.class, tableList
        );
    }
</#if>
<#list fields as field>

    <#if withCodeComments>
    /**
     * Gets the table ${field.name}.
        <#if field.docComments??>
     * <p>
     * <b>Description:</b>
     * <br>
     <@doc_comments_inner field.docComments, 1/>
     *
        <#else>
     *
        </#if>
     * @return Reference to the table ${field.name}.
     */
    </#if>
    public ${field.typeInfo.typeFullName} ${field.getterName}()
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

    <#if withCodeComments>
    /**
     * Validates all tables in the database.
     *
        <#if needsParameterProvider>
     * @param parameterProvider Provider of explicit parameters for all tables.
     *
        </#if>
     * @return Validation report which contains validation result.
     *
     * @throws SQLException If any access to database fails.
     */
    </#if>
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

<#if withCodeComments>
    /**
     * Gets the database name.
     *
     * @return Database name.
     */
</#if>
    public static java.lang.String databaseName()
    {
        return DATABASE_NAME;
    }

<#if withCodeComments>
    /**
     * Gets all table names of the database.
     *
     * @return Array of all table names of the database.
     */
</#if>
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
        this.<@sql_field_member_name field/> = new ${field.typeInfo.typeFullName}(connection,
                tableToAttachedDbNameRelocationMap.get(${field.name}_TABLE_NAME),
                ${field.name}_TABLE_NAME);
</#list>
    }

    private void executeUpdate(java.lang.String sql) throws java.sql.SQLException
    {
        try (final java.sql.Statement statement = connection.createStatement())
        {
            statement.executeUpdate(sql);
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
    private ${field.typeInfo.typeFullName} <@sql_field_member_name field/>;
</#list>
}
