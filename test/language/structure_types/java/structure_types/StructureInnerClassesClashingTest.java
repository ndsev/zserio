package structure_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import structure_types.structure_inner_classes_clashing.ArrayType_array;
import structure_types.structure_inner_classes_clashing.OffsetChecker_array;
import structure_types.structure_inner_classes_clashing.OffsetInitializer_array;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureInnerClassesClashingTest
{
    @Test
    public void writeReadArrayType() throws IOException, ZserioError
    {
        final ArrayType_array testStructure = new ArrayType_array(new long[] {1, 2, 3, 4});

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testStructure.write(writer);
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final ArrayType_array readTestStructure = new ArrayType_array(reader);
        assertEquals(testStructure, readTestStructure);
    }

    @Test
    public void writeReadOffsetChecker() throws IOException, ZserioError
    {
        final OffsetChecker_array testStructure = new OffsetChecker_array(
            new long[4], new long[] {1, 2, 3, 4});

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testStructure.write(writer);
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final OffsetChecker_array readTestStructure = new OffsetChecker_array(reader);
        assertEquals(testStructure, readTestStructure);
    }

    @Test
    public void writeReadOffsetInitializer() throws IOException, ZserioError
    {
        final OffsetInitializer_array testStructure = new OffsetInitializer_array(
            new long[4], new long[] {1, 2, 3, 4});

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testStructure.write(writer);
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final OffsetInitializer_array readTestStructure = new OffsetInitializer_array(reader);
        assertEquals(testStructure, readTestStructure);
    }
}
