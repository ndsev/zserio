package parameterized_types.grand_child_param;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.FileBitStreamReader;

public class GrandChildParamTest
{
    @Test
    public void fileWrite() throws IOException
    {
        final GrandChildParam grandChildParam = createGrandChildParam();
        final File file = new File("test1.bin");
        grandChildParam.write(file);
        checkGrandChildParamInFile(file, grandChildParam);
        final GrandChildParam readGrandChildParam = new GrandChildParam(file);
        assertEquals(grandChildParam, readGrandChildParam);
    }

    private GrandChildParam createGrandChildParam()
    {
        final Item item = new Item(ITEM_CHOICE_HOLDER_HAS_ITEM, ITEM_PARAM, ITEM_EXTRA_PARAM);
        ItemChoice itemChoice = new ItemChoice(ITEM_CHOICE_HOLDER_HAS_ITEM);
        itemChoice.setItem(item);
        final ItemChoiceHolder itemChoiceHolder = new ItemChoiceHolder(itemChoice.getHasItem(), itemChoice);

        return new GrandChildParam(itemChoiceHolder);
    }

    private void checkGrandChildParamInFile(File file, GrandChildParam grandChildParam) throws IOException
    {
        final BitStreamReader stream = new FileBitStreamReader(file);

        final ItemChoiceHolder itemChoiceHolder = grandChildParam.getItemChoiceHolder();
        assertEquals(itemChoiceHolder.getHasItem(), stream.readBool());

        final Item item = itemChoiceHolder.getItemChoice().getItem();
        assertEquals(item.getParam(), stream.readUnsignedShort());
        assertEquals((long)item.getExtraParam(), stream.readUnsignedInt());

        stream.close();
    }

    static final boolean ITEM_CHOICE_HOLDER_HAS_ITEM = true;
    static final int ITEM_PARAM = 0xAABB;
    static final long ITEM_EXTRA_PARAM = 0x11223344;
}
