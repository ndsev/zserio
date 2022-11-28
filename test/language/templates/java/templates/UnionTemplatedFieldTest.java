package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.union_templated_field.UnionTemplatedField;
import templates.union_templated_field.TemplatedUnion_uint16_uint32;
import templates.union_templated_field.TemplatedUnion_float32_float64;
import templates.union_templated_field.TemplatedUnion_Compound_uint16_Compound_uint32;
import templates.union_templated_field.Compound_uint16;
import templates.union_templated_field.Compound_Compound_uint16;

public class UnionTemplatedFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final UnionTemplatedField unionTemplatedField = new UnionTemplatedField();

        final TemplatedUnion_uint16_uint32 uintUnion = new TemplatedUnion_uint16_uint32();
        uintUnion.setField1(42);
        unionTemplatedField.setUintUnion(uintUnion);

        final TemplatedUnion_float32_float64 floatUnion = new TemplatedUnion_float32_float64();
        floatUnion.setField2(42.0f);
        unionTemplatedField.setFloatUnion(floatUnion);

        final TemplatedUnion_Compound_uint16_Compound_uint32 compoundUnion =
                new TemplatedUnion_Compound_uint16_Compound_uint32();
        compoundUnion.setField3(new Compound_Compound_uint16(new Compound_uint16(13)));
        unionTemplatedField.setCompoundUnion(compoundUnion);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        unionTemplatedField.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final UnionTemplatedField readUnionTemplatedField = new UnionTemplatedField(reader);
        reader.close();
        assertTrue(unionTemplatedField.equals(readUnionTemplatedField));
    }
}
