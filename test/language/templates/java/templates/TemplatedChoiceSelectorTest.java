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

import templates.templated_choice_selector.TemplatedChoiceSelector;
import templates.templated_choice_selector.TemplatedChoice_uint32;
import templates.templated_choice_selector.TemplatedChoice_uint16;

public class TemplatedChoiceSelectorTest
{
    @Test
    public void readWrite() throws IOException
    {
        final int selector16 = 0;
        final int selector32 = 2;
        TemplatedChoiceSelector templatedChoiceSelector = new TemplatedChoiceSelector();
        templatedChoiceSelector.setSelector16(selector16);
        templatedChoiceSelector.setSelector32(selector32);

        TemplatedChoice_uint16 uint16Choice = new TemplatedChoice_uint16(selector16);
        uint16Choice.setUint16Field(42);
        templatedChoiceSelector.setUint16Choice(uint16Choice);

        TemplatedChoice_uint32 uint32Choice = new TemplatedChoice_uint32(selector32);
        uint32Choice.setStringField("string");
        templatedChoiceSelector.setUint32Choice(uint32Choice);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        templatedChoiceSelector.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        TemplatedChoiceSelector readTemplatedChoiceSelector = new TemplatedChoiceSelector(reader);
        reader.close();
        assertTrue(templatedChoiceSelector.equals(readTemplatedChoiceSelector));
    }

    private static final File TEST_FILE = new File("test.bin");
}
