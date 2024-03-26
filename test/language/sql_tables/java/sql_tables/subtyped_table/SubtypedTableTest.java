package sql_tables.subtyped_table;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sql_tables.TestDb;
import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class SubtypedTableTest
{
    @BeforeAll
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @BeforeEach
    public void setUp() throws IOException, SQLException
    {
        FileUtil.deleteFileIfExists(file);
        database = new TestDb(file.toString());
        database.createSchema();
    }

    @AfterEach
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
        final String tableName = "subtypedTable";
        assertTrue(isTableInDb(tableName));

        final TestTable subtypedTable = database.getSubtypedTable();
        assertTrue(subtypedTable != null);
    }

    @Test
    public void testGenSubtypedTable() throws SQLException
    {
        final String tableName = "genSubtypedTable";
        final SubtypedTable genSubtypedTable = new SubtypedTable(database.connection(), tableName);
        genSubtypedTable.createTable();
        assertTrue(isTableInDb(tableName));
    }

    @Test
    public void testAnotherSubtypedTable() throws SQLException
    {
        final String tableName = "anotherSubtypedTable";
        final AnotherSubtypedTable anotherSubtypedTable =
                new AnotherSubtypedTable(database.connection(), tableName);
        anotherSubtypedTable.createTable();
        assertTrue(isTableInDb(tableName));
    }

    private boolean isTableInDb(String tableName) throws SQLException
    {
        // check if database does contain table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);)
        {
            statement.setString(1, tableName);
            try (final ResultSet resultSet = statement.executeQuery();)
            {
                if (!resultSet.next())
                    return false;

                // read table name
                final String readTableName = resultSet.getString(1);
                if (resultSet.wasNull() || !readTableName.equals(tableName))
                    return false;
            }
        }

        return true;
    }

    private static final String FILE_NAME = "subtyped_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
