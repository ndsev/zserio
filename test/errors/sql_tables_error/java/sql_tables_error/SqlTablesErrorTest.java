package sql_tables_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class SqlTablesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void databaseArrayField()
    {
        final String error = ":21:38: unexpected token: [";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void databaseField()
    {
        final String error = ":20:5: Invalid usage of SQL database 'TestDatabase' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedFieldName()
    {
        final String error = ":7:13: 'columnA' is already defined in this scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noFields()
    {
        final String error = ":3:1: Ordinary table must have at least one field!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterizedTable()
    {
        final String error = ":3:20: unexpected token: (";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableFieldConstraint()
    {
        final String error = ":7:38: unexpected token: :";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypedTableField()
    {
        final String error = ":17:5: Field 'subtypedTestTable' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableArrayField()
    {
        final String error = ":16:31: unexpected token: [";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableField()
    {
        final String error = ":15:5: Field 'testTable' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unknownPrimaryKeyColumn()
    {
        final String error = ":9:5: Primary key column 'unknownColumn' not found in sql table 'TestTable'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unknownUniqueColumn()
    {
        final String error = ":9:5: Unique column 'unknownColumn' not found in sql table 'TestTable'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void virtualColumn()
    {
        final String error = ":6:17: Ordinary table 'TestTable' cannot contain virtual column 'columnB'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
