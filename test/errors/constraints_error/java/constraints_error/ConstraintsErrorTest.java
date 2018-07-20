package constraints_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class ConstraintsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void fieldInFunctionNotAvailableError()
    {
        final String error = ":17:29: Field 'specialMinValue' is not available! " +
                             "In function 'checkSpecial' called from here:";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldNotAvailableError()
    {
        final String error = ":7:41: Field 'specialMinValue' is not available!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noneBooleanExpression()
    {
        final String error = ":6:35: Constraint expression for field 'constraintValue' is not boolean!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
