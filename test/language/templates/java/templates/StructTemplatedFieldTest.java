package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_templated_field.StructTemplatedField;
import templates.struct_templated_field.Field_uint32;
import templates.struct_templated_field.Field_Compound;
import templates.struct_templated_field.Field_string;
import templates.struct_templated_field.Compound;

public class StructTemplatedFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final StructTemplatedField structTemplatedField = new StructTemplatedField();
        structTemplatedField.setUint32Field(new Field_uint32(13));
        structTemplatedField.setCompoundField(new Field_Compound(new Compound(42)));
        structTemplatedField.setStringField(new Field_string("string"));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structTemplatedField.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final StructTemplatedField readStructTemplatedField = new StructTemplatedField(reader);
        assertTrue(structTemplatedField.equals(readStructTemplatedField));
    }
}
