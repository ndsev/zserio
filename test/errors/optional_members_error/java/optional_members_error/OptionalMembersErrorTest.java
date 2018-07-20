package optional_members_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class OptionalMembersErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void autoOptionalWithExpression()
    {
        final String error = "auto_optional_with_expression_error.zs:6:42: Auto optional field " +
                "'autoOptionalValue' cannot contain if clause!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void compoundFieldInFunctionNotAvailable()
    {
        final String error = "compound_field_in_function_not_available_error.zs:17:23: Field 'header2' is " +
                "not available! In function 'hasOptional2' called from here:";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void compoundFieldNotAvailable()
    {
        final String error = "compound_field_not_available_error.zs:7:23: Field 'header2' is not available!";
        assertTrue(zserioErrors.isPresent(error)); //
    }

    @Test
    public void fieldInFunctionNotAvailable()
    {
        final String error = "field_in_function_not_available_error.zs:17:16: Field 'hasSpecialData' is not " +
                "available! In function 'hasSpecial' called from here:";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldNotAvailable()
    {
        final String error = "field_not_available_error.zs:7:28: Field 'hasSpecialData' is not available!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noneBooleanExpression()
    {
        final String error = "none_boolean_expression_error.zs:6:34: Optional expression for field " +
                "'optionalValue' is not boolean!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noneOptionalMemberWithAutoOptionalParams()
    {
        final String error = "none_optional_with_auto_optional_params_error.zs:20:5: Parametrized field " +
                "'blackTones' is not optional but uses optional parameters!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noneOptionalMemberWithOptionalParams()
    {
        final String error = "none_optional_with_optional_params_error.zs:20:5: Parametrized field " +
                "'blackTones' is not optional but uses optional parameters!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
