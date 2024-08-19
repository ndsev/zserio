package constraints_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class ConstraintsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void fieldInFunctionNotAvailableError()
    {
        final String errors[] = {
                "field_in_function_not_available_error.zs:7:26:     In function 'checkSpecial' called from here",
                "field_in_function_not_available_error.zs:17:31: "
                        + "Unresolved symbol 'specialMinValue' within expression scope!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void fieldNotAvailableError()
    {
        final String error = "field_not_available_error.zs:7:41: Unresolved symbol 'specialMinValue' "
                + "within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldUsedAsIndexedOffset()
    {
        final String errors[] = {
                "field_used_as_indexed_offset_error.zs:5:12:     Field 'offsets' defined here!",
                "field_used_as_indexed_offset_error.zs:6:1:     Field 'offsets' used as an offset here!",
                "field_used_as_indexed_offset_error.zs:7:23: "
                        + "Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void fieldUsedAsOffset()
    {
        final String errors[] = {
                "field_used_as_offset_error.zs:5:12:     Field 'offset' defined here!",
                "field_used_as_offset_error.zs:6:1:     Field 'offset' used as an offset here!",
                "field_used_as_offset_error.zs:7:20: Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void noneBooleanExpression()
    {
        final String error = "none_boolean_expression_error.zs:6:35: Constraint expression for field "
                + "'constraintValue' is not boolean!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
