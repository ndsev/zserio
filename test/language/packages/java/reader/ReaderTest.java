package reader;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedIntArray;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;

public class ReaderTest
{
    @Test
    public void readWrite() throws Exception
    {
        final UnsignedIntArray indexes = new UnsignedIntArray(ARRAY_SIZE);
        final List<Element> array = new ArrayList<Element>();
        final UnsignedIntArray indexesForParameterized = new UnsignedIntArray(ARRAY_SIZE);
        final List<ParameterizedElement> parameterizedArray = new ArrayList<ParameterizedElement>();
        for (int i = 0; i < ARRAY_SIZE; ++i)
        {
            array.add(new Element(i));
            parameterizedArray.add(new ParameterizedElement(i, i));
        }
        final reader.Test test = new reader.Test(indexes, new ObjectArray<Element>(array),
                indexesForParameterized, new ObjectArray<ParameterizedElement>(parameterizedArray));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        test.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final reader.Test readTest = new reader.Test(reader);

        assertEquals(ARRAY_SIZE, readTest.getArray().length());
        assertEquals(ARRAY_SIZE, readTest.getParameterizedArray().length());
        assertEquals(test, readTest);
    }

    private static final int ARRAY_SIZE = 10;
}
