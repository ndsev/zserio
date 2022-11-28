package structure_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import structure_types.simple_structure.SimpleStructure;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class SimpleStructureTest
{
    @Test
    public void emptyConstructor()
    {
        final SimpleStructure simpleStructure = new SimpleStructure();
        assertEquals(0, simpleStructure.getNumberA());
        assertEquals(0, simpleStructure.getNumberB());
        assertEquals(0, simpleStructure.getNumberC());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final byte numberA = 0x00;
        final short numberB = 0x00;
        final byte numberC = 0x00;
        final BitBuffer bitBuffer = writeSimpleStructureToBitBuffer(numberA, numberB, numberC);

        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final SimpleStructure simpleStructure = new SimpleStructure(reader);
        assertEquals(numberA, simpleStructure.getNumberA());
        assertEquals(numberB, simpleStructure.getNumberB());
        assertEquals(numberC, simpleStructure.getNumberC());
    }

    @Test
    public void fieldConstructor() throws IOException, ZserioError
    {
        final byte numberA = 0x01;
        final short numberB = 0xAA;
        final byte numberC = 0x55;
        final SimpleStructure simpleStructure = new SimpleStructure(numberA, numberB, numberC);
        assertEquals(numberA, simpleStructure.getNumberA());
        assertEquals(numberB, simpleStructure.getNumberB());
        assertEquals(numberC, simpleStructure.getNumberC());
    }

    @Test
    public void bitSizeOf()
    {
        final byte numberA = 0x00;
        final short numberB = 0x01;
        final byte numberC = 0x02;
        final SimpleStructure simpleStructure = new SimpleStructure(numberA, numberB, numberC);
        assertEquals(SIMPLE_STRUCTURE_BIT_SIZE, simpleStructure.bitSizeOf());
    }

    @Test
    public void getSetNumberA()
    {
        final SimpleStructure simpleStructure = new SimpleStructure();
        final byte numberA = 0x02;
        simpleStructure.setNumberA(numberA);
        assertEquals(numberA, simpleStructure.getNumberA());
    }

    @Test
    public void getSetNumberB()
    {
        final SimpleStructure simpleStructure = new SimpleStructure();
        final short numberB = 0x23;
        simpleStructure.setNumberB(numberB);
        assertEquals(numberB, simpleStructure.getNumberB());
    }

    @Test
    public void getSetNumberC()
    {
        final SimpleStructure simpleStructure = new SimpleStructure();
        final byte numberC = 0x11;
        simpleStructure.setNumberC(numberC);
        assertEquals(numberC, simpleStructure.getNumberC());
    }

    @Test
    public void equals()
    {
        final SimpleStructure simpleStructure1 = new SimpleStructure();
        final SimpleStructure simpleStructure2 = new SimpleStructure();
        assertTrue(simpleStructure1.equals(simpleStructure2));

        final byte numberA = 0x03;
        final short numberB = 0xDE;
        final byte numberC = 0x55;
        simpleStructure1.setNumberA(numberA);
        simpleStructure1.setNumberB(numberB);
        simpleStructure1.setNumberC(numberC);
        simpleStructure2.setNumberA(numberA);
        simpleStructure2.setNumberB((short)(numberB + 1));
        simpleStructure2.setNumberC(numberC);
        assertFalse(simpleStructure1.equals(simpleStructure2));

        simpleStructure2.setNumberB(numberB);
        assertTrue(simpleStructure1.equals(simpleStructure2));
    }

    @Test
    public void hashCodeMethod()
    {
        final SimpleStructure simpleStructure1 = new SimpleStructure();
        final SimpleStructure simpleStructure2 = new SimpleStructure();
        assertEquals(simpleStructure1.hashCode(), simpleStructure2.hashCode());

        final byte numberA = 0x04;
        final short numberB = 0xCD;
        final byte numberC = 0x57;
        simpleStructure1.setNumberA(numberA);
        simpleStructure1.setNumberB(numberB);
        simpleStructure1.setNumberC(numberC);
        simpleStructure2.setNumberA(numberA);
        simpleStructure2.setNumberB((short)(numberB + 1));
        simpleStructure2.setNumberC(numberC);
        assertTrue(simpleStructure1.hashCode() != simpleStructure2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(1178167, simpleStructure1.hashCode());
        assertEquals(1178204, simpleStructure2.hashCode());

        simpleStructure2.setNumberB(numberB);
        assertEquals(simpleStructure1.hashCode(), simpleStructure2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final byte numberA = 0x05;
        final short numberB = 0x10;
        final byte numberC = 0x44;
        final SimpleStructure simpleStructure = new SimpleStructure(numberA, numberB, numberC);
        final int bitPosition = 1;
        assertEquals(SIMPLE_STRUCTURE_BIT_SIZE + bitPosition, simpleStructure.initializeOffsets(bitPosition));
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final byte numberA = 0x07;
        final short numberB = 0x22;
        final byte numberC = 0x33;
        final SimpleStructure simpleStructure = new SimpleStructure(numberA, numberB, numberC);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        simpleStructure.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final SimpleStructure readsimpleStructure = new SimpleStructure(reader);
        assertEquals(numberA, readsimpleStructure.getNumberA());
        assertEquals(numberB, readsimpleStructure.getNumberB());
        assertEquals(numberC, readsimpleStructure.getNumberC());
        assertTrue(simpleStructure.equals(readsimpleStructure));
    }

    private BitBuffer writeSimpleStructureToBitBuffer(byte numberA, short numberB, byte numberC)
            throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(numberA, 3);
            writer.writeBits(numberB, 8);
            writer.writeBits(numberC, 7);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final int SIMPLE_STRUCTURE_BIT_SIZE = 18;
}
