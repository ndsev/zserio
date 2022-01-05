package subtypes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import subtypes.bitmask_subtype.Permission;
import subtypes.bitmask_subtype.CONST_READ;

public class BitmaskSubtypeTest
{
    @Test
    public void testSubtype()
    {
        assertEquals(Permission.Values.READ, CONST_READ.CONST_READ);
    }
}
