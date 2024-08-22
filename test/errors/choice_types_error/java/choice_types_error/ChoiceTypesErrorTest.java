package choice_types_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class ChoiceTypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void bitmaskCaseError()
    {
        final String error = "bitmask_case_error.zs:27:10: "
                + "Choice 'BitmaskParamChoice' has case with different bitmask type than selector!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void boolCaseError()
    {
        final String error = "bool_case_error.zs:8:10: Choice 'BoolCaseChoice' has incompatible case type!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void compoundSelectorError()
    {
        final String error = "compound_selector_error.zs:8:8: "
                + "Choice 'CompoundSelectorChoice' uses forbidden COMPOUND selector!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void enumCaseError()
    {
        final String error = "enum_case_error.zs:27:10: "
                + "Choice 'EnumParamChoice' has case with different enumeration type than selector!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldCaseError()
    {
        final String error = "field_case_error.zs:23:10: Unresolved symbol 'b' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void floatSelectorError()
    {
        final String error = "float_selector_error.zs:3:8: "
                + "Choice 'FloatSelectorChoice' uses forbidden FLOAT selector!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void multipleCases()
    {
        final String error = "multiple_cases_error.zs:11:10: Choice 'MultipleCasesChoice' has duplicated case!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void multipleCasesWithExpression()
    {
        final String error = "multiple_cases_with_expression_error.zs:11:10: "
                + "Choice 'MultipleCasesChoice' has duplicated case!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void multipleCasesOnEnum()
    {
        final String error = "multiple_cases_on_enum_error.zs:18:10: "
                + "Choice 'MultipleCasesChoice' has duplicated case!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void multipleDefaults()
    {
        final String errors[] = {
                "multiple_defaults_error.zs:14:5: mismatched input 'default' expecting {", // ...
                "multiple_defaults_error.zs:14:5: 'default' is a reserved keyword!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void nonConstantCase()
    {
        final String error =
                "non_constant_case_error.zs:23:10: Choice 'IntChoice' has non-constant case expression!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void optionalMember()
    {
        final String errors[] = {"optional_member_error.zs:6:29: mismatched input 'if' expecting {", // ...
                "optional_member_error.zs:6:29: 'if' is a reserved keyword!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void recursive()
    {
        final String error = "recursive_error.zs:12:28: "
                + "Field 'recursiveValue' is recursive and neither optional nor array which can be empty!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void selectorExpressionUsesIndexedOffsetField()
    {
        final String errors[] = {
                "selector_expression_uses_indexed_offset_field_error.zs:5:12:     Field 'offsets' defined here!",
                "selector_expression_uses_indexed_offset_field_error.zs:19:1: "
                        + "    Field 'offsets' used as an offset here!",
                "selector_expression_uses_indexed_offset_field_error.zs:8:43: "
                        + "Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void selectorExpressionUsesOffsetField()
    {
        final String errors[] = {
                "selector_expression_uses_offset_field_error.zs:5:12:     Field 'offset' defined here!",
                "selector_expression_uses_offset_field_error.zs:19:1: "
                        + "    Field 'offset' used as an offset here!",
                "selector_expression_uses_offset_field_error.zs:8:43: "
                        + "Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void stringSelectorError()
    {
        final String error = "string_selector_error.zs:3:8: "
                + "Choice 'StringSelectorChoice' uses forbidden STRING selector!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unreachableDefault()
    {
        final String error = "unreachable_default_error.zs:11:5: Choice 'UnreachableDefaultChoice' has "
                + "unreachable default case!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unreachableDefaultMulticaseError()
    {
        final String error = "unreachable_default_multicase_error.zs:9:5: "
                + "Choice 'UnreachableDefaultMulticaseChoice' has unreachable default case!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unresolvedEnumItem()
    {
        final String error = "unresolved_enum_item_error.zs:19:31: "
                + "Unresolved symbol 'RED' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unresolvedFieldInConstraint()
    {
        final String error = "unresolved_field_in_constraint_error.zs:9:47: "
                + "Unresolved symbol 'uint16Value' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
