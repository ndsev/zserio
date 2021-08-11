package zserio.runtime.array;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.SizeOf;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.InitializeOffsetsWriter;

/**
 * Interface for array traits.
 *
 * Array traits provides an implementation of zserio functions for one element given by index and raw array
 * holder.
 */
public interface ArrayTraits
{
    /**
     * Checks if bit size of the array elements is always the same.
     *
     * @return true if bit size of the array elements is constant.
     */
    public boolean isBitSizeOfConstant();

    /**
     * Gets the bit size of the array element if it is stored in the bit stream.
     *
     * @param rawArrayHolder Raw array holder.
     * @param bitPosition    Current bit position in the bit stream.
     * @param index          Index of element stored in the raw array holder.
     *
     * @return Bit size of the given array element if it is stored in the bit stream.
     */
    public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index);

    /**
     * Initializes indexed offsets for the array element.
     *
     * @param rawArrayHolder Raw array holder.
     * @param bitPosition    Current bit position in the bit stream.
     * @param index          Index of element stored in the raw array holder.
     *
     * @return Updated bit stream position which points to the first bit after the array element.
     */
    public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index);

    /**
     * Reads the array element from the bit stream.
     *
     * @param rawArrayHolder Raw array holder.
     * @param reader         Bit stream reader to read from.
     * @param index          Index of element stored in the raw array holder.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
            throws IOException, ZserioError;

    /**
     * Writes the array element to the bit stream.
     *
     * @param rawArrayHolder Raw array holder.
     * @param writer         Bit stream write to write to.
     * @param index          Index of element stored in the raw array holder.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
            throws IOException, ZserioError;

    /**
     * Array traits for zserio int8 and int:1...int:8 arrays which are mapped to Java byte[] array.
     */
    public static class SignedBitFieldByteArray implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public SignedBitFieldByteArray(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final byte[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = (byte)reader.readSignedBits(numBits);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final byte[] rawArray = rawArrayHolder.getRawArray();
            writer.writeSignedBits(rawArray[index], numBits);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio int16 and int:9...int:16 arrays which are mapped to Java short[] array.
     */
    public static class SignedBitFieldShortArray implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public SignedBitFieldShortArray(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final short[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = (short)reader.readSignedBits(numBits);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final short[] rawArray = rawArrayHolder.getRawArray();
            writer.writeSignedBits(rawArray[index], numBits);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio int32 and int:17...int:32 arrays which are mapped to Java int[] array.
     */
    public static class SignedBitFieldIntArray implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public SignedBitFieldIntArray(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = (int)reader.readSignedBits(numBits);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            writer.writeSignedBits(rawArray[index], numBits);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio int64 and int:33...int:64 arrays which are mapped to Java int[] array.
     */
    public static class SignedBitFieldLongArray implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public SignedBitFieldLongArray(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readSignedBits(numBits);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            writer.writeSignedBits(rawArray[index], numBits);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio bit:1...bit:7 arrays which are mapped to Java byte[] array.
     */
    public static class BitFieldByteArray implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldByteArray(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final byte[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = (byte)reader.readBits(numBits);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final byte[] rawArray = rawArrayHolder.getRawArray();
            writer.writeBits(rawArray[index], numBits);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio uint8 and bit:8...bit:15 arrays which are mapped to Java short[] array.
     */
    public static class BitFieldShortArray implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldShortArray(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final short[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = (short)reader.readBits(numBits);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final short[] rawArray = rawArrayHolder.getRawArray();
            writer.writeBits(rawArray[index], numBits);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio uint16 and bit:16...bit:31 arrays which are mapped to Java int[] array.
     */
    public static class BitFieldIntArray implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldIntArray(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = (int)reader.readBits(numBits);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            writer.writeBits(rawArray[index], numBits);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio uint32 and bit:32...bit:63 arrays which are mapped to Java long[] array.
     */
    public static class BitFieldLongArray implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldLongArray(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readBits(numBits);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            writer.writeBits(rawArray[index], numBits);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio uint64 and bit:64 arrays which are mapped to ArrayList of BigIntegers.
     */
    public static class BitFieldBigIntegerArray implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return NUM_BITS;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final BigInteger[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readBigInteger(NUM_BITS);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final BigInteger[] rawArray = rawArrayHolder.getRawArray();
            writer.writeBigInteger(rawArray[index], NUM_BITS);
        }

        private static final int NUM_BITS = 64;
    }

    /**
     * Array traits for zserio varint16 arrays which are mapped to Java short[] array.
     */
    public static class VarInt16Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final short[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarInt16(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final short[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarInt16();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final short[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarInt16(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio varint32 arrays which are mapped to Java int[] array.
     */
    public static class VarInt32Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final int[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarInt32(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarInt32();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarInt32(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio varint64 arrays which are mapped to Java long[] array.
     */
    public static class VarInt64Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final long[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarInt64(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarInt64();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarInt64(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio varint arrays which are mapped to Java long[] array.
     */
    public static class VarIntArray implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final long[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarInt(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarInt();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarInt(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio varuint16 arrays which are mapped to Java short[] array.
     */
    public static class VarUInt16Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final short[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarUInt16(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final short[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarUInt16();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final short[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarUInt16(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio varuint32 arrays which are mapped to Java int[] array.
     */
    public static class VarUInt32Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final int[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarUInt32(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarUInt32();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarUInt32(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio varuint64 arrays which are mapped to Java long[] array.
     */
    public static class VarUInt64Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final long[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarUInt64(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarUInt64();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final long[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarUInt64(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio varuint arrays which are mapped to ArrayList of BigIntegers.
     */
    public static class VarUIntArray implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final BigInteger[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarUInt(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final BigInteger[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarUInt();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final BigInteger[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarUInt(rawArray[index]);
        }
    }

    public static class VarSizeArray implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final int[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfVarSize(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readVarSize();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final int[] rawArray = rawArrayHolder.getRawArray();
            writer.writeVarSize(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio float16 arrays which are mapped to Java float[] array.
     */
    public static class Float16Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return 16;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final float[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readFloat16();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final float[] rawArray = rawArrayHolder.getRawArray();
            writer.writeFloat16(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio float32 arrays which are mapped to Java float[] array.
     */
    public static class Float32Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return 32;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final float[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readFloat32();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final float[] rawArray = rawArrayHolder.getRawArray();
            writer.writeFloat32(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio float64 arrays which are mapped to Java double[] array.
     */
    public static class Float64Array implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return 64;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final double[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readFloat64();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final double[] rawArray = rawArrayHolder.getRawArray();
            writer.writeFloat64(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio string arrays which are mapped to ArrayList of Strings.
     */
    public static class StringArray implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final String[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfString(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final String[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readString();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final String[] rawArray = rawArrayHolder.getRawArray();
            writer.writeString(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio bool arrays which are mapped to Java boolean[] array.
     */
    public static class BoolArray implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return 1;
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final boolean[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readBool();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final boolean[] rawArray = rawArrayHolder.getRawArray();
            writer.writeBool(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio extern bit buffer arrays which are mapped to ArrayList of BitBuffers.
     */
    public static class BitBufferArray implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final BitBuffer[] rawArray = rawArrayHolder.getRawArray();

            return BitSizeOfCalculator.getBitSizeOfBitBuffer(rawArray[index]);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            return bitPosition + bitSizeOf(rawArrayHolder, bitPosition, index);
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final BitBuffer[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = reader.readBitBuffer();
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final BitBuffer[] rawArray = rawArrayHolder.getRawArray();
            writer.writeBitBuffer(rawArray[index]);
        }
    }

    /**
     * Array traits for zserio object arrays (without writer part) which are mapped to ArrayList of zserio
     * objects.
     */
    public static class ObjectArray<E extends SizeOf> implements ArrayTraits
    {
        /**
         * Constructor from element factory.
         *
         * @param elementFactory Element factory to construct from.
         */
        public ObjectArray(ElementFactory<E> elementFactory)
        {
            this.elementFactory = elementFactory;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final E[] rawArray = rawArrayHolder.getRawArray();
            return rawArray[index].bitSizeOf(bitPosition);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            throw new UnsupportedOperationException(
                    "Array: initializeOffsets is not implemented for read only ObjectArrayTraits!");
        }

        @Override
        public void read(RawArrayHolder rawArrayHolder, BitStreamReader reader, int index)
                throws IOException, ZserioError
        {
            final E[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index] = elementFactory.create(reader, index);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            throw new UnsupportedOperationException(
                    "Array: write is not implemented for read only ObjectArrayTraits!");
        }

        private final ElementFactory<E> elementFactory;
    }

    /**
     * Array traits for zserio object arrays (with writer part) which are mapped to ArrayList of zserio objects.
     */
    public static class WriteObjectArray<E extends InitializeOffsetsWriter & SizeOf> extends ObjectArray<E>
    {
        /**
         * Constructor from element factory.
         *
         * @param elementFactory Element factory to construct from.
         */
        public WriteObjectArray(ElementFactory<E> elementFactory)
        {
            super(elementFactory);
        }

        @Override
        public long initializeOffsets(RawArrayHolder rawArrayHolder, long bitPosition, int index)
        {
            final E[] rawArray = rawArrayHolder.getRawArray();
            return rawArray[index].initializeOffsets(bitPosition);
        }

        @Override
        public void write(RawArrayHolder rawArrayHolder, BitStreamWriter writer, int index)
                throws IOException, ZserioError
        {
            final E[] rawArray = rawArrayHolder.getRawArray();
            rawArray[index].write(writer);
        }
    }
}
