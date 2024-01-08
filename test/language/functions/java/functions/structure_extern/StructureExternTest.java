package functions.structure_extern;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;

public class StructureExternTest
{
    @Test
    public void getField()
    {
        final TestStructure testStructure = new TestStructure();
        testStructure.setField(FIELD);
        testStructure.setChild(new Child(CHILD_FIELD));
        assertEquals(FIELD, testStructure.funcGetField());
    }

    @Test
    public void getChildField()
    {
        final TestStructure testStructure = new TestStructure();
        testStructure.setField(FIELD);
        testStructure.setChild(new Child(CHILD_FIELD));
        assertEquals(CHILD_FIELD, testStructure.funcGetChildField());
    }

    private static final BitBuffer FIELD = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0}, 11);
    private static final BitBuffer CHILD_FIELD = new BitBuffer(new byte[] {(byte)0xCA, (byte)0xFE}, 15);
}
