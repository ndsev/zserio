package optional_members;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

import optional_members.optional_enumeration.BasicColor;
import optional_members.optional_enumeration.Container;

public class OptionalEnumerationTest
{
    @Test
    public void bitSizeOf()
    {
        final Container container = new Container();
        assertEquals(CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitSizeOf());

        container.setBasicColor(BasicColor.WHITE);
        assertEquals(CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitSizeOf());
    }

    @Test
    public void isBasicColorSetAndUsed()
    {
        final Container container = new Container();
        assertFalse(container.isBasicColorSet());
        assertFalse(container.isBasicColorUsed());

        container.setBasicColor(BasicColor.WHITE);
        assertTrue(container.isBasicColorSet());
        assertTrue(container.isBasicColorUsed());
        assertEquals(BasicColor.WHITE, container.getBasicColor());
    }

    @Test
    public void resetBasicColor()
    {
        final Container container = new Container();
        container.setBasicColor(BasicColor.WHITE);
        assertTrue(container.isBasicColorSet());
        assertTrue(container.isBasicColorUsed());

        container.resetBasicColor();
        assertFalse(container.isBasicColorSet());
        assertFalse(container.isBasicColorUsed());
        assertEquals(null, container.getBasicColor());
    }

    @Test
    public void equals()
    {
        final Container container1 = new Container();
        final Container container2 = new Container();
        assertTrue(container1.equals(container2));

        container1.setBasicColor(BasicColor.WHITE);
        assertFalse(container1.equals(container2));

        container2.setBasicColor(BasicColor.BLACK);
        assertFalse(container1.equals(container2));
    }

    @Test
    public void hashCodeMethod()
    {
        final Container container1 = new Container();
        final Container container2 = new Container();
        assertEquals(container1.hashCode(), container2.hashCode());

        container1.setBasicColor(BasicColor.WHITE);
        assertTrue(container1.hashCode() != container2.hashCode());

        container2.setBasicColor(BasicColor.BLACK);
        assertTrue(container1.hashCode() != container2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(1703, container1.hashCode());
        assertEquals(1702, container2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final Container container = new Container();
        final int bitPosition = 1;
        assertEquals(
                bitPosition + CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.initializeOffsets(bitPosition));

        container.setBasicColor(BasicColor.WHITE);
        assertEquals(bitPosition + CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.initializeOffsets(bitPosition));
    }

    @Test
    public void writeRead() throws IOException
    {
        final Container container = new Container();
        BitBuffer bitBuffer = SerializeUtil.serialize(container);
        checkContainerInBitBuffer(bitBuffer);
        Container readContainer = SerializeUtil.deserialize(Container.class, bitBuffer);
        assertFalse(readContainer.isBasicColorSet());
        assertFalse(readContainer.isBasicColorUsed());

        container.setBasicColor(BasicColor.WHITE);
        bitBuffer = SerializeUtil.serialize(container);
        checkContainerInBitBuffer(bitBuffer, BasicColor.WHITE);
        readContainer = SerializeUtil.deserialize(Container.class, bitBuffer);
        assertEquals(BasicColor.WHITE, readContainer.getBasicColor());
        assertTrue(readContainer.isBasicColorSet());
        assertTrue(readContainer.isBasicColorUsed());
    }

    private static void checkContainerInBitBuffer(BitBuffer bitBuffer) throws IOException
    {
        try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            assertEquals(CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, reader.getBufferBitSize());
            assertEquals(0, reader.readBits(1));
        }
    }

    private static void checkContainerInBitBuffer(BitBuffer bitBuffer, BasicColor basicColor) throws IOException
    {
        try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            assertEquals(CONTAINER_BIT_SIZE_WITH_OPTIONAL, reader.getBufferBitSize());
            assertEquals(1, reader.readBits(1));
            assertEquals(basicColor.getValue(), reader.readByte());
        }
    }

    private static int CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 1;
    private static int CONTAINER_BIT_SIZE_WITH_OPTIONAL = 1 + 8;
}
