package zserio.extension.python;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class PythonSymbolConverterTest
{
    @Test
    public void symbolToModule()
    {
        assertEquals("my_long_structure", PythonSymbolConverter.symbolToModule("MyLongStructure"));
        assertEquals("my_long_structure", PythonSymbolConverter.symbolToModule("my_long_structure"));
        assertEquals("my_long_structure", PythonSymbolConverter.symbolToModule("My_long_structure"));
        assertEquals("my_long_structure", PythonSymbolConverter.symbolToModule("My_Long_Structure"));
        assertEquals("my_long6_structure", PythonSymbolConverter.symbolToModule("MyLong6Structure"));
        assertEquals("my_long6acd_structure", PythonSymbolConverter.symbolToModule("MyLong6ACDStructure"));
        assertEquals("my_long_6acd_structure", PythonSymbolConverter.symbolToModule("MyLong_6ACD_Structure"));

        assertEquals("fts3_virtual_table", PythonSymbolConverter.symbolToModule("Fts3VirtualTable"));
        assertEquals("line2d", PythonSymbolConverter.symbolToModule("Line2D"));
        assertEquals("vector2d_position", PythonSymbolConverter.symbolToModule("Vector2DPosition"));
    }

    @Test
    public void enumItemToSymbol()
    {
        assertEquals("MY_LONG_ENUM_ITEM", PythonSymbolConverter.enumItemToSymbol("MY_LONG_ENUM_ITEM"));
        assertEquals("MY_LONG_ENUM_ITEM", PythonSymbolConverter.enumItemToSymbol("MyLongEnumItem"));
        assertEquals("MY_LONG_ENUM_ITEM", PythonSymbolConverter.enumItemToSymbol("my_long_enum_item"));
        assertEquals("MY_LONG6_ENUM_ITEM", PythonSymbolConverter.enumItemToSymbol("MyLong6EnumItem"));
        assertEquals("MY_LONG6ACD_ENUM_ITEM", PythonSymbolConverter.enumItemToSymbol("MyLong6ACDEnumItem"));
        assertEquals("MY_LONG_6ACD_ENUM_ITEM", PythonSymbolConverter.enumItemToSymbol("MyLong_6ACD_EnumItem"));
    }

    @Test
    public void bitmaskValueToSymbol()
    {
        assertEquals("MY_LONG_BITMASK_VALUE",
                PythonSymbolConverter.bitmaskValueToSymbol("MY_LONG_BITMASK_VALUE"));
        assertEquals("MY_LONG_BITMASK_VALUE",
                PythonSymbolConverter.bitmaskValueToSymbol("MyLongBitmaskValue"));
        assertEquals("MY_LONG_BITMASK_VALUE",
                PythonSymbolConverter.bitmaskValueToSymbol("my_long_bitmask_value"));
        assertEquals("MY_LONG6_BITMASK_VALUE",
                PythonSymbolConverter.bitmaskValueToSymbol("MyLong6BitmaskValue"));
        assertEquals("MY_LONG6ACD_BITMASK_VALUE",
                PythonSymbolConverter.bitmaskValueToSymbol("MyLong6ACDBitmaskValue"));
        assertEquals("MY_LONG_6ACD_BITMASK_VALUE",
                PythonSymbolConverter.bitmaskValueToSymbol("MyLong_6ACD_BitmaskValue"));
    }

    @Test
    public void constantToSymbol()
    {
        assertEquals("MY_LONG_CONSTANT", PythonSymbolConverter.constantToSymbol("MY_LONG_CONSTANT"));
        assertEquals("MY_LONG_CONSTANT", PythonSymbolConverter.constantToSymbol("MyLongConstant"));
        assertEquals("MY_LONG_CONSTANT", PythonSymbolConverter.constantToSymbol("my_long_constant"));
        assertEquals("MY_LONG6_CONSTANT", PythonSymbolConverter.constantToSymbol("MyLong6Constant"));
        assertEquals("MY_LONG6ACD_CONSTANT", PythonSymbolConverter.constantToSymbol("MyLong6ACDConstant"));
        assertEquals("MY_LONG_6ACD_CONSTANT", PythonSymbolConverter.constantToSymbol("MyLong_6ACD_Constant"));
    }
}
