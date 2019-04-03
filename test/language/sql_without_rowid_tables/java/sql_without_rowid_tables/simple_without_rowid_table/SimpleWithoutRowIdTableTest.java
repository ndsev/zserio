package sql_without_rowid_tables.simple_without_rowid_table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_without_rowid_tables.simple_without_rowid_table.SimpleWithoutRowIdDb;

public class SimpleWithoutRowIdTableTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws IOException, SQLException
    {
        FileUtil.deleteFileIfExists(file);
    }

    @After
    public void tearDown() throws SQLException
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    @Test
    public void checkRowIdColumn() throws SQLException
    {
        database = new SimpleWithoutRowIdDb(file.toString());
        database.createSchema();
        assertFalse(isColumnInTable("rowid", TABLE_NAME));
    }

    @Test
    public void createOrdinaryRowIdTable() throws SQLException
    {
        database = new SimpleWithoutRowIdDb(file.toString());
        final SimpleWithoutRowIdTable testTable = database.getSimpleWithoutRowIdTable();
        testTable.createOrdinaryRowIdTable();
        assertTrue(isColumnInTable("rowid", TABLE_NAME));
    }

    @Test
    public void checkWithoutRowIdTableNamesBlackList() throws SQLException
    {
        final Set<String> withoutRowIdTableNamesBlackList = new HashSet<String>();
        withoutRowIdTableNamesBlackList.add(TABLE_NAME);
        database = new SimpleWithoutRowIdDb(file.toString());
        database.createSchema(withoutRowIdTableNamesBlackList);
        assertTrue(isColumnInTable("rowid", TABLE_NAME));
    }

    private boolean isColumnInTable(String columnName, String tableName) throws SQLException
    {
        final String sqlQuery = "SELECT " + columnName + " FROM " + tableName + " LIMIT 0";

        // try select to check if column exists
        try
        {
            final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);
            statement.close();
            return true;
        }
        catch (SQLException exception)
        {
            return false;
        }
    }

    private static final String TABLE_NAME = "simpleWithoutRowIdTable";

    private static final String FILE_NAME = "simple_without_rowid_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private SimpleWithoutRowIdDb database = null;
}
