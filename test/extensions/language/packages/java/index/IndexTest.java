package index;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class IndexTest
{
    @Test
    public void readWrite() throws Exception
    {
        final long[] indexes = new long[ARRAY_SIZE];
        final Element[] array = new Element[ARRAY_SIZE];
        final long[] indexesForParameterized = new long[ARRAY_SIZE];
        final ParameterizedElement[] parameterizedArray = new ParameterizedElement[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; ++i)
        {
            array[i] = new Element(i);
            parameterizedArray[i] = new ParameterizedElement(i, i);
        }
        final index.Test test = new index.Test(indexes, array, indexesForParameterized, parameterizedArray);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        test.initializeOffsets(writer.getBitPosition());
        test.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final index.Test readTest = new index.Test(reader);

        assertEquals(ARRAY_SIZE, readTest.getArray().length);
        assertEquals(ARRAY_SIZE, readTest.getParameterizedArray().length);
        assertEquals(test, readTest);
    }

    private static final int ARRAY_SIZE = 10;
}
