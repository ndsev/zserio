package sql_databases.simple_db;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zserio.runtime.validation.ValidationReport;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class SimpleDbTest
{
    @BeforeAll
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @BeforeEach
    public void setUp() throws SQLException
    {
        FileUtil.deleteFileIfExists(dbFile);
    }

    @Test
    public void fileNameConstructor() throws SQLException
    {
        try (final WorldDb database = new WorldDb(dbFile.toString()))
        {
            database.createSchema();
            checkDb(database);
        }
    }

    @Test
    public void fileNameConstructorTableRelocationMap() throws SQLException
    {
        final Map<String, String> tableToDbFileNameRelocationMap = new HashMap<String, String>();
        try (final WorldDb database = new WorldDb(dbFile.toString(), tableToDbFileNameRelocationMap))
        {
            database.createSchema();
            checkDb(database);
        }
    }

    @Test
    public void connectionConstructor() throws SQLException
    {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty("flags", "CREATE");
        final String uriPath = "jdbc:sqlite:file:" + dbFile.toString();

        try (final Connection connection = DriverManager.getConnection(uriPath, connectionProps);
                final WorldDb database = new WorldDb(connection);)
        {
            database.createSchema();
            checkDb(database);
        }
    }

    @Test
    public void connectionConstructorTableRelocationMap() throws SQLException
    {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty("flags", "CREATE");
        final String uriPath = "jdbc:sqlite:file:" + dbFile.toString();
        final Map<String, String> tableToDbFileNameRelocationMap = new HashMap<String, String>();

        try (final Connection connection = DriverManager.getConnection(uriPath, connectionProps);
                final WorldDb database = new WorldDb(connection, tableToDbFileNameRelocationMap);)
        {
            database.createSchema();
            checkDb(database);
        }
    }

    @Test
    public void tableGetters() throws SQLException
    {
        try (final WorldDb database = new WorldDb(dbFile.toString()))
        {
            database.createSchema();

            assertTrue(isTableInDb(database, EUROPE_TABLE_NAME));
            final GeoMapTable europeTable = database.getEurope();
            assertTrue(europeTable != null);

            assertTrue(isTableInDb(database, AMERICA_TABLE_NAME));
            final GeoMapTable americaTable = database.getAmerica();
            assertTrue(americaTable != null);
        }
    }

    @Test
    public void createSchema() throws SQLException
    {
        try (final WorldDb database = new WorldDb(dbFile.toString()))
        {
            assertFalse(isTableInDb(database, EUROPE_TABLE_NAME));
            assertFalse(isTableInDb(database, AMERICA_TABLE_NAME));
            database.createSchema();
            assertTrue(isTableInDb(database, EUROPE_TABLE_NAME));
            assertTrue(isTableInDb(database, AMERICA_TABLE_NAME));

            checkDb(database);
        }
    }

    @Test
    public void createSchemaWithoutRowIdBlackList() throws SQLException
    {
        try (final WorldDb database = new WorldDb(dbFile.toString()))
        {
            assertFalse(isTableInDb(database, EUROPE_TABLE_NAME));
            assertFalse(isTableInDb(database, AMERICA_TABLE_NAME));
            final Set<String> withoutRowIdTableNamesBlackList = new HashSet<String>();
            database.createSchema(withoutRowIdTableNamesBlackList);
            assertTrue(isTableInDb(database, EUROPE_TABLE_NAME));
            assertTrue(isTableInDb(database, AMERICA_TABLE_NAME));

            checkDb(database);
        }
    }

    @Test
    public void deleteSchema() throws SQLException
    {
        try (final WorldDb database = new WorldDb(dbFile.toString()))
        {
            database.createSchema();

            assertTrue(isTableInDb(database, EUROPE_TABLE_NAME));
            assertTrue(isTableInDb(database, AMERICA_TABLE_NAME));
            database.deleteSchema();
            assertFalse(isTableInDb(database, EUROPE_TABLE_NAME));
            assertFalse(isTableInDb(database, AMERICA_TABLE_NAME));
        }
    }

    @Test
    public void validate() throws SQLException
    {
        try (final WorldDb database = new WorldDb(dbFile.toString()))
        {
            database.createSchema();
            checkDb(database);
        }
    }

    @Test
    public void getDatabaseName()
    {
        assertEquals(WORLD_DB_NAME, WorldDb.databaseName());
    }

    @Test
    public void getTableNames()
    {
        final String[] tableNames = WorldDb.tableNames();
        assertEquals(SimpleDbTest.tableNames.length, tableNames.length);
        for (int i = 0; i < tableNames.length; ++i)
            assertEquals(SimpleDbTest.tableNames[i], tableNames[i]);
    }

    private boolean isTableInDb(WorldDb database, String checkTableName) throws SQLException
    {
        // check if database does contain table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

        try (final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);)
        {
            statement.setString(1, checkTableName);
            try (final ResultSet resultSet = statement.executeQuery();)
            {
                if (!resultSet.next())
                    return false;

                // read table name
                final String readTableName = resultSet.getString(1);
                if (resultSet.wasNull() || !readTableName.equals(checkTableName))
                    return false;
            }
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
}
