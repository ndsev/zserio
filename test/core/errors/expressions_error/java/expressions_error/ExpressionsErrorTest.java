package expressions_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class ExpressionsErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void bitmaskShiftOperator()
    {
        final String error = "bitmask_shift_operator_error.zs:12:17: Integer expressions expected!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void nonConstantStringConcatenation()
    {
        final String error =
                "non_constant_string_concatenation_error.zs:9:16: Constant string expressions expected!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void stringIntegerConcatenation()
    {
        final String error =
                "string_integer_concatenation_error.zs:9:16: Integer or float expressions expected!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void ternaryIncompatibleBitmaskTypesInArgument()
    {
        final String error = "ternary_incompatible_bitmask_types_in_argument_error.zs:26:19: "
                + "Incompatible expression types ('Bitmask1' != 'Bitmask2')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void ternaryIncompatibleBitmaskTypesInConstraint()
    {
        final String error = "ternary_incompatible_bitmask_types_in_constraint_error.zs:20:50: "
                + "Incompatible expression types ('Bitmask1' != 'Bitmask2')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void ternaryIncompatibleCompoundTypesInArgument()
    {
        final String error = "ternary_incompatible_compound_types_in_argument_error.zs:23:19: "
                + "Incompatible expression types ('Param1' != 'Param2')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void ternaryIncompatibleCompoundTypesInConstraint()
    {
        final String error = "ternary_incompatible_compound_types_in_constraint_error.zs:18:42: "
                + "Incompatible expression types ('Compound1' != 'Compound2')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void ternaryIncompatibleEnumTypesInArgument()
    {
        final String error = "ternary_incompatible_enum_types_in_argument_error.zs:26:19: "
                + "Incompatible expression types ('Enum1' != 'Enum2')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void ternaryIncompatibleEnumTypesInOptional()
    {
        final String error = "ternary_incompatible_enum_types_in_optional_error.zs:20:24: "
                + "Incompatible expression types ('Enum1' != 'Enum2')!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongFullType()
    {
        final String error = "wrong_full_type_error.zs:7:33: "
                + "Unresolved symbol 'wrong_full_type_error.someStructure' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongLengthOfSyntax()
    {
        final String error = "wrong_lengthof_syntax_error.zs:9:25: missing '(' at 'fixedArray'";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
