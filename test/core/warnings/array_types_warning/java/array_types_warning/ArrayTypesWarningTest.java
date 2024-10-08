package array_types_warning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        final String warning = "packed_array_choice_has_no_packable_field.zs:40:12: "
                + "Keyword 'packed' doesn't have any effect. "
                + "'ChoiceWithoutPackableField' doesn't contain any packable field.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayStructHasNoPackableField()
    {
        final String warning = "packed_array_struct_has_no_packable_field.zs:48:12: "
                + "Keyword 'packed' doesn't have any effect. "
                + "'StructWithoutPackable' doesn't contain any packable field.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayTemplateHasNoPackableField()
    {
        final String warnings1[] = {"packed_array_template_has_no_packable_field.zs:22:13: "
                        + "    In instantiation of 'Template' required from here",
                "packed_array_template_has_no_packable_field.zs:5:12: "
                        + "Keyword 'packed' doesn't have any effect. 'string' is not packable element type."};
        assertTrue(zserioWarnings.isPresent(warnings1));

        final String warnings2[] = {"packed_array_template_has_no_packable_field.zs:24:13: "
                        + "    In instantiation of 'Template' required from here",
                "packed_array_template_has_no_packable_field.zs:5:12: "
                        +
                        "Keyword 'packed' doesn't have any effect. 'Unpackable' doesn't contain any packable field."};
        assertTrue(zserioWarnings.isPresent(warnings2));
    }

    @Test
    public void packedArrayUnionHasNoPackableField()
    {
        final String warning = "packed_array_union_has_no_packable_field.zs:25:12: "
                + "Union 'UnionWithoutPackableField' doesn't contain any packable field.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableBoolElement()
    {
        final String warning = "packed_array_unpackable_bool_element.zs:23:12: "
                + "Keyword 'packed' doesn't have any effect. 'bool' is not packable element type.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableBytesElement()
    {
        final String warning = "packed_array_unpackable_bytes_element.zs:6:12: "
                + "Keyword 'packed' doesn't have any effect. 'bytes' is not packable element type.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableExternElement()
    {
        final String warning = "packed_array_unpackable_extern_element.zs:6:12: "
                + "Keyword 'packed' doesn't have any effect. 'extern' is not packable element type.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableFloatElement()
    {
        final String warning = "packed_array_unpackable_float_element.zs:6:12: "
                + "Keyword 'packed' doesn't have any effect. 'float64' is not packable element type.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packedArrayUnpackableStringElement()
    {
        final String warning = "packed_array_unpackable_string_element.zs:6:12: "
                + "Keyword 'packed' doesn't have any effect. 'string' is not packable element type.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
