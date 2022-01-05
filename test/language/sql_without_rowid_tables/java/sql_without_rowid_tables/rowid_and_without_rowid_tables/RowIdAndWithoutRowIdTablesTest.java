package sql_without_rowid_tables.rowid_and_without_rowid_tables;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_without_rowid_tables.rowid_and_without_rowid_tables.RowIdAndWithoutRowIdDb;

public class RowIdAndWithoutRowIdTablesTest
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
    public void checkRowIdColumn() throws SQLException
    {
        database = new RowIdAndWithoutRowIdDb(file.toString());
        database.createSchema();
        assertFalse(isColumnInTable("rowid", WITHOUT_ROWID_TABLE_NAME));
        assertTrue(isColumnInTable("rowid", ORDINARY_ROWID_TABLE_NAME));
    }

    @Test
    public void createOrdinaryRowIdTable() throws SQLException
    {
        database = new RowIdAndWithoutRowIdDb(file.toString());
        final WithoutRowIdTable testTable = database.getWithoutRowIdTable();
        testTable.createOrdinaryRowIdTable();
        assertTrue(isColumnInTable("rowid", WITHOUT_ROWID_TABLE_NAME));
    }

    @Test
    public void checkWithoutRowIdTableNamesBlackList() throws SQLException
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
            final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);
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
