package enumeration_types_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class EnumerationTypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void boolEnumValue()
    {
        final String error =
                "bool_enum_value_error.zs:7:18: Enumeration item 'DARK_BLUE' has non-integer value!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinition()
    {
        final String error =
                "cyclic_definition_error.zs:6:26: Unresolved symbol 'DARK_RED' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinitionUsingConstant()
    {
        final String error = "cyclic_definition_using_constant_error.zs:8:13: Cyclic dependency detected in "
                + "expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinitionUsingEnumValue()
    {
        final String error = "cyclic_definition_using_enum_value_error.zs:7:19: Cyclic dependency detected "
                + "in expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void duplicatedEnumItem()
    {
        final String errors[] = {"duplicated_enum_item_error.zs:6:5:     First defined here",
                "duplicated_enum_item_error.zs:7:5: 'DARK_RED' is already defined in this scope!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void duplicatedEnumValue()
    {
        final String error =
                "duplicated_enum_value_error.zs:7:18: Enumeration item 'DARK_BLUE' has duplicated value (1)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void enumTypeValue()
    {
        final String error =
                "enum_type_value_error.zs:7:18: Enumeration item 'DARK_BLUE' has non-integer value!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void notAvailableEnumItem()
    {
        final String error = "not_available_enum_item_error.zs:6:26: Unresolved symbol 'DARK_BLACK' within "
                + "expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void outOfRangeEnumExplicitValue()
    {
        final String error = "out_of_range_enum_explicit_value_error.zs:7:18: "
                + "Enumeration item 'DARK_BLUE' has value (256) out of range <0,255>!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void outOfRangeEnumValue()
    {
        final String error = "out_of_range_enum_value_error.zs:8:5: "
                + "Enumeration item 'DARK_BLACK' has value (256) out of range <0,255>!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void stringEnumError()
    {
        final String error =
                "string_enum_error.zs:3:13: Enumeration 'WrongStringEnum' has forbidden type string!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
