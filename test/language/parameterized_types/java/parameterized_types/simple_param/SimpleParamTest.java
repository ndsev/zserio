package parameterized_types.simple_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.FileBitStreamReader;

public class SimpleParamTest
{
    @Test
    public void parameterConstructor()
    {
        final Item item = new Item(LOWER_VERSION);
        assertFalse(item.isExtraParamUsed());
    }

    @Test
    public void fileConstructor() throws IOException
    {
        final File file = new File("test.bin");
        final long version = LOWER_VERSION;
        writeItemToFile(file, version, ITEM_PARAM, ITEM_EXTRA_PARAM);
        final Item item = new Item(file, version);
        assertEquals(ITEM_PARAM, item.getParam());
        assertFalse(item.isExtraParamUsed());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final File file = new File("test.bin");
        final long version = HIGHER_VERSION;
        writeItemToFile(file, version, ITEM_PARAM, ITEM_EXTRA_PARAM);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final Item item = new Item(stream, version);
        assertEquals(ITEM_PARAM, item.getParam());
        assertTrue(item.isExtraParamUsed());
        assertEquals(Long.valueOf(ITEM_EXTRA_PARAM), item.getExtraParam());
    }

    @Test
    public void fieldConstructor() throws IOException
    {
        final Item item = new Item(HIGHER_VERSION, ITEM_PARAM, (long)ITEM_EXTRA_PARAM);
        assertEquals(ITEM_PARAM, item.getParam());
        assertTrue(item.isExtraParamUsed());
        assertEquals(Long.valueOf(ITEM_EXTRA_PARAM), item.getExtraParam());
    }

    @Test
    public void bitSizeOf()
    {
        final Item item1 = new Item(LOWER_VERSION, ITEM_PARAM, Long.valueOf(ITEM_EXTRA_PARAM));
        assertEquals(ITEM_BIT_SIZE_WITHOUT_OPTIONAL, item1.bitSizeOf());

        final Item item2 = new Item(HIGHER_VERSION, ITEM_PARAM, Long.valueOf(ITEM_EXTRA_PARAM));
        assertEquals(ITEM_BIT_SIZE_WITH_OPTIONAL, item2.bitSizeOf());
    }

    @Test
    public void equals()
    {
        final Item item1 = new Item(LOWER_VERSION);
        final Item item2 = new Item(LOWER_VERSION);
        assertTrue(item1.equals(item2));

        final Item item3 = new Item(HIGHER_VERSION);
        assertFalse(item2.equals(item3));
    }

    @Test
    public void hashCodeMethod()
    {
        final Item item1 = new Item(LOWER_VERSION);
        final Item item2 = new Item(LOWER_VERSION);
        assertEquals(item1.hashCode(), item2.hashCode());

        final Item item3 = new Item(HIGHER_VERSION);
        assertTrue(item2.hashCode() != item3.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final Item item1 = new Item(LOWER_VERSION, ITEM_PARAM, Long.valueOf(ITEM_EXTRA_PARAM));
        final int bitPosition = 1;
        assertEquals(bitPosition + ITEM_BIT_SIZE_WITHOUT_OPTIONAL, item1.initializeOffsets(bitPosition));

        final Item item2 = new Item(HIGHER_VERSION, ITEM_PARAM, Long.valueOf(ITEM_EXTRA_PARAM));
        assertEquals(bitPosition + ITEM_BIT_SIZE_WITH_OPTIONAL, item2.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException
    {
        final long version1 = LOWER_VERSION;
        final Item item1 = new Item(version1);
        item1.setParam(ITEM_PARAM);
        final File file = new File("test.bin");
        item1.write(file);
        checkItemInFile(file, item1, version1);
        Item readItem1 = new Item(file, version1);
        assertEquals(item1, readItem1);

        final long version2 = HIGHER_VERSION;
        final Item item2 = new Item(version2, ITEM_PARAM, (long)ITEM_EXTRA_PARAM);
        item2.write(file);
        checkItemInFile(file, item2, version2);
        Item readItem2 = new Item(file, version2);
        assertEquals(item2, readItem2);
    }

    private void writeItemToFile(File file, long version, short param, int extraParam) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);
        stream.writeShort(param);
        if (version >= HIGHER_VERSION)
            stream.writeInt(extraParam);
        stream.close();
    }

    private void checkItemInFile(File file, Item item, long version) throws IOException
    {
        final FileImageInputStream stream = new FileImageInputStream(file);
        assertEquals(item.getParam(), stream.readUnsignedShort());
        if (version >= HIGHER_VERSION)
            assertEquals(item.getExtraParam(), Long.valueOf(stream.readUnsignedInt()));
        stream.close();
    }

    private static final long LOWER_VERSION = 9;
    private static final long HIGHER_VERSION = 10;

    private static final short ITEM_PARAM = 0xAA;
    private static final int ITEM_EXTRA_PARAM = 0xBB;

    private static final int ITEM_BIT_SIZE_WITHOUT_OPTIONAL = 16;
    private static final int ITEM_BIT_SIZE_WITH_OPTIONAL = 16 + 32;
}
