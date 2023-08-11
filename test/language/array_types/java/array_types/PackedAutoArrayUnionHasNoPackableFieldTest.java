package array_types.packed_auto_array_union_has_no_packable_field;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

public class PackedAutoArrayUnionHasNoPackableFieldTest
{
    @Test
    public void writeRead()
    {
        final StructWithPackable array1[] = {
            new StructWithPackable("A", 65),
            new StructWithPackable("B", 66),
            new StructWithPackable("C", 67),
        };

        final StructWithPackableArray array2[] = {
            new StructWithPackableArray("ABC", new int[] { 65, 66, 67 }),
            new StructWithPackableArray("DEF", new int[] { 68, 69, 70 }),
            new StructWithPackableArray("GHI", new int[] { 71, 72, 73 }),
        };

        final UnionWithoutPackableField array3[] = new UnionWithoutPackableField[] {
            new UnionWithoutPackableField(), new UnionWithoutPackableField(), new UnionWithoutPackableField()
        };
        array3[0].setField1(4.0f);
        array3[1].setField1(1.0f);
        array3[2].setField1(0.0f);

        final PackedAutoArrayUnionHasNoPackableField packedAutoArrayUnionHasNoPackableField =
                new PackedAutoArrayUnionHasNoPackableField(array1, array2, array3);

        SerializeUtil.serializeToFile(packedAutoArrayUnionHasNoPackableField, BLOB_NAME);
        final PackedAutoArrayUnionHasNoPackableField readPackedAutoArrayUnionHasNoPackableField =
                SerializeUtil.deserializeFromFile(PackedAutoArrayUnionHasNoPackableField.class, BLOB_NAME);
        assertEquals(packedAutoArrayUnionHasNoPackableField, readPackedAutoArrayUnionHasNoPackableField);
    }

    private static final String BLOB_NAME = "packed_auto_array_union_has_no_packable_field.blob";
}
