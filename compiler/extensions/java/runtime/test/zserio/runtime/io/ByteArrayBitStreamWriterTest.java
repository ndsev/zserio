package zserio.runtime.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.junit.Test;

public class ByteArrayBitStreamWriterTest
{
    @Test
    public void test1() throws Exception
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (int value : DATA)
                {
                    writer.writeBits(value, 4);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (int value : DATA)
                {
                    assertEquals(value, reader.readBits(4));
                }
            }

            private final int[] DATA = {
                0x6,
                0x7,
                0x8,
                0x9,
                0x1,
                0x2,
                0x3,
                0x4,
                0xc,
                0xd,
                0xe,
                0xf
            };
        });
    }

    @Test
    public void test2() throws Exception
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBits(6, 4);
                writer.writeBits(0x78, 8);
                writer.writeBits(0x91, 8);
                writer.writeBits(0x23, 8);
                writer.writeBits(0x4c, 8);
                writer.writeBits(0xde, 8);
                writer.writeBits(0xf, 4);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                assertEquals(0x6, reader.readBits(4));
                assertEquals(0x7, reader.readBits(4));
                assertEquals(0x8, reader.readBits(4));
                assertEquals(0x9, reader.readBits(4));

                assertEquals(0x1, reader.readBits(4));
                assertEquals(0x2, reader.readBits(4));
                assertEquals(0x3, reader.readBits(4));
                assertEquals(0x4, reader.readBits(4));

                assertEquals(0xc, reader.readBits(4));
                assertEquals(0xd, reader.readBits(4));
                assertEquals(0xe, reader.readBits(4));
                assertEquals(0xf, reader.readBits(4));
            }
        });
    }

    @Test
    public void test3() throws Exception
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBits(6, 4);
                writer.writeShort((short)0x7891);
                writer.writeShort((short)0x234c);
                writer.writeBits(0xd, 4);
                writer.writeBits(0xef, 8);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                assertEquals(0x6, reader.readBits(4));
                assertEquals(0x7, reader.readBits(4));
                assertEquals(0x8, reader.readBits(4));
                assertEquals(0x9, reader.readBits(4));

                assertEquals(0x1, reader.readBits(4));
                assertEquals(0x2, reader.readBits(4));
                assertEquals(0x3, reader.readBits(4));
                assertEquals(0x4, reader.readBits(4));

                assertEquals(0xc, reader.readBits(4));
                assertEquals(0xd, reader.readBits(4));
                assertEquals(0xe, reader.readBits(4));
                assertEquals(0xf, reader.readBits(4));
            }
        });
    }

    @Test
    public void test4() throws Exception
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeShort((short)0x234c);
        writer.writeBits(0xef, 8);
        writer.alignTo(32);
        writer.close();

        final byte[] b = writer.toByteArray();
        assertEquals(b.length * 8L, 32);
    }

    @Test
    public void writeUnalignedData() throws IOException
    {
        // number expected to be written at offset
        final int testValue = 123;

        for (int offset = 0; offset <= 63; ++offset)
        {
            final int bufferByteSize = (8 + offset + 7) / 8;
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter(bufferByteSize);
            // fill the buffer with 1s to check proper masking
            for (int i = 0; i < bufferByteSize; ++i)
                writer.writeBits(0xFF, 8);

            writer.setBitPosition(0);

            if (offset != 0)
                writer.writeBits(0, offset);
            writer.writeBits(testValue, 8);

            // check written value
            byte[] writtenData = writer.toByteArray();
            int writtenTestValue = ((int)writtenData[offset / 8]) << (offset % 8);
            if (offset % 8 != 0)
                writtenTestValue |= (0xFF & writtenData[offset / 8 + 1]) >>> (8 - (offset % 8));
            assertEquals("offset: " + offset, testValue, writtenTestValue);
        }
    }

    @Test
    public void writeByte() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (byte value : DATA)
                {
                    writer.writeByte(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (byte value : DATA)
                {
                    assertEquals(value, reader.readByte());
                }
            }

            private final byte[] DATA =
            {
                0,
                1,
                -1,
                Byte.MAX_VALUE,
                Byte.MIN_VALUE
            };
        });
    }

    @Test
    public void writeShort() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (short value : DATA)
                {
                    writer.writeShort(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (short value : DATA)
                {
                    assertEquals(value, reader.readShort());
                }
            }

            private final short[] DATA =
            {
                0,
                1,
                -1,
                Short.MAX_VALUE,
                Short.MIN_VALUE
            };
        });
    }

    @Test
    public void writeInt() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (int value : DATA)
                {
                    writer.writeInt(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (int value : DATA)
                {
                    assertEquals(value, reader.readInt());
                }
            }

            private final int[] DATA =
            {
                0,
                1,
                -1,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                127,
                137
            };
        });
    }

    @Test
    public void writeLong() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (long value : DATA)
                {
                    writer.writeLong(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (long value : DATA)
                {
                    assertEquals(value, reader.readLong());
                }
            }

            private final long[] DATA =
            {
                0,
                1,
                -1,
                Long.MAX_VALUE,
                Long.MIN_VALUE,
                1111111111L,
                1212121212L
            };
        });
    }

    @Test
    public void writeUnsignedInt() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (long value : DATA)
                {
                    writer.writeUnsignedInt(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (long value : DATA)
                {
                    assertEquals(value, reader.readUnsignedInt());
                }
            }

            private final long[] DATA =
            {
                0,
                1,
                Integer.MAX_VALUE
            };
        });
    }

    @Test
    public void writeUnsignedByte() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (short value : DATA)
                {
                    writer.writeUnsignedByte(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (short value : DATA)
                {
                    assertEquals(value, reader.readUnsignedByte());
                }
            }

            private final short[] DATA =
            {
                5,
                Byte.MAX_VALUE
            };
        });
    }

    @Test
    public void writeBigInteger() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeBigInteger(BigInteger.valueOf(0x1), 4);
                writer.writeBigInteger(BigInteger.valueOf(0x7), 4);
                writer.writeBigInteger(BigInteger.valueOf(0x7f), 8);
                writer.writeBigInteger(BigInteger.valueOf(0x7fff), 16);
                writer.writeBigInteger(BigInteger.valueOf(0x7fffffff), 32);
                writer.writeBigInteger(BigInteger.valueOf(0x7fffffffffffffffL), 64);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                final int[] expectedBytes = {
                    0x17, // 0x1, 0x7 nibbles combined
                    0x7f,
                    0x7f, 0xff,
                    0x7f, 0xff, 0xff, 0xff,
                    0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff
                };
                for (int value : expectedBytes)
                {
                    assertEquals(value, reader.readUnsignedByte());
                }
            }
        });
    }

    @Test
    public void writeFloat16() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                writer.writeFloat16(1.0f);
                writer.writeFloat16(2.0f);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // hand-encoded values
                assertEquals(0x3c00, reader.readUnsignedShort());
                assertEquals(0x4000, reader.readUnsignedShort());
            }
        });
    }

    /**
     * Test capacity.
     *
     * @throws IOException if the writings and readings fail
     */
    @Test
    public void capacity() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeInt(10);
        writer.writeLong(10);
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        assertEquals(10, reader.readInt());
        assertEquals(10, reader.readLong());

        writer = new ByteArrayBitStreamWriter(1234);
        writer.writeByte((byte)127);
        writer.writeBits(7, 4);
        writer.writeInt(123);
        writer.writeLong(12345678910L);

        reader = new ByteArrayBitStreamReader(writer.toByteArray());
        assertEquals((byte)127, reader.readByte());
        assertEquals(7, reader.readBits(4));
        assertEquals(123, reader.readInt());
        assertEquals(12345678910L, reader.readLong());

        try
        {
            writer = new ByteArrayBitStreamWriter(Integer.MAX_VALUE);
            fail();
        }
        catch (final Exception e)
        {
            assertTrue(true);
        }

        try
        {
            writer = new ByteArrayBitStreamWriter(-1);
            fail();
        }
        catch (final Exception e)
        {
            assertTrue(true);
        }
        reader.close();
        writer.close();
    }

    /**
     * Test the writeBool method.
     *
     * @throws IOException if the writing fails
     */
    @Test
    public void writeBool() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                for (boolean value : DATA)
                {
                    writer.writeBool(value);
                }
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                for (boolean value : DATA)
                {
                    assertEquals(value, reader.readBit() != 0 ? true : false);
                }
                assertEquals(DATA.length, getBitOffset(reader));
            }

            private final boolean[] DATA =
            {
                true,
                false
            };
        });
    }

    /**
     * Test the growBuffer method.
     *
     * @throws IOException if the ByteArrayBitStreamWriter cannot be closed
     */
    @Test
    public void growBuffer() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        for (int i = 0; i < 8191; i++)
        {
            writer.writeByte((byte)1);
        }
        assertEquals(8191, writer.getBytePosition());
        writer.close();
    }

    @Test
    public void writeBitsInvalidNumException()
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final int numBits[] = { -1, 0, 65 };
        for (int i = 0; i < numBits.length; ++i)
        {
            try
            {
                writer.writeBits(0x1L, numBits[i]);
                fail();
            }
            catch (IOException e)
            {
                fail();
            }
            catch (IllegalArgumentException e)
            {
                assertTrue(true);
            }
        } // for numbits
    }

    @Test
    public void writeBitsIllegalArgumentException()
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        for (int i = 1; i < 64; i++)
        {
            long minSigned   = -(1L << (i-1));
            long maxUnsigned =  (1L << (i  )) - 1;

            long minSignedViolation   = minSigned   - 1;
            long maxUnsignedViolation = maxUnsigned + 1;

            try
            {
                writer.writeSignedBits(minSigned,   i);
                writer.writeBits(maxUnsigned, i);
            }
            catch (IOException e)
            {
                fail();
            }

            try
            {
                writer.writeBits(minSignedViolation, i);
                System.out.println("unexpected succes writeBits: " + minSignedViolation + " # " + i);
                fail();
            }
            catch (IOException e)
            {
                fail();
            }
            catch (IllegalArgumentException e)
            {
                assertTrue(true);
            }

            try
            {
                writer.writeBits(maxUnsignedViolation, i);
                System.out.println("unexpected succes writeBits: " + maxUnsignedViolation + " # " + i);
                fail();
            }
            catch (IOException e)
            {
                fail();
            }
            catch (IllegalArgumentException e)
            {
                assertTrue(true);
            }
        } // for numBits
    }

    @Test
    public void writeIllegalArgumentException()
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        // Note: no range check for writeBigInteger

        try
        {
            writer.writeUnsignedByte((short)-1);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedShort(-1);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedInt(-1L);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedByte((short)(1 << 8));
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedShort(1 << 16);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }

        try
        {
            writer.writeUnsignedInt(1L << 32);
            fail();
        }
        catch (IOException e)
        {
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(true);
        }
    }

    @Test
    public void writeVarInt16() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarInt16((short)0);
                writer.writeVarInt16((short)+0x3f);
                writer.writeVarInt16((short)-0x3f);

                // 2 bytes
                writer.writeVarInt16((short)+0x7f);
                writer.writeVarInt16((short)-0x7f);
                writer.writeVarInt16((short)+0x3fff);
                writer.writeVarInt16((short)-0x3fff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8)); //  0
                assertEquals(0x3f, reader.readBits(8)); //  111111b =  0x3f
                assertEquals(0xbf, reader.readBits(8)); // -111111b = -0x3f

                // 2 bytes
                assertEquals(0x407f, reader.readBits(16)); //  0b1111111 =  0x7f
                assertEquals(0xc07f, reader.readBits(16)); // -0b1111111 = -0x7f
                assertEquals(0x7fff, reader.readBits(16)); //  0b11111111111111 =  0x3fff
                assertEquals(0xffff, reader.readBits(16)); // -0b11111111111111 = -0x3fff
            }
        });
    }

    @Test
    public void writeVarInt32() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarInt32(0);
                writer.writeVarInt32(+0x3f);
                writer.writeVarInt32(-0x3f);

                // 2 bytes
                writer.writeVarInt32(+0x7f);
                writer.writeVarInt32(-0x7f);
                writer.writeVarInt32(+0x1fff);
                writer.writeVarInt32(-0x1fff);

                // 3 bytes
                writer.writeVarInt32(+0x3fff);
                writer.writeVarInt32(-0x3fff);
                writer.writeVarInt32(+0xfffff);
                writer.writeVarInt32(-0xfffff);

                // 4 bytes
                writer.writeVarInt32(+0x3fffff);
                writer.writeVarInt32(-0x3fffff);
                writer.writeVarInt32(+0xfffffff);
                writer.writeVarInt32(-0xfffffff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8)); //  0
                assertEquals(0x3f, reader.readBits(8)); //  111111b =  0x3f
                assertEquals(0xbf, reader.readBits(8)); // -111111b = -0x3f

                // 2 bytes
                assertEquals(0x407f, reader.readBits(16)); //  0b1111111 =  0x7f
                assertEquals(0xc07f, reader.readBits(16)); // -0b1111111 = -0x7f
                assertEquals(0x7f7f, reader.readBits(16)); //  0b1111111111111 =  0x1fff
                assertEquals(0xff7f, reader.readBits(16)); // -0b1111111111111 = -0x1fff

                // 3 bytes
                assertEquals(0x40ff7fL, reader.readBits(24)); //  0b11111111111111 =  0x3fff
                assertEquals(0xc0ff7fL, reader.readBits(24)); // -0b11111111111111 = -0x3fff
                assertEquals(0x7fff7fL, reader.readBits(24)); //  0b11111111111111 =  0xfffff
                assertEquals(0xffff7fL, reader.readBits(24)); // -0b11111111111111 = -0xfffff

                // 4 bytes
                assertEquals(0x40ffffffL, reader.readBits(32)); //  0x1fffff
                assertEquals(0xc0ffffffL, reader.readBits(32)); // -0x1fffff
                assertEquals(0x7fffffffL, reader.readBits(32)); //  0xfffffff
                assertEquals(0xffffffffL, reader.readBits(32)); // -0xfffffff
            }
        });
    }

    @Test
    public void writeVarUInt16() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarUInt16((short)0);
                writer.writeVarUInt16((short)0x7f);

                // 2 bytes
                writer.writeVarUInt16((short)0xff);
                writer.writeVarUInt16((short)0x7fff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8));    // 0
                assertEquals(0x7f, reader.readBits(8));    // 1111111b = 0x7f

                // 2 bytes
                assertEquals(0x80ff, reader.readBits(16)); // 0b11111111 = 0xff
                assertEquals(0xffff, reader.readBits(16)); // 0b111111111111111 = 0x7fff
            }
        });
    }

    @Test
    public void writeVarUInt32() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarUInt32(0);
                writer.writeVarUInt32(0x7f);

                // 2 bytes
                writer.writeVarUInt32(0xff);
                writer.writeVarUInt32(0x3fff);

                // 3 bytes
                writer.writeVarUInt32(0x7fff);
                writer.writeVarUInt32(0x1fffff);

                // 4 bytes
                writer.writeVarUInt32(0x3fffff);
                writer.writeVarUInt32(0x1fffffff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8));    // 0
                assertEquals(0x7f, reader.readBits(8));    // 1111111b = 0x7f

                // 2 bytes
                assertEquals(0x817f, reader.readBits(16));
                assertEquals(0xff7f, reader.readBits(16));

                // 3 bytes
                assertEquals(0x81ff7f, reader.readBits(24));
                assertEquals(0xffff7f, reader.readBits(24));

                // 4 bytes
                assertEquals(0x80ffffffL, reader.readBits(32));
                assertEquals(0xffffffffL, reader.readBits(32));
            }
        });
    }

    @Test
    public void writeVarSize() throws IOException
    {
        writeReadTest(new WriteReadTestable(){
            @Override
            public void write(ByteArrayBitStreamWriter writer) throws IOException
            {
                // 1 byte
                writer.writeVarSize(0);
                writer.writeVarSize(0x7f);

                // 2 bytes
                writer.writeVarSize(0xff);
                writer.writeVarSize(0x3fff);

                // 3 bytes
                writer.writeVarSize(0x7fff);
                writer.writeVarSize(0x1fffff);

                // 4 bytes
                writer.writeVarSize(0x3fffff);
                writer.writeVarSize(0xfffffff);

                // 5 bytes
                writer.writeVarSize(0x1fffffff);
                writer.writeVarSize(0x7fffffff);
            }

            @Override
            public void read(ImageInputStream reader) throws IOException
            {
                // 1 byte
                assertEquals(0x00, reader.readBits(8));    // 0
                assertEquals(0x7f, reader.readBits(8));    // 1111111b = 0x7f

                // 2 bytes
                assertEquals(0x817f, reader.readBits(16));
                assertEquals(0xff7f, reader.readBits(16));

                // 3 bytes
                assertEquals(0x81ff7f, reader.readBits(24));
                assertEquals(0xffff7f, reader.readBits(24));

                // 4 bytes
                assertEquals(0x81ffff7fL, reader.readBits(32));
                assertEquals(0xffffff7fL, reader.readBits(32));

                // 5 bytes
                assertEquals(0x80ffffffffL, reader.readBits(40));
                assertEquals(0x83ffffffffL, reader.readBits(40));
            }
        });
    }

    /**
     * Describes a test method.
     */
    private enum TestMethod
    {
        /**
         * Test method: write aligned.
         */

        ALIGNED,

        /**
         * Test method: write unaligned.
         */
        UNALIGNED;
    }

    private interface WriteReadTestable
    {
        // don't use BitStreamReader so that this tests solely the writer
        void write(ByteArrayBitStreamWriter writer) throws IOException;
        void read(ImageInputStream reader) throws IOException;
    }

    private void writeReadTest(WriteReadTestable writeReadTest) throws IOException
    {
        for (final TestMethod method : TestMethod.values())
        {
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

            if (method == TestMethod.UNALIGNED)
            {
                writer.writeBits(1, 1);
            }
            writeReadTest.write(writer);
            writer.close();

            final byte[] data = writer.toByteArray();
            if (method == TestMethod.UNALIGNED)
                trimBitFromLeft(data);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            final MemoryCacheImageInputStream reader = new MemoryCacheImageInputStream(inputStream);
            try
            {
                writeReadTest.read(reader);
            }
            finally
            {
                reader.close();
                inputStream.close();
            }
        }
    }

    private static void trimBitFromLeft(byte[] data)
    {
        byte carry = 0;
        for (int i = data.length; i > 0; --i)
        {
            final int currentValue = data[i - 1] & 0xff; // prevent sign extension later (signed byte->int)

            data[i - 1] = (byte)((currentValue << 1) | carry);
            carry = (byte)(currentValue >>> 7); // MSB move to carry
        }
        // the last carry bit is trimmed off
    }

    private static long getBitOffset(ImageInputStream inputStream) throws IOException
    {
        return 8 * inputStream.getStreamPosition() + inputStream.getBitOffset();
    }
}
