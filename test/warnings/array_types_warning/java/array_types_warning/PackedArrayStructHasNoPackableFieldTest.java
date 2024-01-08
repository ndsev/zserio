package array_types_warning.packed_array_struct_has_no_packable_field;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class PackedArrayStructHasNoPackableFieldTest
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

        final StructWithoutPackable array4[] = {
                new StructWithoutPackable(4.0f, new BitBuffer(new byte[] {(byte)0xf0}, 5), 0, "A",
                        new long[] {0, 0, 0}, new boolean[] {true, false, true}),
                new StructWithoutPackable(1.0f, new BitBuffer(new byte[] {(byte)0xe0}, 5), 0, "B",
                        new long[] {0, 0, 0}, new boolean[] {true, false, true}),
                new StructWithoutPackable(0.0f, new BitBuffer(new byte[] {(byte)0xd0}, 5), 0, "C",
                        new long[] {0, 0, 0}, new boolean[] {true, false, true})};

        final EmptyStruct array5[] = {new EmptyStruct(), new EmptyStruct(), new EmptyStruct()};

        final PackedArrayStructHasNoPackableField packedArrayStructHasNoPackableField =
                new PackedArrayStructHasNoPackableField(array1, array2, array3, array4, array5);

        SerializeUtil.serializeToFile(packedArrayStructHasNoPackableField, BLOB_NAME);
        final PackedArrayStructHasNoPackableField readPackedArrayStructHasNoPackableField =
                SerializeUtil.deserializeFromFile(PackedArrayStructHasNoPackableField.class, BLOB_NAME);
        assertEquals(packedArrayStructHasNoPackableField, readPackedArrayStructHasNoPackableField);
    }

    private static final String BLOB_NAME = "packed_array_struct_has_no_packable_field.blob";
}
