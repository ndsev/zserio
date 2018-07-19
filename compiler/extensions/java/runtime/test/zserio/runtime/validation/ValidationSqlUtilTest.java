package zserio.runtime.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;

import zserio.runtime.SqlDatabase;

public class ValidationSqlUtilTest
{
    static
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException excpt)
        {
            throw new RuntimeException("Can't register SQLite JDBC driver!");
        }
    }

    @Test
    public void getColumnTypes() throws SQLException
    {
        final String TABLE_NAME = "ColumnTypesTestTable";

        SqlDatabase testDatabase = new TestSqlDatabase();
        try
        {
            testDatabase.executeUpdate("CREATE TABLE " + TABLE_NAME +
                    "(col1 TEXT, col2 INTEGER NOT NULL PRIMARY KEY, col3 BLOB);");
            final Map<String, ValidationSqlUtil.ColumnDescription> schema =
                    ValidationSqlUtil.getTableSchema(testDatabase, null, TABLE_NAME);

            final ValidationSqlUtil.ColumnDescription col1 = schema.remove("col1");
            assertEquals("col1", col1.getName());
            assertEquals("TEXT", col1.getType());
            assertEquals(false, col1.isNotNull());
            assertEquals(false, col1.isPrimaryKey());

            final ValidationSqlUtil.ColumnDescription col2 = schema.remove("col2");
            assertEquals("col2", col2.getName());
            assertEquals("INTEGER", col2.getType());
            assertEquals(true, col2.isNotNull());
            assertEquals(true, col2.isPrimaryKey());

            final ValidationSqlUtil.ColumnDescription col3 = schema.remove("col3");
            assertEquals("col3", col3.getName());
            assertEquals("BLOB", col3.getType());
            assertEquals(false, col3.isNotNull());
            assertEquals(false, col3.isPrimaryKey());

            assertTrue(schema.isEmpty());
        }
        finally
        {
            testDatabase.close();
        }
    }

    @Test
    public void isHiddenColumnInTable() throws SQLException
    {
        final String TABLE_NAME = "HiddenColumnTestTable";

        SqlDatabase testDatabase = new TestSqlDatabase();
        try
        {
            testDatabase.executeUpdate("CREATE VIRTUAL TABLE " + TABLE_NAME + " USING fts4 " +
                    "(substitutionId TEXT NOT NULL);");
            assertTrue(ValidationSqlUtil.isHiddenColumnInTable(testDatabase, null, TABLE_NAME, "docId"));
            assertFalse(ValidationSqlUtil.isHiddenColumnInTable(testDatabase, null, TABLE_NAME,
                    "languageCode"));
        }
        finally
        {
            testDatabase.close();
        }
    }

    private static class TestSqlDatabase extends SqlDatabase
    {
        public TestSqlDatabase() throws SQLException
        {
            super(createMemoryDb(), new HashMap<String, String>());
        }

        private static Connection createMemoryDb() throws SQLException
        {
            final String uriPath = "jdbc:sqlite::memory:";
            final Properties connectionProps = new Properties();
            connectionProps.setProperty("flags", "CREATE");

            return DriverManager.getConnection(uriPath, connectionProps);
        }
    }
}
