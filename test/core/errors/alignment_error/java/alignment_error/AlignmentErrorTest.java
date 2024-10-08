package alignment_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class AlignmentErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
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
        final String error = "negative_integer_alignment_error.zs:5:7: Alignment expression for field "
                + "'field' is not positive integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noIntegerAlignment()
    {
        final String error = "no_integer_alignment_error.zs:5:7: Alignment expression for field 'field' "
                + "is not positive integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tooBigAlignment()
    {
        final String error = "too_big_alignment_error.zs:5:7: Alignment expression for field 'field' "
                + "is bigger than integer max value (2147483647)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
