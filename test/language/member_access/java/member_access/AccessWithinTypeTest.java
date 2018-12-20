package member_access;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import member_access.access_within_type.Header;
import member_access.access_within_type.Message;

import zserio.runtime.ZserioError;
import zserio.runtime.array.StringArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class AccessWithinTypeTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final int numSentences = 10;
        final boolean wrongArrayLength = false;
        final File file = new File("test.bin");
        writeMessageToFile(file, numSentences, wrongArrayLength);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final Message message = new Message(stream);
        stream.close();
        checkMessage(message, numSentences);
    }

    @Test(expected=IOException.class)
    public void readWrongArrayLength() throws IOException, ZserioError
    {
        final int numSentences = 10;
        final boolean wrongArrayLength = true;
        final File file = new File("test.bin");
        writeMessageToFile(file, numSentences, wrongArrayLength);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final Message message = new Message(stream);
        stream.close();
        checkMessage(message, numSentences);
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final int numSentences = 13;
        final boolean wrongArrayLength = false;
        final Message message = createMessage(numSentences, wrongArrayLength);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        message.write(writer);
        writer.close();
        final Message readMessage = new Message(file);
        checkMessage(readMessage, numSentences);
        assertTrue(message.equals(readMessage));
    }

    @Test(expected=ZserioError.class)
    public void writeWrongArrayLength() throws IOException, ZserioError
    {
        final int numSentences = 13;
        final boolean wrongArrayLength = true;
        final Message message = createMessage(numSentences, wrongArrayLength);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        message.write(writer);
        writer.close();
    }

    private void writeMessageToFile(File file, int numSentences, boolean wrongArrayLength) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeUnsignedShort(VERSION_VALUE);
        writer.writeUnsignedShort(numSentences);
        final int numStrings = (wrongArrayLength) ? numSentences - 1 : numSentences;
        for (int i = 0; i < numStrings; ++i)
            writer.writeString(SENTENCE_PREFIX + i);

        writer.close();
    }

    private void checkMessage(Message message, int numSentences)
    {
        assertEquals(VERSION_VALUE, message.getHeader().getVersion());
        assertEquals(numSentences, message.getHeader().getNumSentences());

        final StringArray sentences = message.getSentences();
        assertEquals(numSentences, sentences.length());
        for (int i = 0; i < numSentences; ++i)
        {
            final String expectedSentence = SENTENCE_PREFIX + i;
            assertTrue(sentences.elementAt(i).equals(expectedSentence));
        }
    }

    private Message createMessage(int numSentences, boolean wrongArrayLength)
    {
        final Header header = new Header(VERSION_VALUE, numSentences);
        final int numStrings = (wrongArrayLength) ? numSentences - 1 : numSentences;
        final StringArray sentences = new StringArray(numStrings);
        for (int i = 0; i < numStrings; ++i)
            sentences.setElementAt(SENTENCE_PREFIX + i, i);

        return new Message(header, sentences);
    }

    private static final int    VERSION_VALUE = 0xAB;
    private static final String SENTENCE_PREFIX = "This is sentence #";
}
