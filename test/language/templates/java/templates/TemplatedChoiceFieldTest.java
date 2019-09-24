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

import templates.templated_choice_field.TemplatedChoiceField;
import templates.templated_choice_field.TemplatedChoice_uint32_uint16;
import templates.templated_choice_field.TemplatedChoice_Compound_uint32_uint16;
import templates.templated_choice_field.Compound_uint32;

public class TemplatedChoiceFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final long selector = 0;
        TemplatedChoiceField templatedChoiceField = new TemplatedChoiceField();
        templatedChoiceField.setSelector(selector);

        TemplatedChoice_uint32_uint16 choice1 = new TemplatedChoice_uint32_uint16(selector);
        choice1.setTemplatedField1(42);
        templatedChoiceField.setChoice1(choice1);

        TemplatedChoice_Compound_uint32_uint16 choice2 = new TemplatedChoice_Compound_uint32_uint16(selector);
        choice2.setTemplatedField1(new Compound_uint32(42));
        templatedChoiceField.setChoice2(choice2);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        templatedChoiceField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        TemplatedChoiceField readTemplatedChoiceField = new TemplatedChoiceField(reader);
        reader.close();
        assertTrue(templatedChoiceField.equals(readTemplatedChoiceField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
