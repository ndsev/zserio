package subtypes;

import static org.junit.Assert.*;

import org.junit.Test;

import subtypes.enum_subtype.Color;
import subtypes.enum_subtype.CONST_BLACK;

public class EnumSubtypeTest
{
    @Test
    public void testSubtype()
    {
        assertEquals(Color.BLACK, CONST_BLACK.CONST_BLACK);
    }
}
