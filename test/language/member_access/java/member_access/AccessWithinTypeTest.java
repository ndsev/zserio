package member_access;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import member_access.access_within_type.Header;
import member_access.access_within_type.Message;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class AccessWithinTypeTest
{
    @Test
    public void readConstructor() throws IOException, ZserioError
    {
        final int numSentences = 10;
        final boolean wrongArrayLength = false;
        final BitBuffer bitBuffer = writeMessageToBitBuffer(numSentences, wrongArrayLength);
        final BitStreamReader stream = new ByteArrayBitStreamReader(bitBuffer);
        final Message message = new Message(stream);
        checkMessage(message, numSentences);
    }

    @Test
    public void readWrongArrayLength() throws IOException, ZserioError
    {
        final int numSentences = 10;
        final boolean wrongArrayLength = true;
        final BitBuffer bitBuffer = writeMessageToBitBuffer(numSentences, wrongArrayLength);
        final BitStreamReader stream = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(IOException.class, () -> new Message(stream));
        stream.close();
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final int numSentences = 13;
        final boolean wrongArrayLength = false;
        final Message message = createMessage(numSentences, wrongArrayLength);
        final BitBuffer bitBuffer = SerializeUtil.serialize(message);

        final Message readMessage = SerializeUtil.deserialize(Message.class, bitBuffer);
        checkMessage(readMessage, numSentences);
        assertTrue(message.equals(readMessage));
    }

    @Test
    public void writeWrongArrayLength() throws IOException, ZserioError
    {
        final int numSentences = 13;
        final boolean wrongArrayLength = true;
        final Message message = createMessage(numSentences, wrongArrayLength);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> message.write(writer));
        writer.close();
    }

    private BitBuffer writeMessageToBitBuffer(int numSentences, boolean wrongArrayLength) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeUnsignedShort(VERSION_VALUE);
            writer.writeUnsignedShort(numSentences);
            final int numStrings = (wrongArrayLength) ? numSentences - 1 : numSentences;
            for (int i = 0; i < numStrings; ++i)
                writer.writeString(SENTENCE_PREFIX + i);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkMessage(Message message, int numSentences)
    {
        assertEquals(VERSION_VALUE, message.getHeader().getVersion());
        assertEquals(numSentences, message.getHeader().getNumSentences());

        final String[] sentences = message.getSentences();
        assertEquals(numSentences, sentences.length);
        for (int i = 0; i < numSentences; ++i)
        {
            final String expectedSentence = SENTENCE_PREFIX + i;
            assertTrue(sentences[i].equals(expectedSentence));
        }
    }

    private Message createMessage(int numSentences, boolean wrongArrayLength)
    {
        final Header header = new Header(VERSION_VALUE, numSentences);
        final int numStrings = (wrongArrayLength) ? numSentences - 1 : numSentences;
        final String[] sentences = new String[numStrings];
        for (int i = 0; i < numStrings; ++i)
            sentences[i] = SENTENCE_PREFIX + i;

        return new Message(header, sentences);
    }

    private static final int    VERSION_VALUE = 0xAB;
    private static final String SENTENCE_PREFIX = "This is sentence #";
}
