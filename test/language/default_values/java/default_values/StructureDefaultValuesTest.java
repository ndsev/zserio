package default_values;

import static org.junit.Assert.*;

import org.junit.Test;

import default_values.structure_default_values.StructureDefaultValues;
import default_values.structure_default_values.BasicColor;
import default_values.structure_default_values.Permission;

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
        assertEquals(1.23f, structureDefaultValues.getFloat16Value(), 0.00001f);
    }

    @Test
    public void checkDefaultFloat32Value()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals(1.234f, structureDefaultValues.getFloat32Value(), 0.00001f);
    }

    @Test
    public void checkDefaultFloat64Value()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals(1.2345, structureDefaultValues.getFloat64Value(), 0.000000001);
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

    @Test
    public void checkDefaultBitmaskValue()
    {
        final StructureDefaultValues structureDefaultValues = new StructureDefaultValues();
        assertEquals(Permission.Values.READ_WRITE, structureDefaultValues.getBitmaskValue());
    }
}
