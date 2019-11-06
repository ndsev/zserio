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

import templates.instantiate_type_as_choice_field.InstantiateTypeAsChoiceField;
import templates.instantiate_type_as_choice_field.Test32;

public class InstantiateTypeAsChoiceFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTypeAsChoiceField instantiateTypeAsChoiceField = new InstantiateTypeAsChoiceField(true);
        instantiateTypeAsChoiceField.setTest(new Test32(13));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateTypeAsChoiceField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        InstantiateTypeAsChoiceField readInstantiateTypeAsChoiceField =
                new InstantiateTypeAsChoiceField(reader, true);
        reader.close();
        assertTrue(instantiateTypeAsChoiceField.equals(readInstantiateTypeAsChoiceField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
