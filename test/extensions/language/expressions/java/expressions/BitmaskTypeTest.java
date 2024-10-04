package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.bitmask_type.BitmaskTypeExpression;
import expressions.bitmask_type.Colors;

public class BitmaskTypeTest
{
    @Test
    public void bitSizeOfNoColor()
    {
        final BitmaskTypeExpression bitmaskTypeExpression = new BitmaskTypeExpression();
        bitmaskTypeExpression.setColors(new Colors());
        bitmaskTypeExpression.setHasNotColorRed(true);

        assertEquals(9, bitmaskTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfRed()
    {
        BitmaskTypeExpression bitmaskTypeExpression = new BitmaskTypeExpression();
        bitmaskTypeExpression.setColors(Colors.Values.RED);
        bitmaskTypeExpression.setHasColorRed(true);

        assertEquals(9, bitmaskTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfGreen()
    {
        BitmaskTypeExpression bitmaskTypeExpression = new BitmaskTypeExpression();
        bitmaskTypeExpression.setColors(Colors.Values.GREEN);
        bitmaskTypeExpression.setHasColorGreen(true);
        bitmaskTypeExpression.setHasNotColorRed(true);
        bitmaskTypeExpression.setHasOtherColorThanRed(true);

        assertEquals(11, bitmaskTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfBlue()
    {
        BitmaskTypeExpression bitmaskTypeExpression = new BitmaskTypeExpression();
        bitmaskTypeExpression.setColors(Colors.Values.BLUE);
        bitmaskTypeExpression.setHasColorBlue(true);
        bitmaskTypeExpression.setHasNotColorRed(true);
        bitmaskTypeExpression.setHasOtherColorThanRed(true);

        assertEquals(11, bitmaskTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfBlueGreen()
    {
        BitmaskTypeExpression bitmaskTypeExpression = new BitmaskTypeExpression();
        bitmaskTypeExpression.setColors(Colors.Values.BLUE.or(Colors.Values.GREEN));
        bitmaskTypeExpression.setHasColorGreen(true);
        bitmaskTypeExpression.setHasColorBlue(true);
        bitmaskTypeExpression.setHasNotColorRed(true);
        bitmaskTypeExpression.setHasOtherColorThanRed(true);

        assertEquals(12, bitmaskTypeExpression.bitSizeOf());
    }

    @Test
    public void bitSizeOfAllColors()
    {
        BitmaskTypeExpression bitmaskTypeExpression = new BitmaskTypeExpression();
        bitmaskTypeExpression.setColors(Colors.Values.RED.or(Colors.Values.GREEN).or(Colors.Values.BLUE));
        bitmaskTypeExpression.setHasColorRed(true);
        bitmaskTypeExpression.setHasColorGreen(true);
        bitmaskTypeExpression.setHasColorBlue(true);
        bitmaskTypeExpression.setHasAllColors(true);
        bitmaskTypeExpression.setHasOtherColorThanRed(true);

        assertEquals(13, bitmaskTypeExpression.bitSizeOf());
    }
}
