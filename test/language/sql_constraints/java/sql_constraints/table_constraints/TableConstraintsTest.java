package sql_constraints.table_constraints;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sql_constraints.TestDb;
import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class TableConstraintsTest
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
    public void primaryKey() throws IOException, SQLException
    {
        final TableConstraintsTable tableConstraintsTable = database.getTableConstraintsTable();
        final TableConstraintsTableRow row = new TableConstraintsTableRow();
        row.setPrimaryKey1(1);
        row.setPrimaryKey2(1);
        row.setNullUniqueValue1();
        row.setNullUniqueValue2();
        tableConstraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void primaryKeyWrong() throws IOException, SQLException
    {
        final TableConstraintsTable tableConstraintsTable = database.getTableConstraintsTable();
        final TableConstraintsTableRow row1 = new TableConstraintsTableRow();
        row1.setPrimaryKey1(1);
        row1.setPrimaryKey2(1);
        row1.setUniqueValue1(1);
        row1.setUniqueValue2(1);
        final TableConstraintsTableRow row2 = new TableConstraintsTableRow();
        row2.setPrimaryKey1(1);
        row2.setPrimaryKey2(1);
        row2.setUniqueValue1(2);
        row2.setUniqueValue2(1);
        final ArrayList<TableConstraintsTableRow> rows = new ArrayList<TableConstraintsTableRow>();
        rows.add(row1);
        rows.add(row2);
        final SQLException thrown = assertThrows(SQLException.class, () -> tableConstraintsTable.write(rows));

        assertThat(thrown.getMessage(),
                containsString("UNIQUE constraint failed: tableConstraintsTable.primaryKey1, "
                        + "tableConstraintsTable.primaryKey2"));
    }

    @Test
    public void unique() throws IOException, SQLException
    {
        final TableConstraintsTable tableConstraintsTable = database.getTableConstraintsTable();
        final TableConstraintsTableRow row = new TableConstraintsTableRow();
        row.setPrimaryKey1(1);
        row.setPrimaryKey2(1);
        row.setUniqueValue1(1);
        row.setUniqueValue2(1);
        tableConstraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void uniqueWrong() throws IOException, SQLException
    {
        final TableConstraintsTable tableConstraintsTable = database.getTableConstraintsTable();
        final TableConstraintsTableRow row1 = new TableConstraintsTableRow();
        row1.setPrimaryKey1(1);
        row1.setPrimaryKey2(1);
        row1.setUniqueValue1(1);
        row1.setUniqueValue2(1);
        final TableConstraintsTableRow row2 = new TableConstraintsTableRow();
        row2.setPrimaryKey1(2);
        row2.setPrimaryKey2(1);
        row2.setUniqueValue1(1);
        row2.setUniqueValue2(1);
        final ArrayList<TableConstraintsTableRow> rows = new ArrayList<TableConstraintsTableRow>();
        rows.add(row1);
        rows.add(row2);
        final SQLException thrown = assertThrows(SQLException.class, () -> tableConstraintsTable.write(rows));

        assertThat(thrown.getMessage(),
                containsString("UNIQUE constraint failed: tableConstraintsTable.uniqueValue1, "
                        + "tableConstraintsTable.uniqueValue2"));
    }

    private static final String FILE_NAME = "table_constraints_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
