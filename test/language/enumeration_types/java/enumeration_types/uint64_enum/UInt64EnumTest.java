package enumeration_types.uint64_enum;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

public class UInt64EnumTest
{
    @Test
    public void constructor()
    {
        final DarkColor darkColor = DarkColor.DARK_RED;
        assertEquals(DarkColor.DARK_RED, darkColor);
    }

    @Test
    public void toEnum()
    {
        DarkColor darkColor = DarkColor.toEnum(BigInteger.valueOf(0));
        assertEquals(DarkColor.NONE, darkColor);

        darkColor = DarkColor.toEnum(BigInteger.valueOf(1));
        assertEquals(DarkColor.DARK_RED, darkColor);

        darkColor = DarkColor.toEnum(BigInteger.valueOf(2));
        assertEquals(DarkColor.DARK_BLUE, darkColor);

        darkColor = DarkColor.toEnum(BigInteger.valueOf(7));
        assertEquals(DarkColor.DARK_BLACK, darkColor);
    }

    @Test(expected=IllegalArgumentException.class)
    public void toEnumFailure()
    {
        DarkColor.toEnum(BigInteger.valueOf(3));
    }
}
