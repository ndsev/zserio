package zserio.runtime.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ByteArrayBitStreamVarNumTest
{
    @Test
    public void varOutOfRange() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(BitStreamWriter writer) throws IOException
            {
                // make sure writer is working
                writeSentinel(writer);

                IOException thrown = assertThrows(IOException.class,
                        () -> writer.writeVarUInt16((short)(((short)1) << (7 + 8))));
                assertThat(thrown.getMessage(), allOf(
                        startsWith("BitSizeOfCalculator: Value '"),
                        endsWith("' is out of range for varuint16!")));

                thrown = assertThrows(IOException.class, () -> writer.writeVarUInt32(1 << (7 + 7 + 7 + 8)));
                assertThat(thrown.getMessage(), allOf(
                        startsWith("BitSizeOfCalculator: Value '"),
                        endsWith("' is out of range for varuint32!")));

                thrown = assertThrows(IOException.class,
                        () -> writer.writeVarUInt64(1L << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)));
                assertThat(thrown.getMessage(), allOf(
                        startsWith("BitSizeOfCalculator: Value '"),
                        endsWith("' is out of range for varuint64!")));

                thrown = assertThrows(IOException.class,
                        () -> writer.writeVarInt16((short)(((short)1) << (6 + 8))));
                assertThat(thrown.getMessage(), allOf(
                        startsWith("BitSizeOfCalculator: Value '"),
                        endsWith("' is out of range for varint16!")));

                thrown = assertThrows(IOException.class, () -> writer.writeVarInt32(1 << (6 + 7 + 7 + 8)));
                assertThat(thrown.getMessage(), allOf(
                        startsWith("BitSizeOfCalculator: Value '"),
                        endsWith("' is out of range for varint32!")));

                thrown = assertThrows(IOException.class,
                        () -> writer.writeVarInt64(1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)));
                assertThat(thrown.getMessage(), allOf(
                        startsWith("BitSizeOfCalculator: Value '"),
                        endsWith("' is out of range for varint64!")));

                thrown = assertThrows(IOException.class, () -> writer.writeVarSize(1 << (2 + 7 + 7 + 7 + 8)));
                assertThat(thrown.getMessage(), allOf(
                        startsWith("BitSizeOfCalculator: Value '"),
                        endsWith("' is out of range for varsize!")));

                {
                    // overflow, 2^32 - 1 is too much ({ 0x83, 0xFF, 0xFF, 0xFF, 0xFF } is the maximum)
                    final byte[] buffer = new byte[] { (byte) 0x8F, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                            (byte)0xFF };
                    final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
                    thrown = assertThrows(IOException.class, () -> reader.readVarSize());
                    assertThat(thrown.getMessage(), allOf(
                            startsWith("ByteArrayBitStreamReader: Read value '"),
                            endsWith("' is out of range for varsize type!")));
                }

                {
                    // overflow, 2^36 - 1 is too much ({ 0x83, 0xFF, 0xFF, 0xFF, 0xFF } is the maximum)
                    final byte[] buffer = new byte[] { (byte) 0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                            (byte)0xFF };
                    final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
                    thrown = assertThrows(IOException.class, () -> reader.readVarSize());
                    assertThat(thrown.getMessage(), allOf(
                            startsWith("ByteArrayBitStreamReader: Read value '"),
                            endsWith("' is out of range for varsize type!")));
                }
            }

            @Override
            public void read(BitStreamReader reader) throws IOException
            {
                readSentinel(reader);
            }
        });
    }

    @Test
    public void varInt64() throws IOException
    {
        final long varInt64Limits[] =
        {
            (1L << 0), (1L << 6) - 1,
            (1L << 6), (1L << (6 + 7)) - 1,
            (1L << (6 + 7)), (1L << (6 + 7 + 7)) - 1,
            (1L << (6 + 7 + 7)), (1L << (6 + 7 + 7 + 7)) - 1,
            (1L << (6 + 7 + 7 + 7)), (1L << (6 + 7 + 7 + 7 + 7)) - 1,
            (1L << (6 + 7 + 7 + 7 + 7)), (1L << (6 + 7 + 7 + 7 + 7 + 7)) - 1,
            (1L << (6 + 7 + 7 + 7 + 7 + 7)), (1L << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
            (1L << (6 + 7 + 7 + 7 + 7 + 7 + 7)), (1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1,
        };

        writeReadTest(new WriteReadTestable() {
            @Override
            public void write(BitStreamWriter writer) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarInt64(sanityVarNums[i]);
                    writer.writeVarInt64(-sanityVarNums[i]);
                }

                for (int i = 0; i < varInt64Limits.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarInt64(varInt64Limits[i]);
                    writer.writeVarInt64(-varInt64Limits[i]);
                }

                writeSentinel(writer);
            }

            @Override
            public void read(BitStreamReader reader) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(sanityVarNums[i], reader.readVarInt64());
                    assertEquals(-sanityVarNums[i], reader.readVarInt64());
                }

                for (int i = 0; i < varInt64Limits.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(varInt64Limits[i], reader.readVarInt64());
                    assertEquals(-varInt64Limits[i], reader.readVarInt64());
                }

                readSentinel(reader);
            }
        });
    }

    @Test
    public void varInt32() throws IOException
    {
        final int varInt32Limits[] =
        {
            (1 << 0), (1 << 6) - 1,
            (1 << 6), (1 << (6 + 7)) - 1,
            (1 << (6 + 7)), (1 << (6 + 7 + 7)) - 1,
            (1 << (6 + 7 + 7)), (1 << (6 + 7 + 7 + 8)) - 1,
        };

        writeReadTest(new WriteReadTestable() {
            @Override
            public void write(BitStreamWriter writer) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarInt32(sanityVarNums[i]);
                    writer.writeVarInt32(-sanityVarNums[i]);
                }

                for (int i = 0; i < varInt32Limits.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarInt32(varInt32Limits[i]);
                    writer.writeVarInt32(-varInt32Limits[i]);
                }

                writeSentinel(writer);
            }

            @Override
            public void read(BitStreamReader reader) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(sanityVarNums[i], reader.readVarInt32());
                    assertEquals(-sanityVarNums[i], reader.readVarInt32());
                }

                for (int i = 0; i < varInt32Limits.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(varInt32Limits[i], reader.readVarInt32());
                    assertEquals(-varInt32Limits[i], reader.readVarInt32());
                }

                readSentinel(reader);
            }
        });
    }

    @Test
    public void varInt16() throws IOException
    {
        final short varInt16Limits[] =
        {
                (((short)1) << 0), (((short)1) << 6) - 1,
                (((short)1) << 6), (((short)1) << (6 + 8)) - 1
        };

        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(BitStreamWriter writer) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarInt16(sanityVarNums[i]);
                    writer.writeVarInt16((short) - sanityVarNums[i]);
                }

                for (int i = 0; i < varInt16Limits.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarInt16(varInt16Limits[i]);
                    writer.writeVarInt16((short) -varInt16Limits[i]);
                }

                writeSentinel(writer);
            }

            @Override
            public void read(BitStreamReader reader) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(sanityVarNums[i], reader.readVarInt16());
                    assertEquals(-sanityVarNums[i], reader.readVarInt16());
                }

                for (int i = 0; i < varInt16Limits.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(varInt16Limits[i], reader.readVarInt16());
                    assertEquals(-varInt16Limits[i], reader.readVarInt16());
                }

                readSentinel(reader);
            }
        });
    }

    @Test
    public void varUInt64() throws IOException
    {
        final long varUInt64Limits[] =
        {
            (1L << 0), (1L << 7) - 1,
            (1L << 7), (1L << (7 + 7)) - 1,
            (1L << (7 + 7)), (1L << (7 + 7 + 7)) - 1,
            (1L << (7 + 7 + 7)), (1L << (7 + 7 + 7 + 7)) - 1,
            (1L << (7 + 7 + 7 + 7)), (1L << (7 + 7 + 7 + 7 + 7)) - 1,
            (1L << (7 + 7 + 7 + 7 + 7)), (1L << (7 + 7 + 7 + 7 + 7 + 7)) - 1,
            (1L << (7 + 7 + 7 + 7 + 7 + 7)), (1L << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1,
            (1L << (7 + 7 + 7 + 7 + 7 + 7 + 7)), (1L << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1,
        };

        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(BitStreamWriter writer) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarUInt64(sanityVarNums[i]);
                }

                for (int i = 0; i < varUInt64Limits.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarUInt64(varUInt64Limits[i]);
                }

                writeSentinel(writer);
            }

            @Override
            public void read(BitStreamReader reader) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(sanityVarNums[i], reader.readVarUInt64());
                }

                for (int i = 0; i < varUInt64Limits.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(varUInt64Limits[i], reader.readVarUInt64());
                }

                readSentinel(reader);
            }
        });
    }

    @Test
    public void varUInt32() throws IOException
    {
        final int varUInt32Limits[] =
        {
            (1 << 0), (1 << 7) - 1,
            (1 << 7), (1 << (7 + 7)) - 1,
            (1 << (7 + 7)), (1 << (7 + 7 + 7)) - 1,
            (1 << (7 + 7 + 7)), (1 << (7 + 7 + 7 + 8)) - 1
        };

        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(BitStreamWriter writer) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarUInt32(sanityVarNums[i]);
                }

                for (int i = 0; i < varUInt32Limits.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarUInt32(varUInt32Limits[i]);
                }

                writeSentinel(writer);
            }

            @Override
            public void read(BitStreamReader reader) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(sanityVarNums[i], reader.readVarUInt32());
                }

                for (int i = 0; i < varUInt32Limits.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(varUInt32Limits[i], reader.readVarUInt32());
                }

                readSentinel(reader);
            }
        });
    }

    @Test
    public void varUInt16() throws IOException
    {
        final short varUInt16Limits[] =
        {
            (((short)1) << 0), (((short)1) << 7) - 1,
            (((short)1) << 7), (((short)1) << (7 + 8)) - 1
        };

        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(BitStreamWriter writer) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarUInt16(sanityVarNums[i]);
                }

                for (int i = 0; i < varUInt16Limits.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarUInt16(varUInt16Limits[i]);
                }

                writeSentinel(writer);
            }

            @Override
            public void read(BitStreamReader reader) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(sanityVarNums[i], reader.readVarUInt16());
                }
                for (int i = 0; i < varUInt16Limits.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(varUInt16Limits[i], reader.readVarUInt16());
                }

                readSentinel(reader);
            }
        });
    }

    @Test
    public void varSize() throws IOException
    {
        final int varSizeLimits[] =
        {
            (1 << 0), (1 << 7) - 1,
            (1 << 7), (1 << (7 + 7)) - 1,
            (1 << (7 + 7)), (1 << (7 + 7 + 7)) - 1,
            (1 << (7 + 7 + 7)), (1 << (7 + 7 + 7 + 7)) - 1,
            (1 << (7 + 7 + 7 + 7)), (1 << (2 + 7 + 7 + 7 + 8)) - 1,
        };

        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(BitStreamWriter writer) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarSize(sanityVarNums[i]);
                }

                for (int i = 0; i < varSizeLimits.length; i++)
                {
                    writeSentinel(writer);
                    writer.writeVarSize(varSizeLimits[i]);
                }

                writeSentinel(writer);
            }

            @Override
            public void read(BitStreamReader reader) throws IOException
            {
                for (int i = 0; i < sanityVarNums.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(sanityVarNums[i], reader.readVarSize());
                }

                for (int i = 0; i < varSizeLimits.length; i++)
                {
                    readSentinel(reader);
                    assertEquals(varSizeLimits[i], reader.readVarSize());
                }

                readSentinel(reader);
            }
        });
    }

    private interface WriteReadTestable
    {
        void write(BitStreamWriter writer) throws IOException;
        void read(BitStreamReader reader) throws IOException;
    }

    private void writeReadTest(WriteReadTestable writeReadTest) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writeReadTest.write(writer);
        final byte[] data = writer.toByteArray();
        writer.close();

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(data);
        try
        {
            writeReadTest.read(reader);
        }
        finally
        {
            reader.close();
        }
    }

    private static void writeSentinel(BitStreamWriter writer) throws IOException
    {
        writer.writeByte((byte)0x00);
        writer.writeByte((byte)0xFF);
        writer.writeByte((byte)0x00);
    }

    private static void readSentinel(BitStreamReader reader) throws IOException
    {
        assertEquals((byte)0x00, reader.readByte());
        assertEquals((byte)0xFF, reader.readByte());
        assertEquals((byte)0x00, reader.readByte());
    }

    private static short sanityVarNums[] =
    {
        0, 10, 100
    };
}
