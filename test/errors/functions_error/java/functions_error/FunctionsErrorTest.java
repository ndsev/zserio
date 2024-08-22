package functions_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class FunctionsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void compoundFieldNotAvailable()
    {
        final String errors[] = {
                "compound_field_not_available_error.zs:7:23:     In function 'hasOptional2' called from here",
                "compound_field_not_available_error.zs:17:16: Unresolved symbol 'header2' within expression scope!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void differentScopes()
    {
        final String errors[] = {"different_scopes_error.zs:15:27:     In function 'getValue' called from here",
                "different_scopes_error.zs:20:55: Unresolved symbol 'val3' within expression scope!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void fieldNotAvailable()
    {
        final String errors[] = {
                "field_not_available_error.zs:7:28:     In function 'hasSpecial' called from here",
                "field_not_available_error.zs:17:16: Unresolved symbol 'hasSpecialData' within expression scope!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void fieldUsedAsIndexedOffset()
    {
        final String errors[] = {
                "field_used_as_indexed_offset_error.zs:5:12:     Field 'offsets' defined here!",
                "field_used_as_indexed_offset_error.zs:16:1:     Field 'offsets' used as an offset here!",
                "field_used_as_indexed_offset_error.zs:9:16: "
                        + "Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void fieldUsedAsOffset()
    {
        final String errors[] = {
                "field_used_as_offset_error.zs:5:12:     Field 'offset' defined here!",
                "field_used_as_offset_error.zs:16:1:     Field 'offset' used as an offset here!",
                "field_used_as_offset_error.zs:9:16: Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void functionCallWithArgument()
    {
        final String error = "function_call_with_argument_error.zs:16:48: extraneous input '2' expecting ')'";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void functionWithParameter()
    {
        final String errors[] = {
                "function_with_parameter_error.zs:7:38: mismatched input 'int32' expecting ')'",
                "function_with_parameter_error.zs:7:38: 'int32' is a reserved keyword!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void recursive()
    {
        final String error = "recursive_error.zs:11:62: Unresolved symbol 'getValue' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongReturnType()
    {
        final String error = "wrong_return_type_error.zs:9:16: Wrong type of value expression (integer "
                + "cannot be assigned to bool)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
