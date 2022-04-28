package optional_members;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;

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
    }

    @Test
    public void initializeOffsets()
    {
        final Container container = new Container();
        container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        final int bitPosition = 1;
        assertEquals(bitPosition + CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL,
                container.initializeOffsets(bitPosition));

        container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        assertEquals(bitPosition + CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException
    {
        final Container container = new Container();
        container.setNonOptionalInt(NON_OPTIONAL_INT_VALUE);
        final File nonOptionalContainerFile = new File("non_optional.bin");
        container.write(nonOptionalContainerFile);
        checkContainerInFile(nonOptionalContainerFile, NON_OPTIONAL_INT_VALUE, null);
        final Container readNonOptionalContainer = new Container(nonOptionalContainerFile);
        assertEquals(NON_OPTIONAL_INT_VALUE, readNonOptionalContainer.getNonOptionalInt());
        assertFalse(readNonOptionalContainer.isAutoOptionalIntSet());
        assertFalse(readNonOptionalContainer.isAutoOptionalIntUsed());

        container.setAutoOptionalInt(AUTO_OPTIONAL_INT_VALUE);
        final File autoOptionalContainerFile = new File("auto_optional.bin");
        container.write(autoOptionalContainerFile);
        checkContainerInFile(autoOptionalContainerFile, NON_OPTIONAL_INT_VALUE, AUTO_OPTIONAL_INT_VALUE);
        final Container readAutoOptionalContainer = new Container(autoOptionalContainerFile);
        assertEquals(NON_OPTIONAL_INT_VALUE, readAutoOptionalContainer.getNonOptionalInt());
        assertTrue(readAutoOptionalContainer.isAutoOptionalIntSet());
        assertTrue(readAutoOptionalContainer.isAutoOptionalIntUsed());
        assertEquals(AUTO_OPTIONAL_INT_VALUE, (int) readAutoOptionalContainer.getAutoOptionalInt());
    }

    private static void checkContainerInFile(File file, int nonOptionalIntValue, Integer autoOptionalIntValue)
            throws IOException
    {
        final FileImageInputStream stream = new FileImageInputStream(file);

        if (autoOptionalIntValue == null)
        {
            assertEquals((CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL + 7) / Byte.SIZE, stream.length());
            assertEquals(nonOptionalIntValue, (int) stream.readBits(32));
            assertEquals(0, stream.readBit());
        }
        else
        {
            assertEquals((CONTAINER_BIT_SIZE_WITH_OPTIONAL + 7) / Byte.SIZE, stream.length());
            assertEquals(nonOptionalIntValue, (int) stream.readBits(32));
            assertEquals(1, stream.readBit());
            assertEquals((int) autoOptionalIntValue, (int) stream.readBits(32));
        }

        stream.close();
    }

    private static int NON_OPTIONAL_INT_VALUE = 0xDEADDEAD;
    private static int AUTO_OPTIONAL_INT_VALUE = 0xBEEFBEEF;

    private static int CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 32 + 1;
    private static int CONTAINER_BIT_SIZE_WITH_OPTIONAL = 32 + 1 + 32;
}
