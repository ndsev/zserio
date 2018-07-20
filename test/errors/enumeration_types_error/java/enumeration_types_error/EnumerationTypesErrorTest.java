package enumeration_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class EnumerationTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void cyclicDefinitionUsingConstant()
    {
        final String error = ":11:34: Cyclic dependency detected in expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinitionUsingEnumValue()
    {
        final String error = ":12:17: Cyclic dependency detected in expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedEnumItem()
    {
        final String error = ":7:5: 'DARK_RED' is already defined in this scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedEnumValue()
    {
        final String error = ":7:18: Enumeration item 'DARK_BLUE' has duplicated value (1)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void nonIntegerEnumValue()
    {
        final String error = ":7:18: Enumeration item 'DARK_BLUE' has non-integer value!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void outOfRangeEnumValue()
    {
        final String error = ":7:18: Enumeration item 'DARK_BLUE' has value (256) out of range <0,255>!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void stringEnumError()
    {
        final String error = ":3:1: Enumeration 'WrongStringEnum' has forbidden type string!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
