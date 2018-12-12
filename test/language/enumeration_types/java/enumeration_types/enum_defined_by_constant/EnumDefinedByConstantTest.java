package enumeration_types.enum_defined_by_constant;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnumDefinedByConstantTest
{
    @Test
    public void colors()
    {
        assertEquals(1, WHITE_COLOR.WHITE_COLOR);
        assertEquals(WHITE_COLOR.WHITE_COLOR, Colors.White.getValue());
        assertEquals(Colors.White.getValue() + 1, Colors.Black.getValue());
    }
}
