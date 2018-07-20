package expressions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;

import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;

import expressions.index_operator.*;

public class IndexOperatorTest
{
    @Test
    public void zeroLength() throws IOException
    {
        ElementList list = createElementList(0);
        assertEquals(LENGTH_SIZE, list.bitSizeOf());
        list = readWrite(list);
        assertEquals(0, list.getLength());
    }

    @Test
    public void oneElement() throws IOException
    {
        final int length = 1;
        ElementList list = createElementList(length);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length);
    }

    // TODO: This should throw an exception
    @Test
    public void oneElementWrongField() throws IOException
    {
        final int length = 1;
        ElementList list = createElementList(1);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length, true);
    }

    @Test
    public void twoElements() throws IOException
    {
        final int length = 2;
        ElementList list = createElementList(length);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length);
    }

    @Test
    public void threeElements() throws IOException
    {
        final int length = 3;
        ElementList list = createElementList(length);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length);
    }

    @Test
    public void fourElements() throws IOException
    {
        final int length = 4;
        ElementList list = createElementList(length);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length);
    }

    private static ElementList createElementList(int length)
    {
        ElementList list = new ElementList();
        List<Element> elements = new ArrayList<Element>();
        list.setElements(elements);
        for (int i = 0; i < length; ++i)
        {
            final boolean isEven = i % 2 + 1 == 2;
            Element element = new Element(isEven);
            if (isEven)
                element.setField8((short)ELEMENTS[i]);
            else
                element.setField16((short)ELEMENTS[i]);

            elements.add(element);
        }

        list.setLength(elements.size());
        return list;
    }

    private static ElementList readWrite(ElementList list) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        list.write(writer);
        byte[] buffer = writer.toByteArray();
        writer.close();
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
        ElementList newList = new ElementList(reader);
        reader.close();
        return newList;
    }

    private static void checkElements(ElementList list, int length)
    {
        checkElements(list, length, false);
    }

    private static void checkElements(ElementList list, int length, boolean lastWrong)
    {
        assertEquals(length, list.getLength());
        for (int i = 0; i < length; ++i)
        {
            final boolean isEven = i % 2 + 1 == 2;
            final boolean wrong = lastWrong && i + 1 == length;
            Element element = list.getElements().elementAt(i);
            assertEquals(ELEMENTS[i], (wrong ? !isEven : isEven) ? element.getField8() : element.getField16());
        }
    }

    private static final int ELEMENTS[] = { 11, 33, 55, 77 };
    private static final int LENGTH_SIZE = 16;
    private static final int FIELD8_SIZE = 8;
    private static final int FIELD16_SIZE = 16;
};
