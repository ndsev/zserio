package array_types_warning.packed_array_unpackable_string_element;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

public class PackedArrayUnpackableStringElementTest
{
    @Test
    public void writeRead()
    {
        final PackedArrayUnpackableStringElement packedArrayUnpackableStringElement =
                new PackedArrayUnpackableStringElement(new long[] {10, 11, 12}, new String[] {"A", "B", "C"});

        SerializeUtil.serializeToFile(packedArrayUnpackableStringElement, BLOB_NAME);
        final PackedArrayUnpackableStringElement readPackedArrayUnpackableStringElement =
                SerializeUtil.deserializeFromFile(PackedArrayUnpackableStringElement.class, BLOB_NAME);
        assertEquals(packedArrayUnpackableStringElement, readPackedArrayUnpackableStringElement);
    }

    private static final String BLOB_NAME = "packed_array_unpackable_string_element.blob";
}
