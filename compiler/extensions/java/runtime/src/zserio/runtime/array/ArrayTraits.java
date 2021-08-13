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
 * Array traits provides an implementation of zserio functions for one element given by index and raw array.
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
     * @param bitPosition Current bit position in the bit stream.
     * @param element     Array element.
     *
     * @return Bit size of the given array element if it is stored in the bit stream.
     */
    public int bitSizeOf(long bitPosition, ArrayElement element);

    /**
     * Initializes indexed offsets for the array element.
     *
     * @param bitPosition Current bit position in the bit stream.
     * @param element     Array element.
     *
     * @return Updated bit stream position which points to the first bit after the array element.
     */
    public long initializeOffsets(long bitPosition, ArrayElement element);

    /**
     * Reads the array element from the bit stream.
     *
     * @param reader Bit stream reader to read from.
     * @param index  Index of the array element to read.
     *
     * @return Array element filled by read element.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError;

    /**
     * Writes the array element to the bit stream.
     *
     * @param writer  Bit stream write to write to.
     * @param element Array element.
     *
     * @throws IOException Failure during bit stream manipulation.
     * @throws ZserioError Failure during offset checking.
     */
    public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError;

    /**
     * Array traits for zserio int8 and int:1...int:8 arrays which are mapped to Java byte[] array.
     */
    public static class SignedBitFieldByteArrayTraits implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public SignedBitFieldByteArrayTraits(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set((byte)reader.readSignedBits(numBits));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeSignedBits(((ArrayElement.ByteArrayElement)element).get(), numBits);
        }

        private final int numBits;
        private final ArrayElement.ByteArrayElement element = new ArrayElement.ByteArrayElement();
    }

    /**
     * Array traits for zserio int16 and int:9...int:16 arrays which are mapped to Java short[] array.
     */
    public static class SignedBitFieldShortArrayTraits implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public SignedBitFieldShortArrayTraits(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set((short)reader.readSignedBits(numBits));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeSignedBits(((ArrayElement.ShortArrayElement)element).get(), numBits);
        }

        private final int numBits;
        private final ArrayElement.ShortArrayElement element = new ArrayElement.ShortArrayElement();
    }

    /**
     * Array traits for zserio int32 and int:17...int:32 arrays which are mapped to Java int[] array.
     */
    public static class SignedBitFieldIntArrayTraits implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public SignedBitFieldIntArrayTraits(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set((int)reader.readSignedBits(numBits));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeSignedBits(((ArrayElement.IntArrayElement)element).get(), numBits);
        }

        private final int numBits;
        private final ArrayElement.IntArrayElement element = new ArrayElement.IntArrayElement();
    }

    /**
     * Array traits for zserio int64 and int:33...int:64 arrays which are mapped to Java long[] array.
     */
    public static class SignedBitFieldLongArrayTraits implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public SignedBitFieldLongArrayTraits(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readSignedBits(numBits));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeSignedBits(((ArrayElement.LongArrayElement)element).get(), numBits);
        }

        private final int numBits;
        private final ArrayElement.LongArrayElement element = new ArrayElement.LongArrayElement();
    }

    /**
     * Array traits for zserio bit:1...bit:7 arrays which are mapped to Java byte[] array.
     */
    public static class BitFieldByteArrayTraits implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldByteArrayTraits(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set((byte)reader.readBits(numBits));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeBits(((ArrayElement.ByteArrayElement)element).get(), numBits);
        }

        private final int numBits;
        private final ArrayElement.ByteArrayElement element = new ArrayElement.ByteArrayElement();
    }

    /**
     * Array traits for zserio uint8 and bit:8...bit:15 arrays which are mapped to Java short[] array.
     */
    public static class BitFieldShortArrayTraits implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldShortArrayTraits(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set((short)reader.readBits(numBits));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeBits(((ArrayElement.ShortArrayElement)element).get(), numBits);
        }

        private final int numBits;
        private final ArrayElement.ShortArrayElement element = new ArrayElement.ShortArrayElement();
    }

    /**
     * Array traits for zserio uint16 and bit:16...bit:31 arrays which are mapped to Java int[] array.
     */
    public static class BitFieldIntArrayTraits implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldIntArrayTraits(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set((int)reader.readBits(numBits));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeBits(((ArrayElement.IntArrayElement)element).get(), numBits);
        }

        private final int numBits;
        private final ArrayElement.IntArrayElement element = new ArrayElement.IntArrayElement();
    }

    /**
     * Array traits for zserio uint32 and bit:32...bit:63 arrays which are mapped to Java long[] array.
     */
    public static class BitFieldLongArrayTraits implements ArrayTraits
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldLongArrayTraits(int numBits)
        {
            this.numBits = numBits;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return numBits;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readBits(numBits));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeBits(((ArrayElement.LongArrayElement)element).get(), numBits);
        }

        private final int numBits;
        private final ArrayElement.LongArrayElement element = new ArrayElement.LongArrayElement();
    }

    /**
     * Array traits for zserio uint64 and bit:64 arrays which are mapped to Java BigInteger[] array.
     */
    public static class BitFieldBigIntegerArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return NUM_BITS;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readBigInteger(NUM_BITS));

            return element;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeBigInteger(((ArrayElement.ObjectArrayElement<BigInteger>)element).get(), NUM_BITS);
        }

        private static final int NUM_BITS = 64;

        private final ArrayElement.ObjectArrayElement<BigInteger> element =
                new ArrayElement.ObjectArrayElement<>();
    }

    /**
     * Array traits for zserio varint16 arrays which are mapped to Java short[] array.
     */
    public static class VarInt16ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarInt16(((ArrayElement.ShortArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarInt16());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarInt16(((ArrayElement.ShortArrayElement)element).get());
        }

        private final ArrayElement.ShortArrayElement element = new ArrayElement.ShortArrayElement();
    }

    /**
     * Array traits for zserio varint32 arrays which are mapped to Java int[] array.
     */
    public static class VarInt32ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarInt32(((ArrayElement.IntArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarInt32());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarInt32(((ArrayElement.IntArrayElement)element).get());
        }

        private final ArrayElement.IntArrayElement element = new ArrayElement.IntArrayElement();
    }

    /**
     * Array traits for zserio varint64 arrays which are mapped to Java long[] array.
     */
    public static class VarInt64ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarInt64(((ArrayElement.LongArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarInt64());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarInt64(((ArrayElement.LongArrayElement)element).get());
        }

        private final ArrayElement.LongArrayElement element = new ArrayElement.LongArrayElement();
    }

    /**
     * Array traits for zserio varint arrays which are mapped to Java long[] array.
     */
    public static class VarIntArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarInt(((ArrayElement.LongArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarInt());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarInt(((ArrayElement.LongArrayElement)element).get());
        }

    private final ArrayElement.LongArrayElement element = new ArrayElement.LongArrayElement();
    }

    /**
     * Array traits for zserio varuint16 arrays which are mapped to Java short[] array.
     */
    public static class VarUInt16ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarUInt16(((ArrayElement.ShortArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarUInt16());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarUInt16(((ArrayElement.ShortArrayElement)element).get());
        }

        private final ArrayElement.ShortArrayElement element = new ArrayElement.ShortArrayElement();
    }

    /**
     * Array traits for zserio varuint32 arrays which are mapped to Java int[] array.
     */
    public static class VarUInt32ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarUInt32(((ArrayElement.IntArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarUInt32());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarUInt32(((ArrayElement.IntArrayElement)element).get());
        }

        private final ArrayElement.IntArrayElement element = new ArrayElement.IntArrayElement();
    }

    /**
     * Array traits for zserio varuint64 arrays which are mapped to Java long[] array.
     */
    public static class VarUInt64ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarUInt64(((ArrayElement.LongArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarUInt64());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarUInt64(((ArrayElement.LongArrayElement)element).get());
        }

        private final ArrayElement.LongArrayElement element = new ArrayElement.LongArrayElement();
    }

    /**
     * Array traits for zserio varuint arrays which are mapped to Java BigInteger[] array.
     */
    public static class VarUIntArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarUInt(
                    ((ArrayElement.ObjectArrayElement<BigInteger>)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarUInt());

            return element;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarUInt(((ArrayElement.ObjectArrayElement<BigInteger>)element).get());
        }

        private final ArrayElement.ObjectArrayElement<BigInteger> element =
                new ArrayElement.ObjectArrayElement<>();
    }

    /**
     * Array traits for zserio varsize arrays which are mapped to Java int[] array.
     */
    public static class VarSizeArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarSize(((ArrayElement.IntArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readVarSize());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeVarSize(((ArrayElement.IntArrayElement)element).get());
        }

        private final ArrayElement.IntArrayElement element = new ArrayElement.IntArrayElement();
    }

    /**
     * Array traits for zserio float16 arrays which are mapped to Java float[] array.
     */
    public static class Float16ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return 16;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readFloat16());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeFloat16(((ArrayElement.FloatArrayElement)element).get());
        }

        private final ArrayElement.FloatArrayElement element = new ArrayElement.FloatArrayElement();
    }

    /**
     * Array traits for zserio float32 arrays which are mapped to Java float[] array.
     */
    public static class Float32ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return 32;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readFloat32());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeFloat32(((ArrayElement.FloatArrayElement)element).get());
        }

        private final ArrayElement.FloatArrayElement element = new ArrayElement.FloatArrayElement();
    }

    /**
     * Array traits for zserio float64 arrays which are mapped to Java double[] array.
     */
    public static class Float64ArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return 64;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readFloat64());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeFloat64(((ArrayElement.DoubleArrayElement)element).get());
        }

        private final ArrayElement.DoubleArrayElement element = new ArrayElement.DoubleArrayElement();
    }

    /**
     * Array traits for zserio string arrays which are mapped to Java String[] array.
     */
    public static class StringArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfString(
                    ((ArrayElement.ObjectArrayElement<String>)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readString());

            return element;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeString(((ArrayElement.ObjectArrayElement<String>)element).get());
        }

        private final ArrayElement.ObjectArrayElement<String> element = new ArrayElement.ObjectArrayElement<>();
    }

    /**
     * Array traits for zserio bool arrays which are mapped to Java boolean[] array.
     */
    public static class BoolArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return true;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return 1;
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readBool());

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeBool(((ArrayElement.BooleanArrayElement)element).get());
        }

        private final ArrayElement.BooleanArrayElement element = new ArrayElement.BooleanArrayElement();
    }

    /**
     * Array traits for zserio extern bit buffer arrays which are mapped to Java BitBuffer[] array.
     */
    public static class BitBufferArrayTraits implements ArrayTraits
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfBitBuffer(
                    ((ArrayElement.ObjectArrayElement<BitBuffer>)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(reader.readBitBuffer());

            return element;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            writer.writeBitBuffer(((ArrayElement.ObjectArrayElement<BitBuffer>)element).get());
        }

        private final ArrayElement.ObjectArrayElement<BitBuffer> element =
                new ArrayElement.ObjectArrayElement<>();
    }

    /**
     * Array traits for zserio object arrays (without writer part) which are mapped to Java zserio object array.
     */
    public static class ObjectArrayTraits<E extends SizeOf> implements ArrayTraits
    {
        /**
         * Constructor from element factory.
         *
         * @param elementFactory Element factory to construct from.
         */
        public ObjectArrayTraits(ElementFactory<E> elementFactory)
        {
            this.elementFactory = elementFactory;
        }

        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return ((ArrayElement.ObjectArrayElement<E>)element).get().bitSizeOf(bitPosition);
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            throw new UnsupportedOperationException(
                    "Array: initializeOffsets is not implemented for read only ObjectArrayTraits!");
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            element.set(elementFactory.create(reader, index));

            return element;
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            throw new UnsupportedOperationException(
                    "Array: write is not implemented for read only ObjectArrayTraits!");
        }

        private final ElementFactory<E> elementFactory;
        private final ArrayElement.ObjectArrayElement<E> element = new ArrayElement.ObjectArrayElement<>();
    }

    /**
     * Array traits for zserio object arrays (with writer part) which are mapped to Java zserio object array.
     */
    public static class WriteObjectArrayTraits<E extends InitializeOffsetsWriter & SizeOf>
            extends ObjectArrayTraits<E>
    {
        /**
         * Constructor from element factory.
         *
         * @param elementFactory Element factory to construct from.
         */
        public WriteObjectArrayTraits(ElementFactory<E> elementFactory)
        {
            super(elementFactory);
        }

        @SuppressWarnings("unchecked")
        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return ((ArrayElement.ObjectArrayElement<E>)element).get().initializeOffsets(bitPosition);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException, ZserioError
        {
            ((ArrayElement.ObjectArrayElement<E>)element).get().write(writer);
        }
    }
}
