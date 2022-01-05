package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import choice_types.function_selector_choice.Selector;
import choice_types.function_selector_choice.TestChoice;

public class FunctionSelectorChoiceTest
{
    @Test
    public void field8()
    {
        final Selector selector = new Selector((byte)8);
        final TestChoice testChoice = new TestChoice(selector);
        testChoice.setField8((byte)0x7F);

        assertEquals(0x7F, testChoice.getField8());
        assertEquals(8, testChoice.bitSizeOf());
    }

    @Test
    public void field16()
    {
        final Selector selector = new Selector((byte)16);
        final TestChoice testChoice = new TestChoice(selector);
        testChoice.setField16((short)0x7F7F);

        assertEquals(0x7F7F, testChoice.getField16());
        assertEquals(16, testChoice.bitSizeOf());
    }
}
