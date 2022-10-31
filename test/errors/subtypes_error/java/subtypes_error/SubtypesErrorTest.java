package subtypes_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class SubtypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
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
                "Cyclic dependency detected in subtype 'X'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void transitiveCyclicDependency()
    {
        final String errors[] =
        {
            "transitive_cyclic_dependency_error.zs:4:11:     Through subtype 'Z' here",
            "transitive_cyclic_dependency_error.zs:5:11:     Through subtype 'X' here",
            "transitive_cyclic_dependency_error.zs:3:11: Cyclic dependency detected in subtype 'Y'!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    private static ZserioErrorOutput zserioErrors;
}
