package parameterized_types_error;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        final String error = "referenced_builtin_type_error.zs:8:5: " +
                "Referenced type 'Item' is not a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedEnumType()
    {
        final String error = "referenced_enum_type_error.zs:12:5: " +
                "Referenced type 'Item' is not a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedNonParameterizedType()
    {
        final String error = "referenced_non_parameterized_type_error.zs:12:5: " +
                "Referenced type 'Item' is not a parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedParameterizedType()
    {
        final String error = "referenced_parameterized_type_error.zs:12:5: " +
                "Referenced type 'Item' is defined as parameterized type!";
        assertTrue(zserioErrors.isPresent(error));
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
        final String error = "wrong_argument_type_error.zs:12:10: " +
                "Wrong type of value expression (float cannot be assigned to uint32)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongBitmaskArgumentType()
    {
        final String error = "wrong_bitmask_argument_type_error.zs:25:19: " +
            "Wrong type of value expression ('Bitmask2' cannot be assigned to 'Bitmask1')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongCompoundArgumentType()
    {
        final String error = "wrong_compound_argument_type_error.zs:23:19: " +
            "Wrong type of value expression ('Param2' cannot be assigned to 'Param1')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongEnumArgumentType()
    {
        final String error = "wrong_enum_argument_type_error.zs:25:19: " +
            "Wrong type of value expression ('Enum2' cannot be assigned to 'Enum1')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongNumberOfArguments()
    {
        final String error = "wrong_number_of_arguments_error.zs:13:5: " +
                "Parameterized type instantiation 'Item' has wrong number of arguments! Expecting 1, got 2!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
