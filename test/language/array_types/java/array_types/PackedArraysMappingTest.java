package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import array_types.packed_arrays_mapping.PackedArraysMapping;
import array_types.packed_arrays_mapping.TestStructure;
import array_types.packed_arrays_mapping.TestEnum;
import array_types.packed_arrays_mapping.TestBitmask;

public class PackedArraysMappingTest
{
    @Test
    public void unsignedIntegerArrays()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        packedArraysMapping.setUint8Array(new short[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setUint16Array(new int[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setUint32Array(new long[FIXED_ARRAY_LENGTH]);

        // just do something with packedArraysMapping not to have SpotBugs warning
        final BigInteger[] bigIntegerArray = new BigInteger[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setUint64Array(bigIntegerArray);
        assertTrue(Arrays.equals(bigIntegerArray, packedArraysMapping.getUint64Array()));
    }

    @Test
    public void signedIntegerArrays()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        packedArraysMapping.setInt8Array(new byte[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setInt16Array(new short[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setInt32Array(new int[FIXED_ARRAY_LENGTH]);

        // just do something with packedArraysMapping not to have SpotBugs warning
        final long[] longArray = new long[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setInt64Array(longArray);
        assertTrue(Arrays.equals(longArray, packedArraysMapping.getInt64Array()));
    }

    @Test
    public void unsignedBitfieldArrays()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        packedArraysMapping.setBitfield8Array(new short[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setBitfield16Array(new int[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setBitfield32Array(new long[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setBitfield63Array(new long[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setUint8Value((short)8);

        // just do something with packedArraysMapping not to have SpotBugs warning
        final BigInteger[] unsignedLongArray = new BigInteger[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setVariableBitfieldLongArray(unsignedLongArray);
        assertTrue(Arrays.equals(unsignedLongArray, packedArraysMapping.getVariableBitfieldLongArray()));
    }

    @Test
    public void signedBitfieldArrays()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        packedArraysMapping.setIntfield8Array(new byte[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setIntfield16Array(new short[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setIntfield32Array(new int[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setIntfield64Array(new long[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setUint8Value((short)8);
        packedArraysMapping.setVariableIntfieldLongArray(new long[FIXED_ARRAY_LENGTH]);

        // just do something with packedArraysMapping not to have SpotBugs warning
        final long[] longArray = new long[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setVariableIntfieldLongArray(longArray);
        assertTrue(Arrays.equals(longArray, packedArraysMapping.getVariableIntfieldLongArray()));
    }

    @Test
    public void variableUnsignedIntegerArrays()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        packedArraysMapping.setVaruint16Array(new short[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setVaruint32Array(new int[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setVaruint64Array(new long[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setVaruintArray(new BigInteger[FIXED_ARRAY_LENGTH]);

        // just do something with packedArraysMapping not to have SpotBugs warning
        final int[] varSizeArray = new int[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setVarsizeArray(varSizeArray);
        assertTrue(Arrays.equals(varSizeArray, packedArraysMapping.getVarsizeArray()));
    }

    @Test
    public void variableSignedIntegerArrays()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        packedArraysMapping.setVarint16Array(new short[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setVarint32Array(new int[FIXED_ARRAY_LENGTH]);
        packedArraysMapping.setVarint64Array(new long[FIXED_ARRAY_LENGTH]);

        // just do something with packedArraysMapping not to have SpotBugs warning
        final long[] varIntArray = new long[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setVarintArray(varIntArray);
        assertTrue(Arrays.equals(varIntArray, packedArraysMapping.getVarintArray()));
    }

    @Test
    public void compoundArray()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        // just do something with packedArraysMapping not to have SpotBugs warning
        final TestStructure[] objectArray = new TestStructure[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setCompoundArray(objectArray);
        assertTrue(Arrays.equals(objectArray, packedArraysMapping.getCompoundArray()));
    }

    @Test
    public void enumArray()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        // just do something with packedArraysMapping not to have SpotBugs warning
        final TestEnum[] enumArray = new TestEnum[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setEnumArray(enumArray);
        assertTrue(Arrays.equals(enumArray, packedArraysMapping.getEnumArray()));
    }

    @Test
    public void bitmaskArray()
    {
        PackedArraysMapping packedArraysMapping = new PackedArraysMapping();

        // just do something with packedArraysMapping not to have FindBugs warning
        final TestBitmask[] bitmaskArray = new TestBitmask[FIXED_ARRAY_LENGTH];
        packedArraysMapping.setBitmaskArray(bitmaskArray);
        assertTrue(Arrays.equals(bitmaskArray, packedArraysMapping.getBitmaskArray()));
    }

    private static final int FIXED_ARRAY_LENGTH = 5;
}
