package optional_members;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

import optional_members.auto_optional.Container;

public class AutoOptionalTest
{
    @Test
    public void bitSizeOf()
    {
        final Container container = new Container();
        container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        assertEquals(CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitSizeOf());

        container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        assertEquals(CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitSizeOf());
    }

    @Test
    public void isAutoOptionalIntSetAndUsed()
    {
        final Container container = new Container();
        container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        assertFalse(container.isAutoOptionalIntSet());
        assertFalse(container.isAutoOptionalIntUsed());

        container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        assertTrue(container.isAutoOptionalIntSet());
        assertTrue(container.isAutoOptionalIntUsed());
    }

    @Test
    public void resetAutoOptionalInt()
    {
        final Container container = new Container();
        container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        assertTrue(container.isAutoOptionalIntSet());
        assertTrue(container.isAutoOptionalIntUsed());

        container.resetAutoOptionalInt();
        assertFalse(container.isAutoOptionalIntSet());
        assertFalse(container.isAutoOptionalIntUsed());
    }

    @Test
    public void equals()
    {
        final Container container1 = new Container();
        final Container container2 = new Container();
        assertTrue(container1.equals(container2));

        container1.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        container1.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        container2.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        assertFalse(container1.equals(container2));

        container2.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        assertTrue(container1.equals(container2));

        container1.resetAutoOptionalInt();
        assertFalse(container1.equals(container2));
    }

    @Test
    public void hashCodeMethod()
    {
        final Container container1 = new Container();
        final Container container2 = new Container();
        assertEquals(container1.hashCode(), container2.hashCode());

        container1.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        container1.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        container2.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        assertTrue(container1.hashCode() != container2.hashCode());

        container2.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        assertEquals(container1.hashCode(), container2.hashCode());

        container1.resetAutoOptionalInt();
        assertTrue(container1.hashCode() != container2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals((int)3735937536L, container1.hashCode());
        assertEquals((int)3994118383L, container2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final Container container = new Container();
        container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        final int bitPosition = 1;
        assertEquals(
                bitPosition + CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.initializeOffsets(bitPosition));

        container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        assertEquals(bitPosition + CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.initializeOffsets(bitPosition));
    }

    @Test
    public void writeRead() throws IOException
    {
        final Container container = new Container();
        container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        final BitBuffer nonOptionalBitBuffer = SerializeUtil.serialize(container);
        checkContainerInBitBuffer(nonOptionalBitBuffer, NON_OPTIONAL_INT_VALUE, null);
        final Container readNonOptionalContainer =
                SerializeUtil.deserialize(Container.class, nonOptionalBitBuffer);
        assertEquals(NON_OPTIONAL_INT_VALUE, readNonOptionalContainer.getNonOptionalInt());
        assertFalse(readNonOptionalContainer.isAutoOptionalIntSet());
        assertFalse(readNonOptionalContainer.isAutoOptionalIntUsed());

        container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        final BitBuffer autoOptionalBitBuffer = SerializeUtil.serialize(container);
        checkContainerInBitBuffer(autoOptionalBitBuffer, NON_OPTIONAL_INT_VALUE, AUTO_OPTIONAL_INT_VALUE);
        final Container readAutoOptionalContainer =
                SerializeUtil.deserialize(Container.class, autoOptionalBitBuffer);
        assertEquals(NON_OPTIONAL_INT_VALUE, readAutoOptionalContainer.getNonOptionalInt());
        assertTrue(readAutoOptionalContainer.isAutoOptionalIntSet());
        assertTrue(readAutoOptionalContainer.isAutoOptionalIntUsed());
        assertEquals(AUTO_OPTIONAL_INT_VALUE, (int)readAutoOptionalContainer.getAutoOptionalInt());
    }

    private static void checkContainerInBitBuffer(
            BitBuffer bitBuffer, int nonOptionalIntValue, Integer autoOptionalIntValue) throws IOException
    {
        try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            if (autoOptionalIntValue == null)
            {
                assertEquals(CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, reader.getBufferBitSize());
                assertEquals(nonOptionalIntValue, (int)reader.readBits(32));
                assertEquals(0, reader.readBits(1));
            }
            else
            {
                assertEquals(CONTAINER_BIT_SIZE_WITH_OPTIONAL, reader.getBufferBitSize());
                assertEquals(nonOptionalIntValue, (int)reader.readBits(32));
                assertEquals(1, reader.readBits(1));
                assertEquals((int)autoOptionalIntValue, (int)reader.readBits(32));
            }
        }
    }

    private static int NON_OPTIONAL_INT_VALUE = 0xDEADDEAD;
    private static int AUTO_OPTIONAL_INT_VALUE = 0xBEEFBEEF;

    private static int CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 32 + 1;
    private static int CONTAINER_BIT_SIZE_WITH_OPTIONAL = 32 + 1 + 32;
}
