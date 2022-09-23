package zserio.runtime.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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

        try (final TestSqlDatabase testDatabase = new TestSqlDatabase())
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
    }

    @Test
    public void isHiddenColumnInTable() throws SQLException
    {
        final String TABLE_NAME = "HiddenColumnTestTable";

        try (final TestSqlDatabase testDatabase = new TestSqlDatabase())
        {
            testDatabase.executeUpdate("CREATE VIRTUAL TABLE " + TABLE_NAME + " USING fts4 " +
                    "(substitutionId TEXT NOT NULL);");
            assertTrue(ValidationSqliteUtil.isHiddenColumnInTable(testDatabase.connection(), null, TABLE_NAME,
                    "docId"));
            assertFalse(ValidationSqliteUtil.isHiddenColumnInTable(testDatabase.connection(), null, TABLE_NAME,
                    "languageCode"));
        }
    }

    @Test
    public void sqlTypeToSqliteType() throws SQLException
    {
        final String tableName = "sqlTypeToSqliteTypeTable";
        try (final TestSqlDatabase testDatabase = new TestSqlDatabase())
        {
            testDatabase.executeUpdate("CREATE TABLE " + tableName + "(id INTEGER PRIMARY KEY, " +
                    "integerCol INTEGER, realCol REAL, textCol TEXT, blobCol BLOB)");

            testDatabase.executeUpdate("INSERT INTO " + tableName + " VALUES (0, NULL, NULL, NULL, NULL)");
            testDatabase.executeUpdate("INSERT INTO " + tableName + " VALUES (1, 13, 1.3, 'STRING', x'00')");
            testDatabase.executeUpdate("INSERT INTO " + tableName + " VALUES (2, 1.3, 'STRING', x'00', 13)");
            try (
                final Statement stmt = testDatabase.connection().createStatement();
                final ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + tableName);
            )
            {
                // first row checks NULL values
                // note that different versions of Xerial JDBC returns different types
                assertTrue(resultSet.next());
                ResultSetMetaData metaData = resultSet.getMetaData();
                assertEquals(Types.INTEGER,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(1)));
                assertThat(ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(2)),
                        anyOf(is(Types.INTEGER), is(Types.NULL)));
                assertThat(ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(3)),
                        anyOf(is(Types.REAL), is(Types.NULL)));
                assertThat(ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(4)),
                        anyOf(is(Types.VARCHAR), is(Types.NULL)));
                assertThat(ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(5)),
                        anyOf(is(Types.BLOB), is(Types.NULL)));

                // second row checks correct values
                assertTrue(resultSet.next());
                metaData = resultSet.getMetaData();
                assertEquals(Types.INTEGER,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(1)));
                assertEquals(Types.INTEGER,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(2)));
                assertEquals(Types.REAL,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(3)));
                assertEquals(Types.VARCHAR,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(4)));
                assertEquals(Types.BLOB,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(5)));

                // third row checks types mismatch - i.e. checks dynamic typing in SQLite
                assertTrue(resultSet.next());
                metaData = resultSet.getMetaData();
                assertEquals(Types.INTEGER,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(1)));
                assertEquals(Types.REAL,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(2)));
                assertEquals(Types.VARCHAR,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(3)));
                assertEquals(Types.BLOB,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(4)));
                assertEquals(Types.INTEGER,
                        ValidationSqliteUtil.sqlTypeToSqliteType(metaData.getColumnType(5)));
            }
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
            try (final Statement statement = connection.createStatement())
            {
                statement.executeUpdate(sql);
            }
        }

        private final Connection connection;
    }
}
