package array_types_warning.packed_array_template_has_no_packable_field;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

public class PackedArrayTemplateHasNoPackableFieldTest
{
    @Test
    public void writeReadU32()
    {
        final T_u32 u32 = new T_u32(new long[] {0, 1, 2, 3, 4, 5});

        final File file = new File(BLOB_NAME_BASE + "_u32.blob");
        SerializeUtil.serializeToFile(u32, file);
        final T_u32 readU32 = SerializeUtil.deserializeFromFile(T_u32.class, file);
        assertEquals(u32, readU32);
    }

    @Test
    public void writeReadStr()
    {
        final T_str str = new T_str(new String[] {"A", "B", "C", "D", "E", "F"});

        final File file = new File(BLOB_NAME_BASE + "_str.blob");
        SerializeUtil.serializeToFile(str, file);
        final T_str readStr = SerializeUtil.deserializeFromFile(T_str.class, file);
        assertEquals(str, readStr);
    }

    @Test
    public void writeReadPackable()
    {
        final T_packable packable = new T_packable(new Packable[] {
                new Packable(0, 4.0, "A"),
                new Packable(1, 1.0, "B"),
                new Packable(2, 0.0, "C"),
        });

        final File file = new File(BLOB_NAME_BASE + "_packable.blob");
        SerializeUtil.serializeToFile(packable, file);
        final T_packable readPackable = SerializeUtil.deserializeFromFile(T_packable.class, file);
        assertEquals(packable, readPackable);
    }

    @Test
    public void writeReadUnpackable()
    {
        final T_unpackable unpackable = new T_unpackable(new Unpackable[] {
                new Unpackable(4.0, "A"),
                new Unpackable(1.0, "B"),
                new Unpackable(0.0, "C"),
        });

        final File file = new File(BLOB_NAME_BASE + "_unpackable.blob");
        SerializeUtil.serializeToFile(unpackable, file);
        final T_unpackable readUnpackable = SerializeUtil.deserializeFromFile(T_unpackable.class, file);
        assertEquals(unpackable, readUnpackable);
    }

    private static final String BLOB_NAME_BASE = "packed_array_template_has_no_packable_field";
}
