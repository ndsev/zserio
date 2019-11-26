package builtin_types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import java.math.BigInteger;

import org.junit.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import builtin_types.all_builtin_types.AllBuiltInTypes;

public class AllBuiltInTypesTest
{
    @Test
    public void uint8Type()
    {
        final short maxUint8Type = (short)0xFF;
        allBuiltInTypes.setUint8Type(maxUint8Type);
        final short uint8Type = allBuiltInTypes.getUint8Type();
        assertEquals(maxUint8Type, uint8Type);
    }

    @Test
    public void uint16Type()
    {
        final int maxUint16Type = (int)0xFFFF;
        allBuiltInTypes.setUint16Type(maxUint16Type);
        final int uint16Type = allBuiltInTypes.getUint16Type();
        assertEquals(maxUint16Type, uint16Type);
    }

    @Test
    public void uint32Type()
    {
        final long maxUint32Type = (long)0xFFFFFFFFL;
        allBuiltInTypes.setUint32Type(maxUint32Type);
        final long uint32Type = allBuiltInTypes.getUint32Type();
        assertEquals(maxUint32Type, uint32Type);
    }

    @Test
    public void uint64Type()
    {
        final BigInteger testUint64Type = BigInteger.TEN;
        allBuiltInTypes.setUint64Type(testUint64Type);
        final BigInteger uint64Type = allBuiltInTypes.getUint64Type();
        assertTrue(uint64Type.equals(testUint64Type));
    }

    @Test
    public void int8Type()
    {
        allBuiltInTypes.setInt8Type(Byte.MAX_VALUE);
        final byte int8Type = allBuiltInTypes.getInt8Type();
        assertEquals(Byte.MAX_VALUE, int8Type);
    }

    @Test
    public void int16Type()
    {
        allBuiltInTypes.setInt16Type(Short.MAX_VALUE);
        final short int16Type = allBuiltInTypes.getInt16Type();
        assertEquals(Short.MAX_VALUE, int16Type);
    }

    @Test
    public void int32Type()
    {
        allBuiltInTypes.setInt32Type(Integer.MAX_VALUE);
        final int int32Type = allBuiltInTypes.getInt32Type();
        assertEquals(Integer.MAX_VALUE, int32Type);
    }

    @Test
    public void int64Type()
    {
        allBuiltInTypes.setInt64Type(Long.MAX_VALUE);
        final long int64Type = allBuiltInTypes.getInt64Type();
        assertEquals(Long.MAX_VALUE, int64Type);
    }

    @Test
    public void bitField7Type()
    {
        final byte maxBitfield7Type = (byte)0x7F;
        allBuiltInTypes.setBitfield7Type(maxBitfield7Type);
        final byte bitfieldType = allBuiltInTypes.getBitfield7Type();
        assertEquals(maxBitfield7Type, bitfieldType);
    }

    @Test
    public void bitField8Type()
    {
        final short maxBitfield8Type = (short)0xFF;
        allBuiltInTypes.setBitfield8Type(maxBitfield8Type);
        final short bitfieldType = allBuiltInTypes.getBitfield8Type();
        assertEquals(maxBitfield8Type, bitfieldType);
    }

    @Test
    public void bitField15Type()
    {
        final short maxBitfield15Type = (short)0x7FFF;
        allBuiltInTypes.setBitfield15Type(maxBitfield15Type);
        final short bitfieldType = allBuiltInTypes.getBitfield15Type();
        assertEquals(maxBitfield15Type, bitfieldType);
    }

    @Test
    public void bitField16Type()
    {
        final int maxBitfield16Type = (int)0xFFFF;
        allBuiltInTypes.setBitfield16Type(maxBitfield16Type);
        final int bitfieldType = allBuiltInTypes.getBitfield16Type();
        assertEquals(maxBitfield16Type, bitfieldType);
    }

    @Test
    public void bitField31Type()
    {
        final int maxBitfield31Type = (int)0x7FFFFFFF;
        allBuiltInTypes.setBitfield31Type(maxBitfield31Type);
        final int bitfieldType = allBuiltInTypes.getBitfield31Type();
        assertEquals(maxBitfield31Type, bitfieldType);
    }

    @Test
    public void bitField32Type()
    {
        final long maxBitfield32Type = (long)0xFFFFFFFFL;
        allBuiltInTypes.setBitfield32Type(maxBitfield32Type);
        final long bitfieldType = allBuiltInTypes.getBitfield32Type();
        assertEquals(maxBitfield32Type, bitfieldType);
    }

    @Test
    public void bitField63Type()
    {
        final long maxBitfield63Type = (long)0x7FFFFFFFFFFFFFFFL;
        allBuiltInTypes.setBitfield63Type(maxBitfield63Type);
        final long bitfieldType = allBuiltInTypes.getBitfield63Type();
        assertEquals(maxBitfield63Type, bitfieldType);
    }

    @Test
    public void variableBitfieldType()
    {
        final BigInteger maxVariableBitfieldType = BigInteger.valueOf(0x7FFFFFFFFFFFFFFFL);
        allBuiltInTypes.setVariableBitfieldType(maxVariableBitfieldType);
        final BigInteger variableBitfieldType = allBuiltInTypes.getVariableBitfieldType();
        assertEquals(maxVariableBitfieldType, variableBitfieldType);
    }

    @Test
    public void variableBitField8Type()
    {
        final short maxVariableBitfield8Type = (short)0xFF;
        allBuiltInTypes.setVariableBitfield8Type(maxVariableBitfield8Type);
        final short variableBitfieldType = allBuiltInTypes.getVariableBitfield8Type();
        assertEquals(maxVariableBitfield8Type, variableBitfieldType);
    }

    @Test
    public void intField8Type()
    {
        allBuiltInTypes.setIntfield8Type(Byte.MAX_VALUE);
        final byte intField8Type = allBuiltInTypes.getIntfield8Type();
        assertEquals(Byte.MAX_VALUE, intField8Type);
    }

    @Test
    public void intField16Type()
    {
        allBuiltInTypes.setIntfield16Type(Short.MAX_VALUE);
        final short intField16Type = allBuiltInTypes.getIntfield16Type();
        assertEquals(Short.MAX_VALUE, intField16Type);
    }

    @Test
    public void intField32Type()
    {
        allBuiltInTypes.setIntfield32Type(Integer.MAX_VALUE);
        final int intField32Type = allBuiltInTypes.getIntfield32Type();
        assertEquals(Integer.MAX_VALUE, intField32Type);
    }

    @Test
    public void intField64Type()
    {
        allBuiltInTypes.setIntfield64Type(Long.MAX_VALUE);
        final long intField64Type = allBuiltInTypes.getIntfield64Type();
        assertEquals(Long.MAX_VALUE, intField64Type);
    }

    @Test
    public void variableIntfieldType()
    {
        allBuiltInTypes.setVariableIntfieldType(Short.MAX_VALUE);
        final short variableIntfieldType = allBuiltInTypes.getVariableIntfieldType();
        assertEquals(Short.MAX_VALUE, variableIntfieldType);
    }

    @Test
    public void variableIntField8Type()
    {
        allBuiltInTypes.setVariableIntfield8Type(Byte.MAX_VALUE);
        final byte variableIntField8Type = allBuiltInTypes.getVariableIntfield8Type();
        assertEquals(Byte.MAX_VALUE, variableIntField8Type);
    }

    @Test
    public void float16Type()
    {
        allBuiltInTypes.setFloat16Type(Float.MAX_VALUE);
        final float float16Type = allBuiltInTypes.getFloat16Type();
        assertTrue(float16Type - Float.MAX_VALUE <= Float.MIN_VALUE);
    }

    @Test
    public void float32Type()
    {
        allBuiltInTypes.setFloat32Type(Float.MAX_VALUE);
        final float float32Type = allBuiltInTypes.getFloat32Type();
        assertTrue(float32Type - Float.MAX_VALUE <= Float.MIN_VALUE);
    }

    @Test
    public void float64Type()
    {
        allBuiltInTypes.setFloat64Type(Double.MAX_VALUE);
        final double float64Type = allBuiltInTypes.getFloat16Type();
        assertTrue(float64Type - Double.MAX_VALUE <= Double.MIN_VALUE);
    }

    @Test
    public void varuint16Type()
    {
        final short maxVaruint16Type = ((short)1 << 15) - 1;
        allBuiltInTypes.setVaruint16Type(maxVaruint16Type);
        final short varuint16Type = allBuiltInTypes.getVaruint16Type();
        assertEquals(maxVaruint16Type, varuint16Type);
    }

    @Test
    public void varuint32Type()
    {
        final int maxVaruint32Type = ((int)1 << 29) - 1;
        allBuiltInTypes.setVaruint32Type(maxVaruint32Type);
        final int varuint32Type = allBuiltInTypes.getVaruint32Type();
        assertEquals(maxVaruint32Type, varuint32Type);
    }

    @Test
    public void varuint64Type()
    {
        final long maxVaruint64Type = ((long)1 << 57) - 1;
        allBuiltInTypes.setVaruint64Type(maxVaruint64Type);
        final long varuint64Type = allBuiltInTypes.getVaruint64Type();
        assertEquals(maxVaruint64Type, varuint64Type);
    }

    @Test
    public void varuintType()
    {
        final BigInteger minVaruintType = BigInteger.ZERO;
        allBuiltInTypes.setVaruintType(minVaruintType);
        final BigInteger readMinVaruintType = allBuiltInTypes.getVaruintType();
        assertEquals(minVaruintType, readMinVaruintType);

        final BigInteger maxVaruintType = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
        allBuiltInTypes.setVaruintType(maxVaruintType);
        final BigInteger readMaxVaruintType = allBuiltInTypes.getVaruintType();
        assertEquals(maxVaruintType, readMaxVaruintType);
    }

    @Test
    public void varint16Type()
    {
        final short maxVarint16Type = ((short)1 << 14) - 1;
        allBuiltInTypes.setVarint16Type(maxVarint16Type);
        final short varint16Type = allBuiltInTypes.getVarint16Type();
        assertEquals(maxVarint16Type, varint16Type);
    }

    @Test
    public void varint32Type()
    {
        final int maxVarint32Type = ((int)1 << 28) - 1;
        allBuiltInTypes.setVarint32Type(maxVarint32Type);
        final int varint32Type = allBuiltInTypes.getVarint32Type();
        assertEquals(maxVarint32Type, varint32Type);
    }

    @Test
    public void varint64Type()
    {
        final long maxVarint64Type = ((long)1 << 56) - 1;
        allBuiltInTypes.setVarint64Type(maxVarint64Type);
        final long varint64Type = allBuiltInTypes.getVarint64Type();
        assertEquals(maxVarint64Type, varint64Type);
    }

    @Test
    public void varintType()
    {
        final long minVarintType = Long.MIN_VALUE;
        allBuiltInTypes.setVarintType(minVarintType);
        final long readMinVarintType = allBuiltInTypes.getVarintType();
        assertEquals(minVarintType, readMinVarintType);

        final long maxVarintType = Long.MAX_VALUE;
        allBuiltInTypes.setVarintType(maxVarintType);
        final long readMaxVarintType = allBuiltInTypes.getVarintType();
        assertEquals(maxVarintType, readMaxVarintType);
    }

    @Test
    public void boolType()
    {
        allBuiltInTypes.setBoolType(true);
        final boolean boolType = allBuiltInTypes.getBoolType();
        assertEquals(true, boolType);
    }

    @Test
    public void stringType()
    {
        final String testString = "TEST";
        allBuiltInTypes.setStringType(testString);
        final String stringType = allBuiltInTypes.getStringType();
        assertEquals(testString, stringType);
    }

    @Test
    public void externType()
    {
        final BitBuffer testExtern = new BitBuffer(new byte[]{(byte)0xCD, (byte)0x03}, 10);
        allBuiltInTypes.setExternType(testExtern);
        final BitBuffer externType = allBuiltInTypes.getExternType();
        assertEquals(testExtern, externType);
    }

    @Test
    public void bitSizeOf()
    {
        allBuiltInTypes.setBoolType(true);
        allBuiltInTypes.setUint8Type((short)1);
        allBuiltInTypes.setUint16Type((int)0xFFFF);
        allBuiltInTypes.setUint32Type((long)0xFFFFFFFFL);
        allBuiltInTypes.setUint64Type(BigInteger.TEN);
        allBuiltInTypes.setInt8Type(Byte.MAX_VALUE);
        allBuiltInTypes.setInt16Type(Short.MAX_VALUE);
        allBuiltInTypes.setInt32Type(Integer.MAX_VALUE);
        allBuiltInTypes.setInt64Type(Long.MAX_VALUE);
        allBuiltInTypes.setBitfield7Type((byte)0x7F);
        allBuiltInTypes.setBitfield8Type((short)0xFF);
        allBuiltInTypes.setBitfield15Type((short)0x7FFF);
        allBuiltInTypes.setBitfield16Type((int)0xFFFF);
        allBuiltInTypes.setBitfield31Type((int)0x7FFFFFFF);
        allBuiltInTypes.setBitfield32Type((long)0xFFFFFFFFL);
        allBuiltInTypes.setBitfield63Type((long)0x7FFFFFFFFFFFFFFFL);
        allBuiltInTypes.setVariableBitfieldType(BigInteger.valueOf(1));
        allBuiltInTypes.setVariableBitfield8Type((short)0xFF);
        allBuiltInTypes.setIntfield8Type(Byte.MAX_VALUE);
        allBuiltInTypes.setIntfield16Type(Short.MAX_VALUE);
        allBuiltInTypes.setIntfield32Type(Integer.MAX_VALUE);
        allBuiltInTypes.setIntfield64Type(Long.MAX_VALUE);
        allBuiltInTypes.setVariableIntfieldType((short)1);
        allBuiltInTypes.setVariableIntfield8Type(Byte.MAX_VALUE);
        allBuiltInTypes.setFloat16Type(Float.MAX_VALUE);
        allBuiltInTypes.setFloat32Type(Float.MAX_VALUE);
        allBuiltInTypes.setFloat64Type(Double.MAX_VALUE);
        allBuiltInTypes.setVaruint16Type((short)(((short)1 << 15) - 1));
        allBuiltInTypes.setVaruint32Type(((int)1 << 29) - 1);
        allBuiltInTypes.setVaruint64Type(((long)1 << 57) - 1);
        allBuiltInTypes.setVaruintType(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE));
        allBuiltInTypes.setVarint16Type((short)(((short)1 << 14) - 1));
        allBuiltInTypes.setVarint32Type(((int)1 << 28) - 1);
        allBuiltInTypes.setVarint64Type(((long)1 << 56) - 1);
        allBuiltInTypes.setVarintType(Long.MAX_VALUE);
        allBuiltInTypes.setStringType("TEST");
        allBuiltInTypes.setExternType(new BitBuffer(new byte[]{(byte)0xCD, (byte)0x03}, 10));
        final int expectedBitSizeOf = 1102;
        assertEquals(expectedBitSizeOf, allBuiltInTypes.bitSizeOf());
    }

    @Test
    public void readWrite() throws IOException
    {
        allBuiltInTypes.setBoolType(true);
        allBuiltInTypes.setUint8Type((short)8);
        allBuiltInTypes.setUint16Type((int)0xFFFF);
        allBuiltInTypes.setUint32Type((long)0xFFFFFFFFL);
        allBuiltInTypes.setUint64Type(BigInteger.TEN);
        allBuiltInTypes.setInt8Type(Byte.MAX_VALUE);
        allBuiltInTypes.setInt16Type(Short.MAX_VALUE);
        allBuiltInTypes.setInt32Type(Integer.MAX_VALUE);
        allBuiltInTypes.setInt64Type(Long.MAX_VALUE);
        allBuiltInTypes.setBitfield7Type((byte)0x7F);
        allBuiltInTypes.setBitfield8Type((short)0xFF);
        allBuiltInTypes.setBitfield15Type((short)0x7FFF);
        allBuiltInTypes.setBitfield16Type((int)0xFFFF);
        allBuiltInTypes.setBitfield31Type((int)0x7FFFFFFF);
        allBuiltInTypes.setBitfield32Type((long)0xFFFFFFFFL);
        allBuiltInTypes.setBitfield63Type((long)0x7FFFFFFFFFFFFFFFL);
        allBuiltInTypes.setVariableBitfieldType(BigInteger.valueOf(0xFF));
        allBuiltInTypes.setVariableBitfield8Type((short)0xFF);
        allBuiltInTypes.setIntfield8Type(Byte.MAX_VALUE);
        allBuiltInTypes.setIntfield16Type(Short.MAX_VALUE);
        allBuiltInTypes.setIntfield32Type(Integer.MAX_VALUE);
        allBuiltInTypes.setIntfield64Type(Long.MAX_VALUE);
        allBuiltInTypes.setVariableIntfieldType((short)Byte.MAX_VALUE);
        allBuiltInTypes.setVariableIntfield8Type(Byte.MAX_VALUE);
        allBuiltInTypes.setFloat16Type(1.0f);
        allBuiltInTypes.setFloat32Type(Float.MAX_VALUE);
        allBuiltInTypes.setFloat64Type(Double.MAX_VALUE);
        allBuiltInTypes.setVaruint16Type((short)(((short)1 << 15) - 1));
        allBuiltInTypes.setVaruint32Type(((int)1 << 29) - 1);
        allBuiltInTypes.setVaruint64Type(((long)1 << 57) - 1);
        allBuiltInTypes.setVaruintType(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE));
        allBuiltInTypes.setVarint16Type((short)(((short)1 << 14) - 1));
        allBuiltInTypes.setVarint32Type(((int)1 << 28) - 1);
        allBuiltInTypes.setVarint64Type(((long)1 << 56) - 1);
        allBuiltInTypes.setVarintType(Long.MAX_VALUE);
        allBuiltInTypes.setStringType("TEST");
        allBuiltInTypes.setExternType(new BitBuffer(new byte[]{(byte)0xCD, (byte)0x03}, 10));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        allBuiltInTypes.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final AllBuiltInTypes readAllBuiltInTypes = new AllBuiltInTypes(reader);
        reader.close();
        assertEquals(allBuiltInTypes, readAllBuiltInTypes);
    }

    private final AllBuiltInTypes allBuiltInTypes = new AllBuiltInTypes();

    private static final File TEST_FILE = new File("test.bin");
}
