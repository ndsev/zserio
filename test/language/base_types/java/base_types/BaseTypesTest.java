package base_types;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

public class BaseTypesTest
{
    @Test
    public void uint8Type()
    {
        final short maxUint8Type = (short)0xFF;
        baseTypes.setUint8Type(maxUint8Type);
        final short uint8Type = baseTypes.getUint8Type();
        assertEquals(maxUint8Type, uint8Type);
    }

    @Test
    public void uint16Type()
    {
        final int maxUint16Type = (int)0xFFFF;
        baseTypes.setUint16Type(maxUint16Type);
        final int uint16Type = baseTypes.getUint16Type();
        assertEquals(maxUint16Type, uint16Type);
    }

    @Test
    public void uint32Type()
    {
        final long maxUint32Type = (long)0xFFFFFFFF;
        baseTypes.setUint32Type(maxUint32Type);
        final long uint32Type = baseTypes.getUint32Type();
        assertEquals(maxUint32Type, uint32Type);
    }

    @Test
    public void uint64Type()
    {
        final BigInteger testUint64Type = BigInteger.TEN;
        baseTypes.setUint64Type(testUint64Type);
        final BigInteger uint64Type = baseTypes.getUint64Type();
        assertTrue(uint64Type.equals(testUint64Type));
    }

    @Test
    public void int8Type()
    {
        baseTypes.setInt8Type(Byte.MAX_VALUE);
        final byte int8Type = baseTypes.getInt8Type();
        assertEquals(Byte.MAX_VALUE, int8Type);
    }

    @Test
    public void int16Type()
    {
        baseTypes.setInt16Type(Short.MAX_VALUE);
        final short int16Type = baseTypes.getInt16Type();
        assertEquals(Short.MAX_VALUE, int16Type);
    }

    @Test
    public void int32Type()
    {
        baseTypes.setInt32Type(Integer.MAX_VALUE);
        final int int32Type = baseTypes.getInt32Type();
        assertEquals(Integer.MAX_VALUE, int32Type);
    }

    @Test
    public void int64Type()
    {
        baseTypes.setInt64Type(Long.MAX_VALUE);
        final long int64Type = baseTypes.getInt64Type();
        assertEquals(Long.MAX_VALUE, int64Type);
    }

    @Test
    public void bitField7Type()
    {
        final byte maxBitfield7Type = (byte)0x7F;
        baseTypes.setBitfield7Type(maxBitfield7Type);
        final byte bitfieldType = baseTypes.getBitfield7Type();
        assertEquals(maxBitfield7Type, bitfieldType);
    }

    @Test
    public void bitField8Type()
    {
        final short maxBitfield8Type = (short)0xFF;
        baseTypes.setBitfield8Type(maxBitfield8Type);
        final short bitfieldType = baseTypes.getBitfield8Type();
        assertEquals(maxBitfield8Type, bitfieldType);
    }

    @Test
    public void bitField15Type()
    {
        final short maxBitfield15Type = (short)0x7FFF;
        baseTypes.setBitfield15Type(maxBitfield15Type);
        final short bitfieldType = baseTypes.getBitfield15Type();
        assertEquals(maxBitfield15Type, bitfieldType);
    }

    @Test
    public void bitField16Type()
    {
        final int maxBitfield16Type = (int)0xFFFF;
        baseTypes.setBitfield16Type(maxBitfield16Type);
        final int bitfieldType = baseTypes.getBitfield16Type();
        assertEquals(maxBitfield16Type, bitfieldType);
    }

    @Test
    public void bitField31Type()
    {
        final int maxBitfield31Type = (int)0x7FFFFFFF;
        baseTypes.setBitfield31Type(maxBitfield31Type);
        final int bitfieldType = baseTypes.getBitfield31Type();
        assertEquals(maxBitfield31Type, bitfieldType);
    }

    @Test
    public void bitField32Type()
    {
        final long maxBitfield32Type = (long)0xFFFFFFFF;
        baseTypes.setBitfield32Type(maxBitfield32Type);
        final long bitfieldType = baseTypes.getBitfield32Type();
        assertEquals(maxBitfield32Type, bitfieldType);
    }

    @Test
    public void bitField63Type()
    {
        final long maxBitfield63Type = (long)0x7FFFFFFFFFFFFFFFL;
        baseTypes.setBitfield63Type(maxBitfield63Type);
        final long bitfieldType = baseTypes.getBitfield63Type();
        assertEquals(maxBitfield63Type, bitfieldType);
    }

    @Test
    public void variableBitfieldType()
    {
        final long maxVariableBitfieldType = (long)0x7FFFFFFFFFFFFFFFL;
        baseTypes.setVariableBitfieldType(maxVariableBitfieldType);
        final long variableBitfieldType = baseTypes.getVariableBitfieldType();
        assertEquals(maxVariableBitfieldType, variableBitfieldType);
    }

    @Test
    public void variableBitField8Type()
    {
        final short maxBitfield8Type = (short)0xFF;
        baseTypes.setBitfield8Type(maxBitfield8Type);
        final short bitfieldType = baseTypes.getBitfield8Type();
        assertEquals(maxBitfield8Type, bitfieldType);
    }

    @Test
    public void intField8Type()
    {
        baseTypes.setIntfield8Type(Byte.MAX_VALUE);
        final byte intField8Type = baseTypes.getIntfield8Type();
        assertEquals(Byte.MAX_VALUE, intField8Type);
    }

    @Test
    public void intField16Type()
    {
        baseTypes.setIntfield16Type(Short.MAX_VALUE);
        final short intField16Type = baseTypes.getIntfield16Type();
        assertEquals(Short.MAX_VALUE, intField16Type);
    }

    @Test
    public void intField32Type()
    {
        baseTypes.setIntfield32Type(Integer.MAX_VALUE);
        final int intField32Type = baseTypes.getIntfield32Type();
        assertEquals(Integer.MAX_VALUE, intField32Type);
    }

    @Test
    public void intField64Type()
    {
        baseTypes.setIntfield64Type(Long.MAX_VALUE);
        final long intField64Type = baseTypes.getIntfield64Type();
        assertEquals(Long.MAX_VALUE, intField64Type);
    }

    @Test
    public void variableIntfieldType()
    {
        baseTypes.setVariableIntfieldType(Short.MAX_VALUE);
        final short variableIntfieldType = baseTypes.getVariableIntfieldType();
        assertEquals(Short.MAX_VALUE, variableIntfieldType);
    }

    @Test
    public void variableIntField8Type()
    {
        baseTypes.setIntfield8Type(Byte.MAX_VALUE);
        final byte intField8Type = baseTypes.getIntfield8Type();
        assertEquals(Byte.MAX_VALUE, intField8Type);
    }

    @Test
    public void float16Type()
    {
        baseTypes.setFloat16Type(Float.MAX_VALUE);
        final float float16Type = baseTypes.getFloat16Type();
        assertTrue(float16Type - Float.MAX_VALUE <= Float.MIN_VALUE);
    }

    @Test
    public void float32Type()
    {
        baseTypes.setFloat32Type(Float.MAX_VALUE);
        final float float32Type = baseTypes.getFloat32Type();
        assertTrue(float32Type - Float.MAX_VALUE <= Float.MIN_VALUE);
    }

    @Test
    public void float64Type()
    {
        baseTypes.setFloat64Type(Double.MAX_VALUE);
        final double float64Type = baseTypes.getFloat16Type();
        assertTrue(float64Type - Double.MAX_VALUE <= Double.MIN_VALUE);
    }

    @Test
    public void varuint16Type()
    {
        final short maxVaruint16Type = ((short)1 << 15) - 1;
        baseTypes.setVaruint16Type(maxVaruint16Type);
        final short varuint16Type = baseTypes.getVaruint16Type();
        assertEquals(maxVaruint16Type, varuint16Type);
    }

    @Test
    public void varuint32Type()
    {
        final int maxVaruint32Type = ((int)1 << 31) - 1;
        baseTypes.setVaruint32Type(maxVaruint32Type);
        final int varuint32Type = baseTypes.getVaruint32Type();
        assertEquals(maxVaruint32Type, varuint32Type);
    }

    @Test
    public void varuint64Type()
    {
        final long maxVaruint64Type = ((long)1 << 63) - 1;
        baseTypes.setVaruint64Type(maxVaruint64Type);
        final long varuint64Type = baseTypes.getVaruint64Type();
        assertEquals(maxVaruint64Type, varuint64Type);
    }

    @Test
    public void varuintType()
    {
        final BigInteger minVaruintType = BigInteger.ZERO;
        baseTypes.setVaruintType(minVaruintType);
        final BigInteger readMinVaruintType = baseTypes.getVaruintType();
        assertEquals(minVaruintType, readMinVaruintType);

        final BigInteger maxVaruintType = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
        baseTypes.setVaruintType(maxVaruintType);
        final BigInteger readMaxVaruintType = baseTypes.getVaruintType();
        assertEquals(maxVaruintType, readMaxVaruintType);
    }

    @Test
    public void varint16Type()
    {
        final short maxVarint16Type = ((short)1 << 14) - 1;
        baseTypes.setVarint16Type(maxVarint16Type);
        final short varint16Type = baseTypes.getVarint16Type();
        assertEquals(maxVarint16Type, varint16Type);
    }

    @Test
    public void varint32Type()
    {
        final int maxVarint32Type = ((int)1 << 30) - 1;
        baseTypes.setVarint32Type(maxVarint32Type);
        final int varint32Type = baseTypes.getVarint32Type();
        assertEquals(maxVarint32Type, varint32Type);
    }

    @Test
    public void varint64Type()
    {
        final long maxVarint64Type = ((long)1 << 62) - 1;
        baseTypes.setVarint64Type(maxVarint64Type);
        final long varint64Type = baseTypes.getVarint64Type();
        assertEquals(maxVarint64Type, varint64Type);
    }

    @Test
    public void varintType()
    {
        final long minVarintType = Long.MIN_VALUE;
        baseTypes.setVarintType(minVarintType);
        final long readMinVarintType = baseTypes.getVarintType();
        assertEquals(minVarintType, readMinVarintType);

        final long maxVarintType = Long.MAX_VALUE;
        baseTypes.setVarintType(maxVarintType);
        final long readMaxVarintType = baseTypes.getVarintType();
        assertEquals(maxVarintType, readMaxVarintType);
    }

    @Test
    public void boolType()
    {
        baseTypes.setBoolType(true);
        final boolean boolType = baseTypes.getBoolType();
        assertEquals(true, boolType);
    }

    @Test
    public void stringType()
    {
        final String testString = "TEST";
        baseTypes.setStringType(testString);
        final String stringType = baseTypes.getStringType();
        assertTrue(stringType.equals(testString));
    }

    private final BaseTypes baseTypes = new BaseTypes();
}
