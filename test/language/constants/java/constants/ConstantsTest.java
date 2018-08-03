package constants;

import static org.junit.Assert.*;

import org.junit.Test;

import java.math.BigInteger;

public class ConstantsTest
{
    @Test
    public void uint8MinConstant()
    {
        assertEquals((short)0x00, __ConstType.UINT8_MIN_CONSTANT);
    }

    @Test
    public void uint8MaxConstant()
    {
        assertEquals((short)0xFF, __ConstType.UINT8_MAX_CONSTANT);
    }

    @Test
    public void uint16MinConstant()
    {
        assertEquals(0x0000, __ConstType.UINT16_MIN_CONSTANT);
    }

    @Test
    public void uint16MaxConstant()
    {
        assertEquals(0xFFFF, __ConstType.UINT16_MAX_CONSTANT);
    }

    @Test
    public void uint32MinConstant()
    {
        assertEquals(0x00000000, __ConstType.UINT32_MIN_CONSTANT);
    }

    @Test
    public void uint32MaxConstant()
    {
        assertEquals(0xFFFFFFFFL, __ConstType.UINT32_MAX_CONSTANT);
    }

    @Test
    public void uint64MinConstant()
    {
        final BigInteger expectedValue = new BigInteger("0000000000000000", 16);
        assertEquals(expectedValue, __ConstType.UINT64_MIN_CONSTANT);
    }

    @Test
    public void uint64MaxConstant()
    {
        final BigInteger expectedValue = new BigInteger("FFFFFFFFFFFFFFFF", 16);
        assertEquals(expectedValue, __ConstType.UINT64_MAX_CONSTANT);
    }

    @Test
    public void int8MinConstant()
    {
        assertEquals(Byte.MIN_VALUE, __ConstType.INT8_MIN_CONSTANT);
    }

    @Test
    public void int8MaxConstant()
    {
        assertEquals(Byte.MAX_VALUE, __ConstType.INT8_MAX_CONSTANT);
    }

    @Test
    public void int16MinConstant()
    {
        assertEquals(Short.MIN_VALUE, __ConstType.INT16_MIN_CONSTANT);
    }

    @Test
    public void int16MaxConstant()
    {
        assertEquals(Short.MAX_VALUE, __ConstType.INT16_MAX_CONSTANT);
    }

    @Test
    public void int32MinConstant()
    {
        assertEquals(Integer.MIN_VALUE, __ConstType.INT32_MIN_CONSTANT);
    }

    @Test
    public void int32MaxConstant()
    {
        assertEquals(Integer.MAX_VALUE, __ConstType.INT32_MAX_CONSTANT);
    }

    @Test
    public void int64MinConstant()
    {
        assertEquals(Long.MIN_VALUE, __ConstType.INT64_MIN_CONSTANT);
    }

    @Test
    public void int64MaxConstant()
    {
        assertEquals(Long.MAX_VALUE, __ConstType.INT64_MAX_CONSTANT);
    }

    @Test
    public void bitField8MinConstant()
    {
        assertEquals((short)0x00, __ConstType.BITFIELD8_MIN_CONSTANT);
    }

    @Test
    public void bitField8MaxConstant()
    {
        assertEquals((short)0xFF, __ConstType.BITFIELD8_MAX_CONSTANT);
    }

    @Test
    public void variableBitfieldConstant()
    {
        assertEquals((short)0xAB, __ConstType.VARIABLE_BITFIELD_CONSTANT);
    }

    @Test
    public void intField8MinConstant()
    {
        assertEquals(Byte.MIN_VALUE, __ConstType.INTFIELD8_MIN_CONSTANT);
    }

    @Test
    public void intField8MaxConstant()
    {
        assertEquals(Byte.MAX_VALUE, __ConstType.INTFIELD8_MAX_CONSTANT);
    }

    @Test
    public void variableIntfieldConstant()
    {
        assertEquals((byte)0x12, __ConstType.VARIABLE_INTFIELD_CONSTANT);
    }

    @Test
    public void floatConstant()
    {
        assertEquals(3.13f, __ConstType.FLOAT16_CONSTANT, Float.MIN_VALUE);
    }

    @Test
    public void varuint16MinConstant()
    {
        assertEquals((short)0x0000, __ConstType.VARUINT16_MIN_CONSTANT);
    }

    @Test
    public void varuint16MaxConstant()
    {
        assertEquals((short)0x7FFF, __ConstType.VARUINT16_MAX_CONSTANT);
    }

    @Test
    public void varuint32MinConstant()
    {
        assertEquals(0x00000000, __ConstType.VARUINT32_MIN_CONSTANT);
    }

    @Test
    public void varuint32MaxConstant()
    {
        assertEquals(0x1FFFFFFF, __ConstType.VARUINT32_MAX_CONSTANT);
    }

    @Test
    public void varuint64MinConstant()
    {
        assertEquals(0x0000000000000000L, __ConstType.VARUINT64_MIN_CONSTANT);
    }

    public void varuint64MaxConstant()
    {
        assertEquals(0x01FFFFFFFFFFFFFFL, __ConstType.VARUINT64_MAX_CONSTANT);
    }

    @Test
    public void varuintMinConstant()
    {
        assertEquals(BigInteger.ZERO, __ConstType.VARUINT_MIN_CONSTANT);
    }

    @Test
    public void varuintMaxConstant()
    {
        assertEquals(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE), __ConstType.VARUINT_MAX_CONSTANT);
    }

    @Test
    public void varint16MinConstant()
    {
        assertEquals((short)-16383, __ConstType.VARINT16_MIN_CONSTANT);
    }

    @Test
    public void varint16MaxConstant()
    {
        assertEquals((short)16383, __ConstType.VARINT16_MAX_CONSTANT);
    }

    @Test
    public void varint32MinConstant()
    {
        assertEquals(-268435455, __ConstType.VARINT32_MIN_CONSTANT);
    }

    @Test
    public void varint32MaxConstant()
    {
        assertEquals(268435455, __ConstType.VARINT32_MAX_CONSTANT);
    }

    @Test
    public void varint64MinConstant()
    {
        assertEquals(-72057594037927935L, __ConstType.VARINT64_MIN_CONSTANT);
    }

    @Test
    public void varint64MaxConstant()
    {
        assertEquals(72057594037927935L, __ConstType.VARINT64_MAX_CONSTANT);
    }

    @Test
    public void varintMinConstant()
    {
        assertEquals(Long.MIN_VALUE, __ConstType.VARINT_MIN_CONSTANT);
    }

    @Test
    public void varintMaxConstant()
    {
        assertEquals(Long.MAX_VALUE, __ConstType.VARINT_MAX_CONSTANT);
    }

    @Test
    public void boolTrueConstant()
    {
        assertEquals(true, __ConstType.BOOL_TRUE_CONSTANT);
    }

    @Test
    public void boolFalseConstant()
    {
        assertEquals(false, __ConstType.BOOL_FALSE_CONSTANT);
    }

    @Test
    public void stringConstant()
    {
        assertEquals("Test \"Quated\" String", __ConstType.STRING_CONSTANT);
    }

    @Test
    public void unicodeEscStringConstant()
    {
        assertEquals("Test string with unicode escape \u0019", __ConstType.UNICODE_ESC_STRING_CONSTANT);
    }

    @Test
    public void hexEscStringConstant()
    {
        assertEquals("Test string with hexadecimal escape \u0019", __ConstType.HEX_ESC_STRING_CONSTANT);
    }

    @Test
    public void octalEscStringConstant()
    {
        assertEquals("Test string with octal escape \031", __ConstType.OCTAL_ESC_STRING_CONSTANT);
    }

    @Test
    public void constantDefinedByEnum()
    {
        assertEquals(Colors.BLACK, __ConstType.DEFAULT_PEN_COLOR); // constant defined by enum
    }

    @Test
    public void constantDefinedByConstant()
    {
        assertEquals(__ConstType.UINT32_FULL_MASK, __ConstType.UINT32_MAX_CONSTANT);
    }
}
