package array_types_warning.packed_array_unpackable_bool_element;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

public class PackedArrayUnpackableBoolElementTest
{
    @Test
    public void writeRead()
    {
        final PackedArrayUnpackableBoolElement packedArrayUnpackableBoolElement =
                new PackedArrayUnpackableBoolElement(
                        new long[] {0, 1, 2},
                        new TestEnum[] {TestEnum.ONE, TestEnum.TWO, TestEnum.ONE},
                        new TestBitmask[] {
                                TestBitmask.Values.BLACK, TestBitmask.Values.BLACK, TestBitmask.Values.BLACK
                        },
                        new byte[] {(byte)0, (byte)1, (byte)2},
                        (short)5,
                        new long[] {0, -1, -2},
                        new boolean[] {true, false, true}
                );

        SerializeUtil.serializeToFile(packedArrayUnpackableBoolElement, BLOB_NAME);
        final PackedArrayUnpackableBoolElement readPackedArrayUnpackableBoolElement =
                SerializeUtil.deserializeFromFile(PackedArrayUnpackableBoolElement.class, BLOB_NAME);
        assertEquals(packedArrayUnpackableBoolElement, readPackedArrayUnpackableBoolElement);
    }

    private static final String BLOB_NAME = "packed_array_unpackable_bool_element.blob";
}
