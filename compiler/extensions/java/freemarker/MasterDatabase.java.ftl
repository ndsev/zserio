<#include "FileHeader.inc.ftl">
<@standard_header generatorDescription, rootPackageName, javaMajorVersion, [
        "java.util.HashMap",
        "java.util.Set",
        "java.net.URISyntaxException",
        "java.sql.Connection",
        "java.sql.SQLException"
]/>
<@imports ["zserio.runtime.SqlDatabase"]/>
<#if withWriterCode>
<@imports ["zserio.runtime.SqlDatabaseWriter"]/>
</#if>
<#if withValidationCode>
<@imports ["zserio.runtime.validation.ValidationReport"]/>
</#if>

<@class_header generatorDescription/>
public class MasterDatabase extends SqlDatabase<#if withWriterCode> implements SqlDatabaseWriter</#if><#rt>
        <#lt><#if withValidationCode>, SqlDatabaseValidator</#if>
{
    public MasterDatabase(String fileName) throws SQLException, URISyntaxException
    {
        super(fileName, Mode.<#if withWriterCode>CREATE<#else>READONLY</#if>, new HashMap<String, String>());
        initDatabases(getConnection());
    }

    public MasterDatabase(Connection externalConnection)
    {
        super(externalConnection, new HashMap<String, String>());
        initDatabases(externalConnection);
    }

    public SqlDatabase[] getDatabases()
    {
        return (SqlDatabase[]) databases.clone();
    }
<#if withWriterCode>

    @Override
    public void createSchema() throws SQLException
    {
        final boolean wasTransactionStarted = startTransaction();
        for (SqlDatabase db : databases)
            ((SqlDatabaseWriter)db).createSchema();

        endTransaction(wasTransactionStarted);
    }

    @Override
    public void createSchema(Set<String> withoutRowIdTableNamesBlackList) throws SQLException
    {
        final boolean wasTransactionStarted = startTransaction();
        for (SqlDatabase db : databases)
            ((SqlDatabaseWriter)db).createSchema(withoutRowIdTableNamesBlackList);

        endTransaction(wasTransactionStarted);
    }

    @Override
    public void deleteSchema() throws SQLException
    {
        final boolean wasTransactionStarted = startTransaction();
        for (SqlDatabase db : databases)
            ((SqlDatabaseWriter)db).deleteSchema();

        endTransaction(wasTransactionStarted);
    }
</#if>
<#if withValidationCode>

    @Override
    public ValidationReport validate(IParameterProvider parameterProvider) throws SQLException
    {
        final ValidationReport report = new ValidationReport();
        for (SqlDatabase db : databases)
            report.add(((SqlDatabaseValidator)db).validate(parameterProvider));

        return report;
    }
</#if>

    private void initDatabases(Connection connection)
    {
<#list databases as database>
        databases[${database_index}] = new ${database.typeName}(connection);
</#list>
    }

    private final SqlDatabase[] databases = new SqlDatabase[${databases?size}];
}
