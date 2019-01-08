package sql_tables.subtyped_table;

import static org.junit.Assert.*;

import java.io.File;

import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public void setUp() throws SQLException
    {
        FileUtil.deleteFileIfExists(dbFile);
    }

    @Test
    public void testSubtypedTable() throws SQLException, URISyntaxException
    {
        TestDb db = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try
        {
            db = new TestDb(dbFile.toString());
            db.createSchema();
            // check if database does contain the table
            final String sqlQuery =
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='subtypedTable'";
            statement = db.prepareStatement(sqlQuery);
            resultSet = statement.executeQuery();
            assertTrue(resultSet.next());
            final String tableName = resultSet.getString(1);
            assertEquals("subtypedTable", tableName);

            // check table getter
            final TestTable subtypedTable = db.getSubtypedTable();
            assertTrue(subtypedTable != null);
        }
        catch (RuntimeException e)
        {
            fail("RuntimeException thrown: " + e.getMessage());
        }
        finally
        {
            try
            {
                if (resultSet != null)
                    resultSet.close();
            }
            finally
            {
                try
                {
                    if (statement != null)
                        statement.close();
                }
                finally
                {
                    if (db != null)
                        db.close();
                }
            }
        }
    }

    private static final String DB_FILE_NAME = "subtyped_table_test.sqlite";

    private final File dbFile = new File(DB_FILE_NAME);
}
