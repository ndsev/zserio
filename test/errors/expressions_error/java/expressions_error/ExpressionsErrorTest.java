package expressions_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class ExpressionsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void bitmaskShiftOperator()
    {
        final String error = "bitmask_shift_operator_error.zs:12:17: Integer expressions expected!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void nonConstantStringConcatenation()
    {
        final String error =
                "non_constant_string_concatenation_error.zs:9:16: Constant string expressions expected!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void stringIntegerConcatenation()
    {
        final String error =
                "string_integer_concatenation_error.zs:9:16: Integer or float expressions expected!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongFullType()
    {
        final String error = "wrong_full_type_error.zs:7:33: " +
                "Unresolved symbol 'wrong_full_type_error.someStructure' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongLengthOfSyntax()
    {
        final String error = "wrong_lengthof_syntax_error.zs:9:25: missing '(' at 'fixedArray'";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
