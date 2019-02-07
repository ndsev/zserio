package sql_tables.subtyped_table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_tables.TestDb;

import zserio.runtime.array.ObjectArray;

public class SubtypedTableTest
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
        database = new TestDb(file.toString());
        database.createSchema();
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
    public void testSubtypedTable() throws SQLException
    {
        assertTrue(isTableInDb());

        final TestTable subtypedTable = database.getSubtypedTable();
        assertTrue(subtypedTable != null);
    }

    private boolean isTableInDb() throws SQLException
    {
        // check if database does contain table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME +
                "'";

        final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);
        try
        {
            final ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return false;

            // read table name
            final String tableName = resultSet.getString(1);
            if (resultSet.wasNull() || !tableName.equals(TABLE_NAME))
                return false;
        }
        finally
        {
            statement.close();
        }

        return true;
    }

    private static final String TABLE_NAME = "subtypedTable";

    private static final String FILE_NAME = "subtyped_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
