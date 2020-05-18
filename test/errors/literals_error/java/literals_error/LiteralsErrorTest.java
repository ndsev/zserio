package literals_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class LiteralsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void hexadecimalStringLiteral()
    {
        final String error = "hexadecimal_string_literal_error.zs:3:43: '\"This is wrong escaped hexadecimal " +
                "character \\xWRONG\"' is an invalid string literal!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void multilineStringLiteral()
    {
        final String errors[] = {
                "multiline_string_literal_error.zs:3:41: '\"This is forbidden multiline string literal",
                "which is not enclosed by quotes in each row.\"' is an invalid string literal!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void octalStringLiteral()
    {
        final String error = "octal_string_literal_error.zs:3:37: '\"This is wrong escaped octal character " +
                "\\09\"' is an invalid string literal!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unicodeStringLiteral()
    {
        final String error = "unicode_string_literal_error.zs:3:39: '\"This is wrong escaped unicode " +
                "character \\uBAD\"' is an invalid string literal!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongHexadecimalLiteral()
    {
        final String error =
                "wrong_hexadecimal_literal_error.zs:3:41: '0xWRONG' is an invalid token!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongOctalLiteral()
    {
        final String error = "wrong_octal_literal_error.zs:3:36: '09' is an invalid token!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
