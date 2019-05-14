package parameterized_types_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class ParameterizedTypesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void noArguments()
    {
        // TODO:
        final String error = "no_arguments_error.zs:10:21: missing ':' at 'parameterized'";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void noParameters()
    {
        final String error = "no_parameters_error.zs:3:21: mismatched input ')' expecting {";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedNonCompoundType()
    {
        final String error = "referenced_non_compound_type_error.zs:8:5: " +
                "Parameterized type instantiation 'Item()' does not refer to a compound type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void referencedNonParameterizedType()
    {
        final String error = "referenced_non_parameterized_type_error.zs:12:5: " +
                "Parameterized type instantiation 'Item()' does not refer to a parameterized type!";
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
    public void wrongNumberOfArguments()
    {
        final String error = "wrong_number_of_arguments_error.zs:13:5: " +
                "Parameterized type instantiation 'Item()' has wrong number of arguments (1 != 2)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
