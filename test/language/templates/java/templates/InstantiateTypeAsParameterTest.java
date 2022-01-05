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

import templates.instantiate_type_as_parameter.InstantiateTypeAsParameter;
import templates.instantiate_type_as_parameter.P32;
import templates.instantiate_type_as_parameter.Parameterized_P32;

public class InstantiateTypeAsParameterTest
{
    @Test
    public void readWrite() throws IOException
    {
        final P32 param = new P32(2);
        final long[] array = new long[] { 13, 42 };
        final Parameterized_P32 parameterized = new Parameterized_P32(param, array);

        final InstantiateTypeAsParameter instantiateTypeAsParameter = new InstantiateTypeAsParameter(
                param, parameterized);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateTypeAsParameter.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        InstantiateTypeAsParameter readInstantiateTypeAsParameter =
                new InstantiateTypeAsParameter(reader);
        reader.close();
        assertTrue(instantiateTypeAsParameter.equals(readInstantiateTypeAsParameter));
    }

    private static final File TEST_FILE = new File("test.bin");
}
