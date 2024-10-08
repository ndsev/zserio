package parameterized_types.grand_child_param;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class GrandChildParamTest
{
    @Test
    public void writeReadFile() throws IOException
    {
        final GrandChildParam grandChildParam = createGrandChildParam();
        SerializeUtil.serializeToFile(grandChildParam, BLOB_NAME);

        final GrandChildParam readGrandChildParam =
                SerializeUtil.deserializeFromFile(GrandChildParam.class, BLOB_NAME);
        assertEquals(grandChildParam, readGrandChildParam);
    }

    @Test
    public void writeRead() throws IOException
    {
        final GrandChildParam grandChildParam = createGrandChildParam();
        final BitBuffer bitBuffer = SerializeUtil.serialize(grandChildParam);
        checkGrandChildParamInBitBuffer(bitBuffer, grandChildParam);

        final GrandChildParam readGrandChildParam = SerializeUtil.deserialize(GrandChildParam.class, bitBuffer);
        assertEquals(grandChildParam, readGrandChildParam);
    }

    private ItemChoiceHolder createItemChoiceHolder()
    {
        final Item item = new Item(ITEM_CHOICE_HOLDER_HAS_ITEM, ITEM_PARAM, ITEM_EXTRA_PARAM);
        ItemChoice itemChoice = new ItemChoice(ITEM_CHOICE_HOLDER_HAS_ITEM);
        itemChoice.setItem(item);

        return new ItemChoiceHolder(itemChoice.getHasItem(), itemChoice);
    }

    private GrandChildParam createGrandChildParam()
    {
        final ItemChoiceHolder itemChoiceHolder = createItemChoiceHolder();
        final ItemChoiceHolder[] itemChoiceHolderArray = new ItemChoiceHolder[] {createItemChoiceHolder()};
        final long[] dummyArray = new long[] {0};

        return new GrandChildParam(itemChoiceHolder, itemChoiceHolderArray, dummyArray);
    }

    private void checkItemChoiceHolderInStream(BitStreamReader reader, ItemChoiceHolder itemChoiceHolder)
            throws IOException
    {
        assertEquals(itemChoiceHolder.getHasItem(), reader.readBool());

        final Item item = itemChoiceHolder.getItemChoice().getItem();
        assertEquals(item.getParam(), reader.readBits(16));
        assertEquals((long)item.getExtraParam(), reader.readBits(32));
    }

    private void checkGrandChildParamInBitBuffer(BitBuffer bitBuffer, GrandChildParam grandChildParam)
            throws IOException
    {
        try (final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            final ItemChoiceHolder itemChoiceHolder = grandChildParam.getItemChoiceHolder();
            checkItemChoiceHolderInStream(reader, itemChoiceHolder);

            final ItemChoiceHolder[] itemChoiceHolderArray = grandChildParam.getItemChoiceHolderArray();
            assertEquals(itemChoiceHolderArray.length, reader.readVarSize());
            checkItemChoiceHolderInStream(reader, itemChoiceHolderArray[0]);

            final boolean isDummyArrayUsed = grandChildParam.isDummyArrayUsed();
            assertEquals(isDummyArrayUsed, reader.readBool());
            if (isDummyArrayUsed)
            {
                final long[] dummyArray = grandChildParam.getDummyArray();
                assertEquals(dummyArray.length, reader.readVarSize());
                assertEquals(dummyArray[0], reader.readBits(32));
            }
        }
    }

    private static final String BLOB_NAME = "grand_child_param.blob";
    private static final boolean ITEM_CHOICE_HOLDER_HAS_ITEM = true;
    private static final int ITEM_PARAM = 0xAABB;
    private static final long ITEM_EXTRA_PARAM = 0x11223344;
}
