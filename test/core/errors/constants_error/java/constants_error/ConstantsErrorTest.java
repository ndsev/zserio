package constants_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class ConstantsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void choiceTypeConstant()
    {
        final String error = "choice_type_constant_error.zs:11:17: "
                + "Constants can be defined only for built-in types, enums or bitmasks!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constant0x8000()
    {
        final String error = "constant_0x8000_error.zs:3:30: Initializer value '32768' of "
                + "'WRONG_CONSTANT' exceeds the bounds of its type 'int16'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constant0xffff()
    {
        final String error = "constant_0xffff_error.zs:3:30: Initializer value '65535' of "
                + "'WRONG_CONSTANT' exceeds the bounds of its type 'int16'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinitionUsingConstant()
    {
        final String error = "cyclic_definition_using_constant_error.zs:7:25: "
                + "Cyclic dependency detected in expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void cyclicDefinitionUsingEnumValue()
    {
        final String error = "cyclic_definition_using_enum_value_error.zs:12:37: "
                + "Cyclic dependency detected in expression evaluation!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void invalidConstantName()
    {
        final String errors[] = {
                "invalid_constant_name_error.zs:4:13: mismatched input 'align' expecting {", // ...
                "invalid_constant_name_error.zs:4:13: 'align' is a reserved keyword!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureTypeConstant()
    {
        final String error = "structure_type_constant_error.zs:8:17: Constants can be defined only for "
                + "built-in types, enums or bitmasks!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypedCompoundTypeConstant()
    {
        final String error = "subtyped_compound_type_constant_error.zs:12:23: Constants can be defined only "
                + "for built-in types, enums or bitmasks!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongBoolConstant()
    {
        final String error = "wrong_bool_constant_error.zs:3:34: Wrong type of value expression "
                + "(integer cannot be assigned to bool)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongDecimalConstant()
    {
        final String error = "wrong_decimal_constant_error.zs:3:38: Wrong type of value expression "
                + "(boolean cannot be assigned to int32)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongFloatConstant()
    {
        final String error = "wrong_float_constant_error.zs:3:38: Wrong type of value expression "
                + "(string cannot be assigned to float32)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongStringConstant()
    {
        final String error = "wrong_string_constant_error.zs:3:38: Wrong type of value expression "
                + "(boolean cannot be assigned to string)!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
