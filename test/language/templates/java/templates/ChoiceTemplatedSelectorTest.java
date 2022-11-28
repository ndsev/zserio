package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.choice_templated_selector.ChoiceTemplatedSelector;
import templates.choice_templated_selector.TemplatedChoice_uint32_Shift32;
import templates.choice_templated_selector.TemplatedChoice_uint16_Shift16;

public class ChoiceTemplatedSelectorTest
{
    @Test
    public void readWrite() throws IOException
    {
        final int selector16 = 0;
        final int selector32 = 1;
        final ChoiceTemplatedSelector choiceTemplatedSelector = new ChoiceTemplatedSelector();
        choiceTemplatedSelector.setSelector16(selector16);
        choiceTemplatedSelector.setSelector32(selector32);

        final TemplatedChoice_uint16_Shift16 uint16Choice = new TemplatedChoice_uint16_Shift16(selector16);
        uint16Choice.setUint16Field(42);
        choiceTemplatedSelector.setUint16Choice(uint16Choice);

        final TemplatedChoice_uint32_Shift32 uint32Choice = new TemplatedChoice_uint32_Shift32(selector32);
        uint32Choice.setStringField("string");
        choiceTemplatedSelector.setUint32Choice(uint32Choice);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        choiceTemplatedSelector.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        ChoiceTemplatedSelector readChoiceTemplatedSelector = new ChoiceTemplatedSelector(reader);
        assertTrue(choiceTemplatedSelector.equals(readChoiceTemplatedSelector));
    }
}
