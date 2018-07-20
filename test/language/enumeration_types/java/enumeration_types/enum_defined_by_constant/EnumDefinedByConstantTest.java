package enumeration_types.enum_defined_by_constant;

import enumeration_types.__ConstType;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnumDefinedByConstantTest
{
    @Test
    public void colors()
    {
        assertEquals(1, __ConstType.WHITE_COLOR);
        assertEquals(__ConstType.WHITE_COLOR, Colors.White.getValue());
        assertEquals(Colors.White.getValue() + 1, Colors.Black.getValue());
    }
}
