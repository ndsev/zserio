package sql_databases.db_with_relocation;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zserio.runtime.SqlDatabase;
import zserio.runtime.validation.ValidationReport;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class DbWithRelocationTest
{
    @BeforeAll
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @BeforeEach
    public void setUp() throws IOException, SQLException
    {
        FileUtil.deleteFileIfExists(europeDbFile);
        europeDb = new EuropeDb(EUROPE_DB_FILE_NAME);
        europeDb.createSchema();

        FileUtil.deleteFileIfExists(americaDbFile);
        final Map<String, String> tableToDbFileNameRelocationMap = new HashMap<String, String>();
        tableToDbFileNameRelocationMap.put(RELOCATED_SLOVAKIA_TABLE_NAME, EUROPE_DB_FILE_NAME);
        tableToDbFileNameRelocationMap.put(RELOCATED_CZECHIA_TABLE_NAME, EUROPE_DB_FILE_NAME);
        americaDb = new AmericaDb(AMERICA_DB_FILE_NAME, tableToDbFileNameRelocationMap);
        americaDb.createSchema();
    }

    @AfterEach
    public void tearDown() throws SQLException
    {
        if (americaDb != null)
        {
            americaDb.close();
            americaDb = null;
        }

        if (europeDb != null)
        {
            europeDb.close();
            europeDb = null;
        }
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
        final List<CountryMapTableRow> writtenRows =
                createCountryMapTableRows(updateTileId, (short)'a', (short)'A');
        final CountryMapTable relocatedTable = americaDb.getSlovakia();
        relocatedTable.write(writtenRows);

        // update it
        final List<CountryMapTableRow> updatedRows =
                createCountryMapTableRows(updateTileId, (short)'b', (short)'B');
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
        final List<CountryMapTableRow> writtenRows =
                createCountryMapTableRows(updateTileId, (short)'c', (short)'C');
        final CountryMapTable relocatedTable = americaDb.getCzechia();
        relocatedTable.write(writtenRows);

        // update it
        final List<CountryMapTableRow> updatedRows =
                createCountryMapTableRows(updateTileId, (short)'d', (short)'D');
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
        try (final PreparedStatement statement = americaDb.connection().prepareStatement(sqlQuery);
                final ResultSet resultSet = statement.executeQuery();)
        {
            while (resultSet.next())
            {
                final String databaseName = resultSet.getString(2);
                assertFalse(resultSet.wasNull());
                assertTrue(attachedDatabaseNames.contains(databaseName));
                attachedDatabaseNames.remove(databaseName);
            }
        }

        assertEquals(1, attachedDatabaseNames.size());
        assertFalse(attachedDatabaseNames.contains("main"));
    }

    private static boolean isRelocatedTableInDb(String relocatedTableName, SqlDatabase db) throws SQLException
    {
        // check if database does contain relocated table
        final String sqlQuery =
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + relocatedTableName + "'";

        try (final PreparedStatement statement = db.connection().prepareStatement(sqlQuery);
                final ResultSet resultSet = statement.executeQuery();)
        {
            if (!resultSet.next())
                return false;

            // read table name
            final String tableName = resultSet.getString(1);
            if (resultSet.wasNull() || !tableName.equals(relocatedTableName))
                return false;
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

    private static void checkCountryMapTableRows(
            List<CountryMapTableRow> expectedRows, List<CountryMapTableRow> currentRows)
    {
        assertEquals(expectedRows.size(), currentRows.size());
        for (int i = 0; i < expectedRows.size(); ++i)
        {
            assertEquals(expectedRows.get(i).getTileId(), currentRows.get(i).getTileId());
            assertEquals(expectedRows.get(i).getTile(), currentRows.get(i).getTile());
        }
    }

    private static final String EUROPE_DB_FILE_NAME = "db_with_relocation_test_europe.sqlite";
    private static final String AMERICA_DB_FILE_NAME = "db_with_relocation_test_america.sqlite";

    private static final String RELOCATED_SLOVAKIA_TABLE_NAME = "slovakia";
    private static final String RELOCATED_CZECHIA_TABLE_NAME = "czechia";

    private static final int NUM_ALL_EUROPE_DB_TABLES = 1;
    private static final int NUM_ALL_AMERICA_DB_TABLES = 4;

    private static final Set<String> attachedDatabaseNames = new HashSet<String>(Arrays.asList(
            "main", "AmericaDb_" + RELOCATED_SLOVAKIA_TABLE_NAME, "AmericaDb_" + RELOCATED_CZECHIA_TABLE_NAME));

    private final File europeDbFile = new File(EUROPE_DB_FILE_NAME);
    private final File americaDbFile = new File(AMERICA_DB_FILE_NAME);

    private EuropeDb europeDb = null;
    private AmericaDb americaDb = null;
}
