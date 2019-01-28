package sql_databases.simple_db;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import zserio.runtime.SqlDatabase.Mode;
import zserio.runtime.validation.ValidationReport;

public class SimpleDbTest
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
        database = new WorldDb(dbFile.toString());
        database.createSchema();
        checkDb(database);
        database.close();
    }

    @Test
    public void fileNameConstructorTableRelocationMap() throws SQLException, URISyntaxException
    {
        final Map<String, String> tableToDbFileNameRelocationMap = new HashMap<String, String>();
        database = new WorldDb(dbFile.toString(), tableToDbFileNameRelocationMap);
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

        database = new WorldDb(connection);
        database.createSchema();
        checkDb(database);
        connection.close();
    }

    @Test
    public void connectionConstructorTableRelocationMap() throws SQLException
    {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty("flags", Mode.CREATE.toString());
        final String uriPath = "jdbc:sqlite:file:" + dbFile.toString();
        final Connection connection = DriverManager.getConnection(uriPath, connectionProps);

        final Map<String, String> tableToDbFileNameRelocationMap = new HashMap<String, String>();
        database = new WorldDb(connection, tableToDbFileNameRelocationMap);
        database.createSchema();
        checkDb(database);
        connection.close();
    }

    @Test
    public void tableGetters() throws SQLException, URISyntaxException
    {
        database = new WorldDb(dbFile.toString());
        database.createSchema();

        assertTrue(isTableInDb(EUROPE_TABLE_NAME));
        final GeoMapTable europeTable = database.getEurope();
        assertTrue(europeTable != null);

        assertTrue(isTableInDb(AMERICA_TABLE_NAME));
        final GeoMapTable americaTable = database.getAmerica();
        assertTrue(americaTable != null);

        database.close();
    }

    @Test
    public void createSchema() throws SQLException, URISyntaxException
    {
        database = new WorldDb(dbFile.toString());

        assertFalse(isTableInDb(EUROPE_TABLE_NAME));
        assertFalse(isTableInDb(AMERICA_TABLE_NAME));
        database.createSchema();
        assertTrue(isTableInDb(EUROPE_TABLE_NAME));
        assertTrue(isTableInDb(AMERICA_TABLE_NAME));

        checkDb(database);
        database.close();
    }

    @Test
    public void createSchemaWithoutRowIdBlackList() throws SQLException, URISyntaxException
    {
        database = new WorldDb(dbFile.toString());

        assertFalse(isTableInDb(EUROPE_TABLE_NAME));
        assertFalse(isTableInDb(AMERICA_TABLE_NAME));
        final Set<String> withoutRowIdTableNamesBlackList = new HashSet<String>();
        database.createSchema(withoutRowIdTableNamesBlackList);
        assertTrue(isTableInDb(EUROPE_TABLE_NAME));
        assertTrue(isTableInDb(AMERICA_TABLE_NAME));

        checkDb(database);
        database.close();
    }

    @Test
    public void deleteSchema() throws SQLException, URISyntaxException
    {
        database = new WorldDb(dbFile.toString());
        database.createSchema();

        assertTrue(isTableInDb(EUROPE_TABLE_NAME));
        assertTrue(isTableInDb(AMERICA_TABLE_NAME));
        database.deleteSchema();
        assertFalse(isTableInDb(EUROPE_TABLE_NAME));
        assertFalse(isTableInDb(AMERICA_TABLE_NAME));

        database.close();
    }

    @Test
    public void validate() throws SQLException, URISyntaxException
    {
        database = new WorldDb(dbFile.toString());
        database.createSchema();
        checkDb(database);
        database.close();
    }

    @Test
    public void getDatabaseName()
    {
        assertEquals(WORLD_DB_NAME, WorldDb.getDatabaseName());
    }

    @Test
    public void getTableNames()
    {
        final String[] tableNames = WorldDb.getTableNames();
        assertEquals(SimpleDbTest.tableNames.length, tableNames.length);
        for (int i = 0; i < tableNames.length; ++i)
            assertEquals(SimpleDbTest.tableNames[i], tableNames[i]);
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

    private static void checkDb(WorldDb database) throws SQLException
    {
        final ValidationReport report = database.validate();
        assertEquals(NUM_ALL_TABLES, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    private static final String WORLD_DB_NAME = "WorldDb";
    private static final String EUROPE_TABLE_NAME = "europe";
    private static final String AMERICA_TABLE_NAME = "america";
    private static final String tableNames[] = new String[] {EUROPE_TABLE_NAME, AMERICA_TABLE_NAME};

    private static int NUM_ALL_TABLES = tableNames.length;

    private static final String DB_FILE_NAME = "simple_db_test.sqlite";

    private final File dbFile = new File(DB_FILE_NAME);
    private WorldDb database = null;
}
