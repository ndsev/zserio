package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

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

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateTypeAsStructArrayField.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        InstantiateTypeAsStructArrayField readInstantiateTypeAsStructArrayField =
                new InstantiateTypeAsStructArrayField(reader);
        assertTrue(instantiateTypeAsStructArrayField.equals(readInstantiateTypeAsStructArrayField));
    }
}
