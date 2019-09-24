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

import templates.templated_choice_enum_selector.TemplatedChoiceEnumSelector;
import templates.templated_choice_enum_selector.TemplatedChoice_EnumFromZero;
import templates.templated_choice_enum_selector.TemplatedChoice_EnumFromOne;
import templates.templated_choice_enum_selector.EnumFromZero;
import templates.templated_choice_enum_selector.EnumFromOne;

public class TemplatedChoiceEnumSelectorTest
{
    @Test
    public void readWrite() throws IOException
    {
        final EnumFromZero selectorFromZero = EnumFromZero.ONE;
        final EnumFromOne selectorFromOne = EnumFromOne.THREE;
        TemplatedChoiceEnumSelector templatedChoiceEnumSelector = new TemplatedChoiceEnumSelector();
        templatedChoiceEnumSelector.setSelectorFromZero(selectorFromZero);
        templatedChoiceEnumSelector.setSelectorFromOne(selectorFromOne);

        TemplatedChoice_EnumFromZero fromZeroChoice = new TemplatedChoice_EnumFromZero(selectorFromZero);
        fromZeroChoice.setUint16Field(42);
        templatedChoiceEnumSelector.setFromZeroChoice(fromZeroChoice);

        TemplatedChoice_EnumFromOne fromOneChoice = new TemplatedChoice_EnumFromOne(selectorFromOne);
        fromOneChoice.setStringField("string");
        templatedChoiceEnumSelector.setFromOneChoice(fromOneChoice);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        templatedChoiceEnumSelector.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        TemplatedChoiceEnumSelector readTemplatedChoiceEnumSelector = new TemplatedChoiceEnumSelector(reader);
        reader.close();
        assertTrue(templatedChoiceEnumSelector.equals(readTemplatedChoiceEnumSelector));
    }

    private static final File TEST_FILE = new File("test.bin");
}
