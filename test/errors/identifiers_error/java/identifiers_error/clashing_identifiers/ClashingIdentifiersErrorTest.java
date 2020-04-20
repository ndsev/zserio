package identifiers_error.clashing_identifiers;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class ClashingIdentifiersErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void clashingChoiceParamNames()
    {
        final String errors[] =
        {
            "clashing_choice_param_names_error.zs:8:21:    Conflicting symbol defined here!",
            "clashing_choice_param_names_error.zs:8:35: " +
                    "Symbol 'Param' differs only in a case of its first letter!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingStructureFieldAndParamNames()
    {
        final String errors[] =
        {
            "clashing_structure_field_and_param_names_error.zs:3:24:    Conflicting symbol defined here!",
            "clashing_structure_field_and_param_names_error.zs:5:11: " +
                    "Symbol 'value' differs only in a case of its first letter!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingStructureFieldNames()
    {
        final String errors[] =
        {
            "clashing_structure_field_names_error.zs:5:11:    Conflicting symbol defined here!",
            "clashing_structure_field_names_error.zs:6:11: " +
                    "Symbol 'Field1' differs only in a case of its first letter!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingStructureFunctionNames()
    {
        final String errors[] =
        {
            "clashing_structure_function_names_error.zs:5:20:    Conflicting symbol defined here!",
            "clashing_structure_function_names_error.zs:10:20: " +
                    "Symbol 'Func1' differs only in a case of its first letter!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void clashingUnionFieldNames()
    {
        final String errors[] =
        {
            "clashing_union_field_names_error.zs:5:11:    Conflicting symbol defined here!",
            "clashing_union_field_names_error.zs:6:11: " +
                    "Symbol 'Field1' differs only in a case of its first letter!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    private static ZserioErrors zserioErrors;
}
