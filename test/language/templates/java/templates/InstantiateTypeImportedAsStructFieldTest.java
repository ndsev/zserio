package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

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

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateTypeImportedAsStructField.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        InstantiateTypeImportedAsStructField readInstantiateTypeImportedAsStructField =
                new InstantiateTypeImportedAsStructField(reader);
        assertTrue(instantiateTypeImportedAsStructField.equals(readInstantiateTypeImportedAsStructField));
    }
}
