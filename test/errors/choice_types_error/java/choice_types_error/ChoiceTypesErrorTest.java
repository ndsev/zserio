package choice_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class ChoiceTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void boolCaseError()
    {
        final String error = ":8:10: Choice 'BoolCaseChoice' has incompatible case type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void enumCaseError()
    {
        final String error =
                ":24:23: Choice 'EnumParamChoice' has case with different enumeration type than selector!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void floatSelectorError()
    {
        final String error = ":3:1: Choice 'FloatSelectorChoice' uses forbidden float selector!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void multipleCases()
    {
        final String error = ":11:10: Choice 'MultipleCasesChoice' has duplicated case!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void multipleDefaults()
    {
        final String error = ":14:5: expecting RCURLY, found 'default'";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void nonConstantCase()
    {
        final String error = ":23:10: Choice 'IntChoice' has non-constant case expression!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void optionalMember()
    {
        final String error = ":6:29: unexpected token: if";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void stringSelectorError()
    {
        final String error = ":3:1: Choice 'StringSelectorChoice' uses forbidden string selector!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unreachableDefault()
    {
        final String error = ":11:5: Choice 'UnreachableDefaultChoice' has unreachable default case!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
