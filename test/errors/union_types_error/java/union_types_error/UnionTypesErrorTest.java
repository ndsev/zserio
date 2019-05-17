package union_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class UnionTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void optionalMember()
    {
        final String error = "optional_field_error.zs:6:18: " +
                "mismatched input 'if' expecting {':', '[', ';'} ('if' is a reserved keyword)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void recursive()
    {
        final String error =
                "recursive_error.zs:6:17: Field 'field2' is recursive and neither optional nor array!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unresolvedFieldInConstraint()
    {
        final String error = "unresolved_field_in_constraint_error.zs:6:35: Unresolved symbol 'field1' " +
                "within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
