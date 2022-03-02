package member_access_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class MemberAccessErrorTest
{
    @Test
    public void wrongFieldName() throws IOException
    {
        final ZserioErrorOutput zserioErrors = new ZserioErrorOutput();
        final String error = "wrong_field_name_error.zs:6:34: " +
                "Unresolved symbol 'wrongFieldName' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongFieldUsage() throws IOException
    {
        final ZserioErrorOutput zserioErrors = new ZserioErrorOutput();
        final String error = "wrong_field_usage_error.zs:5:34: " +
                "Unresolved symbol 'data' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }
}
