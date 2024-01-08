package parameterized_types_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class ParameterizedTypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void noArguments()
    {
        final String error = "no_arguments_error.zs:10:21: missing ':' at 'parameterized'";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noParameters()
    {
        final String error = "no_parameters_error.zs:3:21: mismatched input ')' expecting {"; // ...
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedBuiltInType()
    {
        final String[] errors = {"referenced_builtin_type_error.zs:3:9:     See 'uint32' definition here",
                "referenced_builtin_type_error.zs:3:16:     See subtype 'Item' definition here",
                "referenced_builtin_type_error.zs:5:14:     See subtype 'Subtype' definition here",
                "referenced_builtin_type_error.zs:10:5: "
                        + "Referenced type 'Subtype' is not a parameterized type!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void referencedEnumType()
    {
        final String[] errors = {"referenced_enum_type_error.zs:3:13:     See 'Item' definition here",
                "referenced_enum_type_error.zs:12:5: "
                        + "Referenced type 'Item' is not a parameterized type!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void referencedNonParameterizedType()
    {
        final String[] errors = {
                "referenced_non_parameterized_type_error.zs:3:8:     See 'Item' definition here",
                "referenced_non_parameterized_type_error.zs:12:5: "
                        + "Referenced type 'Item' is not a parameterized type!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void referencedParameterizedType()
    {
        final String[] errors = {"referenced_parameterized_type_error.zs:3:8:     See 'Item' definition here",
                "referenced_parameterized_type_error.zs:12:5: "
                        + "Referenced type 'Item' is defined as parameterized type!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void referencedUnknownParameterizedType()
    {
        final String error = "referenced_unknown_parameterized_type_error.zs:12:5: "
                + "Unresolved referenced type 'WrongItem'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongArgumentType()
    {
        final String error = "wrong_argument_type_error.zs:12:10: "
                + "Wrong type of value expression (float cannot be assigned to uint32)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongBitmaskArgumentType()
    {
        final String error = "wrong_bitmask_argument_type_error.zs:25:19: "
                + "Wrong type of value expression ('Bitmask2' cannot be assigned to 'Bitmask1')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongCompoundArgumentType()
    {
        final String error = "wrong_compound_argument_type_error.zs:23:19: "
                + "Wrong type of value expression ('Param2' cannot be assigned to 'Param1')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongEnumArgumentType()
    {
        final String error = "wrong_enum_argument_type_error.zs:25:19: "
                + "Wrong type of value expression ('Enum2' cannot be assigned to 'Enum1')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void tooFewArguments()
    {
        final String[] errors = {"too_few_arguments_error.zs:3:8:     See 'Item' definition here",
                "too_few_arguments_error.zs:13:5: "
                        +
                        "Parameterized type instantiation of 'Item' has too few arguments! Expecting 2, got 1!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void tooManyArguments()
    {
        final String[] errors = {"too_many_arguments_error.zs:3:8:     See 'Item' definition here",
                "too_many_arguments_error.zs:9:26:     See template instantiation 'ItemU16' definition here",
                "too_many_arguments_error.zs:11:17:     See subtype 'Subtype' definition here",
                "too_many_arguments_error.zs:17:5: "
                        +
                        "Parameterized type instantiation of 'Subtype' has too many arguments! Expecting 1, got 2!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    private static ZserioErrorOutput zserioErrors;
}
