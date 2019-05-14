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
        final String error = "wrong_field_name_error.zs:6:34: " +
                "Unresolved symbol 'wrongFieldName' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongFieldUsage() throws IOException
    {
        final ZserioErrors zserioErrors = new ZserioErrors();
        final String error = "wrong_field_usage_error.zs:5:34: " +
                "Unresolved symbol 'data' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }
}
