package array_types_warning;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class ArrayTypesWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void packedArrayChoiceHasNoPackableField()
    {
        final String warning = "packed_array_choice_has_no_packable_field.zs:40:12: " +
                "'ChoiceWithoutPackableField' doesn't contain any packable field!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayStructHasNoPackableField()
    {
        final String warning = "packed_array_struct_has_no_packable_field.zs:48:12: " +
                "'StructWithoutPackable' doesn't contain any packable field!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayTemplateHasNoPackableField()
    {
        final String warnings1[] =
        {
            "packed_array_template_has_no_packable_field.zs:22:13: "
                    + "In instantiation of 'Template' required from here",
            "packed_array_template_has_no_packable_field.zs:5:12: 'string' is not packable element type!"
        };
        assertTrue(zserioWarnings.isPresent(warnings1));

        final String warnings2[] =
        {
            "packed_array_template_has_no_packable_field.zs:24:13: " +
                    "In instantiation of 'Template' required from here",
            "packed_array_template_has_no_packable_field.zs:5:12: " +
                    "'Unpackable' doesn't contain any packable field!"
        };
        assertTrue(zserioWarnings.isPresent(warnings2));
    }

    @Test
    public void packedArrayUnionHasNoPackableField()
    {
        final String warning = "packed_array_union_has_no_packable_field.zs:25:12: " +
                "'UnionWithoutPackableField' doesn't contain any packable field!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableBoolElement()
    {
        final String warning =
                "packed_array_unpackable_bool_element.zs:23:12: 'bool' is not packable element type!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableExternElement()
    {
        final String warning =
                "packed_array_unpackable_extern_element.zs:6:12: 'extern' is not packable element type!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableFloatElement()
    {
        final String warning =
                "packed_array_unpackable_float_element.zs:6:12: 'float64' is not packable element type!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableStringElement()
    {
        final String warning =
                "packed_array_unpackable_string_element.zs:6:12: 'string' is not packable element type!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
