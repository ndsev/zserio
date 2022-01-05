package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class ByteArrayBitStreamReaderTest
{
    @Test
    public void bitBufferConstructor() throws IOException
    {
        final BitBuffer bitBuffer = new BitBuffer(new byte[]{(byte)0xAE, (byte)0xEA, (byte)0x80}, 17);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);

        assertEquals(bitBuffer.getBitSize(), reader.getBufferBitSize());
        assertEquals(0xAEE, reader.readBits(12));
        assertEquals(0x0A, reader.readBits(4));
        assertEquals(0x01, reader.readBits(1));

        // check eof
        assertThrows(IOException.class, () -> reader.readBits(1));

        reader.close();
    }

    @Test
    public void bitBufferConstructorOverflow() throws IOException
    {
        final BitBuffer bitBuffer = new BitBuffer(new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xF0}, 19);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);

        assertEquals(bitBuffer.getBitSize(), reader.getBufferBitSize());

        assertThrows(IOException.class, () -> reader.readBits(20));

        reader.close();
    }

    @Test
    public void readUnalignedData() throws IOException
    {
        // number expected to read at offset
        final int testValue = 123;

        for (int offset = 0; offset <= 64; ++offset)
        {
            // write test value at offset to data buffer
            final byte[] buffer = new byte[(8 + offset + 7) / 8];
            buffer[offset / 8] = (byte)(testValue >> (offset % 8));
            if (offset % 8 != 0) // don't write behind the buffer
                buffer[offset / 8 + 1] = (byte)(testValue << (8 - offset % 8));

            final BitBuffer bitBuffer = new BitBuffer(buffer, 8 + offset);
            final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);

            // read offset bits
            if (offset != 0) // java reader cannot read 0 bits
                assertEquals(0, reader.readBits(offset));

            // read magic number
            assertEquals(testValue, reader.readBits(8), "offset: " + offset);

            // check eof
            assertThrows(IOException.class, () -> reader.readBits(1), "offset: " + offset + "!");
        }
    }

    /**
     * Test the exception in the protected readRange method.
     */
    @Test
    public void rangeMinException() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertThrows(IllegalArgumentException.class, () -> reader.readBits(-1));
            }
        });
    }

    /**
     * Test the exception in the protected readRange method.
     */
    @Test
    public void rangeMaxException() throws IOException
    {
        writeReadTest(new SampleWriteReadTest(){
            @Override
            public void read(ByteArrayBitStreamReader reader) throws IOException
            {
                assertThrows(IllegalArgumentException.class, () -> reader.readBits(65));
            }
        });
    }

    /**
     * Test the bit offset getter.
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
                final short uint8 = reader.readUnsignedByte();
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
                final short uint8 = reader.readUnsignedByte();
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

    @Test
    public void signedBitfield2() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeShort((short)-10000);
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
                reader.setBitPosition(reader.getBitPosition() + 1);
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
                reader.setBitPosition(reader.getBitPosition() + 10);
                assertEquals(10, reader.getBitPosition());
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
    public void readFloat16() throws IOException
    {
        final ByteArrayBitStreamWriter babsw = new ByteArrayBitStreamWriter();
        babsw.writeFloat16(1.0f);
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(babsw.toByteArray());
        assertEquals(1.0f, in.readFloat16(), 0);
        in.close();
    }

    @Test
    public void readTooMuch() throws IOException
    {
        // stream containing 1 byte of data
        final ByteArrayBitStreamReader in = new ByteArrayBitStreamReader(new byte[] {0x33});
        // 5 out of 8 bits are attempted to read. expected to just go fine
        in.readBits(5);
        // 9 out of 8 bits are attempted to read. expected to throw documented exception
        assertThrows(IOException.class, () -> in.readBits(4));
    }

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
