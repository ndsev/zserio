package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import org.junit.jupiter.api.Test;

public class ByteArrayBitStreamStringTest
{
    @Test
    public void readStrings() throws IOException
    {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            try (final PrintStream os = new PrintStream(baos, false, "UTF-8"))
            {
                // note that all lengths fits to first byte of varuint64
                os.write(7);
                os.print("HAMBURG");
                os.write(8); // Ü is C3 9C in UTF8
                os.print("MÜNCHEN");
                os.write(5); // Ö is C3 96 in UTF8
                os.print("KÖLN");
            }

            try (final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(baos.toByteArray()))
            {
                final String hh = in.readString();
                assertEquals("HAMBURG", hh);

                final String m = in.readString();
                assertEquals("MÜNCHEN", m);

                final String k = in.readString();
                assertEquals("KÖLN", k);
            }
        }
    }

    @Test
    public void writeZserioAndReadJdk() throws IOException
    {
        writeZserioAndReadJdk("HAMBURG", "MUNCHEN", "KOLN");
    }

    @Test
    public void writeZserioAndReadJdkUmlaut() throws IOException
    {
        writeZserioAndReadJdk("HAMBURG", "MÜNCHEN", "KÖLN");
    }

    @Test
    public void writeZserioAndReadZserio() throws IOException
    {
        writeZserioAndReadZserio("HAMBURG", "MUNCHEN", "KOLN");
    }

    @Test
    public void writeZserioAndReadZserioUmlaut() throws IOException
    {
        writeZserioAndReadZserio("HAMBURG", "MÜNCHEN", "KÖLN");
    }

    @Test
    public void writeAndReadUnaligned() throws IOException
    {
        writeZserioAndReadZserioUnaligned("HAMBURG", "MUNCHEN", "KOLN");
    }

    @Test
    public void writeAndReadUnalignedUmlaut() throws IOException
    {
        writeZserioAndReadZserioUnaligned("HAMBURG", "MÜNCHEN", "KÖLN");
    }

    private void writeZserioAndReadJdk(final String s1, final String s2, final String s3) throws IOException
    {
        final char[] charBuffer = new char[80];
        int numChars;

        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeString(s1);
            writer.writeString(s2);
            writer.writeString(s3);

            try (final Reader fileReader = new InputStreamReader(
                         new ByteArrayInputStream(writer.toByteArray()), "UTF-8"))
            {
                numChars = fileReader.read(charBuffer);
            }
        }

        final byte[] buffer = new String(charBuffer, 0, numChars).getBytes("UTF-8");

        final byte b1 = buffer[0];
        final String ss1 = new String(buffer, 1, b1, "UTF-8");
        assertEquals(s1, ss1);
        final byte b2 = buffer[1 + b1];
        final String ss2 = new String(buffer, 1 + b1 + 1, b2, "UTF-8");
        assertEquals(s2, ss2);
        final byte b3 = buffer[1 + b1 + 1 + b2];
        final String ss3 = new String(buffer, 1 + b1 + 1 + b2 + 1, b3, "UTF-8");
        assertEquals(s3, ss3);
        assertEquals(b1 + b2 + b3 + 3, buffer.length);
    }

    private void writeZserioAndReadZserio(final String s1, final String s2, final String s3) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeString(s1);
            writer.writeString(s2);
            writer.writeString(s3);

            try (final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(writer.toByteArray()))
            {
                final String hh = in.readString();
                assertEquals(s1, hh);

                final String m = in.readString();
                assertEquals(s2, m);

                final String k = in.readString();
                assertEquals(s3, k);
            }
        }
    }

    private void writeZserioAndReadZserioUnaligned(final String s1, final String s2, final String s3)
            throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(7, 4);
            writer.writeString(s1);
            writer.writeString(s2);
            writer.writeString(s3);

            try (final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(writer.toByteArray()))
            {
                final long b = in.readBits(4);
                assertEquals(7, b);

                final String hh = in.readString();
                assertEquals(s1, hh);

                final String m = in.readString();
                assertEquals(s2, m);

                final String k = in.readString();
                assertEquals(s3, k);
            }
        }
    }
}
