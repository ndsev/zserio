package sql_databases_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class SqlDatabasesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void databaseField()
    {
        final String error = ":20:5: Invalid usage of SQL database 'TestDatabase' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void structureField()
    {
        final String error = ":19:5: Field 'testStructure' is not a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypedStructureField()
    {
        final String error = ":15:5: Field 'subtypedTestStructure' is not a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
