package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import choice_types.enum_with_removed_item_param_choice.EnumWithRemovedItemParamChoice;
import choice_types.enum_with_removed_item_param_choice.Selector;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class EnumWithRemovedItemParamChoiceTest
{
    @Test
    public void writeRead()
    {
        final EnumWithRemovedItemParamChoice enumWithRemovedItemParamChoice =
                new EnumWithRemovedItemParamChoice(Selector.ZSERIO_REMOVED_GREY);
        enumWithRemovedItemParamChoice.setGreyData(0xCAFE);

        final BitBuffer bitBuffer = SerializeUtil.serialize(enumWithRemovedItemParamChoice);

        final EnumWithRemovedItemParamChoice readEnumWithRemovedItemParamChoice = SerializeUtil.deserialize(
                EnumWithRemovedItemParamChoice.class, bitBuffer, Selector.ZSERIO_REMOVED_GREY);
        assertEquals(enumWithRemovedItemParamChoice, readEnumWithRemovedItemParamChoice);
    }
}
