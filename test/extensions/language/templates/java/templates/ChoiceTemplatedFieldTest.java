package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.choice_templated_field.ChoiceTemplatedField;
import templates.choice_templated_field.Compound_uint32;
import templates.choice_templated_field.TemplatedChoice_Compound_uint32_uint16;
import templates.choice_templated_field.TemplatedChoice_uint32_uint16;

public class ChoiceTemplatedFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final long selector = 0;
        final ChoiceTemplatedField choiceTemplatedField = new ChoiceTemplatedField();
        choiceTemplatedField.setSelector(selector);

        final TemplatedChoice_uint32_uint16 choice1 = new TemplatedChoice_uint32_uint16(selector);
        choice1.setTemplatedField1(42);
        choiceTemplatedField.setChoice1(choice1);

        final TemplatedChoice_Compound_uint32_uint16 choice2 =
                new TemplatedChoice_Compound_uint32_uint16(selector);
        choice2.setTemplatedField1(new Compound_uint32(42));
        choiceTemplatedField.setChoice2(choice2);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        choiceTemplatedField.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        ChoiceTemplatedField readChoiceTemplatedField = new ChoiceTemplatedField(reader);
        assertTrue(choiceTemplatedField.equals(readChoiceTemplatedField));
    }
}
