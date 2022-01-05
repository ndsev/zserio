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

import templates.choice_templated_field.ChoiceTemplatedField;
import templates.choice_templated_field.TemplatedChoice_uint32_uint16;
import templates.choice_templated_field.TemplatedChoice_Compound_uint32_uint16;
import templates.choice_templated_field.Compound_uint32;

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

        final TemplatedChoice_Compound_uint32_uint16 choice2 = new TemplatedChoice_Compound_uint32_uint16(selector);
        choice2.setTemplatedField1(new Compound_uint32(42));
        choiceTemplatedField.setChoice2(choice2);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        choiceTemplatedField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        ChoiceTemplatedField readChoiceTemplatedField = new ChoiceTemplatedField(reader);
        reader.close();
        assertTrue(choiceTemplatedField.equals(readChoiceTemplatedField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
