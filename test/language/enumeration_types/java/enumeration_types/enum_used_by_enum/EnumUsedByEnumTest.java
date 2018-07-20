package enumeration_types.enum_used_by_enum;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnumUsedByEnumTest
{
    @Test
    public void lightColor()
    {
        assertEquals(VALUE_LIGHT_RED, LightColor.LIGHT_RED.getValue());
        assertEquals(VALUE_LIGHT_GREEN, LightColor.LIGHT_GREEN.getValue());
        assertEquals(VALUE_LIGHT_BLUE, LightColor.LIGHT_BLUE.getValue());
    }

    @Test
    public void darkColor()
    {
        assertEquals(VALUE_DARK_RED, DarkColor.DARK_RED.getValue());
        assertEquals(VALUE_DARK_GREEN, DarkColor.DARK_GREEN.getValue());
        assertEquals(VALUE_DARK_BLUE, DarkColor.DARK_BLUE.getValue());
    }

    @Test
    public void color()
    {
        assertEquals(VALUE_NONE, Color.NONE.getValue());

        assertEquals(VALUE_LIGHT_RED, Color.LIGHT_RED.getValue());
        assertEquals(VALUE_LIGHT_GREEN, Color.LIGHT_GREEN.getValue());
        assertEquals(VALUE_LIGHT_BLUE, Color.LIGHT_BLUE.getValue());
        assertEquals(VALUE_LIGHT_PINK, Color.LIGHT_PINK.getValue());

        assertEquals(VALUE_DARK_RED, Color.DARK_RED.getValue());
        assertEquals(VALUE_DARK_GREEN, Color.DARK_GREEN.getValue());
        assertEquals(VALUE_DARK_BLUE, Color.DARK_BLUE.getValue());
        assertEquals(VALUE_DARK_PINK, Color.DARK_PINK.getValue());
    }

    private static byte VALUE_NONE          = 0x00;
    private static byte VALUE_LIGHT_RED     = 0x01;
    private static byte VALUE_LIGHT_GREEN   = 0x02;
    private static byte VALUE_LIGHT_BLUE    = 0x03;
    private static byte VALUE_LIGHT_PINK    = 0x04;
    private static byte VALUE_DARK_RED      = 0x11;
    private static byte VALUE_DARK_GREEN    = 0x12;
    private static byte VALUE_DARK_BLUE     = 0x13;
    private static byte VALUE_DARK_PINK     = 0x14;
}
