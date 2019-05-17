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
    public void wrongDecimalConstant()
    {
        final String error = "wrong_octal_literal_error.zs:3:36: mismatched input '09' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
