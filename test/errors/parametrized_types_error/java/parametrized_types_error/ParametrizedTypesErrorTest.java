package parametrized_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class ParametrizedTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void referencedNonCompoundType()
    {
        final String error =
                ":8:5: Parametrized type instantiation 'Item()' does not refer to a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedNonParametrizedType()
    {
        final String error =
                ":12:5: Parametrized type instantiation 'Item()' does not refer to a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedParametrizedType()
    {
        final String error = ":12:5: Referenced type 'Item' is defined as parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedUnknownParametrizedType()
    {
        final String error = ":12:5: Unresolved referenced type 'WrongItem'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongNumberOfArguments()
    {
        final String error = ":13:5: Parametrized type instantiation 'Item()' has wrong number of " +
                "arguments (1 != 2)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongArgumentType()
    {
        final String error = ":12:10: Wrong type of value expression (float cannot be assigned to uint32)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
