package optional_members;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

import optional_members.optional_expression.BasicColor;
import optional_members.optional_expression.BlackColor;
import optional_members.optional_expression.Container;

public class OptionalExpressionTest
{
    @Test
    public void bitSizeOf()
    {
        final Container container = new Container();
        container.setBasicColor(BasicColor.WHITE);
        assertEquals(CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.bitSizeOf());

        container.setBasicColor(BasicColor.BLACK);
        container.setNumBlackTones(NUM_BLACK_TONES);
        container.setBlackColor(createBlackColor(NUM_BLACK_TONES));
        assertEquals(CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.bitSizeOf());
    }

    @Test
    public void isNumBlackTonesSetAndUsed()
    {
        final Container container = new Container();
        container.setBasicColor(BasicColor.WHITE);
        assertFalse(container.isNumBlackTonesSet());
        assertFalse(container.isNumBlackTonesUsed());

        container.setBasicColor(BasicColor.BLACK);
        container.setNumBlackTones(NUM_BLACK_TONES);
        assertTrue(container.isNumBlackTonesSet());
        assertTrue(container.isNumBlackTonesUsed());
        assertEquals(NUM_BLACK_TONES, (short)container.getNumBlackTones());

        container.setBasicColor(BasicColor.WHITE); // set but not used
        assertTrue(container.isNumBlackTonesSet());
        assertFalse(container.isNumBlackTonesUsed());

        container.setBasicColor(BasicColor.BLACK);
        container.resetNumBlackTones(); // used but not set
        assertFalse(container.isNumBlackTonesSet());
        assertTrue(container.isNumBlackTonesUsed());
    }

    @Test
    public void resetNumBlackTones()
    {
        final Container container = new Container();
        container.setBasicColor(BasicColor.BLACK);
        container.setNumBlackTones(NUM_BLACK_TONES);
        assertTrue(container.isNumBlackTonesSet());
        assertTrue(container.isNumBlackTonesUsed());

        container.resetNumBlackTones(); // used but not set
        assertFalse(container.isNumBlackTonesSet());
        assertTrue(container.isNumBlackTonesUsed());
        assertEquals(null, container.getNumBlackTones());
    }

    @Test
    public void isBlackColorSetAndUsed()
    {
        final Container container = new Container();
        container.setBasicColor(BasicColor.WHITE);
        assertFalse(container.isBlackColorSet());
        assertFalse(container.isBlackColorUsed());

        container.setBasicColor(BasicColor.BLACK);
        final BlackColor blackColor = createBlackColor(NUM_BLACK_TONES);
        container.setBlackColor(blackColor);
        assertTrue(container.isBlackColorSet());
        assertTrue(container.isBlackColorUsed());
        assertTrue(blackColor.equals(container.getBlackColor()));

        container.resetBlackColor(); // used but not set
        assertFalse(container.isBlackColorSet());
        assertTrue(container.isBlackColorUsed());
        assertEquals(null, container.getBlackColor());
    }

    @Test
    public void resetBlackColor()
    {
        final Container container = new Container();
        container.setBasicColor(BasicColor.BLACK);
        final BlackColor blackColor = createBlackColor(NUM_BLACK_TONES);
        container.setBlackColor(blackColor);
        assertTrue(container.isBlackColorSet());
        assertTrue(container.isBlackColorUsed());

        container.resetBlackColor(); // used but not set
        assertFalse(container.isBlackColorSet());
        assertTrue(container.isBlackColorUsed());
        assertEquals(null, container.getBlackColor());
    }

    @Test
    public void equals()
    {
        final Container container1 = new Container();
        final Container container2 = new Container();
        assertTrue(container1.equals(container2));

        container1.setBasicColor(BasicColor.WHITE);
        container2.setBasicColor(BasicColor.BLACK);
        container2.setNumBlackTones(NUM_BLACK_TONES);
        container2.setBlackColor(createBlackColor(NUM_BLACK_TONES));
        assertFalse(container1.equals(container2));

        container2.setBasicColor(BasicColor.WHITE); // set but not used
        assertTrue(container1.equals(container2));
    }

    @Test
    public void hashCodeMethod()
    {
        final Container container1 = new Container();
        final Container container2 = new Container();
        assertEquals(container1.hashCode(), container2.hashCode());

        container1.setBasicColor(BasicColor.WHITE);
        container2.setBasicColor(BasicColor.BLACK);
        container2.setNumBlackTones(NUM_BLACK_TONES);
        container2.setBlackColor(createBlackColor(NUM_BLACK_TONES));
        assertTrue(container1.hashCode() != container2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(1703, container1.hashCode());
        assertEquals(2393199, container2.hashCode());

        container2.setBasicColor(BasicColor.WHITE);
        container2.setNumBlackTones(null);
        container2.setBlackColor(null);
        assertEquals(container1.hashCode(), container2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final Container container = new Container();
        container.setBasicColor(BasicColor.WHITE);
        final int bitPosition = 1;
        assertEquals(
                bitPosition + CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL, container.initializeOffsets(bitPosition));

        container.setBasicColor(BasicColor.BLACK);
        container.setNumBlackTones(NUM_BLACK_TONES);
        container.setBlackColor(createBlackColor(NUM_BLACK_TONES));
        assertEquals(bitPosition + CONTAINER_BIT_SIZE_WITH_OPTIONAL, container.initializeOffsets(bitPosition));
    }

    @Test
    public void writeRead() throws IOException
    {
        final Container container = new Container();
        container.setBasicColor(BasicColor.WHITE);
        final BitBuffer whiteBitBuffer = SerializeUtil.serialize(container);
        checkContainerInBitBuffer(whiteBitBuffer, BasicColor.WHITE, NUM_BLACK_TONES);
        Container readContainer = SerializeUtil.deserialize(Container.class, whiteBitBuffer);
        assertEquals(BasicColor.WHITE, readContainer.getBasicColor());
        assertFalse(readContainer.isNumBlackTonesSet());
        assertFalse(readContainer.isNumBlackTonesUsed());
        assertFalse(readContainer.isBlackColorSet());
        assertFalse(readContainer.isBlackColorUsed());

        container.setBasicColor(BasicColor.BLACK);
        container.setNumBlackTones(NUM_BLACK_TONES);
        final BlackColor blackColor = createBlackColor(NUM_BLACK_TONES);
        container.setBlackColor(blackColor);
        final BitBuffer blackBitBuffer = SerializeUtil.serialize(container);
        checkContainerInBitBuffer(blackBitBuffer, BasicColor.BLACK, NUM_BLACK_TONES);
        readContainer = SerializeUtil.deserialize(Container.class, blackBitBuffer);
        assertEquals(BasicColor.BLACK, readContainer.getBasicColor());
        assertEquals(NUM_BLACK_TONES, (short)readContainer.getNumBlackTones());
        assertTrue(blackColor.equals(readContainer.getBlackColor()));
        assertTrue(readContainer.isNumBlackTonesSet());
        assertTrue(readContainer.isNumBlackTonesUsed());
        assertTrue(readContainer.isBlackColorSet());
        assertTrue(readContainer.isBlackColorUsed());
    }

    private static BlackColor createBlackColor(short numBlackTones)
    {
        final BlackColor blackColor = new BlackColor(numBlackTones);

        final int tones[] = new int[numBlackTones];
        for (short i = 0; i < numBlackTones; ++i)
            tones[i] = i + 1;
        blackColor.setTones(tones);

        return blackColor;
    }

    private static void checkContainerInBitBuffer(
            BitBuffer bitBuffer, BasicColor basicColor, short numBlackTones) throws IOException
    {
        try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            if (basicColor == BasicColor.WHITE)
            {
                assertEquals(8, reader.getBufferBitSize());
                assertEquals(basicColor.getValue(), reader.readByte());
            }
            else
            {
                assertEquals((long)(1 + 1 + 4 * numBlackTones) * Byte.SIZE, reader.getBufferBitSize());
                assertEquals(basicColor.getValue(), reader.readByte());
                assertEquals(numBlackTones, reader.readByte());
                for (short i = 0; i < numBlackTones; ++i)
                    assertEquals(i + 1, reader.readInt());
            }
        }
    }

    private static short NUM_BLACK_TONES = 2;

    private static int CONTAINER_BIT_SIZE_WITHOUT_OPTIONAL = 8;
    private static int CONTAINER_BIT_SIZE_WITH_OPTIONAL = 8 + 8 + 32 * NUM_BLACK_TONES;
}
