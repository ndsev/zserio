package default_values;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;

import default_values.extended_default_values.ExtendedDefaultValues;
import test_utils.CompoundUtil;

public class ExtendedDefaultValuesTest
{
    @Test
    public void checkNoDefaultU32Field()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertTrue(0 == data.getNoDefaultU32Field());
    }

    @Test
    public void checkNoDefaultStringField()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertNull(data.getNoDefaultStringField());
    }

    @Test
    public void checkExtendedDefaultBoolField()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertTrue(data.isExtendedDefaultBoolFieldPresent());
        assertTrue(data.getExtendedDefaultBoolField());
    }

    @Test
    public void checkExtendedDefaultStringField()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertTrue(data.isExtendedDefaultStringFieldPresent());
        assertTrue("default".equals(data.getExtendedDefaultStringField()));
    }

    @Test
    public void checkExtendedOptionalDefaultFloatField()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertTrue(data.isExtendedOptionalDefaultFloatFieldPresent());
        assertNotNull(data.getExtendedOptionalDefaultFloatField());
        assertEquals(1.234F, data.getExtendedOptionalDefaultFloatField().floatValue());
    }

    @Test
    public void checkExtendedNoDefaultU32Field()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertTrue(data.isExtendedNoDefaultU32FieldPresent());
        assertEquals(0, data.getExtendedNoDefaultU32Field());
    }

    @Test
    public void checkExtendedNoDefaultExternField()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertTrue(data.isExtendedNoDefaultExternFieldPresent());
        assertNull(data.getExtendedNoDefaultExternField());
    }

    @Test
    public void checkExtendedOptionalNoDefaultU32Field()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertTrue(data.isExtendedOptionalNoDefaultU32FieldPresent());
        assertNull(data.getExtendedOptionalNoDefaultU32Field());
    }

    @Test
    public void checkExtendedOptionalNoDefaultBytesField()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        assertTrue(data.isExtendedOptionalNoDefaultBytesFieldPresent());
        assertNull(data.getExtendedOptionalNoDefaultBytesField());
    }

    @Test
    public void writeRead()
    {
        final ExtendedDefaultValues data = new ExtendedDefaultValues();
        data.setNoDefaultStringField("test");
        data.setExtendedNoDefaultExternField(new BitBuffer(new byte[10]));
        CompoundUtil.writeReadTest(ExtendedDefaultValues.class, data);
    }
}
