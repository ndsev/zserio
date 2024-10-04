package array_types_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class ArrayTypesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
    }

    @Test
    public void arrayLengthNotAvailable()
    {
        final String error = "array_length_field_not_available_error.zs:7:18: "
                + "Unresolved symbol 'array2Size' within expression scope!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void arrayLengthFieldUsedAsIndexedOffset()
    {
        final String errors[] = {
                "array_length_field_used_as_indexed_offset_error.zs:5:12:     Field 'offsets' defined here!",
                "array_length_field_used_as_indexed_offset_error.zs:16:1: "
                        + "    Field 'offsets' used as an offset here!",
                "array_length_field_used_as_indexed_offset_error.zs:10:18: "
                        + "Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void arrayLengthFieldUsedAsOffset()
    {
        final String errors[] = {
                "array_length_field_used_as_offset_error.zs:5:12:     Field 'offset' defined here!",
                "array_length_field_used_as_offset_error.zs:6:1:     Field 'offset' used as an offset here!",
                "array_length_field_used_as_offset_error.zs:7:19: "
                        + "Fields used as offsets cannot be used in expressions!",
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void deprecatedImplicitArray()
    {
        final String errors[] = {
                "deprecated_implicit_array_error.zs:5:5: For strong compatibility reason, please consider "
                        + "to use command line option '-allowImplicitArrays'.",
                "deprecated_implicit_array_error.zs:5:5: "
                        + "Implicit arrays are deprecated and will be removed from the language!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void implicitArrayBitfieldWithWrongLength()
    {
        final String error = "implicit_array_bitfield_with_wrong_length_error.zs:5:14: "
                + "Implicit arrays are allowed only for types which have fixed size rounded to bytes!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayBool()
    {
        final String error = "implicit_array_bool_error.zs:5:14: "
                + "Implicit arrays are allowed only for types which have fixed size rounded to bytes!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayComplexWithFieldBehind()
    {
        final String errors[] = {
                "implicit_array_complex_with_field_behind_error.zs:6:21:     implicit array is used here",
                "implicit_array_complex_with_field_behind_error.zs:12:24:     implicit array is used here",
                "implicit_array_complex_with_field_behind_error.zs:19:37:     implicit array is used here",
                "implicit_array_complex_with_field_behind_error.zs:41:40:     implicit array is used here",
                "implicit_array_complex_with_field_behind_error.zs:44:12: Field 'field' follows an implicit array!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void implicitArrayCompound()
    {
        final String error = "implicit_array_compound_error.zs:10:14: "
                + "Implicit arrays are allowed only for types which have fixed size rounded to bytes!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayDynamicBitfield()
    {
        final String error = "implicit_array_dynamic_bitfield_error.zs:6:14: "
                + "Implicit arrays are allowed only for types which have fixed size rounded to bytes!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayNotLast()
    {
        final String errors[] = {"implicit_array_not_last_error.zs:5:21:     implicit array is used here",
                "implicit_array_not_last_error.zs:6:21: Field 'wrong' follows an implicit array!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void implicitArrayString()
    {
        final String error = "implicit_array_string_error.zs:5:14: "
                + "Implicit arrays are allowed only for types which have fixed size rounded to bytes!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayStructNotLast()
    {
        final String errors[] = {
                "implicit_array_struct_not_last_error.zs:6:20:     implicit array is used here",
                "implicit_array_struct_not_last_error.zs:11:24:     implicit array is used here",
                "implicit_array_struct_not_last_error.zs:12:12: Field 'anotherField' follows an implicit array!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void implicitArrayVarint()
    {
        final String error = "implicit_array_varint_error.zs:5:14: "
                + "Implicit arrays are allowed only for types which have fixed size rounded to bytes!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayWithAutoArrayBehind()
    {
        final String errors[] = {
                "implicit_array_with_auto_array_behind_error.zs:5:21:     implicit array is used here",
                "implicit_array_with_auto_array_behind_error.zs:10:24:     implicit array is used here",
                "implicit_array_with_auto_array_behind_error.zs:11:12: Field 'array' follows an implicit array!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void implicitArrayWithAutoOptionalBehind()
    {
        final String errors[] = {
                "implicit_array_with_auto_optional_behind_error.zs:5:21:     implicit array is used here",
                "implicit_array_with_auto_optional_behind_error.zs:10:24:     implicit array is used here",
                "implicit_array_with_auto_optional_behind_error.zs:11:21: "
                        + "Field 'autoOptional' follows an implicit array!"};
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void implicitArrayWithLength()
    {
        final String error = "implicit_array_with_length_error.zs:6:27: Length expression is not allowed "
                + "for implicit arrays!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayWithIndexedOffsets()
    {
        final String error = "implicit_array_with_indexed_offsets_error.zs:14:9: "
                + "Implicit arrays cannot have indexed offsets!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void implicitArrayWithPackedArrayBehind()
    {
        final String errors[] = {
                "implicit_array_with_packed_array_behind_error.zs:5:21:     implicit array is used here",
                "implicit_array_with_packed_array_behind_error.zs:24:24:     implicit array is used here",
                "implicit_array_with_packed_array_behind_error.zs:28:26: "
                        + "Field 'packedArray3' follows an implicit array!"};
        assertTrue(zserioErrors.isPresent(errors));
    };

    @Test
    public void nonEmptyArrayRecursion()
    {
        final String error = "non_empty_array_recursion_error.zs:9:33: "
                + "Field 'array3' is recursive and neither optional nor array which can be empty!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void packedImplicitArray()
    {
        final String error = "packed_implicit_array_error.zs:5:12: Implicit arrays cannot be packed!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongArrayLengthType()
    {
        final String error = "wrong_array_length_type_error.zs:6:21: Invalid length expression for array. "
                + "Length must be integer!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrorOutput zserioErrors;
}
