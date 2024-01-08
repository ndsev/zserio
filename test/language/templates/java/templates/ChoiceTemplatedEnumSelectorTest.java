package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.choice_templated_enum_selector.ChoiceTemplatedEnumSelector;
import templates.choice_templated_enum_selector.EnumFromOne;
import templates.choice_templated_enum_selector.EnumFromZero;
import templates.choice_templated_enum_selector.TemplatedChoice_EnumFromOne;
import templates.choice_templated_enum_selector.TemplatedChoice_EnumFromZero;

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

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        choiceTemplatedEnumSelector.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        ChoiceTemplatedEnumSelector readChoiceTemplatedEnumSelector = new ChoiceTemplatedEnumSelector(reader);
        assertTrue(choiceTemplatedEnumSelector.equals(readChoiceTemplatedEnumSelector));
    }
}
