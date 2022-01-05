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

import templates.instantiate_type_as_struct_array_field.InstantiateTypeAsStructArrayField;
import templates.instantiate_type_as_struct_array_field.Test32;

public class InstantiateTypeAsStructArrayFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTypeAsStructArrayField instantiateTypeAsStructArrayField =
                new InstantiateTypeAsStructArrayField();
        instantiateTypeAsStructArrayField.setTest(
                new Test32[] { new Test32(13), new Test32(17),new Test32(23) });

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateTypeAsStructArrayField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        InstantiateTypeAsStructArrayField readInstantiateTypeAsStructArrayField =
                new InstantiateTypeAsStructArrayField(reader);
        reader.close();
        assertTrue(instantiateTypeAsStructArrayField.equals(readInstantiateTypeAsStructArrayField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
