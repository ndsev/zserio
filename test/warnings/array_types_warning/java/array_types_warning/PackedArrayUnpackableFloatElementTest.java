package array_types_warning.packed_array_unpackable_float_element;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

public class PackedArrayUnpackableFloatElementTest
{
    @Test
    public void writeRead()
    {
        final PackedArrayUnpackableFloatElement packedArrayUnpackableFloatElement =
                new PackedArrayUnpackableFloatElement(
                        new long[] {10, 11, 12},
                        new double[] {4.0, 1.0, 0.0}
                );

        SerializeUtil.serializeToFile(packedArrayUnpackableFloatElement, BLOB_NAME);
        final PackedArrayUnpackableFloatElement readPackedArrayUnpackableFloatElement =
                SerializeUtil.deserializeFromFile(PackedArrayUnpackableFloatElement.class, BLOB_NAME);
        assertEquals(packedArrayUnpackableFloatElement, readPackedArrayUnpackableFloatElement);
    }

    private static final String BLOB_NAME = "packed_array_unpackable_float_element.blob";
}
