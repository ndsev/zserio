package array_types_warning.packed_array_unpackable_extern_element;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class PackedArrayUnpackableExternElementTest
{
    @Test
    public void writeRead()
    {
        final PackedArrayUnpackableExternElement packedArrayUnpackableExternElement =
                new PackedArrayUnpackableExternElement(
                        new long[] {10, 11, 12},
                        new BitBuffer[] {
                                new BitBuffer(new byte[] {(byte)0xff, (byte)0xc0}, 10),
                                new BitBuffer(new byte[] {(byte)0xff, (byte)0x80}, 10),
                                new BitBuffer(new byte[] {(byte)0xff, (byte)0x40}, 10)
                        }
                );

        SerializeUtil.serializeToFile(packedArrayUnpackableExternElement, BLOB_NAME);
        final PackedArrayUnpackableExternElement readPackedArrayUnpackableExternElement =
                SerializeUtil.deserializeFromFile(PackedArrayUnpackableExternElement.class, BLOB_NAME);
        assertEquals(packedArrayUnpackableExternElement, readPackedArrayUnpackableExternElement);
    }

    private static final String BLOB_NAME = "packed_array_unpackable_extern_element.blob";
}
