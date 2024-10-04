package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import choice_types.expression_selector_choice.ExpressionSelectorChoice;

public class ExpressionSelectorChoiceTest
{
    @Test
    public void field8()
    {
        final ExpressionSelectorChoice expressionSelectorChoice = new ExpressionSelectorChoice(0);
        final byte value8 = 0x7F;
        expressionSelectorChoice.setField8(value8);

        assertEquals(value8, expressionSelectorChoice.getField8());
        assertEquals(8, expressionSelectorChoice.bitSizeOf());
    }

    @Test
    public void field16()
    {
        final ExpressionSelectorChoice expressionSelectorChoice = new ExpressionSelectorChoice(1);
        final short value16 = 0x7F7F;
        expressionSelectorChoice.setField16(value16);

        assertEquals(value16, expressionSelectorChoice.getField16());
        assertEquals(16, expressionSelectorChoice.bitSizeOf());
    }

    @Test
    public void field32()
    {
        final ExpressionSelectorChoice expressionSelectorChoice = new ExpressionSelectorChoice(2);
        final int value32 = 0x7F7F7F7F;
        expressionSelectorChoice.setField32(value32);

        assertEquals(value32, expressionSelectorChoice.getField32());
        assertEquals(32, expressionSelectorChoice.bitSizeOf());
    }
}
