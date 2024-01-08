package array_types_warning.packed_array_choice_has_no_packable_field;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

public class PackedArrayChoiceHasNoPackableFieldTest
{
    @Test
    public void writeRead()
    {
        final StructWithPackable array1[] = {
                new StructWithPackable("A", 65),
                new StructWithPackable("B", 66),
                new StructWithPackable("C", 67),
        };

        final UnionWithPackableField array2[] = new UnionWithPackableField[] {
                new UnionWithPackableField(), new UnionWithPackableField(), new UnionWithPackableField()};
        array2[0].setField2(TestEnum.ONE);
        array2[1].setField2(TestEnum.TWO);
        array2[2].setField2(TestEnum.ONE);

        final StructWithPackableArray array3[] = {
                new StructWithPackableArray("ABC", new int[] {65, 66, 67}),
                new StructWithPackableArray("DEF", new int[] {68, 69, 70}),
                new StructWithPackableArray("GHI", new int[] {71, 72, 73}),
        };

        final ChoiceWithoutPackableField array4[] = {new ChoiceWithoutPackableField(true),
                new ChoiceWithoutPackableField(true), new ChoiceWithoutPackableField(true)};
        array4[0].setField1(4.0f);
        array4[1].setField1(1.0f);
        array4[2].setField1(0.0f);

        final PackedArrayChoiceHasNoPackableField packedArrayChoiceHasNoPackableField =
                new PackedArrayChoiceHasNoPackableField(array1, array2, array3, array4);

        SerializeUtil.serializeToFile(packedArrayChoiceHasNoPackableField, BLOB_NAME);
        final PackedArrayChoiceHasNoPackableField readPackedArrayChoiceHasNoPackableField =
                SerializeUtil.deserializeFromFile(PackedArrayChoiceHasNoPackableField.class, BLOB_NAME);
        assertEquals(packedArrayChoiceHasNoPackableField, readPackedArrayChoiceHasNoPackableField);
    }

    private static final String BLOB_NAME = "packed_array_choice_has_no_packable_field.blob";
}
