package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_type_as_struct_field.InstantiateTypeAsStructField;
import templates.instantiate_type_as_struct_field.Test32;

public class InstantiateTypeAsStructFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTypeAsStructField instantiateTypeAsStructField = new InstantiateTypeAsStructField();
        instantiateTypeAsStructField.setTest(new Test32(13));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateTypeAsStructField.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        InstantiateTypeAsStructField readInstantiateTypeAsStructField =
                new InstantiateTypeAsStructField(reader);
        assertTrue(instantiateTypeAsStructField.equals(readInstantiateTypeAsStructField));
    }
}
