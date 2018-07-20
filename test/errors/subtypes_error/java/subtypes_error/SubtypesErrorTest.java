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
        final String error = "13:9: Invalid use of SQL database 'TestDatabase' as a type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void simpleCyclicDependency()
    {
        final String error = "3:1: Cyclic dependency detected in subtype 'X' definition!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void transitiveCyclicDependency()
    {
        // concrete error depends on current HashSet implementation
        final String errorVariant1 = ":3:1: Cyclic dependency detected in subtype 'Y' definition!";
        final String errorVariant2 = ":4:1: Cyclic dependency detected in subtype 'Z' definition!";
        final String errorVariant3 = ":5:1: Cyclic dependency detected in subtype 'X' definition!";
        assertTrue(zserioErrors.isPresent(errorVariant1) || zserioErrors.isPresent(errorVariant2) || zserioErrors.isPresent(errorVariant3));
    }

    private static ZserioErrors zserioErrors;
}
