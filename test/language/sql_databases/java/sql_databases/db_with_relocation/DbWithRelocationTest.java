package sql_databases.db_with_relocation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import zserio.runtime.SqlDatabase;
import zserio.runtime.SqlDatabase.Mode;
import zserio.runtime.validation.ValidationReport;

public class DbWithRelocationTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws IOException, URISyntaxException, SQLException
    {
        europeDb = new EuropeDb(EUROPE_DB_FILE_URI_NAME);
        europeDb.createSchema();

        final Map<String, String> tableToDbFileNameRelocationMap = new HashMap<String, String>();
        tableToDbFileNameRelocationMap.put(RELOCATED_SLOVAKIA_TABLE_NAME, EUROPE_DB_FILE_URI_NAME);
        tableToDbFileNameRelocationMap.put(RELOCATED_CZECHIA_TABLE_NAME, EUROPE_DB_FILE_URI_NAME);
        americaDb = new AmericaDb(AMERICA_DB_FILE_NAME, tableToDbFileNameRelocationMap);
        americaDb.createSchema();
    }

    @After
    public void tearDown() throws SQLException
    {
        closeDb(americaDb);
        FileUtil.deleteFileIfExists(americaDbFile);

        closeDb(europeDb);
        FileUtil.deleteFileIfExists(europeDbFile);
    }

    @Test
    public void tableGetters()
    {
        final CountryMapTable germanyTable = europeDb.getGermany();
        assertTrue(germanyTable != null);

        final CountryMapTable usaTable = americaDb.getUsa();
        assertTrue(usaTable != null);

        final CountryMapTable canadaTable = americaDb.getCanada();
        assertTrue(canadaTable != null);

        final CountryMapTable slovakiaTable = americaDb.getSlovakia();
        assertTrue(slovakiaTable != null);

        final CountryMapTable czechiaTable = americaDb.getCzechia();
        assertTrue(czechiaTable != null);
    }

    @Test
    public void validate() throws SQLException
    {
        ValidationReport report = europeDb.validate();
        assertEquals(NUM_ALL_EUROPE_DB_TABLES, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());

        report = americaDb.validate();
        assertEquals(NUM_ALL_AMERICA_DB_TABLES, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    @Test
    public void checkRelocatedSlovakiaTable() throws SQLException, IOException
    {
        // check that americaDb does not contain relocated table
        assertFalse(isRelocatedTableInDb(RELOCATED_SLOVAKIA_TABLE_NAME, americaDb));

        // check that europeDb does contain relocated table
        assertTrue(isRelocatedTableInDb(RELOCATED_SLOVAKIA_TABLE_NAME, europeDb));

        // write to relocated table
        final int updateTileId = 1;
        final List<CountryMapTableRow> writtenRows = createCountryMapTableRows(updateTileId, (short)'a',
                (short)'A');
        final CountryMapTable relocatedTable = americaDb.getSlovakia();
        relocatedTable.write(writtenRows);

        // update it
        final List<CountryMapTableRow> updatedRows = createCountryMapTableRows(updateTileId, (short)'b',
                (short)'B');
        final String updateCondition = "tileId=" + updateTileId;
        relocatedTable.update(updatedRows.get(0), updateCondition);

        // read it back
        final List<CountryMapTableRow> readRows = relocatedTable.read();
        checkCountryMapTableRows(updatedRows, readRows);

        // validate
        final ValidationReport report = relocatedTable.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(1, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    @Test
    public void checkRelocatedCzechiaTable() throws SQLException, IOException
    {
        // check that americaDb does not contain relocated table
        assertFalse(isRelocatedTableInDb(RELOCATED_CZECHIA_TABLE_NAME, americaDb));

        // check that europeDb does contain relocated table
        assertTrue(isRelocatedTableInDb(RELOCATED_CZECHIA_TABLE_NAME, europeDb));

        // write to relocated table
        final int updateTileId = 1;
        final List<CountryMapTableRow> writtenRows = createCountryMapTableRows(updateTileId, (short)'c',
                (short)'C');
        final CountryMapTable relocatedTable = americaDb.getCzechia();
        relocatedTable.write(writtenRows);

        // update it
        final List<CountryMapTableRow> updatedRows = createCountryMapTableRows(updateTileId, (short)'d',
                (short)'D');
        final String updateCondition = "tileId=" + updateTileId;
        relocatedTable.update(updatedRows.get(0), updateCondition);

        // read it back
        final List<CountryMapTableRow> readRows = relocatedTable.read();
        checkCountryMapTableRows(updatedRows, readRows);

        // validate
        final ValidationReport report = relocatedTable.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(1, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    @Test
    public void checkAttachedDatabases() throws SQLException
    {
        final String sqlQuery = "PRAGMA database_list";
        final PreparedStatement statement = americaDb.prepareStatement(sqlQuery);
        try
        {
            final ResultSet resultSet = statement.executeQuery();
            for (int i = 0; i < attachedDatabaseNames.length; ++i)
            {
                assertTrue(resultSet.next());

                final String databaseName = resultSet.getString(2);
                assertFalse(resultSet.wasNull());
                assertEquals(attachedDatabaseNames[i], databaseName);
            }

            assertFalse(resultSet.next());
        }
        finally
        {
            statement.close();
        }
    }

    private static void closeDb(SqlDatabase database) throws SQLException
    {
        if (database != null)
            database.close();
    }

    private static boolean isRelocatedTableInDb(String relocatedTableName, SqlDatabase db) throws SQLException
    {
        // check if database does contain relocated table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                relocatedTableName + "'";

        final PreparedStatement statement = db.prepareStatement(sqlQuery);
        try
        {
            final ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return false;

            // read table name
            final String tableName = resultSet.getString(1);
            if (resultSet.wasNull() || !tableName.equals(relocatedTableName))
                return false;
        }
        finally
        {
            statement.close();
        }

        return true;
    }

    private static List<CountryMapTableRow> createCountryMapTableRows(int tileId, short version, short data)
    {
        final CountryMapTableRow row = new CountryMapTableRow();
        row.setTileId(tileId);
        row.setTile(new Tile(version, data));
        final List<CountryMapTableRow> rows = new ArrayList<CountryMapTableRow>();
        rows.add(row);

        return rows;
    }

    private static void checkCountryMapTableRows(List<CountryMapTableRow> expectedRows,
            List<CountryMapTableRow> currentRows)
    {
        assertEquals(expectedRows.size(), currentRows.size());
        for (int i = 0; i < expectedRows.size(); ++i)
        {
            assertEquals(expectedRows.get(i).getTileId(), currentRows.get(i).getTileId());
            assertEquals(expectedRows.get(i).getTile(), currentRows.get(i).getTile());
        }
    }

    private static final String EUROPE_DB_FILE_NAME = "db_with_relocation_test_europe.sqlite";
    private static final String EUROPE_DB_FILE_URI_PARAMS = "?zv=zlib&password=my16BytePassword";
    private static final String EUROPE_DB_FILE_URI_NAME = EUROPE_DB_FILE_NAME + EUROPE_DB_FILE_URI_PARAMS;
    private static final String AMERICA_DB_FILE_NAME = "db_with_relocation_test_america.sqlite";

    private static final String RELOCATED_SLOVAKIA_TABLE_NAME = "slovakia";
    private static final String RELOCATED_CZECHIA_TABLE_NAME = "czechia";

    private static final int NUM_ALL_EUROPE_DB_TABLES = 1;
    private static final int NUM_ALL_AMERICA_DB_TABLES = 4;

    private static final String attachedDatabaseNames[] = new String[]
            { "main", "AmericaDb_" + RELOCATED_SLOVAKIA_TABLE_NAME };

    private final File  europeDbFile = new File(EUROPE_DB_FILE_NAME);
    private final File  americaDbFile = new File(AMERICA_DB_FILE_NAME);

    private EuropeDb    europeDb = null;
    private AmericaDb   americaDb = null;
}
