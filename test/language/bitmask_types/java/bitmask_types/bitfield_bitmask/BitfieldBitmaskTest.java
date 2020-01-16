package bitmask_types.bitfield_bitmask;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class BitfieldBitmaskTest
{
    @Test
    public void emptyConstructor()
    {
        final Permission permission = new Permission();
        assertEquals(0, permission.getValue());
    }

    @Test
    public void valueConstructor()
    {
        final Permission permission = new Permission(WRITE_VALUE);
        assertTrue(permission.and(Permission.Values.WRITE).equals(Permission.Values.WRITE));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueConstructorUnderLowerBound()
    {
        new Permission((byte)-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueConstructorAboveUpperBound()
    {
        new Permission((byte)(1 << PERMISSION_BITSIZEOF));
    }

    @Test
    public void readConstructor() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(Permission.Values.WRITE.getValue(), PERMISSION_BITSIZEOF);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final Permission readPermission = new Permission(reader);
        assertEquals(Permission.Values.WRITE, readPermission);
    }

    @Test
    public void bitSizeOf()
    {
        assertEquals(PERMISSION_BITSIZEOF, Permission.Values.NONE.bitSizeOf());
        assertEquals(PERMISSION_BITSIZEOF, Permission.Values.NONE.bitSizeOf(1));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;
        assertEquals(bitPosition + PERMISSION_BITSIZEOF, Permission.Values.READ.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        assertTrue(Permission.Values.NONE.equals(Permission.Values.NONE));
        assertTrue(Permission.Values.READ.equals(Permission.Values.READ));
        assertTrue(Permission.Values.WRITE.equals(Permission.Values.WRITE));

        final Permission read = Permission.Values.READ;
        assertTrue(read.equals(Permission.Values.READ));
        assertTrue(Permission.Values.READ.equals(read));
        assertFalse(read.equals(Permission.Values.WRITE));
        assertFalse(Permission.Values.WRITE.equals(read));

        final Permission write = new Permission(WRITE_VALUE);
        assertTrue(write.equals(Permission.Values.WRITE));
        assertTrue(Permission.Values.WRITE.equals(write));
        assertFalse(write.equals(Permission.Values.READ));
        assertFalse(Permission.Values.READ.equals(write));

        assertTrue(read.equals(read));
        assertTrue(write.equals(write));
        assertFalse(read.equals(write));
    }

    @Test
    public void hashCodeMethod()
    {
        final Permission read = Permission.Values.READ;
        final Permission write = Permission.Values.WRITE;
        assertEquals(read.hashCode(), Permission.Values.READ.hashCode());
        assertEquals(read.hashCode(), new Permission(READ_VALUE).hashCode());
        assertEquals(write.hashCode(), Permission.Values.WRITE.hashCode());
        assertEquals(write.hashCode(), new Permission(WRITE_VALUE).hashCode());
        assertFalse(read.hashCode() == write.hashCode());
        assertFalse(read.hashCode() == Permission.Values.NONE.hashCode());
    }

    @Test
    public void toStringMethod()
    {
        assertEquals("0[NONE]", Permission.Values.NONE.toString());
        assertEquals("2[READ]", Permission.Values.READ.toString());
        assertEquals("4[WRITE]", Permission.Values.WRITE.toString());
        assertEquals("6[READ | WRITE]", Permission.Values.READ.or(Permission.Values.WRITE).toString());
        assertEquals("7[READ | WRITE]", new Permission((byte)7).toString());
    }

    @Test
    public void write() throws IOException
    {
        final Permission permission = Permission.Values.READ;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        permission.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final Permission readPermission = new Permission(reader);
        assertEquals(permission, readPermission);
    }

    @Test
    public void getValue()
    {
        assertEquals(NONE_VALUE, Permission.Values.NONE.getValue());
        assertEquals(READ_VALUE, Permission.Values.READ.getValue());
        assertEquals(WRITE_VALUE, Permission.Values.WRITE.getValue());
    }

    @Test
    public void or()
    {
        final Permission read = Permission.Values.READ;
        final Permission write = Permission.Values.WRITE;

        assertEquals(read.or(write), Permission.Values.READ.or(Permission.Values.WRITE));
        assertEquals(read, read.or(Permission.Values.NONE));
        assertEquals(write, Permission.Values.NONE.or(write));
        assertEquals(READ_VALUE | WRITE_VALUE, read.or(write).getValue());
    }

    @Test
    public void and()
    {
        final Permission read = Permission.Values.READ;
        final Permission write = Permission.Values.WRITE;
        final Permission readwrite = Permission.Values.READ.or(Permission.Values.WRITE);

        assertEquals(read, readwrite.and(read));
        assertEquals(write, readwrite.and(write));
        assertEquals(Permission.Values.NONE, readwrite.and(Permission.Values.NONE));
        assertEquals(Permission.Values.NONE, read.and(Permission.Values.NONE));
        assertEquals(Permission.Values.NONE, write.and(Permission.Values.NONE));
        assertEquals(Permission.Values.NONE, read.and(write));
        assertEquals(read, read.and(read).and(read).and(read));
    }

    @Test
    public void xor()
    {
        final Permission read = Permission.Values.READ;
        final Permission write = Permission.Values.WRITE;

        assertEquals(read.xor(write), Permission.Values.READ.xor(Permission.Values.WRITE));
        assertEquals(READ_VALUE ^ WRITE_VALUE, read.xor(write).getValue());
        assertEquals(read, (read.xor(write)).and(read));
        assertEquals(write, (read.xor(write)).and(write));
        assertEquals(Permission.Values.NONE, read.xor(read));
        assertEquals(Permission.Values.NONE, write.xor(write));
    }

    @Test
    public void not()
    {
        final Permission none = Permission.Values.NONE;
        final Permission read = Permission.Values.READ;
        final Permission write = Permission.Values.WRITE;

        assertEquals(write, read.not().and(write));
        assertEquals(none, read.not().and(read));
        assertEquals(write, none.not().and(write));
        assertEquals(read, none.not().and(read));
        assertEquals(read.or(write), none.not().and(read.or(write)));
    }

    private static int PERMISSION_BITSIZEOF = 3;

    private static byte NONE_VALUE = 0;
    private static byte READ_VALUE = 2;
    private static byte WRITE_VALUE = 4;
}
