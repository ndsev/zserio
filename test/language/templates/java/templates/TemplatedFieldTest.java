package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.templated_field.TemplatedField;
import templates.templated_field.Field_uint32;
import templates.templated_field.Field_Compound;
import templates.templated_field.Field_string;
import templates.templated_field.Compound;

public class TemplatedFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TemplatedField templatedField = new TemplatedField();
        templatedField.setUint32Field(new Field_uint32(13));
        templatedField.setCompoundField(new Field_Compound(new Compound(42)));
        templatedField.setStringField(new Field_string("string"));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        templatedField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final TemplatedField readTemplatedField = new TemplatedField(reader);
        reader.close();
        assertTrue(templatedField.equals(readTemplatedField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
