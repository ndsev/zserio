package functions.structure_param;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class StructureParamTest
{
    @Test
    public void checkMetresConverterCaller() throws IOException
    {
        final MetresConverterCaller metresConverterCaller = createMetresConverterCaller();
        assertEquals(CONVERTED_CM_VALUE, metresConverterCaller.getCm());

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        metresConverterCaller.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final byte[] expectedByteArray = writeMetresConverterCallerToByteArray();
        assertTrue(Arrays.equals(expectedByteArray, writtenByteArray));

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final MetresConverterCaller readMetresConverterCaller = new MetresConverterCaller(reader);
        assertEquals(metresConverterCaller, readMetresConverterCaller);
    }

    private byte[] writeMetresConverterCallerToByteArray() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(VALUE_A, 16);
        writer.writeBits(CONVERTED_CM_VALUE, 16);
        writer.close();

        return writer.toByteArray();
    }

    private MetresConverterCaller createMetresConverterCaller()
    {
        final MetresConverterCaller metresConverterCaller = new MetresConverterCaller();

        final MetresConverter metresConverter = new MetresConverter(M_VALUE_TO_CONVERT);
        metresConverter.setA(VALUE_A);

        metresConverterCaller.setMetresConverter(metresConverter);
        metresConverterCaller.setCm(CONVERTED_CM_VALUE);

        return metresConverterCaller;
    }

    private static int VALUE_A = 0xABCD;
    private static int M_VALUE_TO_CONVERT = 2;
    private static int CONVERTED_CM_VALUE = M_VALUE_TO_CONVERT * 100;
}
