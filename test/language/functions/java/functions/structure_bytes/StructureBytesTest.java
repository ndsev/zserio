package functions.structure_bytes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureBytesTest
{
    @Test
    public void getField()
    {
        final TestStructure testStructure = new TestStructure();
        testStructure.setField(FIELD);
        testStructure.setChild(new Child(CHILD_FIELD));
        assertArrayEquals(FIELD, testStructure.funcGetField());
    }

    @Test
    public void getChildField()
    {
        final TestStructure testStructure = new TestStructure();
        testStructure.setField(FIELD);
        testStructure.setChild(new Child(CHILD_FIELD));
        assertArrayEquals(CHILD_FIELD, testStructure.funcGetChildField());
    }

    private static final byte[] FIELD = new byte[]{(byte)0xAB, (byte)0xE0};
    private static final byte[] CHILD_FIELD = new byte[]{(byte)0xCA, (byte)0xFE};
}
