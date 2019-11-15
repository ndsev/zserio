package sql_constraints;

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

import sql_constraints.constraints.ImportedConstant;
import sql_constraints.constraints.ImportedEnum;

public class SqlConstraintsTest
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
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("NOT NULL constraint failed: constraintsTable.withoutSql");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setNullWithoutSql();
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlNotNull() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("NOT NULL constraint failed: constraintsTable.sqlNotNull");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setNullSqlNotNull();
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlDefaultNull() throws IOException, SQLException
    {
        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setNullSqlDefaultNull();
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlNull() throws IOException, SQLException
    {
        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setNullSqlNull();
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckConstant() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: constraintsTable");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckConstant(ConstraintsConstant.ConstraintsConstant);
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckImportedConstant() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: constraintsTable");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckImportedConstant(ImportedConstant.ImportedConstant);
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckEnum() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: constraintsTable");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckEnum(ConstraintsEnum.VALUE2);
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckImportedEnum() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: constraintsTable");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckImportedEnum(ImportedEnum.TWO);
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckUnicodeEscape() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: constraintsTable");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckUnicodeEscape(WRONG_UNICODE_ESCAPE_CONST);
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckHexEscape() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: constraintsTable");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckHexEscape(WRONG_HEX_ESCAPE_CONST);
        constraintsTable.write(Arrays.asList(row));
    }

    @Test
    public void sqlCheckOctalEscape() throws IOException, SQLException
    {
        expectedException.expect(SQLException.class);
        expectedException.expectMessage("CHECK constraint failed: constraintsTable");

        final ConstraintsTable constraintsTable = database.getConstraintsTable();
        final ConstraintsTableRow row = new ConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckOctalEscape(WRONG_OCTAL_ESCAPE_CONST);
        constraintsTable.write(Arrays.asList(row));
    }

    private void fillRow(ConstraintsTableRow row)
    {
        row.setWithoutSql(1);
        row.setSqlNotNull(1);
        row.setSqlDefaultNull(1);
        row.setSqlNull(1);
        row.setSqlCheckConstant(1);
        row.setSqlCheckImportedConstant(1);
        row.setSqlCheckEnum(ConstraintsEnum.VALUE1);
        row.setSqlCheckImportedEnum(ImportedEnum.ONE);
        row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
        row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
        row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    }

    private static final String FILE_NAME = "sql_constraints_test.sqlite";

    private static final short UNICODE_ESCAPE_CONST = 1;
    private static final short HEX_ESCAPE_CONST = 2;
    private static final short OCTAL_ESCAPE_CONST = 3;

    private static final short WRONG_UNICODE_ESCAPE_CONST = 0;
    private static final short WRONG_HEX_ESCAPE_CONST = 0;
    private static final short WRONG_OCTAL_ESCAPE_CONST = 0;

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
