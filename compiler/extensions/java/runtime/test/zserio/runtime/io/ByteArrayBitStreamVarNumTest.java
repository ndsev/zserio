/**
 *
 */
package zserio.runtime.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

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

                try
                {
                    writer.writeVarUInt16((short)(((short)1) << (7 + 8)));
                    fail();
                }
                catch (IOException e)
                {
                    assertTrue(e.getMessage().startsWith("ByteArrayBitStreamWriter: Can't write VarUInt16. "
                                + "Value ") && e.getMessage().endsWith(" is out of range."));
                }

                try
                {
                    writer.writeVarUInt32(1 << (7 + 7 + 7 + 8));
                    fail();
                }
                catch (IOException e)
                {
                    assertTrue(e.getMessage().startsWith("ByteArrayBitStreamWriter: Can't write VarUInt32. "
                            + "Value ") && e.getMessage().endsWith(" is out of range."));
                }

                try
                {
                    writer.writeVarUInt64(1L << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8));
                    fail();
                }
                catch (IOException e)
                {
                    assertTrue(e.getMessage().startsWith("ByteArrayBitStreamWriter: Can't write VarUInt64. "
                            + "Value ") && e.getMessage().endsWith(" is out of range."));
                }

                try
                {
                    writer.writeVarInt16((short)(((short)1) << (6 + 8)));
                    fail();
                }
                catch (IOException e)
                {
                    assertTrue(e.getMessage().startsWith("ByteArrayBitStreamWriter: Can't write VarInt16. "
                            + "Value ") && e.getMessage().endsWith(" is out of range."));
                }

                try
                {
                    writer.writeVarInt32(1 << (6 + 7 + 7 + 8));
                    fail();
                }
                catch (IOException e)
                {
                    assertTrue(e.getMessage().startsWith("ByteArrayBitStreamWriter: Can't write VarInt32. "
                            + "Value ") && e.getMessage().endsWith(" is out of range."));
                }

                try
                {
                    writer.writeVarInt64(1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8));
                    fail();
                }
                catch (IOException e)
                {
                    assertTrue(e.getMessage().startsWith("ByteArrayBitStreamWriter: Can't write VarInt64. "
                            + "Value ") && e.getMessage().endsWith(" is out of range."));
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
