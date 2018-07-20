package member_access_error;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class MemberAccessErrorTest
{
    @Test
    public void wrongFieldName() throws IOException
    {
        final ZserioErrors zserioErrors = new ZserioErrors();
        final String error = ":6:48: Unresolved symbol in '.' expression!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongFieldUsage() throws IOException
    {
        final ZserioErrors zserioErrors = new ZserioErrors();
        final String error = ":5:34: Field 'data' is not available!";
        assertTrue(zserioErrors.isPresent(error));
    }
}
