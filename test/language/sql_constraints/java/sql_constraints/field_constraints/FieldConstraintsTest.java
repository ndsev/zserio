package sql_constraints.field_constraints;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_constraints.TestDb;

public class FieldConstraintsTest
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
    public void withoutSql() throws IOException, SQLException
    {
        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setNullWithoutSql();
        fieldConstraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlNotNull() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("NOT NULL constraint failed: fieldConstraintsTable.sqlNotNull");

        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setNullSqlNotNull();
        fieldConstraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlDefaultNull() throws IOException, SQLException
    {
        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setNullSqlDefaultNull();
        fieldConstraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckUnicodeEscape() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: fieldConstraintsTable");

        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckUnicodeEscape(WRONG_UNICODE_ESCAPE_CONST);
        fieldConstraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckHexEscape() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: fieldConstraintsTable");

        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckHexEscape(WRONG_HEX_ESCAPE_CONST);
        fieldConstraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckOctalEscape() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: fieldConstraintsTable");

        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckOctalEscape(WRONG_OCTAL_ESCAPE_CONST);
        fieldConstraintsTable.write(Arrays.asList(row));
    }

    private void fillRow(FieldConstraintsTableRow row)
    {
        row.setWithoutSql(1);
        row.setSqlNotNull(1);
        row.setSqlDefaultNull(1);
        row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
        row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
        row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    }

    private static final String FILE_NAME = "field_constraints_test.sqlite";

    private static final short UNICODE_ESCAPE_CONST = 1;
    private static final short HEX_ESCAPE_CONST = 2;
    private static final short OCTAL_ESCAPE_CONST = 3;

    private static final short WRONG_UNICODE_ESCAPE_CONST = 0;
    private static final short WRONG_HEX_ESCAPE_CONST = 0;
    private static final short WRONG_OCTAL_ESCAPE_CONST = 0;

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
