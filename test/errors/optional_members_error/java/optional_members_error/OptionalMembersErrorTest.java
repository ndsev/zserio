package optional_members_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class OptionalMembersErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void autoOptionalWithExpression()
    {
        final String error = "auto_optional_with_expression_error.zs:6:39: "
                + "Auto optional field 'autoOptionalValue' cannot contain if clause!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void compoundFieldNotAvailable()
    {
        final String error = "compound_field_not_available_error.zs:7:23: "
                + "Unresolved symbol 'header2' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldItselfNotAvailable()
    {
        final String error = "field_itself_not_available_error.zs:6:26: "
                + "Unresolved symbol 'extraData' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldNotAvailable()
    {
        final String error = "field_not_available_error.zs:7:28: "
                + "Unresolved symbol 'hasSpecialData' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldUsedAsIndexedOffset()
    {
        final String errors[] = {
                "field_used_as_indexed_offset_error.zs:5:12:     Field 'offsets' defined here!",
                "field_used_as_indexed_offset_error.zs:6:1:     Field 'offsets' used as an offset here!",
                "field_used_as_indexed_offset_error.zs:13:21: "
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
                "field_used_as_offset_error.zs:7:21: Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void noneBooleanExpression()
    {
        final String error = "none_boolean_expression_error.zs:6:34: "
                + "Optional expression for field 'optionalValue' is not boolean!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
