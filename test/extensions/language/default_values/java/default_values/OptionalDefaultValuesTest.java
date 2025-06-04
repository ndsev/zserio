package default_values;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import default_values.optional_default_values.OptionalDefaultValues;
import test_utils.CompoundUtil;

public class OptionalDefaultValuesTest
{
    @Test
    public void checkOptionalNoDefaultBoolField()
    {
        final OptionalDefaultValues data = new OptionalDefaultValues();
        assertFalse(data.isOptionalNoDefaultBoolFieldSet());
    }

    @Test
    public void checkOptionalNoDefaultStringField()
    {
        final OptionalDefaultValues data = new OptionalDefaultValues();
        assertFalse(data.isOptionalNoDefaultStringFieldSet());
    }

    @Test
    public void checkOptionalDefaultU32Field()
    {
        final OptionalDefaultValues data = new OptionalDefaultValues();
        assertTrue(data.isOptionalDefaultU32FieldSet());
        assertTrue(13 == data.getOptionalDefaultU32Field());
    }

    @Test
    public void checkOptionalDefaultF64Field()
    {
        final OptionalDefaultValues data = new OptionalDefaultValues();
        assertTrue(data.isOptionalDefaultF64FieldSet());
        assertTrue(1.234 == data.getOptionalDefaultF64Field());
    }

    @Test
    public void checkOptionalDefaultStringField()
    {
        final OptionalDefaultValues data = new OptionalDefaultValues();
        assertTrue(data.isOptionalDefaultStringFieldSet());
        assertTrue("default".equals(data.getOptionalDefaultStringField()));
    }

    @Test
    public void writeRead()
    {
        final OptionalDefaultValues data = new OptionalDefaultValues();
        CompoundUtil.writeReadTest(OptionalDefaultValues.class, data);
    }
}
