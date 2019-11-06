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

import templates.instantiate_type_imported_as_struct_field.InstantiateTypeImportedAsStructField;
import templates.instantiate_type_imported_as_struct_field.pkg.Test32;

public class InstantiateTypeImportedAsStructFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTypeImportedAsStructField instantiateTypeImportedAsStructField =
                new InstantiateTypeImportedAsStructField();
        instantiateTypeImportedAsStructField.setTest(new Test32(13));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateTypeImportedAsStructField.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        InstantiateTypeImportedAsStructField readInstantiateTypeImportedAsStructField =
                new InstantiateTypeImportedAsStructField(reader);
        reader.close();
        assertTrue(instantiateTypeImportedAsStructField.equals(readInstantiateTypeImportedAsStructField));
    }

    private static final File TEST_FILE = new File("test.bin");
}
