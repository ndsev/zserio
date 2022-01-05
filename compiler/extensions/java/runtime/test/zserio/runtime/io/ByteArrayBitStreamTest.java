package zserio.runtime.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.*;
import java.math.BigInteger;

public class ByteArrayBitStreamTest
{
    @Test
    public void unsignedBits() throws Exception
    {
        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeBits", long.class, int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readBits", int.class);

        // all possible numBits (up to 63! bits - unsigned long doesn't exists in Java)
        for (int numBits = 1; numBits <= 63; ++numBits)
        {
            // max value and some smaller values
            final long maxValue = (1L << numBits) - 1;
            final Long data[] =
            {
                maxValue,
                maxValue >> 1,
                maxValue >> 2,
                1L,
                0L,
                1L,
                maxValue >> 2,
                maxValue >> 1,
                maxValue
            };

            testBitsImpl(writeMethod, readMethod, data, numBits);
        }
    }

    @Test
    public void signedBits() throws Exception
    {
        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeSignedBits", long.class, int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readSignedBits", int.class);

        // all possible numBits (up to 64 bits)
        for (int numBits = 1; numBits <= 64; ++numBits)
        {
            // min and max values and some smaller values
            final long minValue = -1L << (numBits - 1);
            final long maxValue = (1L << (numBits - 1)) - 1;
            final Long data[] =
            {
                minValue,
                maxValue,
                minValue >> 1,
                maxValue >> 1,
                minValue >> 2,
                maxValue >> 2,
                0L,
                maxValue >> 2,
                minValue >> 2,
                maxValue >> 1,
                minValue >> 1,
                maxValue,
                minValue
            };

            testBitsImpl(writeMethod, readMethod, data, numBits);
        }
    }

    @Test
    public void unsignedInt() throws Exception
    {
        long maxValue = (1L << 32) - 1;
        Long values[] =
        {
            maxValue,
            maxValue >> 8,
            maxValue >> 16,
            maxValue >> 24,
            1L,
            0L,
            1L,
            maxValue >> 24,
            maxValue >> 16,
            maxValue >> 8,
            maxValue
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeUnsignedInt", long.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readUnsignedInt");
        testImpl(writeMethod, readMethod, values, 31);
    }

    @Test
    public void signedInt() throws Exception
    {
        int minValue = Integer.MIN_VALUE;
        int maxValue = Integer.MAX_VALUE;
        Integer values[] =
        {
            minValue,
            maxValue,
            minValue >> 8,
            maxValue >> 8,
            minValue >> 16,
            maxValue >> 16,
            minValue >> 24,
            maxValue >> 24,
            -1,
            1,
            0,
            1,
            -1,
            maxValue >> 24,
            minValue >> 24,
            maxValue >> 16,
            minValue >> 16,
            maxValue >> 8,
            minValue >> 8,
            maxValue,
            minValue
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeInt", int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readInt");
        testImpl(writeMethod, readMethod, values, 31);
    }

    @Test
    public void unsignedShort() throws Exception
    {
        int maxValue = (1 << 16) - 1;
        Integer values[] =
        {
            maxValue,
            maxValue >> 8,
            1,
            0,
            1,
            maxValue >> 8,
            maxValue
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeUnsignedShort", int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readUnsignedShort");
        testImpl(writeMethod, readMethod, values, 15);
    }

    @Test
    public void signedShort() throws Exception
    {
        short minValue = Short.MIN_VALUE;
        short maxValue = Short.MAX_VALUE;
        Short values[] =
        {
            minValue,
            maxValue,
            (short)(minValue >> 8),
            (short)(maxValue >> 8),
            -1,
            1,
            0,
            1,
            -1,
            (short)(maxValue >> 8),
            (short)(minValue >> 8),
            maxValue,
            minValue
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeShort", short.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readShort");
        testImpl(writeMethod, readMethod, values, 15);
    }

    @Test
    public void unsignedByte() throws Exception
    {
        short maxValue = (1 << 8) - 1;
        Short values[] =
        {
            maxValue,
            (short)(maxValue >> 4),
            1,
            0,
            1,
            (short)(maxValue >> 4),
            maxValue
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeUnsignedByte", short.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readUnsignedByte");
        testImpl(writeMethod, readMethod, values, 7);
    }

    @Test
    public void signedByte() throws Exception
    {
        byte minValue = Byte.MIN_VALUE;
        byte maxValue = Byte.MAX_VALUE;
        Byte values[] =
        {
            minValue,
            maxValue,
            (byte)(minValue >> 4),
            (byte)(maxValue >> 4),
            -1,
            1,
            0,
            1,
            -1,
            (byte)(maxValue >> 4),
            (byte)(minValue >> 4),
            maxValue,
            minValue
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeByte", byte.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readByte");
        testImpl(writeMethod, readMethod, values, 7);
    }

    @Test
    public void bigInteger() throws Exception
    {
        // single method for both signed and unsigned writing
        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod(
                "writeBigInteger", BigInteger.class, int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readBigInteger", int.class);

        // all possible numBits
        for (int numBits = 1; numBits < 65; ++numBits)
        {
            BigInteger maxValue = BigInteger.ONE.shiftLeft(numBits).subtract(BigInteger.ONE);
            BigInteger values[] =
            {
                maxValue,
                maxValue.shiftRight(8),
                maxValue.shiftRight(16),
                maxValue.shiftRight(24),
                maxValue.shiftRight(32),
                maxValue.shiftRight(40),
                maxValue.shiftRight(48),
                maxValue.shiftRight(56),
                BigInteger.ONE,
                BigInteger.ZERO,
                BigInteger.ONE,
                maxValue.shiftRight(56),
                maxValue.shiftRight(48),
                maxValue.shiftRight(40),
                maxValue.shiftRight(32),
                maxValue.shiftRight(24),
                maxValue.shiftRight(16),
                maxValue.shiftRight(8)
            };

            testBitsImpl(writeMethod, readMethod, values, numBits);
        }
    }

    @Test
    public void signedBigInteger() throws Exception
    {
        // single method for both signed and unsigned writing
        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod(
                "writeBigInteger", BigInteger.class, int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readSignedBigInteger", int.class);

        // all possible numBits
        for (int numBits = 1; numBits < 65; ++numBits)
        {
            BigInteger maxValue = BigInteger.ONE.shiftLeft(numBits - 1).subtract(BigInteger.ONE);
            BigInteger minValue = maxValue.negate();
            BigInteger values[] =
            {
                minValue,
                maxValue,
                minValue.shiftRight(8),
                maxValue.shiftRight(8),
                minValue.shiftRight(16),
                maxValue.shiftRight(16),
                minValue.shiftRight(24),
                maxValue.shiftRight(24),
                minValue.shiftRight(32),
                maxValue.shiftRight(32),
                minValue.shiftRight(40),
                maxValue.shiftRight(40),
                minValue.shiftRight(48),
                maxValue.shiftRight(48),
                minValue.shiftRight(56),
                maxValue.shiftRight(56),
                BigInteger.ZERO,
                maxValue.shiftRight(56),
                minValue.shiftRight(56),
                maxValue.shiftRight(48),
                minValue.shiftRight(48),
                maxValue.shiftRight(40),
                minValue.shiftRight(40),
                maxValue.shiftRight(32),
                minValue.shiftRight(32),
                maxValue.shiftRight(24),
                minValue.shiftRight(24),
                maxValue.shiftRight(16),
                minValue.shiftRight(16),
                maxValue.shiftRight(8),
                minValue.shiftRight(8)
            };

            testBitsImpl(writeMethod, readMethod, values, numBits);
        }
    }

    @Test
    public void float16() throws Exception
    {
        Float values[] =
        {
            -42.5f,
            -2.0f,
            0.0f,
            0.6171875f,
            0.875f,
            2.0f,
            9.875f,
            42.5f
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeFloat16", float.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readFloat16");
        testImpl(writeMethod, readMethod, values, 15);
    }

    @Test
    public void float32() throws Exception
    {
        Float values[] =
        {
            -42.5f,
            -2.0f,
            0.0f,
            0.6171875f,
            0.875f,
            2.0f,
            9.875f,
            42.5f
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeFloat32", float.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readFloat32");
        testImpl(writeMethod, readMethod, values, 31);
    }

    @Test
    public void float64() throws Exception
    {
        Double values[] =
        {
            -42.5,
            -2.0,
            0.0,
            0.6171875,
            0.875,
            2.0,
            9.875,
            42.5
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeFloat64", double.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readFloat64");
        testImpl(writeMethod, readMethod, values, 63);
    }

    @Test
    public void bitBuffer() throws Exception
    {
        BitBuffer values[] =
        {
            new BitBuffer(new byte[]{(byte)0xAB, (byte)0x07}, 11),
            new BitBuffer(new byte[]{(byte)0xAB, (byte)0xCD, (byte)0x7F}, 23)
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeBitBuffer", BitBuffer.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readBitBuffer");
        testImpl(writeMethod, readMethod, values, 7);
    }

    @Test
    public void string() throws Exception
    {
        String values[] =
        {
            "Hello World",
            "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2\0nonWrittenPart",
            "Price: " +
                    new String(new byte[] { (byte)0xE2, (byte)0x82, (byte)0x93 }, "UTF-8") +
                    " 3 what's this? -> " +
                    new String(new byte[] { (byte)0xC2, (byte)0xA2 }, "UTF-8")
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeString", String.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readString");
        testImpl(writeMethod, readMethod, values, 7);
    }

    @Test
    public void bool() throws Exception
    {
        Boolean values[] =
        {
            false,
            true,
            true,
            false,
            false,
            true,
            false,
            true,
            false,
            false,
            true,
            true,
            false
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeBool", boolean.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readBool");
        testImpl(writeMethod, readMethod, values, 1);
    }

    @Test
    public void varInt16() throws Exception
    {
        Short values[] =
        {
            // 1 byte
            (short)0,
            -(short)1,
            +(short)1,
            -(short)((1 << (6)) - 1),
            +(short)((1 << (6)) - 1),
            // 2 bytes
            -(short)((1 << (6))),
            +(short)((1 << (6))),
            -(short)((1 << (6 + 8)) - 1),
            +(short)((1 << (6 + 8)) - 1),
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarInt16", short.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarInt16");
        testImpl(writeMethod, readMethod, values, 15);
    }

    @Test
    public void varInt32() throws Exception
    {
        Integer values[] =
        {
            // 1 byte
            0,
            -((1)),
            +((1)),
            -((1 << (6)) - 1),
            +((1 << (6)) - 1),
            // 2 bytes
            -((1 << (6))),
            +((1 << (6))),
            -((1 << (6 + 7)) - 1),
            +((1 << (6 + 7)) - 1),
            // 3 bytes
            -((1 << (6 + 7))),
            +((1 << (6 + 7))),
            -((1 << (6 + 7 + 7)) - 1),
            +((1 << (6 + 7 + 7)) - 1),
            // 4 bytes
            -((1 << (6 + 7 + 7))),
            +((1 << (6 + 7 + 7))),
            -((1 << (6 + 7 + 7 + 8)) - 1),
            +((1 << (6 + 7 + 7 + 8)) - 1)
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarInt32", int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarInt32");
        testImpl(writeMethod, readMethod, values, 31);
    }

    @Test
    public void varInt64() throws Exception
    {
        Long values[] =
        {
            // 1 byte
             0L,
            -((1L)),
            +((1L)),
            -((1L << (6)) - 1),
            +((1L << (6)) - 1),
            // 2 bytes
            -((1L << (6))),
            +((1L << (6))),
            -((1L << (6 + 7)) - 1),
            +((1L << (6 + 7)) - 1),
            // 3 bytes
            -((1L << (6 + 7))),
            +((1L << (6 + 7))),
            -((1L << (6 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7)) - 1),
            // 4 bytes
            -((1L << (6 + 7 + 7))),
            +((1L << (6 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 8)) - 1),
            +((1L << (6 + 7 + 7 + 8)) - 1)
            // 5 bytes
            -((1L << (6 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7)) - 1),
            // 6 bytes
            -((1L << (6 + 7 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7)) - 1),
            // 7 bytes
            -((1L << (6 + 7 + 7 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            // 8 bytes
            -((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarInt64", long.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarInt64");
        testImpl(writeMethod, readMethod, values, 63);
    }

    @Test
    public void varUInt16() throws Exception
    {
        Short values[] =
        {
            // 1 byte
            (short)0,
            (short)1,
            (short)((1 << (7)) - 1),
            // 2 bytes
            (short)((1 << (7))),
            (short)((1 << (7 + 8)) - 1),
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarUInt16", short.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarUInt16");
        testImpl(writeMethod, readMethod, values, 15);
    }

    @Test
    public void varUInt32() throws Exception
    {
        Integer values[] =
        {
            // 1 byte
            ((0)),
            ((1)),
            ((1 << (7)) - 1),
            // 2 bytes
            ((1 << (7))),
            ((1 << (7 + 7)) - 1),
            // 3 bytes
            ((1 << (7 + 7))),
            ((1 << (7 + 7 + 7)) - 1),
            // 4 bytes
            ((1 << (7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 8)) - 1)
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarUInt32", int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarUInt32");
        testImpl(writeMethod, readMethod, values, 31);
    }

    @Test
    public void varUInt64() throws Exception
    {
        Long values[] =
        {
            // 1 byte
            ((0L)),
            ((1L)),
            ((1L << (7)) - 1),
            // 2 bytes
            ((1L << (7))),
            ((1L << (7 + 7)) - 1),
            // 3 bytes
            ((1L << (7 + 7))),
            ((1L << (7 + 7 + 7)) - 1),
            // 4 bytes
            ((1L << (7 + 7 + 7))),
            ((1L << (7 + 7 + 7 + 8)) - 1),
            // 5 bytes
            ((1L << (7 + 7 + 7 + 7))),
            ((1L << (7 + 7 + 7 + 7 + 7)) - 1),
            // 6 bytes
            ((1L << (7 + 7 + 7 + 7 + 7))),
            ((1L << (7 + 7 + 7 + 7 + 7 + 7)) - 1),
            // 7 bytes
            ((1L << (7 + 7 + 7 + 7 + 7 + 7))),
            ((1L << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            // 8 bytes
            ((1L << (7 + 7 + 7 + 7 + 7 + 7 + 7))),
            ((1L << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarUInt64", long.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarUInt64");
        testImpl(writeMethod, readMethod, values, 63);
    }

    @Test
    public void varInt() throws Exception
    {
        Long values[] =
        {
            // 1 byte
            0L,
            -((1L)),
            +((1L)),
            -((1L << (6)) - 1),
            +((1L << (6)) - 1),
            // 2 bytes
            -((1L << (6))),
            +((1L << (6))),
            -((1L << (6 + 7)) - 1),
            +((1L << (6 + 7)) - 1),
            // 3 bytes
            -((1L << (6 + 7))),
            +((1L << (6 + 7))),
            -((1L << (6 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7)) - 1),
            // 4 bytes
            -((1L << (6 + 7 + 7))),
            +((1L << (6 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 8)) - 1),
            +((1L << (6 + 7 + 7 + 8)) - 1)
            // 5 bytes
            -((1L << (6 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7)) - 1),
            // 6 bytes
            -((1L << (6 + 7 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7)) - 1),
            // 7 bytes
            -((1L << (6 + 7 + 7 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            // 8 bytes
            -((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            // 9 bytes
            -((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7))),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7))),
            -((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
            +((1L << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
            // 1 byte
            Long.MIN_VALUE // special case, encoded as -0
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarInt", long.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarInt");
        testImpl(writeMethod, readMethod, values, 63);
    }

    @Test
    public void varUInt() throws Exception
    {
        BigInteger values[] =
        {
            // 1 byte
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.ONE.shiftLeft(7).subtract(BigInteger.ONE),
            // 2 bytes
            BigInteger.ONE.shiftLeft(7),
            BigInteger.ONE.shiftLeft(7 + 7).subtract(BigInteger.ONE),
            // 3 bytes
            BigInteger.ONE.shiftLeft(7 + 7),
            BigInteger.ONE.shiftLeft(7 + 7 + 7).subtract(BigInteger.ONE),
            // 4 bytes
            BigInteger.ONE.shiftLeft(7 + 7 + 7),
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7).subtract(BigInteger.ONE),
            // 5 bytes
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7),
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7).subtract(BigInteger.ONE),
            // 6 bytes
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7),
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7 + 7).subtract(BigInteger.ONE),
            // 7 bytes
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7 + 7),
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7 + 7 + 7).subtract(BigInteger.ONE),
            // 8 bytes
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7 + 7 + 7),
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7 + 7 + 7 + 7).subtract(BigInteger.ONE),
            // 9 bytes
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7 + 7 + 7 + 7),
            BigInteger.ONE.shiftLeft(7 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8).subtract(BigInteger.ONE)
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarUInt", BigInteger.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarUInt");
        testImpl(writeMethod, readMethod, values, 63);
    }

    @Test
    public void varSize() throws Exception
    {
        Integer values[] =
        {
            // 1 byte
            ((0)),
            ((1)),
            ((1 << (7)) - 1),
            // 2 bytes
            ((1 << (7))),
            ((1 << (7 + 7)) - 1),
            // 3 bytes
            ((1 << (7 + 7))),
            ((1 << (7 + 7 + 7)) - 1),
            // 4 bytes
            ((1 << (7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7)) - 1),
            // 5 bytes
            ((1 << (7 + 7 + 7 + 7))),
            ((1 << (2 + 7 + 7 + 7 + 8)) - 1)
        };

        Method writeMethod = ByteArrayBitStreamWriter.class.getMethod("writeVarSize", int.class);
        Method readMethod = ByteArrayBitStreamReader.class.getMethod("readVarSize");
        testImpl(writeMethod, readMethod, values, 31);
    }

    @Test
    public void bitPosition() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        try
        {
            writer.writeBits(0xaaaa, 16);
            assertEquals(16, writer.getBitPosition());
            writer.setBitPosition(8);
            assertEquals(8, writer.getBitPosition());
            writer.writeBits(0xff, 8);
            assertEquals(16, writer.getBitPosition());
            writer.setBitPosition(13);
            assertEquals(13, writer.getBitPosition());
            writer.writeBits(0, 2);
            assertEquals(15, writer.getBitPosition());
            writer.setBitPosition(16);
            assertEquals(16, writer.getBitPosition());
        }
        finally
        {
            writer.close();
        }

        final byte[] buffer = writer.toByteArray();

        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
        try
        {
            assertEquals(0xaaf9, reader.readBits(16));
            assertEquals(16, reader.getBitPosition());
            reader.setBitPosition(8);
            assertEquals(8, reader.getBitPosition());
            assertEquals(0xf9, reader.readBits(8));
            assertEquals(16, reader.getBitPosition());
            reader.setBitPosition(13);
            assertEquals(13, reader.getBitPosition());
            assertEquals(0, reader.readBits(2));
            assertEquals(15, reader.getBitPosition());
            assertEquals(1, reader.readBits(1));
            assertEquals(16, reader.getBitPosition());
        }
        finally
        {
            reader.close();
        }
    }

    @Test
    public void alignTo() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        try
        {
            writer.writeBits(5, 3);
            writer.alignTo(8);
            assertEquals(8, writer.getBitPosition());
            writer.writeBits(0, 1);
            writer.alignTo(16);
            assertEquals(16, writer.getBitPosition());
            writer.writeBits(0xAA, 9);
            writer.alignTo(32);
            assertEquals(32, writer.getBitPosition());
            writer.writeBits(0xACA, 13);
            writer.alignTo(64);
            assertEquals(64, writer.getBitPosition());
            writer.writeBits(0xCAFE, 16);
        }
        finally
        {
            writer.close();
        }

        final byte[] buffer = writer.toByteArray();

        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
        try
        {
            assertEquals(5, reader.readBits(3));
            reader.alignTo(8);
            assertEquals(8, reader.getBitPosition());
            assertEquals(0, reader.readBits(1));
            reader.alignTo(16);
            assertEquals(16, reader.getBitPosition());
            assertEquals(0xAA, reader.readBits(9));
            reader.alignTo(32);
            assertEquals(32, reader.getBitPosition());
            assertEquals(0xACA, reader.readBits(13));
            reader.alignTo(64);
            assertEquals(64, reader.getBitPosition());
            assertEquals(0xCAFE, reader.readBits(16));
        }
        finally
        {
            reader.close();
        }
    }

    private void testImpl(Method writeMethod, Method readMethod, Object[] values,
            int maxStartBitPos) throws Exception
    {
        try
        {
            // all possible start bit positions
            for (int bitPos = 0; bitPos <= maxStartBitPos; ++bitPos)
            {
                try
                {
                    ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
                    if (bitPos > 0)
                        writer.writeBits(0, bitPos);
                    for (int i = 0; i < values.length; ++i)
                    {
                        writeMethod.invoke(writer, values[i]);
                    }

                    byte[] buffer = writer.toByteArray();
                    writer.close();
                    ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
                    if (bitPos > 0)
                        reader.readBits(bitPos);
                    for (int i = 0; i < values.length; ++i)
                        assertEquals(values[i], readMethod.invoke(reader));
                    reader.close();
                }
                catch (AssertionError e)
                {
                    throw new AssertionError(
                            "[bitPos=" + bitPos + "]: " + e.getMessage());
                }
            }
        }
        catch (InvocationTargetException e)
        {
            fail(e.getTargetException().toString());
        }
    }

    private void testBitsImpl(Method writeMethod, Method readMethod,
            Object[] values, int numBits) throws Exception
    {
        try
        {
            // all possible start bit positions
            for (int bitPos = 0; bitPos < numBits; ++bitPos)
            {
                try
                {
                    ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
                    if (bitPos > 0)
                        writer.writeBits(0, bitPos);
                    for (int i = 0; i < values.length; ++i)
                        writeMethod.invoke(writer, values[i], numBits);

                    byte[] buffer = writer.toByteArray();
                    writer.close();
                    ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(buffer);
                    if (bitPos > 0)
                        reader.readBits(bitPos);
                    for (int i = 0; i < values.length; ++i)
                        assertEquals(values[i], readMethod.invoke(reader, numBits));
                    reader.close();
                }
                catch (AssertionError e)
                {
                    throw new AssertionError(
                            "[numBits=" + numBits + ", bitPos=" + bitPos + "]: " + e.getMessage());
                }
            }
        }
        catch (InvocationTargetException e)
        {
            fail(e.getTargetException().toString());
        }
    }
}
