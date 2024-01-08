package enumeration_types.enum_defined_by_constant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EnumDefinedByConstantTest
{
    @Test
    public void colors()
    {
        assertEquals(1, WHITE_COLOR.WHITE_COLOR);
        assertEquals(WHITE_COLOR.WHITE_COLOR, Colors.WHITE.getValue());
        assertEquals(Colors.WHITE.getValue() + 1, Colors.BLACK.getValue());
    }
}
