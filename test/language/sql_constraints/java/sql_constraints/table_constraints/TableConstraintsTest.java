package sql_constraints.table_constraints;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_constraints.TestDb;

public class TableConstraintsTest
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("UNIQUE constraint failed: tableConstraintsTable.primaryKey1, " +
        		"tableConstraintsTable.primaryKey2");

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
        tableConstraintsTable.write(rows);
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
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("UNIQUE constraint failed: tableConstraintsTable.uniqueValue1, " +
        		"tableConstraintsTable.uniqueValue2");

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
        tableConstraintsTable.write(rows);
    }

    private static final String FILE_NAME = "table_constraints_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
