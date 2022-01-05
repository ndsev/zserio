package zserio.runtime.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import zserio.runtime.SqlDatabaseReader;

public class ValidationSqliteUtilTest
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

        TestSqlDatabase testDatabase = new TestSqlDatabase();
        try
        {
            testDatabase.executeUpdate("CREATE TABLE " + TABLE_NAME +
                    "(col1 TEXT, col2 INTEGER NOT NULL PRIMARY KEY, col3 BLOB);");
            final Map<String, ValidationSqliteUtil.ColumnDescription> schema =
                    ValidationSqliteUtil.getTableSchema(testDatabase.connection(), null, TABLE_NAME);

            final ValidationSqliteUtil.ColumnDescription col1 = schema.remove("col1");
            assertEquals("col1", col1.getName());
            assertEquals("TEXT", col1.getType());
            assertEquals(false, col1.isNotNull());
            assertEquals(false, col1.isPrimaryKey());

            final ValidationSqliteUtil.ColumnDescription col2 = schema.remove("col2");
            assertEquals("col2", col2.getName());
            assertEquals("INTEGER", col2.getType());
            assertEquals(true, col2.isNotNull());
            assertEquals(true, col2.isPrimaryKey());

            final ValidationSqliteUtil.ColumnDescription col3 = schema.remove("col3");
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

        TestSqlDatabase testDatabase = new TestSqlDatabase();
        try
        {
            testDatabase.executeUpdate("CREATE VIRTUAL TABLE " + TABLE_NAME + " USING fts4 " +
                    "(substitutionId TEXT NOT NULL);");
            assertTrue(ValidationSqliteUtil.isHiddenColumnInTable(testDatabase.connection(), null, TABLE_NAME,
                    "docId"));
            assertFalse(ValidationSqliteUtil.isHiddenColumnInTable(testDatabase.connection(), null, TABLE_NAME,
                    "languageCode"));
        }
        finally
        {
            testDatabase.close();
        }
    }

    private static class TestSqlDatabase implements SqlDatabaseReader
    {
        public TestSqlDatabase() throws SQLException
        {
            final String uriPath = "jdbc:sqlite::memory:";
            final Properties connectionProps = new Properties();
            connectionProps.setProperty("flags", "CREATE");

            connection = DriverManager.getConnection(uriPath, connectionProps);
        }

        @Override
        public void close() throws SQLException
        {
            connection.close();
        }

        @Override
        public Connection connection()
        {
            return connection;
        }

        public void executeUpdate(String sql) throws SQLException
        {
            final Statement statement = connection.createStatement();
            try
            {
                statement.executeUpdate(sql);
            }
            finally
            {
                statement.close();
            }
        }

        private final Connection connection;
    }
}
