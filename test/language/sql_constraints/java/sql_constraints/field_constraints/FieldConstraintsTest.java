package sql_constraints.field_constraints;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_constraints.TestDb;

public class FieldConstraintsTest
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
        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setNullSqlNotNull();
        final SQLException thrown =
                assertThrows(SQLException.class, () -> fieldConstraintsTable.write(Arrays.asList(row)));

        assertThat(thrown.getMessage(),
                containsString("NOT NULL constraint failed: fieldConstraintsTable.sqlNotNull"));
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
    public void sqlCheckConstant() throws IOException, SQLException
    {
        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckConstant(WRONG_CONSTRAINTS_CONSTANT);
        final SQLException thrown =
                assertThrows(SQLException.class, () -> fieldConstraintsTable.write(Arrays.asList(row)));

        assertThat(thrown.getMessage(), containsString("CHECK constraint failed: fieldConstraintsTable"));
    }

    @Test
    public void sqlCheckImportedConstant() throws IOException, SQLException
    {

        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckImportedConstant(WRONG_IMPORTED_CONSTRAINTS_CONSTANT);
        final SQLException thrown =
                assertThrows(SQLException.class, () -> fieldConstraintsTable.write(Arrays.asList(row)));

        assertThat(thrown.getMessage(), containsString("CHECK constraint failed: fieldConstraintsTable"));
    }

    @Test
    public void sqlCheckUnicodeEscape() throws IOException, SQLException
    {
        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckUnicodeEscape(WRONG_UNICODE_ESCAPE_CONST);
        final SQLException thrown =
                assertThrows(SQLException.class, () -> fieldConstraintsTable.write(Arrays.asList(row)));

        assertThat(thrown.getMessage(), containsString("CHECK constraint failed: fieldConstraintsTable"));
    }

    @Test
    public void sqlCheckHexEscape() throws IOException, SQLException
    {
        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckHexEscape(WRONG_HEX_ESCAPE_CONST);
        final SQLException thrown =
                assertThrows(SQLException.class, () -> fieldConstraintsTable.write(Arrays.asList(row)));

        assertThat(thrown.getMessage(), containsString("CHECK constraint failed: fieldConstraintsTable"));
    }

    @Test
    public void sqlCheckOctalEscape() throws IOException, SQLException
    {
        final FieldConstraintsTable fieldConstraintsTable = database.getFieldConstraintsTable();
        final FieldConstraintsTableRow row = new FieldConstraintsTableRow();
        fillRow(row);
        row.setSqlCheckOctalEscape(WRONG_OCTAL_ESCAPE_CONST);
        final SQLException thrown =
                assertThrows(SQLException.class, () -> fieldConstraintsTable.write(Arrays.asList(row)));

        assertThat(thrown.getMessage(), containsString("CHECK constraint failed: fieldConstraintsTable"));
    }

    private void fillRow(FieldConstraintsTableRow row)
    {
        row.setWithoutSql(1);
        row.setSqlNotNull(1);
        row.setSqlDefaultNull(1);
        row.setSqlCheckConstant(1);
        row.setSqlCheckImportedConstant(1);
        row.setSqlCheckUnicodeEscape(UNICODE_ESCAPE_CONST);
        row.setSqlCheckHexEscape(HEX_ESCAPE_CONST);
        row.setSqlCheckOctalEscape(OCTAL_ESCAPE_CONST);
    }

    private static final String FILE_NAME = "field_constraints_test.sqlite";

    private static final short UNICODE_ESCAPE_CONST = 1;
    private static final short HEX_ESCAPE_CONST = 2;
    private static final short OCTAL_ESCAPE_CONST = 3;

    private static final int WRONG_CONSTRAINTS_CONSTANT = 124;
    private static final long WRONG_IMPORTED_CONSTRAINTS_CONSTANT = 322;

    private static final short WRONG_UNICODE_ESCAPE_CONST = 0;
    private static final short WRONG_HEX_ESCAPE_CONST = 0;
    private static final short WRONG_OCTAL_ESCAPE_CONST = 0;

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
