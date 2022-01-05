package expressions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;

import expressions.index_operator.*;

public class IndexOperatorTest
{
    @Test
    public void zeroLength() throws IOException
    {
        final ElementList list = createElementList(0);
        assertEquals(LENGTH_SIZE, list.bitSizeOf());

        final ElementList readlist = readWrite(list);
        assertEquals(0, readlist.getLength());
    }

    @Test
    public void oneElement() throws IOException
    {
        final int length = 1;
        final ElementList list = createElementList(length);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length);
    }

    @Test
    public void twoElements() throws IOException
    {
        final int length = 2;
        final ElementList list = createElementList(length);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length);
    }

    @Test
    public void threeElements() throws IOException
    {
        final int length = 3;
        final ElementList list = createElementList(length);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length);
    }

    @Test
    public void fourElements() throws IOException
    {
        final int length = 4;
        final ElementList list = createElementList(length);
        assertEquals(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
        checkElements(readWrite(list), length);
    }

    private static ElementList createElementList(int length)
    {
        final Element[] elements = new Element[length];
        for (int i = 0; i < length; ++i)
        {
            final boolean isEven = i % 2 + 1 == 2;
            final Element element = new Element(isEven);
            if (isEven)
                element.setField8((short)ELEMENTS[i]);
            else
                element.setField16((short)ELEMENTS[i]);

            elements[i] = element;
        }

        final ElementList list = new ElementList();
        list.setLength(elements.length);
        list.setElements(elements);

        return list;
    }

    private static ElementList readWrite(ElementList list) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        list.write(writer);
        final byte[] buffer = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
        final ElementList newList = new ElementList(reader);
        reader.close();

        return newList;
    }

    private static void checkElements(ElementList list, int length)
    {
        assertEquals(length, list.getLength());
        for (int i = 0; i < length; ++i)
        {
            final boolean isEven = i % 2 + 1 == 2;
            final Element element = list.getElements()[i];
            assertEquals(ELEMENTS[i], isEven ? element.getField8() : element.getField16());
        }
    }

    private static final int ELEMENTS[] = { 11, 33, 55, 77 };
    private static final int LENGTH_SIZE = 16;
    private static final int FIELD8_SIZE = 8;
    private static final int FIELD16_SIZE = 16;
};
