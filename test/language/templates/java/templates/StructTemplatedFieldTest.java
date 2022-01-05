package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structTemplatedField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final StructTemplatedField readStructTemplatedField = new StructTemplatedField(reader);
        reader.close();
        assertTrue(structTemplatedField.equals(readStructTemplatedField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
