package union_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import union_types.union_with_parameterized_field.TestUnion;
import union_types.union_with_parameterized_field.ArrayHolder;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UnionWithParameterizedFieldTest
{
    @Test
    public void emptyConstructor() throws ZserioError, IOException
    {
        final TestUnion testUnion = new TestUnion();
        testUnion.setArrayHolder(new ArrayHolder((short)10));
        assertEquals((short)10, testUnion.getArrayHolder().getSize());
    }

    @Test
    public void readConstructor() throws ZserioError, IOException
    {
        final TestUnion testUnion = new TestUnion();
        final ArrayHolder arrayHolder = new ArrayHolder((short)10);
        arrayHolder.setArray(new long[10]);
        testUnion.setArrayHolder(arrayHolder);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testUnion.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final TestUnion readTestUnion = new TestUnion(reader);
        assertEquals((short)10, readTestUnion.getArrayHolder().getSize());
    }

    @Test
    public void hashCodeMethod()
    {
        TestUnion testUnion1 = new TestUnion();
        TestUnion testUnion2 = new TestUnion();
        assertEquals(testUnion1.hashCode(), testUnion2.hashCode());
        testUnion1.setField(33);
        assertFalse(testUnion1.hashCode() == testUnion2.hashCode());
        testUnion2.setField(33);
        assertEquals(testUnion1.hashCode(), testUnion2.hashCode());
        testUnion2.setField(32);
        assertFalse(testUnion1.hashCode() == testUnion2.hashCode());
        testUnion2.setArrayHolder(new ArrayHolder((short)10, new long[10]));
        assertFalse(testUnion1.hashCode() == testUnion2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(31520, testUnion1.hashCode());
        assertEquals(1174142900, testUnion2.hashCode());

        testUnion1.setArrayHolder(new ArrayHolder((short)10, new long[10]));
        assertEquals(testUnion1.hashCode(), testUnion2.hashCode());
    }
}
