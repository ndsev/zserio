package templates_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class TemplatesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void constUsedAsType()
    {
        final String errors[] =
        {
            "constant_used_as_type_error.zs:12:5: In instantiation of 'TestStruct' required from here",
            "constant_used_as_type_error.zs:5:5: Invalid usage of constant 'CONST' as a type!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constraintExpressionsExpectsConstant()
    {
        final String errors[] =
        {
            "constraint_expression_expects_constant_error.zs:10:5: " +
                    "In instantiation of 'TestStruct' required from here",
            "constraint_expression_expects_constant_error.zs:5:27: " +
                    "Unresolved symbol 'uint32' within expression scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void duplicatedFieldName()
    {
        final String errors[] =
        {
            "duplicated_field_name_error.zs:5:7:     First defined here",
            "duplicated_field_name_error.zs:6:7: 'value' is already defined in this scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void enumNotATemplate()
    {
        final String error = "enum_not_a_template_error.zs:11:5: 'Enumeration' is not a template!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void fieldNotAvailableInFunction()
    {
        final String errors[] =
        {
            "field_not_available_in_function_error.zs:21:5: " +
                    "In instantiation of 'TestStruct' required from here",
            "field_not_available_in_function_error.zs:16:5: " +
                    "In instantiation of 'FieldNotAvailable' required from here",
            "field_not_available_in_function_error.zs:5:30: In function 'getField2' called from here",
            "field_not_available_in_function_error.zs:10:16: " +
                    "Unresolved symbol 'field2' within expression scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiateDuplicated()
    {
        final String errors[] =
        {
            "instantiate_duplicated_error.zs:8:13: In instantiation of 'Test' required from here",
            "instantiate_duplicated_error.zs:8:26:     First requested here",
            "instantiate_duplicated_error.zs:9:26: Ambiguous request to instantiate template 'Test'!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiateDuplicatedName()
    {
        final String errors[] =
        {
            "instantiate_duplicated_name_error.zs:8:26:     First defined here",
            "instantiate_duplicated_name_error.zs:9:27: 'Str' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiateDuplicatedViaImport()
    {
        final String errors[] =
        {
            "instantiate_duplicated_via_import_error.zs:5:13: In instantiation of 'Test' required from here",
            "instantiate_duplicated_via_import_error.zs:5:26:     First requested here",
            "instantiate_duplicated_via_import_error/pkg.zs:8:26: " +
                    "Ambiguous request to instantiate template 'Test'!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiateMissingTemplateArguments()
    {
        final String error = "instantiate_missing_template_arguments_error.zs:8:13: " +
                "Missing template arguments for template 'Test'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void instantiateNameClash()
    {
        final String errors[] =
        {
            "instantiate_name_clash_error.zs:13:26:     First defined here",
            "instantiate_name_clash_error.zs:14:27: 'U32' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiateNameClashWithTemplate()
    {
        final String errors[] =
        {
            "instantiate_name_clash_with_template_error.zs:10:5: In instantiation of 'Test' required from here",
            "instantiate_name_clash_with_template_error.zs:13:26:     First defined here",
            "instantiate_name_clash_with_template_error.zs:3:8: " +
                    "'Test_uint32' is already defined in package 'instantiate_name_clash_with_template_error'!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiateNameClashWithType()
    {
        final String errors[] =
        {
            "instantiate_name_clash_with_type_error.zs:3:8:     First defined here",
            "instantiate_name_clash_with_type_error.zs:13:26: 'Other' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiateNoTemplate()
    {
        final String error = "instantiate_no_template_error.zs:3:8: 'Test' is not a template!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void instantiationNameClashAcrossPackages()
    {
        final String errors[] =
        {
            "pkg2.zs:12:5: In instantiation of 'TestStruct' required from here",
            "pkg1.zs:12:5:     First instantiated here",
            "test_struct.zs:3:8: Instantiation name 'TestStruct_Test' already exits!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiationNameClash()
    {
        final String errors[] =
        {
            "instantiation_name_clash_error.zs:38:5: In instantiation of 'TestStruct' required from here",
            "instantiation_name_clash_error.zs:33:5: In instantiation of 'Template' required from here",
            "instantiation_name_clash_error.zs:32:5:     First instantiated here",
            "instantiation_name_clash_error.zs:38:5:     Required in instantiation of 'TestStruct' from here",
            "instantiation_name_clash_error.zs:23:8: Instantiation name 'Template_A_B_C' already exits!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void instantiationNameClashOtherTemplate()
    {
        final String errors[] =
        {
            "instantiation_name_clash_other_template_error.zs:21:5: " +
                    "In instantiation of 'Test' required from here",
            "instantiation_name_clash_other_template_error.zs:20:5: " +
                    "    First seen in instantiation of 'Test_A' from here",
            "instantiation_name_clash_other_template_error.zs:8:8: " +
                    "Instantiation name 'Test_A_uint32' already exits!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    };

    @Test
    public void instantiationNameClashWithType()
    {
        final String errors[] =
        {
            "instantiation_name_clash_with_type_error.zs:15:5: In instantiation of 'Test' required from here",
            "instantiation_name_clash_with_type_error.zs:3:8:     First defined here",
            "instantiation_name_clash_with_type_error.zs:8:8: " +
                    "'Test_uint32' is already defined in package 'instantiation_name_clash_with_type_error'!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    };

    @Test
    public void instantiationViaSubtype()
    {
        final String errors[] =
        {
            "instantiation_via_subtype_error.zs:13:9: In instantiation of 'TestStructure' required from here",
            "instantiation_via_subtype_error.zs:9:16: Unexpected dot expression 'field'!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void missingTemplateArguments()
    {
        final String error = "missing_template_arguments_error.zs:10:5: " +
                "Missing template arguments for template 'TestStruct'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void missingTypeParameters()
    {
        final String errors[] =
        {
            "missing_type_parameters_error.zs:15:5: In instantiation of 'TestStruct' required from here",
            "missing_type_parameters_error.zs:5:5: " +
                    "Referenced type 'Parameterized' is defined as parameterized type!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void parameterizedBuiltinType()
    {
        final String errors[] =
        {
            "parameterized_builtin_type_error.zs:11:5: In instantiation of 'TestStruct' required from here",
            "parameterized_builtin_type_error.zs:6:5: Referenced type 'uint32' is not a parameterized type!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void parameterizedCompoundType()
    {
        final String errors[] =
        {
            "parameterized_compound_type_error.zs:16:5: In instantiation of 'TestStruct' required from here",
            "parameterized_compound_type_error.zs:11:5: " +
                    "Referenced type 'Compound' is not a parameterized type!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void symbolWithTemplateParameterClash()
    {
        final String errors[] =
        {
            "symbol_with_template_parameter_clash_error.zs:3:19:     First defined here",
            "symbol_with_template_parameter_clash_error.zs:5:12: 'T' is already defined in this scope!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void symbolWithTypeNameClash()
    {
        final String errors[] =
        {
            "symbol_with_type_name_clash_error.zs:15:5: In instantiation of 'TestStruct' required from here",
            "symbol_with_type_name_clash_error.zs:5:7: 'Field' is a defined type in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void templatableNotATemplate()
    {
        final String error = "templatable_not_a_template_error.zs:10:5: 'Templatable' is not a template!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void templatedTemplateParameter()
    {
        final String error = "templated_template_parameter_error.zs:5:5: " +
                "Template parameter cannot be used as a template!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unresolvedReferenceInTemplate()
    {
        final String errors[] =
        {
            "unresolved_reference_in_template_error.zs:10:5: " +
                    "In instantiation of 'TestStruct' required from here",
            "unresolved_reference_in_template_error.zs:5:5: Unresolved referenced type 'Unresolved'!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void unresolvedTemplateInstantiation()
    {
        final String error = "unresolved_template_instantiation_error.zs:5:5: " +
                "Unresolved referenced type 'TemplatedStruct'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unresolvedTemplateInstantiationInTemplate()
    {
        final String errors[] =
        {
            "unresolved_template_instantiation_in_template_error.zs:15:5: " +
                    "In instantiation of 'TestStruct' required from here",
            "unresolved_template_instantiation_in_template_error.zs:10:5: " +
                    "In instantiation of 'InnerStruct' required from here",
            "unresolved_template_instantiation_in_template_error.zs:5:5: " +
                    "Unresolved referenced type 'Unresolved'!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void wrongNumberOfArguments()
    {
        final String error = "wrong_number_of_arguments_error.zs:11:5: " +
                "Wrong number of template arguments for template 'TestStruct'! Expecting 2, got 1!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
