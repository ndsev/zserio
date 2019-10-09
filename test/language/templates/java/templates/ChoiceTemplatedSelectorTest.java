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

import templates.choice_templated_selector.ChoiceTemplatedSelector;
import templates.choice_templated_selector.TemplatedChoice_uint32_SHIFT32;
import templates.choice_templated_selector.TemplatedChoice_uint16_SHIFT16;

public class ChoiceTemplatedSelectorTest
{
    @Test
    public void readWrite() throws IOException
    {
        final int selector16 = 0;
        final int selector32 = 1;
        ChoiceTemplatedSelector choiceTemplatedSelector = new ChoiceTemplatedSelector();
        choiceTemplatedSelector.setSelector16(selector16);
        choiceTemplatedSelector.setSelector32(selector32);

        TemplatedChoice_uint16_SHIFT16 uint16Choice = new TemplatedChoice_uint16_SHIFT16(selector16);
        uint16Choice.setUint16Field(42);
        choiceTemplatedSelector.setUint16Choice(uint16Choice);

        TemplatedChoice_uint32_SHIFT32 uint32Choice = new TemplatedChoice_uint32_SHIFT32(selector32);
        uint32Choice.setStringField("string");
        choiceTemplatedSelector.setUint32Choice(uint32Choice);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        choiceTemplatedSelector.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        ChoiceTemplatedSelector readChoiceTemplatedSelector = new ChoiceTemplatedSelector(reader);
        reader.close();
        assertTrue(choiceTemplatedSelector.equals(readChoiceTemplatedSelector));
    }

    private static final File TEST_FILE = new File("test.bin");
}
