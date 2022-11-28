package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_type_as_choice_field.InstantiateTypeAsChoiceField;
import templates.instantiate_type_as_choice_field.Test32;

public class InstantiateTypeAsChoiceFieldTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTypeAsChoiceField instantiateTypeAsChoiceField = new InstantiateTypeAsChoiceField(true);
        instantiateTypeAsChoiceField.setTest(new Test32(13));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateTypeAsChoiceField.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        InstantiateTypeAsChoiceField readInstantiateTypeAsChoiceField =
                new InstantiateTypeAsChoiceField(reader, true);
        assertTrue(instantiateTypeAsChoiceField.equals(readInstantiateTypeAsChoiceField));
    }
}
