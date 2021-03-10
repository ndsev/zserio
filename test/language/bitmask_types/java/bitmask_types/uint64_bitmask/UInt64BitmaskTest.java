package bitmask_types.uint64_bitmask;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UInt64BitmaskTest
{
    @Test
    public void emptyConstructor()
    {
        final Permission permission = new Permission();
        assertEquals(BigInteger.ZERO, permission.getValue());
    }

    @Test
    public void valueConstructor()
    {
        final Permission permission = new Permission(WRITE_PERMISSION_VALUE);
        assertTrue(permission.and(Permission.Values.write_permission).equals(
                Permission.Values.write_permission));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueConstructorUnderLowerBound()
    {
        new Permission(BigInteger.ONE.negate());
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueConstructorAboveUpperBound()
    {
        new Permission(BigInteger.ONE.shiftLeft(PERMISSION_BITSIZEOF));
    }

    @Test
    public void readConstructor() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBigInteger(Permission.Values.write_permission.getValue(), PERMISSION_BITSIZEOF);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final Permission readPermission = new Permission(reader);
        assertEquals(Permission.Values.write_permission, readPermission);
    }

    @Test
    public void bitSizeOf()
    {
        assertEquals(PERMISSION_BITSIZEOF, Permission.Values.nonePermission.bitSizeOf());
        assertEquals(PERMISSION_BITSIZEOF, Permission.Values.nonePermission.bitSizeOf(1));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;
        assertEquals(bitPosition + PERMISSION_BITSIZEOF,
                Permission.Values.READ_PERMISSION.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        assertTrue(Permission.Values.nonePermission.equals(Permission.Values.nonePermission));
        assertTrue(Permission.Values.READ_PERMISSION.equals(Permission.Values.READ_PERMISSION));
        assertTrue(Permission.Values.write_permission.equals(Permission.Values.write_permission));

        final Permission read = Permission.Values.READ_PERMISSION;
        assertTrue(read.equals(Permission.Values.READ_PERMISSION));
        assertTrue(Permission.Values.READ_PERMISSION.equals(read));
        assertFalse(read.equals(Permission.Values.write_permission));
        assertFalse(Permission.Values.write_permission.equals(read));

        final Permission write = new Permission(WRITE_PERMISSION_VALUE);
        assertTrue(write.equals(Permission.Values.write_permission));
        assertTrue(Permission.Values.write_permission.equals(write));
        assertFalse(write.equals(Permission.Values.READ_PERMISSION));
        assertFalse(Permission.Values.READ_PERMISSION.equals(write));

        assertTrue(read.equals(read));
        assertTrue(write.equals(write));
        assertFalse(read.equals(write));
    }

    @Test
    public void hashCodeMethod()
    {
        final Permission read = Permission.Values.READ_PERMISSION;
        final Permission write = Permission.Values.write_permission;
        assertEquals(read.hashCode(), Permission.Values.READ_PERMISSION.hashCode());
        assertEquals(read.hashCode(), new Permission(READ_PERMISSION_VALUE).hashCode());
        assertEquals(write.hashCode(), Permission.Values.write_permission.hashCode());
        assertEquals(write.hashCode(), new Permission(WRITE_PERMISSION_VALUE).hashCode());
        assertFalse(read.hashCode() == write.hashCode());
        assertFalse(read.hashCode() == Permission.Values.nonePermission.hashCode());
    }

    @Test
    public void toStringMethod()
    {
        assertEquals("0[nonePermission]", Permission.Values.nonePermission.toString());
        assertEquals("2[READ_PERMISSION]", Permission.Values.READ_PERMISSION.toString());
        assertEquals("4[write_permission]", Permission.Values.write_permission.toString());
        assertEquals("6[READ_PERMISSION | write_permission]",
                Permission.Values.READ_PERMISSION.or(Permission.Values.write_permission).toString());
        assertEquals("7[READ_PERMISSION | write_permission]", new Permission(BigInteger.valueOf(7)).toString());
        assertEquals("255[READ_PERMISSION | write_permission | CreatePermission]",
                new Permission(BigInteger.valueOf(255)).toString());
    }

    @Test
    public void write() throws IOException
    {
        final Permission permission = Permission.Values.READ_PERMISSION;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        permission.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final Permission readPermission = new Permission(reader);
        assertEquals(permission, readPermission);
    }

    @Test
    public void getValue()
    {
        assertEquals(NONE_PERMISSION_VALUE, Permission.Values.nonePermission.getValue());
        assertEquals(READ_PERMISSION_VALUE, Permission.Values.READ_PERMISSION.getValue());
        assertEquals(WRITE_PERMISSION_VALUE, Permission.Values.write_permission.getValue());
    }

    @Test
    public void or()
    {
        final Permission read = Permission.Values.READ_PERMISSION;
        final Permission write = Permission.Values.write_permission;

        assertEquals(read.or(write), Permission.Values.READ_PERMISSION.or(Permission.Values.write_permission));
        assertEquals(read, read.or(Permission.Values.nonePermission));
        assertEquals(write, Permission.Values.nonePermission.or(write));
        assertEquals(READ_PERMISSION_VALUE.or(WRITE_PERMISSION_VALUE), read.or(write).getValue());
    }

    @Test
    public void and()
    {
        final Permission read = Permission.Values.READ_PERMISSION;
        final Permission write = Permission.Values.write_permission;
        final Permission readwrite = Permission.Values.READ_PERMISSION.or(Permission.Values.write_permission);

        assertEquals(read, readwrite.and(read));
        assertEquals(write, readwrite.and(write));
        assertEquals(Permission.Values.nonePermission, readwrite.and(Permission.Values.nonePermission));
        assertEquals(Permission.Values.nonePermission, read.and(Permission.Values.nonePermission));
        assertEquals(Permission.Values.nonePermission, write.and(Permission.Values.nonePermission));
        assertEquals(Permission.Values.nonePermission, read.and(write));
        assertEquals(read, read.and(read).and(read).and(read));
    }

    @Test
    public void xor()
    {
        final Permission read = Permission.Values.READ_PERMISSION;
        final Permission write = Permission.Values.write_permission;

        assertEquals(read.xor(write),
                Permission.Values.READ_PERMISSION.xor(Permission.Values.write_permission));
        assertEquals(READ_PERMISSION_VALUE.xor(WRITE_PERMISSION_VALUE), read.xor(write).getValue());
        assertEquals(read, (read.xor(write)).and(read));
        assertEquals(write, (read.xor(write)).and(write));
        assertEquals(Permission.Values.nonePermission, read.xor(read));
        assertEquals(Permission.Values.nonePermission, write.xor(write));
    }

    @Test
    public void not()
    {
        final Permission none = Permission.Values.nonePermission;
        final Permission read = Permission.Values.READ_PERMISSION;
        final Permission write = Permission.Values.write_permission;

        assertEquals(write, read.not().and(write));
        assertEquals(none, read.not().and(read));
        assertEquals(write, none.not().and(write));
        assertEquals(read, none.not().and(read));
        assertEquals(read.or(write), none.not().and(read.or(write)));
    }

    private static int PERMISSION_BITSIZEOF = 64;

    private static BigInteger NONE_PERMISSION_VALUE = BigInteger.ZERO;
    private static BigInteger READ_PERMISSION_VALUE = BigInteger.valueOf(2);
    private static BigInteger WRITE_PERMISSION_VALUE = BigInteger.valueOf(4);
}
