package parameterized_types.enumeration_param;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class EnumerationParamTest
{
    @Test
    public void equals() throws IOException
    {
        final EnumerationParam enumerationParam1 = new EnumerationParam(BasicColor.WHITE, 0);
        final EnumerationParam enumerationParam2 = new EnumerationParam(BasicColor.WHITE, 0);
        assertTrue(enumerationParam1.equals(enumerationParam2));

        enumerationParam2.setField(1);
        assertFalse(enumerationParam1.equals(enumerationParam2));

        final EnumerationParam enumerationParam3 = new EnumerationParam(BasicColor.BLACK, null);
        assertFalse(enumerationParam1.equals(enumerationParam3));
        assertFalse(enumerationParam2.equals(enumerationParam3));
    }

    @Test
    public void hashCodeMethod() throws IOException
    {
        final EnumerationParam enumerationParam1 = new EnumerationParam(BasicColor.WHITE, 0);
        final EnumerationParam enumerationParam2 = new EnumerationParam(BasicColor.WHITE, 0);
        assertEquals(enumerationParam1.hashCode(), enumerationParam2.hashCode());

        enumerationParam2.setField(1);
        assertTrue(enumerationParam1.hashCode() != enumerationParam2.hashCode());

        final EnumerationParam enumerationParam3 = new EnumerationParam(BasicColor.BLACK, null);
        assertTrue(enumerationParam1.hashCode() != enumerationParam3.hashCode());
        assertTrue(enumerationParam2.hashCode() != enumerationParam3.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(63011, enumerationParam1.hashCode());
        assertEquals(63012, enumerationParam2.hashCode());
        assertEquals(1702, enumerationParam3.hashCode());
    }

    @Test
    public void writeRead() throws IOException
    {
        final EnumerationParam enumerationParam = new EnumerationParam(BasicColor.WHITE, 1);
        final BitBuffer bitBuffer = SerializeUtil.serialize(enumerationParam);
        final EnumerationParam readEnumerationParam =
                SerializeUtil.deserialize(EnumerationParam.class, bitBuffer, BasicColor.WHITE);
        assertEquals(enumerationParam, readEnumerationParam);
    }
}
