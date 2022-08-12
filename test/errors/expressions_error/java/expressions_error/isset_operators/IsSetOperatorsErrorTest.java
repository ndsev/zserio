package expressions_error.isset_operators;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class IsSetOperatorsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void arrayType()
    {
        final String error = "array_type_error.zs:11:31: 'bitmaskArray' is not a bitmask!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void enumType()
    {
        final String error = "enum_type_error.zs:10:35: 'enumField' is not a bitmask!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void integerType()
    {
        final String error = "integer_type_error.zs:11:33: 'field' is not a bitmask!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongBitmaskType()
    {
        final String error = "wrong_bitmask_type_error.zs:16:47: 'WrongBitmask' does not match to 'Bitmask'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
