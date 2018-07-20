package structure_types_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class StructureTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void circularContainment()
    {
        final String error = ":3:1: Circular containment between 'Item' and 'ItemHolder'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constantUsedAsType()
    {
        final String error = ":8:5: Invalid usage of constant 'ConstantUsedAsType' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void databaseArrayField()
    {
        final String error = ":20:5: Invalid use of SQL database 'TestDatabaseForArray' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void databaseField()
    {
        final String error = ":20:5: Invalid use of SQL database 'TestDatabase' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitNonArray()
    {
        final String error = ":6:27: unexpected token: ;";
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
        final String error = ":15:5: Field 'testTableArray' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableField()
    {
        final String error = ":15:5: Field 'testTable' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
