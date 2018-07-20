package sql_databases;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import zserio.runtime.SqlDatabase;
import zserio.runtime.SqlDatabase.Mode;
import zserio.runtime.validation.ValidationReport;

public class MasterDatabaseTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws SQLException
    {
        FileUtil.deleteFileIfExists(dbFile);
    }

    @Test
    public void fileNameConstructor() throws SQLException, URISyntaxException
    {
        database = new MasterDatabase(dbFile.toString());
        database.createSchema();
        checkDb(database);
        database.close();
    }

    @Test
    public void connectionConstructor() throws SQLException
    {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty("flags", Mode.CREATE.toString());
        final String uriPath = "jdbc:sqlite:file:" + dbFile.toString();
        final Connection connection = DriverManager.getConnection(uriPath, connectionProps);

        database = new MasterDatabase(connection);
        database.createSchema();
        checkDb(database);
        connection.close();
    }

    @Test
    public void getDatabases() throws SQLException, URISyntaxException
    {
        database = new MasterDatabase(dbFile.toString());
        database.createSchema();

        final SqlDatabase[] databases = database.getDatabases();
        assertTrue(databases != null);
        assertEquals(NUM_ALL_DATABASES, databases.length);

        database.close();
    }

    @Test
    public void createSchema() throws SQLException, URISyntaxException
    {
        database = new MasterDatabase(dbFile.toString());

        for (String tableName : ALL_TABLE_NAMES)
            assertFalse(isTableInDb(tableName));
        database.createSchema();
        for (String tableName : ALL_TABLE_NAMES)
            assertTrue(isTableInDb(tableName));

        checkDb(database);
        database.close();
    }

    @Test
    public void createSchemaWithoutRowIdBlackList() throws SQLException, URISyntaxException
    {
        database = new MasterDatabase(dbFile.toString());

        for (String tableName : ALL_TABLE_NAMES)
            assertFalse(isTableInDb(tableName));
        final Set<String> withoutRowIdTableNamesBlackList = new HashSet<String>();
        database.createSchema(withoutRowIdTableNamesBlackList);
        for (String tableName : ALL_TABLE_NAMES)
            assertTrue(isTableInDb(tableName));

        checkDb(database);
        database.close();
    }

    @Test
    public void deleteSchema() throws SQLException, URISyntaxException
    {
        database = new MasterDatabase(dbFile.toString());
        database.createSchema();

        for (String tableName : ALL_TABLE_NAMES)
            assertTrue(isTableInDb(tableName));
        database.deleteSchema();
        for (String tableName : ALL_TABLE_NAMES)
            assertFalse(isTableInDb(tableName));

        database.close();
    }

    @Test
    public void validate() throws SQLException, URISyntaxException
    {
        database = new MasterDatabase(dbFile.toString());
        database.createSchema();
        checkDb(database);
        database.close();
    }

    private boolean isTableInDb(String checkTableName) throws SQLException
    {
        // check if database does contain table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                checkTableName + "'";

        final PreparedStatement statement = database.prepareStatement(sqlQuery);
        try
        {
            final ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return false;

            // read table name
            final String readTableName = resultSet.getString(1);
            if (resultSet.wasNull() || !readTableName.equals(checkTableName))
                return false;
        }
        finally
        {
            statement.close();
        }

        return true;
    }

    private static void checkDb(MasterDatabase database) throws SQLException
    {
        final ValidationReport report = database.validate(null);
        assertEquals(NUM_ALL_TABLES, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    private static final int NUM_ALL_DATABASES = 3;
    private static final int NUM_ALL_TABLES = 7;

    private static final String[] ALL_TABLE_NAMES = {"europe", "america", "usa", "canada", "slovakia",
        "czechia", "germany"};

    private static final String DB_FILE_NAME = "master_db_test.sqlite";

    private final File dbFile = new File(DB_FILE_NAME);
    private MasterDatabase database = null;
}
