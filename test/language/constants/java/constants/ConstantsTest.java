package constants;

import static org.junit.Assert.*;

import org.junit.Test;

import java.math.BigInteger;

public class ConstantsTest
{
    @Test
    public void uint8MinConstant()
    {
        assertEquals((short)0x00, UINT8_MIN_CONSTANT.UINT8_MIN_CONSTANT);
    }

    @Test
    public void uint8MaxConstant()
    {
        assertEquals((short)0xFF, UINT8_MAX_CONSTANT.UINT8_MAX_CONSTANT);
    }

    @Test
    public void uint16MinConstant()
    {
        assertEquals(0x0000, UINT16_MIN_CONSTANT.UINT16_MIN_CONSTANT);
    }

    @Test
    public void uint16MaxConstant()
    {
        assertEquals(0xFFFF, UINT16_MAX_CONSTANT.UINT16_MAX_CONSTANT);
    }

    @Test
    public void uint32MinConstant()
    {
        assertEquals(0x00000000, UINT32_MIN_CONSTANT.UINT32_MIN_CONSTANT);
    }

    @Test
    public void uint32MaxConstant()
    {
        assertEquals(0xFFFFFFFFL, UINT32_MAX_CONSTANT.UINT32_MAX_CONSTANT);
    }

    @Test
    public void uint64MinConstant()
    {
        final BigInteger expectedValue = new BigInteger("0000000000000000", 16);
        assertEquals(expectedValue, UINT64_MIN_CONSTANT.UINT64_MIN_CONSTANT);
    }

    @Test
    public void uint64MaxConstant()
    {
        final BigInteger expectedValue = new BigInteger("FFFFFFFFFFFFFFFF", 16);
        assertEquals(expectedValue, UINT64_MAX_CONSTANT.UINT64_MAX_CONSTANT);
    }

    @Test
    public void int8MinConstant()
    {
        assertEquals(Byte.MIN_VALUE, INT8_MIN_CONSTANT.INT8_MIN_CONSTANT);
    }

    @Test
    public void int8MaxConstant()
    {
        assertEquals(Byte.MAX_VALUE, INT8_MAX_CONSTANT.INT8_MAX_CONSTANT);
    }

    @Test
    public void int16MinConstant()
    {
        assertEquals(Short.MIN_VALUE, INT16_MIN_CONSTANT.INT16_MIN_CONSTANT);
    }

    @Test
    public void int16MaxConstant()
    {
        assertEquals(Short.MAX_VALUE, INT16_MAX_CONSTANT.INT16_MAX_CONSTANT);
    }

    @Test
    public void int32MinConstant()
    {
        assertEquals(Integer.MIN_VALUE, INT32_MIN_CONSTANT.INT32_MIN_CONSTANT);
    }

    @Test
    public void int32MaxConstant()
    {
        assertEquals(Integer.MAX_VALUE, INT32_MAX_CONSTANT.INT32_MAX_CONSTANT);
    }

    @Test
    public void int64MinConstant()
    {
        assertEquals(Long.MIN_VALUE, INT64_MIN_CONSTANT.INT64_MIN_CONSTANT);
    }

    @Test
    public void int64MaxConstant()
    {
        assertEquals(Long.MAX_VALUE, INT64_MAX_CONSTANT.INT64_MAX_CONSTANT);
    }

    @Test
    public void bitField8MinConstant()
    {
        assertEquals((short)0x00, BITFIELD8_MIN_CONSTANT.BITFIELD8_MIN_CONSTANT);
    }

    @Test
    public void bitField8MaxConstant()
    {
        assertEquals((short)0xFF, BITFIELD8_MAX_CONSTANT.BITFIELD8_MAX_CONSTANT);
    }

    @Test
    public void variableBitfieldConstant()
    {
        assertEquals((short)0xAB, VARIABLE_BITFIELD_CONSTANT.VARIABLE_BITFIELD_CONSTANT);
    }

    @Test
    public void intField8MinConstant()
    {
        assertEquals(Byte.MIN_VALUE, INTFIELD8_MIN_CONSTANT.INTFIELD8_MIN_CONSTANT);
    }

    @Test
    public void intField8MaxConstant()
    {
        assertEquals(Byte.MAX_VALUE, INTFIELD8_MAX_CONSTANT.INTFIELD8_MAX_CONSTANT);
    }

    @Test
    public void variableIntfieldConstant()
    {
        assertEquals((byte)0x12, VARIABLE_INTFIELD_CONSTANT.VARIABLE_INTFIELD_CONSTANT);
    }

    @Test
    public void float16Constant()
    {
        assertEquals(3.13f, FLOAT16_CONSTANT.FLOAT16_CONSTANT, 0.00001f);
    }

    @Test
    public void float32Constant()
    {
        assertEquals(3.131f, FLOAT32_CONSTANT.FLOAT32_CONSTANT, 0.00001f);
    }

    @Test
    public void float64Constant()
    {
        assertEquals(3.1314, FLOAT64_CONSTANT.FLOAT64_CONSTANT, 0.000000001);
    }

    @Test
    public void varuint16MinConstant()
    {
        assertEquals((short)0x0000, VARUINT16_MIN_CONSTANT.VARUINT16_MIN_CONSTANT);
    }

    @Test
    public void varuint16MaxConstant()
    {
        assertEquals((short)0x7FFF, VARUINT16_MAX_CONSTANT.VARUINT16_MAX_CONSTANT);
    }

    @Test
    public void varuint32MinConstant()
    {
        assertEquals(0x00000000, VARUINT32_MIN_CONSTANT.VARUINT32_MIN_CONSTANT);
    }

    @Test
    public void varuint32MaxConstant()
    {
        assertEquals(0x1FFFFFFF, VARUINT32_MAX_CONSTANT.VARUINT32_MAX_CONSTANT);
    }

    @Test
    public void varuint64MinConstant()
    {
        assertEquals(0x0000000000000000L, VARUINT64_MIN_CONSTANT.VARUINT64_MIN_CONSTANT);
    }

    @Test
    public void varuint64MaxConstant()
    {
        assertEquals(0x01FFFFFFFFFFFFFFL, VARUINT64_MAX_CONSTANT.VARUINT64_MAX_CONSTANT);
    }

    @Test
    public void varuintMinConstant()
    {
        assertEquals(BigInteger.ZERO, VARUINT_MIN_CONSTANT.VARUINT_MIN_CONSTANT);
    }

    @Test
    public void varuintMaxConstant()
    {
        assertEquals(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE),
                VARUINT_MAX_CONSTANT.VARUINT_MAX_CONSTANT);
    }

    @Test
    public void varint16MinConstant()
    {
        assertEquals((short)-16383, VARINT16_MIN_CONSTANT.VARINT16_MIN_CONSTANT);
    }

    @Test
    public void varint16MaxConstant()
    {
        assertEquals((short)16383, VARINT16_MAX_CONSTANT.VARINT16_MAX_CONSTANT);
    }

    @Test
    public void varint32MinConstant()
    {
        assertEquals(-268435455, VARINT32_MIN_CONSTANT.VARINT32_MIN_CONSTANT);
    }

    @Test
    public void varint32MaxConstant()
    {
        assertEquals(268435455, VARINT32_MAX_CONSTANT.VARINT32_MAX_CONSTANT);
    }

    @Test
    public void varint64MinConstant()
    {
        assertEquals(-72057594037927935L, VARINT64_MIN_CONSTANT.VARINT64_MIN_CONSTANT);
    }

    @Test
    public void varint64MaxConstant()
    {
        assertEquals(72057594037927935L, VARINT64_MAX_CONSTANT.VARINT64_MAX_CONSTANT);
    }

    @Test
    public void varintMinConstant()
    {
        assertEquals(Long.MIN_VALUE, VARINT_MIN_CONSTANT.VARINT_MIN_CONSTANT);
    }

    @Test
    public void varintMaxConstant()
    {
        assertEquals(Long.MAX_VALUE, VARINT_MAX_CONSTANT.VARINT_MAX_CONSTANT);
    }

    @Test
    public void boolTrueConstant()
    {
        assertEquals(true, BOOL_TRUE_CONSTANT.BOOL_TRUE_CONSTANT);
    }

    @Test
    public void boolFalseConstant()
    {
        assertEquals(false, BOOL_FALSE_CONSTANT.BOOL_FALSE_CONSTANT);
    }

    @Test
    public void stringConstant()
    {
        assertEquals("Test \"Quated\" String", STRING_CONSTANT.STRING_CONSTANT);
    }

    @Test
    public void unicodeEscStringConstant()
    {
        assertEquals("Test string with unicode escape \u0019",
                UNICODE_ESC_STRING_CONSTANT.UNICODE_ESC_STRING_CONSTANT);
    }

    @Test
    public void hexEscStringConstant()
    {
        assertEquals("Test string with hexadecimal escape \u0019",
                HEX_ESC_STRING_CONSTANT.HEX_ESC_STRING_CONSTANT);
    }

    @Test
    public void octalEscStringConstant()
    {
        assertEquals("Test string with octal escape \031",
                OCTAL_ESC_STRING_CONSTANT.OCTAL_ESC_STRING_CONSTANT);
    }

    @Test
    public void constantDefinedByConstant()
    {
        assertEquals(UINT32_FULL_MASK.UINT32_FULL_MASK, UINT32_MAX_CONSTANT.UINT32_MAX_CONSTANT);
    }

    @Test
    public void constantDefinedByEnum()
    {
        assertEquals(Colors.BLACK, DEFAULT_PEN_COLOR.DEFAULT_PEN_COLOR); // constant defined by enum
    }

    @Test
    public void constantDefinedByEnumValueof()
    {
        assertEquals(Colors.BLACK.getValue(), DEFAULT_PEN_COLOR_VALUE.DEFAULT_PEN_COLOR_VALUE);
    }

    @Test
    public void subtypeToInt25Constant()
    {
        assertEquals(25, SUBTYPE_INT25_CONSTANT.SUBTYPE_INT25_CONSTANT);
    }

    @Test
    public void subtypeToEnumConstant()
    {
        assertEquals(Colors.BLUE, SUBTYPE_BLUE_COLOR_CONSTANT.SUBTYPE_BLUE_COLOR_CONSTANT);
    }
}

