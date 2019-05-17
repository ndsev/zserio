package subtypes_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class SubtypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void databaseSubtype()
    {
        final String error = "database_subtype_error.zs:13:9: " +
                "Invalid usage of SQL database 'TestDatabase' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void parameterizedSubtype()
    {
        final String error = "parameterized_subtype_error.zs:12:5: " +
                "Referenced type 'Subtype' is defined as parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void simpleCyclicDependency()
    {
        final String error = "simple_cyclic_dependency_error.zs:3:11: " +
                "Cyclic dependency detected in subtype 'X' definition!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void transitiveCyclicDependency()
    {
        final String error = "transitive_cyclic_dependency_error.zs:3:11: Cyclic dependency detected in " +
                "subtype 'Y' definition!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
