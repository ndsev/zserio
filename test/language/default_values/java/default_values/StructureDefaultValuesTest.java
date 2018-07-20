package default_values;

import static org.junit.Assert.*;

import org.junit.Test;

import default_values.structure_default_values.BasicColor;
import default_values.structure_default_values.StructureDefaultValues;


public class StructureDefaultValuesTest
{
    @Test
    public void checkDefaultBoolValue()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals(true, structureDefaultValues.getBoolValue());
    }

    @Test
    public void checkDefaultBit4Value()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals(0x0F, structureDefaultValues.getBit4Value().byteValue());
    }

    @Test
    public void checkDefaultInt16Value()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals(0x0BEE, structureDefaultValues.getInt16Value());
    }

    @Test
    public void checkDefaultFloat16Value()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals(1.23f, structureDefaultValues.getFloat16Value(), Float.MIN_VALUE);
    }

    @Test
    public void checkDefaultStringValue()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals("string", structureDefaultValues.getStringValue());
    }

    @Test
    public void checkDefaultEnumValue()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals(BasicColor.BLACK, structureDefaultValues.getEnumValue());
    }
}
