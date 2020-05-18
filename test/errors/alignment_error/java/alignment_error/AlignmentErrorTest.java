package alignment_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class AlignmentErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void fieldAlignment()
    {
        final String error =
                "field_alignment_error.zs:6:7: Alignment expression for field 'field' is not positive integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void negativeIntegerAlignment()
    {
        final String error = "negative_integer_alignment_error.zs:5:7: Alignment expression for field " +
                "'field' is not positive integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noIntegerAlignment()
    {
        final String error = "no_integer_alignment_error.zs:5:7: Alignment expression for field 'field' " +
                "is not positive integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
