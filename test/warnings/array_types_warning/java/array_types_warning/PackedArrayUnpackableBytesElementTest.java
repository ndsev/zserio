package array_types_warning.packed_array_unpackable_bytes_element;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

public class PackedArrayUnpackableBytesElementTest
{
    @Test
    public void writeRead()
    {
        final PackedArrayUnpackableBytesElement packedArrayUnpackableBytesElement =
                new PackedArrayUnpackableBytesElement(
                        new long[] {10, 11, 12},
                        new byte[][] {{0, 1, 2}, {11, 12, 13}, {100, 101, 102}}
                );

        SerializeUtil.serializeToFile(packedArrayUnpackableBytesElement, BLOB_NAME);
        final PackedArrayUnpackableBytesElement readPackedArrayUnpackableBytesElement =
                SerializeUtil.deserializeFromFile(PackedArrayUnpackableBytesElement.class, BLOB_NAME);
        assertEquals(packedArrayUnpackableBytesElement, readPackedArrayUnpackableBytesElement);
    }

    private static final String BLOB_NAME = "packed_array_unpackable_bytes_element.blob";
}
