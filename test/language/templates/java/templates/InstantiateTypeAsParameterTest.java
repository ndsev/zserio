package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_type_as_parameter.InstantiateTypeAsParameter;
import templates.instantiate_type_as_parameter.P32;
import templates.instantiate_type_as_parameter.Parameterized_P32;

public class InstantiateTypeAsParameterTest
{
    @Test
    public void readWrite() throws IOException
    {
        final P32 param = new P32(2);
        final long[] array = new long[] {13, 42};
        final Parameterized_P32 parameterized = new Parameterized_P32(param, array);

        final InstantiateTypeAsParameter instantiateTypeAsParameter =
                new InstantiateTypeAsParameter(param, parameterized);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateTypeAsParameter.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        InstantiateTypeAsParameter readInstantiateTypeAsParameter = new InstantiateTypeAsParameter(reader);
        assertTrue(instantiateTypeAsParameter.equals(readInstantiateTypeAsParameter));
    }
}
