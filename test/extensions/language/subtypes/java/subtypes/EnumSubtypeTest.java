package subtypes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import subtypes.enum_subtype.CONST_BLACK;
import subtypes.enum_subtype.Color;

public class EnumSubtypeTest
{
    @Test
    public void testSubtype()
    {
        assertEquals(Color.BLACK, CONST_BLACK.CONST_BLACK);
    }
}
