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

import templates.choice_templated_enum_selector.ChoiceTemplatedEnumSelector;
import templates.choice_templated_enum_selector.TemplatedChoice_EnumFromZero;
import templates.choice_templated_enum_selector.TemplatedChoice_EnumFromOne;
import templates.choice_templated_enum_selector.EnumFromZero;
import templates.choice_templated_enum_selector.EnumFromOne;

public class ChoiceTemplatedEnumSelectorTest
{
    @Test
    public void readWrite() throws IOException
    {
        final EnumFromZero selectorFromZero = EnumFromZero.ONE;
        final EnumFromOne selectorFromOne = EnumFromOne.THREE;
        final ChoiceTemplatedEnumSelector choiceTemplatedEnumSelector = new ChoiceTemplatedEnumSelector();
        choiceTemplatedEnumSelector.setSelectorFromZero(selectorFromZero);
        choiceTemplatedEnumSelector.setSelectorFromOne(selectorFromOne);

        final TemplatedChoice_EnumFromZero fromZeroChoice = new TemplatedChoice_EnumFromZero(selectorFromZero);
        fromZeroChoice.setUint16Field(42);
        choiceTemplatedEnumSelector.setFromZeroChoice(fromZeroChoice);

        final TemplatedChoice_EnumFromOne fromOneChoice = new TemplatedChoice_EnumFromOne(selectorFromOne);
        fromOneChoice.setStringField("string");
        choiceTemplatedEnumSelector.setFromOneChoice(fromOneChoice);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        choiceTemplatedEnumSelector.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        ChoiceTemplatedEnumSelector readChoiceTemplatedEnumSelector = new ChoiceTemplatedEnumSelector(reader);
        reader.close();
        assertTrue(choiceTemplatedEnumSelector.equals(readChoiceTemplatedEnumSelector));
    }

    private static final File TEST_FILE = new File("test.bin");
}
