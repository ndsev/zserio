package functions.choice_array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class ChoiceArrayTest
{
    @Test
    public void checkChoiceArrayFunctionElement0() throws IOException
    {
        checkChoiceArrayFunction(0);
    }

    @Test
    public void checkChoiceArrayFunctionElement1() throws IOException
    {
        checkChoiceArrayFunction(1);
    }

    @Test
    public void checkChoiceArrayFunctionElement2() throws IOException
    {
        checkChoiceArrayFunction(2);
    }

    @Test
    public void checkChoiceArrayFunctionExplicitElement() throws IOException
    {
        checkChoiceArrayFunction(NUM_ITEM_ELEMENTS);
    }

    private byte[] writeOuterArrayToByteArray(int pos) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(NUM_ITEM_ELEMENTS, 16);

        for (Item item : ITEMS)
        {
            writer.writeBits(item.getA(), 8);
            writer.writeBits(item.getB(), 8);
        }

        final short isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? (short)1 : (short)0;
        writer.writeBits(isExplicit, 8);
        short elementA;
        if (isExplicit != 0)
        {
            writer.writeBits(EXPLICIT_ITEM.getA(), 8);
            writer.writeBits(EXPLICIT_ITEM.getB(), 8);
            elementA = EXPLICIT_ITEM.getA();
        }
        else
        {
            writer.writeBits(pos, 16);
            elementA = ITEMS[pos].getA();
        }

        if (elementA == ELEMENT_A_FOR_EXTRA_VALUE)
            writer.writeSignedBits(EXTRA_VALUE, 32);

        writer.close();

        return writer.toByteArray();
    }

    private Inner createInner(int pos)
    {
        final OuterArray outerArray = new OuterArray();

        outerArray.setNumElements(NUM_ITEM_ELEMENTS);
        outerArray.setValues(ITEMS);

        final Inner inner = new Inner();
        inner.setOuterArray(outerArray);

        final short isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? (short)1 : (short)0;
        inner.setIsExplicit(isExplicit);

        final ItemRef itemRef = new ItemRef(inner.getIsExplicit(), outerArray);
        short elementA;
        if (isExplicit != 0)
        {
            itemRef.setItem(EXPLICIT_ITEM);
            elementA = EXPLICIT_ITEM.getA();
        }
        else
        {
            itemRef.setPos(pos);
            elementA = ITEMS[pos].getA();
        }
        inner.setRef(itemRef);

        if (elementA == ELEMENT_A_FOR_EXTRA_VALUE)
            inner.setExtra(EXTRA_VALUE);

        return inner;
    }

    private void checkChoiceArrayFunction(int pos) throws IOException
    {
        final Inner inner = createInner(pos);
        final Item readElement = inner.getRef().funcGetElement();
        if (pos >= NUM_ITEM_ELEMENTS)
        {
            assertEquals(EXPLICIT_ITEM, readElement);
        }
        else
        {
            assertEquals(ITEMS[pos], readElement);
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        inner.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();
        writer.close();

        final byte[] expectedByteArray = writeOuterArrayToByteArray(pos);
        assertTrue(Arrays.equals(expectedByteArray, writtenByteArray));

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writtenByteArray);
        final Inner readInner = new Inner(reader);
        assertEquals(inner, readInner);
    }

    private static final short ELEMENT_A_FOR_EXTRA_VALUE = 20;
    private static final int EXTRA_VALUE = 4711;

    private static final Item[] ITEMS = new Item[] {
            new Item((short)12, (short)13),
            new Item(ELEMENT_A_FOR_EXTRA_VALUE, (short)18),
            new Item((short)17, (short)14)
    };
    private static final int NUM_ITEM_ELEMENTS = ITEMS.length;

    private static final Item EXPLICIT_ITEM = new Item((short)27, (short)29);
}
