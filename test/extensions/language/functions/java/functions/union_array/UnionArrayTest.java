package functions.union_array;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class UnionArrayTest
{
    @Test
    public void checkInnerElement0() throws IOException
    {
        checkInner(0);
    }

    @Test
    public void checkInnerElement1() throws IOException
    {
        checkInner(1);
    }

    @Test
    public void checkInnerElement2() throws IOException
    {
        checkInner(2);
    }

    @Test
    public void checkOuterArrayExplicitElement() throws IOException
    {
        checkInner(NUM_ITEM_ELEMENTS);
    }

    private byte[] writeInnerToByteArray(int pos) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(NUM_ITEM_ELEMENTS, 16);

        for (Item item : ITEMS)
        {
            writer.writeBits(item.getValueA(), 8);
            writer.writeBits(item.getValueB(), 8);
        }

        final short isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? (short)1 : (short)0;
        writer.writeVarSize(isExplicit != 0 ? 0 : 1);
        if (isExplicit != 0)
        {
            writer.writeBits(EXPLICIT_ITEM.getValueA(), 8);
            writer.writeBits(EXPLICIT_ITEM.getValueB(), 8);
        }
        else
        {
            writer.writeBits(pos, 16);
        }

        writer.close();

        return writer.toByteArray();
    }

    private Inner createInner(int pos)
    {
        final OuterArray outerArray = new OuterArray();
        outerArray.setNumElements(NUM_ITEM_ELEMENTS);
        outerArray.setValues(ITEMS);

        final short isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? (short)1 : (short)0;
        final ItemRef itemRef = new ItemRef(outerArray);
        if (isExplicit != 0)
        {
            itemRef.setItem(EXPLICIT_ITEM);
        }
        else
        {
            itemRef.setPosition(pos);
        }

        return new Inner(outerArray, itemRef);
    }

    private void checkInner(int pos) throws IOException
    {
        final Inner inner = createInner(pos);
        final short isExplicit = (pos >= NUM_ITEM_ELEMENTS) ? (short)1 : (short)0;
        if (isExplicit != 0)
        {
            assertEquals(EXPLICIT_ITEM, inner.getRef().funcGetItem());
        }
        else
        {
            assertEquals(ITEMS[pos], inner.getRef().funcGetElement());
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        inner.write(writer);
        final byte[] writtenByteArray = writer.toByteArray();

        final byte[] expectedByteArray = writeInnerToByteArray(pos);
        assertTrue(Arrays.equals(expectedByteArray, writtenByteArray));

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writtenByteArray, writer.getBitPosition());
        final Inner readInner = new Inner(reader);

        assertEquals(inner, readInner);
    }

    private static final Item[] ITEMS = new Item[] {
            new Item((short)12, (short)13), new Item((short)42, (short)18), new Item((short)17, (short)14)};
    private static final int NUM_ITEM_ELEMENTS = ITEMS.length;

    private static final Item EXPLICIT_ITEM = new Item((short)27, (short)29);
}
