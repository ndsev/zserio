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

import templates.instantiate_type_as_struct_field.InstantiateTypeAsStructField;
import templates.instantiate_type_as_struct_field.Test32;

public class InstantiateTypeAsStructFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTypeAsStructField instantiateTypeAsStructField = new InstantiateTypeAsStructField();
        instantiateTypeAsStructField.setTest(new Test32(13));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateTypeAsStructField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        InstantiateTypeAsStructField readInstantiateTypeAsStructField =
                new InstantiateTypeAsStructField(reader);
        reader.close();
        assertTrue(instantiateTypeAsStructField.equals(readInstantiateTypeAsStructField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
