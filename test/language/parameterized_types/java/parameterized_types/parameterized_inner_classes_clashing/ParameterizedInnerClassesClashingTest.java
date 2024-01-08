package parameterized_types.parameterized_inner_classes_clashing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class ParameterizedInnerClassesClashingTest
{
    @Test
    public void writeReadElementFactory() throws IOException, ZserioError
    {
        final long param = 100;
        final ElementFactory_array testStructure = new ElementFactory_array(
                param, new Compound[] {new Compound(param, 13), new Compound(param, 42)});

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testStructure.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final ElementFactory_array readTestStructure = new ElementFactory_array(reader);
        assertEquals(testStructure, readTestStructure);
    }

    @Test
    public void writeReadElementInitializer() throws IOException, ZserioError
    {
        final long param = 100;
        final ElementInitializer_array testStructure = new ElementInitializer_array(
                param, new Compound[] {new Compound(param, 13), new Compound(param, 42)});

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testStructure.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final ElementInitializer_array readTestStructure = new ElementInitializer_array(reader);
        assertEquals(testStructure, readTestStructure);
    }

    @Test
    public void writeReadElementChildrenInitializer() throws IOException, ZserioError
    {
        final long param = 100;
        final ElementChildrenInitializer_array testStructure =
                new ElementChildrenInitializer_array(new Parent[] {new Parent(param, new Compound(param, 13)),
                        new Parent(param, new Compound(param, 42))});

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testStructure.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final ElementChildrenInitializer_array readTestStructure = new ElementChildrenInitializer_array(reader);
        assertEquals(testStructure, readTestStructure);
    }
}
