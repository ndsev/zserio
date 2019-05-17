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
    public void constantUsedAsType()
    {
        final String error =
                "constant_used_as_type_error.zs:8:5: Invalid usage of constant 'ConstantUsedAsType' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void databaseArrayField()
    {
        final String error = "database_array_field_error.zs:20:5: " +
                "Invalid usage of SQL database 'TestDatabaseForArray' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitNonArray()
    {
        final String error = "implicit_non_array_field_error.zs:6:5: " +
                "Implicit keyword can be used only for arrays!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void indirectArrayRecursion()
    {
        final String error = "indirect_array_recursion_error.zs:13:17: " +
                "Indirect recursion between 'Item' and 'ItemHolder'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void indirectRecursion()
    {
        final String error = "indirect_recursion_error.zs:13:17: " +
                "Indirect recursion between 'Item' and 'ItemHolder'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void recursive()
    {
        final String error =
                "recursive_error.zs:7:17: Field 'item' is recursive and neither optional nor array!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void databaseField()
    {
        final String error =
                "database_field_error.zs:20:5: Invalid usage of SQL database 'TestDatabase' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypedTableField()
    {
        final String error =
                "subtyped_table_field_error.zs:17:19: Field 'subtypedTestTable' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableArrayField()
    {
        final String error = "table_array_field_error.zs:15:17: Field 'testTableArray' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tableField()
    {
        final String error = ":15:17: Field 'testTable' cannot be a sql table!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
