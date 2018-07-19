/**
 *
 */
package zserio.runtime.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;

import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.junit.Test;

public class ByteArrayBitStreamReaderTest
{
    /**
     * Test the get byte order method.
     */
    @Test
    public void getByteOrder()
    {
        final ByteOrder expected = ByteOrder.BIG_ENDIAN;

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(new byte[] {});
        assertEquals(expected, reader.getByteOrder());
    }

    /**
     * Test the exception in the protected readRange method.
     *
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void rangeMinException() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                reader.readBits(-1);
            }
        });
    }

    /**
     * Test the exception in the protected readRange method.
     *
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void rangeMaxException() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                reader.readBits(65);
            }
        });
    }

    /**
     * Test the bit offset getter.
     * @throws IOException
     */
    @Test
    public void bitOffset() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(0, reader.getBitOffset());
            }
        });
    }

    @Test
    public void readByte() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                byte b;
                long pos;
                long bytePos;

                b = reader.readByte();
                assertTrue(b == 6 * 16 + 7);
                b = reader.readByte();
                assertTrue(b == 8 * 16 + 9 - 256);
                pos = reader.getBitPosition();
                assertTrue(pos == 16);
                bytePos = reader.getBytePosition();
                assertTrue(bytePos == 2);

                reader.setBitPosition(0);

                b = reader.readByte();
                assertTrue(b == 6 * 16 + 7);
                b = reader.readByte();
                assertTrue(b == 8 * 16 + 9 - 256);
                pos = reader.getBitPosition();
                assertTrue(pos == 16);
                bytePos = reader.getBytePosition();
                assertTrue(bytePos == 2);
            }
        });
    }

    @Test
    public void readUnsignedByte() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                int b = reader.readByte();
                assertTrue(b == 6 * 16 + 7);
                b = reader.readByte();
                assertTrue(b == 8 * 16 + 9 - 256);
                long pos = reader.getBitPosition();
                assertTrue(pos == 16);
                long bytePos = reader.getBytePosition();
                assertTrue(bytePos == 2);

                reader.setBitPosition(0);

                b = reader.readByte();
                assertTrue(b == 6 * 16 + 7);
                b = reader.readByte();
                assertTrue(b == 8 * 16 + 9 - 256);
                pos = reader.getBitPosition();
                assertTrue(pos == 16);
                bytePos = reader.getBytePosition();
                assertTrue(bytePos == 2);
            }
        });
    }

    @Test
    public void readUnsignedInt1() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                final short uint8 = (short)reader.readUnsignedByte();
                assertEquals(uint8, 0x67);
                final long uint32 = reader.readUnsignedInt();
                assertEquals(uint32, 0x891234CDL);
            }
        });
    }

    @Test
    public void readUnsignedShort() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                final short uint8 = (short)reader.readUnsignedByte();
                assertEquals(uint8, 0x67);
                final int uint16 = reader.readUnsignedShort();
                assertEquals(uint16, 0x8912);
            }
        });
    }

    @Test
    public void bitStreamReader() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                long v = reader.readBits(2);
                assertTrue(v == 6 >> 2);
                assertTrue(reader.getBitPosition() == 2);
                assertTrue(reader.getBytePosition() == 0);
                v = reader.readBits(2);
                assertTrue(v == (6 & 0x03));
                assertTrue(reader.getBitPosition() == 4);
                assertTrue(reader.getBytePosition() == 0);
                v = reader.readBits(2);
                assertTrue(v == 7 >> 2);
                assertTrue(reader.getBitPosition() == 6);
                assertTrue(reader.getBytePosition() == 0);
                v = reader.readBits(2);
                assertTrue(v == (7 & 0x03));
                assertTrue(reader.getBitPosition() == 8);
                assertTrue(reader.getBytePosition() == 1);
                v = reader.readBits(2);
                assertTrue(v == 8 >> 2);
                assertTrue(reader.getBitPosition() == 10);
                assertTrue(reader.getBytePosition() == 1);
                v = reader.readBits(2);
                assertTrue(v == (8 & 0x03));
                assertTrue(reader.getBitPosition() == 12);
                assertTrue(reader.getBytePosition() == 1);
                v = reader.readBits(2);
                assertTrue(v == 9 >> 2);
                assertTrue(reader.getBitPosition() == 14);
                assertTrue(reader.getBytePosition() == 1);
                v = reader.readBits(2);
                assertTrue(v == (9 & 0x03));
                assertTrue(reader.getBitPosition() == 16);
                assertTrue(reader.getBytePosition() == 2);
            }
        });
    }

    @Test
    public void readByteNotAligned() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                long v;
                int uint8;
                v = reader.readBits(4);
                assertTrue(v == 6);
                uint8 = reader.readUnsignedByte();
                assertTrue(uint8 == 0x78);
                assertTrue(reader.getBitPosition() == 12);
                assertTrue(reader.getBytePosition() == 1);
                uint8 = reader.readUnsignedByte();
                assertTrue(uint8 == 0x91);
                assertTrue(reader.getBitPosition() == 20);
                assertTrue(reader.getBytePosition() == 2);
            }
        });
    }

    @Test
    public void setBitPosition() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                short s;
                final short t = 0x6789;
                int uint8;
                s = reader.readShort();
                assertTrue(s == t);

                reader.setBitPosition(0);
                uint8 = reader.readUnsignedByte();
                assertTrue(uint8 == 0x67);
                assertTrue(reader.getBytePosition() == 1);
                assertTrue(reader.getBitPosition() == 8);

                reader.setBitPosition(1);
                uint8 = reader.readUnsignedByte();
                assertTrue(uint8 == (t & 0x7F80) >> 7);
                assertTrue(reader.getBytePosition() == 1);
                assertTrue(reader.getBitPosition() == 9);

                reader.setBitPosition(2);
                uint8 = reader.readUnsignedByte();
                assertTrue(uint8 == (t & 0x3FC0) >> 6);
                assertTrue(reader.getBytePosition() == 1);
                assertTrue(reader.getBitPosition() == 10);

                reader.setBitPosition(3);
                uint8 = reader.readUnsignedByte();
                assertTrue(uint8 == (t & 0x1FE0) >> 5);
                assertTrue(reader.getBytePosition() == 1);
                assertTrue(reader.getBitPosition() == 11);

                reader.setBitPosition(4);
                uint8 = reader.readUnsignedByte();
                assertTrue(uint8 == 0x78);
                assertTrue(reader.getBytePosition() == 1);
                assertTrue(reader.getBitPosition() == 12);
            }
        });
    }

    @Test
    public void signedBitfield1() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeByte(-1);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(-1L, reader.readSignedBits(3));
                assertEquals(-1L, reader.readSignedBits(2));
                assertEquals(-1L, reader.readSignedBits(3));
            }
        });
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readLine() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                reader.readLine();
            }
        });
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readUTF() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                reader.readUTF();
            }
        });
    }

    /**
     * Test the skipBytes() method.
     *
     * @throws IOException if the skipping fails
     */
    @Test
    public void skipBytes() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(0, reader.getBytePosition());
                assertEquals(1, reader.skipBytes(1)); // mostly to silent FindBugs
                assertEquals(1, reader.getBytePosition());
                assertEquals(3, reader.skipBytes(3)); // mostly to silent FindBugs
                assertEquals(4, reader.getBytePosition());
            }
        });
    }

    /**
     * Test the seek method.
     *
     * @throws IOException
     */
    @Test
    public void seek() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                reader.seek(1);
                assertEquals(-119, reader.readByte());
                reader.seek(0);
                assertEquals(103, reader.readByte());
                reader.seek(1);
                assertEquals(-119, reader.readByte());
                reader.seek(100);
            }
        });
    }

    /**
     * Test the rewind method.
     *
     * @throws IOException if the reader method calls fails
     */
    @Test
    public void rewind() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                reader.readByte();
                reader.readByte();
                assertEquals(2, reader.getBytePosition());
                reader.rewind();
                assertEquals(0, reader.getBytePosition());
            }
        });
    }

    @Test
    public void signedBitfield2() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeShort(-10000);
        writer.close();
        final byte[] blob = writer.toByteArray();
        assertEquals(2, blob.length);

        final BitStreamReader sReader = new ByteArrayBitStreamReader(blob);
        final long s = sReader.readSignedBits(16);
        assertEquals(-10000L, s);

        final BitStreamReader uReader = new ByteArrayBitStreamReader(blob);
        final long u = uReader.readBits(16);
        assertEquals(55536L, u);
    }

    @Test
    public void signedBitfield3() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
            }
        });

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeSignedBits(-5, 10);
        writer.writeSignedBits(-6, 10);
        writer.writeSignedBits(-7, 10);
        writer.close();
        final byte[] blob = writer.toByteArray();
        assertEquals(4, blob.length);

        final BitStreamReader sReader = new ByteArrayBitStreamReader(blob);
        long s1 = sReader.readSignedBits(10);
        long s2 = sReader.readSignedBits(10);
        long s3 = sReader.readSignedBits(10);
        assertEquals(-5L, s1);
        assertEquals(-6L, s2);
        assertEquals(-7L, s3);

        final BitStreamReader uReader = new ByteArrayBitStreamReader(blob);
        long u1 = uReader.readBits(10);
        long u2 = uReader.readBits(10);
        long u3 = uReader.readBits(10);
        assertEquals(1019, u1);
        assertEquals(1018, u2);
        assertEquals(1017, u3);
    }

    @Test
    public void alignTo() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                reader.alignTo(10);
                assertEquals(0, reader.getBitPosition());
                reader.skipBits(1);
                reader.alignTo(10);
                assertEquals(10, reader.getBitPosition());
            }
        });
    }

    @Test
    public void skipBits() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(0, reader.getBitPosition());
                reader.skipBits(10);
                assertEquals(10, reader.getBitPosition());
            }
        });
    }

    @Test
    public void readBoolean() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeBoolean(true);
                writer.writeBoolean(false);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertTrue(reader.readBoolean());
                assertFalse(reader.readBoolean());
            }
        });
    }

    @Test
    public void readLong() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeLong(Long.MAX_VALUE);
                writer.writeLong(Long.MIN_VALUE);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(Long.MAX_VALUE, reader.readLong());
                assertEquals(Long.MIN_VALUE, reader.readLong());
            }
        });
    }

    @Test
    public void readInt() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeInt(Integer.MAX_VALUE);
                writer.writeInt(Integer.MIN_VALUE);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(Integer.MAX_VALUE, reader.readInt());
                assertEquals(Integer.MIN_VALUE, reader.readInt());
            }
        });
    }

    @Test
    public void readDouble() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeDouble(Double.MAX_VALUE);
                writer.writeDouble(1.0d);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(Double.MAX_VALUE, reader.readDouble(), 0);
                assertEquals(1.0d, reader.readDouble(), 0);
            }
        });
    }

    @Test
    public void readChar() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeChar('c');
                writer.writeChars("abc");
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals('c', reader.readChar());
                assertEquals('a', reader.readChar());
                assertEquals('b', reader.readChar());
                assertEquals('c', reader.readChar());
            }
        });
    }

    @Test
    public void readString() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                for (String s : DATA)
                {
                    writeString(writer, s);
                }
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                for (String s : DATA)
                {
                    assertEquals(s, reader.readString());
                }
            }

            // write string in a format understood by ByteArrayBitStreamReader.readString()
            private void writeString(ImageOutputStream writer, String s) throws IOException
            {
                // see DATA below, all strings have length which fits to first byte of varint64
                writer.writeByte(s.length());
                writer.write(s.getBytes("UTF8"));
            }

            private /*static*/ final String DATA[] =
            {
                "",
                "tmp",
                "test"
            };
        });
    }

    @Test
    public void readBool() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                for (boolean value : DATA)
                {
                    writeBool(writer, value);
                }
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                for (boolean value : DATA)
                {
                    assertEquals(value, reader.readBool());
                }
            }

            // write boolean in a format understood by ByteArrayBitStreamReader.readBool()
            private void writeBool(ImageOutputStream writer, boolean value) throws IOException
            {
                writer.writeBit(value ? 1 : 0);
            }

            private /*static*/ final boolean DATA[] =
            {
                false,
                false,
                true
            };
        });
    }

    @Test
    public void readBigInteger() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeLong(10);
                writer.writeLong(Long.MAX_VALUE);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(BigInteger.TEN, reader.readBigInteger(64));
                assertEquals(BigInteger.valueOf(Long.MAX_VALUE), reader.readBigInteger(64));
            }
        });
    }

    @Test
    public void readSignedBigInteger() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(BigInteger.valueOf(103), reader.readSignedBigInteger(8));
                assertEquals(BigInteger.valueOf(-8), reader.readSignedBigInteger(4));
            }
        });
    }

    @Test
    public void readSignedBigInteger2() throws IOException
    {
        final BigInteger bigIntLongLong = BigInteger.valueOf(Long.MAX_VALUE);
        final BigInteger bigIntLongLongLong = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);

        final ByteArrayBitStreamWriter babsw = new ByteArrayBitStreamWriter();
        babsw.writeBigInteger(bigIntLongLong, 64);
        babsw.writeBigInteger(bigIntLongLongLong, 64);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(babsw.toByteArray());

        assertEquals(bigIntLongLong, in.readSignedBigInteger(64));
        assertEquals(bigIntLongLongLong, in.readBigInteger(64));
    }

    @Test
    public void readFully() throws IOException
    {
        final byte[] data = new byte[] { (byte)0, (byte)1 };

        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                for (byte value: data)
                {
                    writer.writeByte(value);
                }
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                byte[] received = new byte[data.length];
                reader.readFully(received);

                for (int i = 0; i < received.length; i++)
                {
                    assertEquals(data[i], received[i]);
                }
            }
        });
    }

    @Test
    public void readFully2() throws IOException
    {
        final byte[] data = new byte[] { (byte)0, (byte)1 };

        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                for (byte value: data)
                {
                    writer.writeByte(value);
                }
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                byte[] received = new byte[data.length];
                reader.readFully(received, 0, data.length);
            }
        });
    }

    @Test
    public void read() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeByte((byte)1);
                writer.writeByte((byte)1);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(1, reader.read());
                assertEquals(1, reader.read());
                assertEquals(0, reader.getBitOffset());
            }
        });

        final ByteArrayBitStreamWriter babsw = new ByteArrayBitStreamWriter();

        babsw.writeUnsignedByte((short)1);
        babsw.writeUnsignedByte((short)1);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(babsw.toByteArray());
        assertEquals(1, in.read());
        assertEquals(1, in.read());
        assertEquals(0, in.getBitOffset());

    }

    @Test
    public void readFloat16() throws IOException
    {
        final ByteArrayBitStreamWriter babsw = new ByteArrayBitStreamWriter();
        babsw.writeFloat16(1.0f);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(babsw.toByteArray());
        assertEquals(1.0f, in.readFloat16(), 0);
        in.close();
    }

    @Test
    public void readFloat() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeFloat(1.0f);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(1.0f, reader.readFloat(), 0);
            }
        });
    }

    @Test
    public void readBit() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeBits(2 /*0b0010*/, 4);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(0, reader.readBit());
                assertEquals(0, reader.readBit());
                assertEquals(1, reader.readBit());
                assertEquals(0, reader.readBit());
            }
        });
    }

    @Test(expected = IOException.class)
    public void readTooMuch() throws IOException
    {
        // stream containing 1 byte of data
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(new byte[] {0x33});
        in.readBits(5); // 5 out of 8 bits are attempted to read. expected to just go fine
        in.readBits(4); // 9 out of 8 bits are attempted to read. expected to throw documented exception
    }

    @Test
    public void littleEndian() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ImageOutputStream writer) throws IOException
            {
                writer.writeShort(12);
                writer.writeInt(13);
                writer.writeLong(11111110111L);
                writer.writeBits(2 /*0b10*/, 2);
            }

            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertEquals(12, reader.readShort());
                assertEquals(13, reader.readInt());
                assertEquals(11111110111L, reader.readLong());
                assertEquals(1, reader.readBit());
                assertEquals(0, reader.readBit());
            }
        });
    }

    /*
    FIXME: this test fails
    @Test
    public void readUnaligned63Bits() throws IOException
    {
        final byte[] data =
        {
            (byte)0x0f, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
            (byte)0xe0
        };

        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(data);

        assertEquals(0, reader.readBits(4));
        assertEquals(0x7FFFFFFFFFFFFFFFL, reader.readBits(63));
        assertEquals(0, reader.readBits(5));
    }
    */

    private interface WriteReadTestable
    {
        // don't use BitStreamWriter so that this tests solely the reader
        void write(ImageOutputStream writer) throws IOException;
        void read(ByteArrayBitStreamReader reader) throws IOException;
    }

    private abstract static class SampleWriteReadTest implements WriteReadTestable
    {
        public void write(ImageOutputStream writer) throws IOException
        {
            writer.writeBits(6, 4);
            writer.writeBits(7, 4);
            writer.writeBits(8, 4);
            writer.writeBits(9, 4);
            writer.writeBits(1, 4);
            writer.writeBits(2, 4);
            writer.writeBits(3, 4);
            writer.writeBits(4, 4);
            writer.writeBits(12, 4);
            writer.writeBits(13, 4);
            writer.writeBits(14, 4);
            writer.writeBits(15, 4);
        }
    }

    private void writeReadTest(WriteReadTestable writeReadTest) throws IOException
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final MemoryCacheImageOutputStream writer = new MemoryCacheImageOutputStream(outputStream);
        writeReadTest.write(writer);
        writer.close();
        outputStream.close();

        final byte[] data = outputStream.toByteArray();

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
}
