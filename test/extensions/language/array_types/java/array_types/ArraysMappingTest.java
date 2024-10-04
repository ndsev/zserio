package array_types;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;

import array_types.arrays_mapping.ArraysMapping;
import array_types.arrays_mapping.TestBitmask;
import array_types.arrays_mapping.TestEnum;
import array_types.arrays_mapping.TestStructure;

public class ArraysMappingTest
{
    @Test
    public void unsignedIntegerArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setUint8Array(new short[FIXED_ARRAY_LENGTH]);
        arraysMapping.setUint16Array(new int[FIXED_ARRAY_LENGTH]);
        arraysMapping.setUint32Array(new long[FIXED_ARRAY_LENGTH]);

        // just do something with arraysMapping not to have SpotBugs warning
        final BigInteger[] bigIntegerArray = new BigInteger[FIXED_ARRAY_LENGTH];
        arraysMapping.setUint64Array(bigIntegerArray);
        assertTrue(Arrays.equals(bigIntegerArray, arraysMapping.getUint64Array()));
    }

    @Test
    public void signedIntegerArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setInt8Array(new byte[FIXED_ARRAY_LENGTH]);
        arraysMapping.setInt16Array(new short[FIXED_ARRAY_LENGTH]);
        arraysMapping.setInt32Array(new int[FIXED_ARRAY_LENGTH]);

        // just do something with arraysMapping not to have SpotBugs warning
        final long[] longArray = new long[FIXED_ARRAY_LENGTH];
        arraysMapping.setInt64Array(longArray);
        assertTrue(Arrays.equals(longArray, arraysMapping.getInt64Array()));
    }

    @Test
    public void unsignedBitfieldArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setBitfield8Array(new short[FIXED_ARRAY_LENGTH]);
        arraysMapping.setBitfield16Array(new int[FIXED_ARRAY_LENGTH]);
        arraysMapping.setBitfield32Array(new long[FIXED_ARRAY_LENGTH]);
        arraysMapping.setBitfield63Array(new long[FIXED_ARRAY_LENGTH]);
        arraysMapping.setUint8Value((short)8);
        arraysMapping.setVariableBitfieldLongArray(new BigInteger[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVariableBitfieldIntArray(new int[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVariableBitfieldShortArray(new int[FIXED_ARRAY_LENGTH]);

        // just do something with arraysMapping not to have SpotBugs warning
        final short[] unsignedByteArray = new short[FIXED_ARRAY_LENGTH];
        arraysMapping.setVariableBitfieldByteArray(unsignedByteArray);
        assertTrue(Arrays.equals(unsignedByteArray, arraysMapping.getVariableBitfieldByteArray()));

        arraysMapping.setLength64(BigInteger.valueOf(64));
        arraysMapping.setVariableBitfield64Array(new BigInteger[FIXED_ARRAY_LENGTH]);
    }

    @Test
    public void signedBitfieldArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setIntfield8Array(new byte[FIXED_ARRAY_LENGTH]);
        arraysMapping.setIntfield16Array(new short[FIXED_ARRAY_LENGTH]);
        arraysMapping.setIntfield32Array(new int[FIXED_ARRAY_LENGTH]);
        arraysMapping.setIntfield64Array(new long[FIXED_ARRAY_LENGTH]);
        arraysMapping.setUint8Value((short)8);
        arraysMapping.setVariableIntfieldLongArray(new long[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVariableIntfieldIntArray(new int[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVariableIntfieldShortArray(new short[FIXED_ARRAY_LENGTH]);

        // just do something with arraysMapping not to have SpotBugs warning
        final byte[] byteArray = new byte[FIXED_ARRAY_LENGTH];
        arraysMapping.setVariableIntfieldByteArray(byteArray);
        assertTrue(Arrays.equals(byteArray, arraysMapping.getVariableIntfieldByteArray()));

        arraysMapping.setLength32(64);
        arraysMapping.setVariableIntfield64Array(new long[FIXED_ARRAY_LENGTH]);
    }

    @Test
    public void floatArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setFloat16Array(new float[FIXED_ARRAY_LENGTH]);
        arraysMapping.setFloat32Array(new float[FIXED_ARRAY_LENGTH]);

        // just do something with arraysMapping not to have SpotBugs warning
        final double[] float64Array = new double[FIXED_ARRAY_LENGTH];
        arraysMapping.setFloat64Array(float64Array);
        assertTrue(Arrays.equals(float64Array, arraysMapping.getFloat64Array()));
    }

    @Test
    public void variableUnsignedIntegerArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setVaruint16Array(new short[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVaruint32Array(new int[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVaruint64Array(new long[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVaruintArray(new BigInteger[FIXED_ARRAY_LENGTH]);

        // just do something with arraysMapping not to have SpotBugs warning
        final int[] varSizeArray = new int[FIXED_ARRAY_LENGTH];
        arraysMapping.setVarsizeArray(varSizeArray);
        assertTrue(Arrays.equals(varSizeArray, arraysMapping.getVarsizeArray()));
    }

    @Test
    public void variableSignedIntegerArrays()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        arraysMapping.setVarint16Array(new short[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVarint32Array(new int[FIXED_ARRAY_LENGTH]);
        arraysMapping.setVarint64Array(new long[FIXED_ARRAY_LENGTH]);

        // just do something with arraysMapping not to have SpotBugs warning
        final long[] varIntArray = new long[FIXED_ARRAY_LENGTH];
        arraysMapping.setVarintArray(varIntArray);
        assertTrue(Arrays.equals(varIntArray, arraysMapping.getVarintArray()));
    }

    @Test
    public void boolArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        // just do something with arraysMapping not to have SpotBugs warning
        final boolean[] boolArray = new boolean[FIXED_ARRAY_LENGTH];
        arraysMapping.setBoolArray(boolArray);
        assertTrue(Arrays.equals(boolArray, arraysMapping.getBoolArray()));
    }

    @Test
    public void stringArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();
        final String[] stringArray = new String[FIXED_ARRAY_LENGTH];
        arraysMapping.setStringArray(stringArray);
        assertTrue(Arrays.equals(stringArray, arraysMapping.getStringArray()));
    }

    @Test
    public void bytesArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();
        final byte[][] bytesArray = new byte[FIXED_ARRAY_LENGTH][];
        arraysMapping.setBytesArray(bytesArray);
        assertTrue(Arrays.equals(bytesArray, arraysMapping.getBytesArray()));
    }

    @Test
    public void externArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();
        final BitBuffer[] externArray = new BitBuffer[FIXED_ARRAY_LENGTH];
        arraysMapping.setExternArray(externArray);
        assertTrue(Arrays.equals(externArray, arraysMapping.getExternArray()));
    }

    @Test
    public void compoundArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        // just do something with arraysMapping not to have SpotBugs warning
        final TestStructure[] objectArray = new TestStructure[FIXED_ARRAY_LENGTH];
        arraysMapping.setCompoundArray(objectArray);
        assertTrue(Arrays.equals(objectArray, arraysMapping.getCompoundArray()));
    }

    @Test
    public void enumArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        // just do something with arraysMapping not to have SpotBugs warning
        final TestEnum[] enumArray = new TestEnum[FIXED_ARRAY_LENGTH];
        arraysMapping.setEnumArray(enumArray);
        assertTrue(Arrays.equals(enumArray, arraysMapping.getEnumArray()));
    }

    @Test
    public void bitmaskArray()
    {
        ArraysMapping arraysMapping = new ArraysMapping();

        // just do something with arraysMapping not to have FindBugs warning
        final TestBitmask[] bitmaskArray = new TestBitmask[FIXED_ARRAY_LENGTH];
        arraysMapping.setBitmaskArray(bitmaskArray);
        assertTrue(Arrays.equals(bitmaskArray, arraysMapping.getBitmaskArray()));
    }

    private static final int FIXED_ARRAY_LENGTH = 5;
}
