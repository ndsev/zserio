package union_types_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class UnionTypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void optionalMember()
    {
        final String errors[] =
        {
            "optional_field_error.zs:6:18: mismatched input 'if' expecting {", // ...
            "optional_field_error.zs:6:18: 'if' is a reserved keyword!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void recursive()
    {
        final String error = "recursive_error.zs:6:17: " +
                "Field 'field2' is recursive and neither optional nor array which can be empty!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unresolvedFieldInConstraint()
    {
        final String error = "unresolved_field_in_constraint_error.zs:6:35: Unresolved symbol 'field1' " +
                "within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
