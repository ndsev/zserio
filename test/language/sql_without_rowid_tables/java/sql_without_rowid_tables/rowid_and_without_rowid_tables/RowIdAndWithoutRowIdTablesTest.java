package sql_without_rowid_tables.rowid_and_without_rowid_tables;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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

import sql_without_rowid_tables.rowid_and_without_rowid_tables.RowIdAndWithoutRowIdDb;

import zserio.runtime.SqlDatabase.Mode;

public class RowIdAndWithoutRowIdTablesTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws IOException, URISyntaxException, SQLException
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
    public void checkRowIdColumn() throws SQLException, URISyntaxException
    {
        database = new RowIdAndWithoutRowIdDb(file.toString());
        database.createSchema();
        assertFalse(isColumnInTable("rowid", WITHOUT_ROWID_TABLE_NAME));
        assertTrue(isColumnInTable("rowid", ORDINARY_ROWID_TABLE_NAME));
    }

    @Test
    public void createOrdinaryRowIdTable() throws SQLException, URISyntaxException
    {
        database = new RowIdAndWithoutRowIdDb(file.toString());
        final WithoutRowIdTable testTable = database.getWithoutRowIdTable();
        testTable.createOrdinaryRowIdTable();
        assertTrue(isColumnInTable("rowid", WITHOUT_ROWID_TABLE_NAME));
    }

    @Test
    public void checkWithoutRowIdTableNamesBlackList() throws SQLException, URISyntaxException
    {
        final Set<String> withoutRowIdTableNamesBlackList = new HashSet<String>();
        withoutRowIdTableNamesBlackList.add(WITHOUT_ROWID_TABLE_NAME);
        database = new RowIdAndWithoutRowIdDb(file.toString());
        database.createSchema(withoutRowIdTableNamesBlackList);
        assertTrue(isColumnInTable("rowid", WITHOUT_ROWID_TABLE_NAME));
        assertTrue(isColumnInTable("rowid", ORDINARY_ROWID_TABLE_NAME));
    }

    private boolean isColumnInTable(String columnName, String tableName) throws SQLException
    {
        final String sqlQuery = "SELECT " + columnName + " FROM " + tableName + " LIMIT 0";

        // try select to check if column exists
        try
        {
            final PreparedStatement statement = database.prepareStatement(sqlQuery);
            statement.close();
            return true;
        }
        catch (SQLException exception)
        {
            return false;
        }
    }

    private static final String WITHOUT_ROWID_TABLE_NAME = "withoutRowIdTable";
    private static final String ORDINARY_ROWID_TABLE_NAME = "ordinaryRowIdTable";

    private static final String FILE_NAME = "rowid_and_without_rowid_tables_test.sqlite";

    private final File file = new File(FILE_NAME);
    private RowIdAndWithoutRowIdDb database = null;
}
