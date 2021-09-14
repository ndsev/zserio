package zserio.runtime.array;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.SizeOf;
import zserio.runtime.array.ArrayElement.IntegralArrayElement;
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
     * Gets packed array traits.
     *
     * @return Instance of packed array traits or null if the array is not packable.
     */
    public PackedArrayTraits getPackedArrayTraits();

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
     */
    public ArrayElement read(BitStreamReader reader, int index) throws IOException;

    /**
     * Writes the array element to the bit stream.
     *
     * @param writer  Bit stream write to write to.
     * @param element Array element.
     *
     * @throws IOException Failure during bit stream manipulation.
     */
    public void write(BitStreamWriter writer, ArrayElement element) throws IOException;

    /**
     * Interface for integral array traits.
     */
    public static interface IntegralArrayTraits extends ArrayTraits
    {
        /**
         * Creates integral array element from given big integer value.
         *
         * @param value Big integer value of the element to create.
         *
         * @return Integral array element.
         */
        public IntegralArrayElement fromBigInteger(BigInteger value);

        /**
         * Reads the array element from the bit stream.
         *
         * @param reader Bit stream reader to read from.
         *
         * @return Array element filled by read element.
         *
         * @throws IOException Failure during bit stream manipulation.
         */
        public IntegralArrayElement read(BitStreamReader reader) throws IOException;
    }

    /**
     * Implementation of common integral array traits functionality.
     */
    public static abstract class IntegralArrayTraitsBase implements IntegralArrayTraits
    {
        /**
         * Constructor.
         */
        public IntegralArrayTraitsBase()
        {
            this.packedArrayTraits = new PackedArrayTraits.IntegralPackedArrayTraits(this);
        }

        @Override
        public PackedArrayTraits getPackedArrayTraits()
        {
            return packedArrayTraits;
        }

        @Override
        public IntegralArrayElement read(BitStreamReader reader, int index) throws IOException
        {
            return read(reader);
        }

        private final PackedArrayTraits packedArrayTraits;
    }

    /**
     * Array traits for zserio int8 and int:1...int:8 arrays which are mapped to Java byte[] array.
     */
    public static class SignedBitFieldByteArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.ByteArrayElement((byte)reader.readSignedBits(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeSignedBits(((ArrayElement.ByteArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.ByteArrayElement(bigInteger.byteValue());
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio int16 and int:9...int:16 arrays which are mapped to Java short[] array.
     */
    public static class SignedBitFieldShortArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.ShortArrayElement((short)reader.readSignedBits(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeSignedBits(((ArrayElement.ShortArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.ShortArrayElement(bigInteger.shortValue());
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio int32 and int:17...int:32 arrays which are mapped to Java int[] array.
     */
    public static class SignedBitFieldIntArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.IntArrayElement((int)reader.readSignedBits(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeSignedBits(((ArrayElement.IntArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.IntArrayElement(bigInteger.intValue());
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio int64 and int:33...int:64 arrays which are mapped to Java long[] array.
     */
    public static class SignedBitFieldLongArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.LongArrayElement(reader.readSignedBits(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeSignedBits(((ArrayElement.LongArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.LongArrayElement(bigInteger.longValue());
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio bit:1...bit:7 arrays which are mapped to Java byte[] array.
     */
    public static class BitFieldByteArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.ByteArrayElement((byte)reader.readBits(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeBits(((ArrayElement.ByteArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.ByteArrayElement(bigInteger.byteValue());
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio uint8 and bit:8...bit:15 arrays which are mapped to Java short[] array.
     */
    public static class BitFieldShortArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.ShortArrayElement((short)reader.readBits(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeBits(((ArrayElement.ShortArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.ShortArrayElement(bigInteger.shortValue());
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio uint16 and bit:16...bit:31 arrays which are mapped to Java int[] array.
     */
    public static class BitFieldIntArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.IntArrayElement((int)reader.readBits(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeBits(((ArrayElement.IntArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.IntArrayElement(bigInteger.intValue());
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio uint32 and bit:32...bit:63 arrays which are mapped to Java long[] array.
     */
    public static class BitFieldLongArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.LongArrayElement(reader.readBits(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeBits(((ArrayElement.LongArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.LongArrayElement(bigInteger.longValue());
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio uint64, bit:64 and dynamic bit field arrays which are mapped to
     * Java BigInteger[] array.
     */
    public static class BitFieldBigIntegerArrayTraits extends IntegralArrayTraitsBase
    {
        /**
         * Constructor from number of bits of zserio type.
         *
         * @param numBits Number of bits of zserio type.
         */
        public BitFieldBigIntegerArrayTraits(int numBits)
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.BigIntegerArrayElement(reader.readBigInteger(numBits));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeBigInteger(((ArrayElement.BigIntegerArrayElement)element).get(), numBits);
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.BigIntegerArrayElement(bigInteger);
        }

        private final int numBits;
    }

    /**
     * Array traits for zserio varint16 arrays which are mapped to Java short[] array.
     */
    public static class VarInt16ArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.ShortArrayElement(reader.readVarInt16());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarInt16(((ArrayElement.ShortArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.ShortArrayElement(bigInteger.shortValue());
        }
    }

    /**
     * Array traits for zserio varint32 arrays which are mapped to Java int[] array.
     */
    public static class VarInt32ArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.IntArrayElement(reader.readVarInt32());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarInt32(((ArrayElement.IntArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.IntArrayElement(bigInteger.intValue());
        }
    }

    /**
     * Array traits for zserio varint64 arrays which are mapped to Java long[] array.
     */
    public static class VarInt64ArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.LongArrayElement(reader.readVarInt64());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarInt64(((ArrayElement.LongArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.LongArrayElement(bigInteger.longValue());
        }
    }

    /**
     * Array traits for zserio varint arrays which are mapped to Java long[] array.
     */
    public static class VarIntArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.LongArrayElement(reader.readVarInt());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarInt(((ArrayElement.LongArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.LongArrayElement(bigInteger.longValue());
        }
    }

    /**
     * Array traits for zserio varuint16 arrays which are mapped to Java short[] array.
     */
    public static class VarUInt16ArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.ShortArrayElement(reader.readVarUInt16());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarUInt16(((ArrayElement.ShortArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.ShortArrayElement(bigInteger.shortValue());
        }
    }

    /**
     * Array traits for zserio varuint32 arrays which are mapped to Java int[] array.
     */
    public static class VarUInt32ArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.IntArrayElement(reader.readVarUInt32());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarUInt32(((ArrayElement.IntArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.IntArrayElement(bigInteger.intValue());
        }
    }

    /**
     * Array traits for zserio varuint64 arrays which are mapped to Java long[] array.
     */
    public static class VarUInt64ArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.LongArrayElement(reader.readVarUInt64());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarUInt64(((ArrayElement.LongArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.LongArrayElement(bigInteger.longValue());
        }
    }

    /**
     * Array traits for zserio varuint arrays which are mapped to Java BigInteger[] array.
     */
    public static class VarUIntArrayTraits extends IntegralArrayTraitsBase
    {
        @Override
        public boolean isBitSizeOfConstant()
        {
            return false;
        }

        @Override
        public int bitSizeOf(long bitPosition, ArrayElement element)
        {
            return BitSizeOfCalculator.getBitSizeOfVarUInt(
                    ((ArrayElement.BigIntegerArrayElement)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.BigIntegerArrayElement(reader.readVarUInt());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarUInt(((ArrayElement.BigIntegerArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.BigIntegerArrayElement(bigInteger);
        }
    }

    /**
     * Array traits for zserio varsize arrays which are mapped to Java int[] array.
     */
    public static class VarSizeArrayTraits extends IntegralArrayTraitsBase
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
        public IntegralArrayElement read(BitStreamReader reader) throws IOException
        {
            return new ArrayElement.IntArrayElement(reader.readVarSize());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeVarSize(((ArrayElement.IntArrayElement)element).get());
        }

        @Override
        public IntegralArrayElement fromBigInteger(BigInteger bigInteger)
        {
            return new ArrayElement.IntArrayElement(bigInteger.intValue());
        }
    }

    /**
     * Array traits for zserio float16 arrays which are mapped to Java float[] array.
     */
    public static class Float16ArrayTraits implements ArrayTraits
    {
        @Override
        public PackedArrayTraits getPackedArrayTraits()
        {
            return null;
        }

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
        public ArrayElement read(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayElement.FloatArrayElement(reader.readFloat16());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeFloat16(((ArrayElement.FloatArrayElement)element).get());
        }
    }

    /**
     * Array traits for zserio float32 arrays which are mapped to Java float[] array.
     */
    public static class Float32ArrayTraits implements ArrayTraits
    {
        @Override
        public PackedArrayTraits getPackedArrayTraits()
        {
            return null;
        }

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
        public ArrayElement read(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayElement.FloatArrayElement(reader.readFloat32());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeFloat32(((ArrayElement.FloatArrayElement)element).get());
        }
    }

    /**
     * Array traits for zserio float64 arrays which are mapped to Java double[] array.
     */
    public static class Float64ArrayTraits implements ArrayTraits
    {
        @Override
        public PackedArrayTraits getPackedArrayTraits()
        {
            return null;
        }

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
        public ArrayElement read(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayElement.DoubleArrayElement(reader.readFloat64());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeFloat64(((ArrayElement.DoubleArrayElement)element).get());
        }
    }

    /**
     * Array traits for zserio string arrays which are mapped to Java String[] array.
     */
    public static class StringArrayTraits implements ArrayTraits
    {
        @Override
        public PackedArrayTraits getPackedArrayTraits()
        {
            return null;
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
            return BitSizeOfCalculator.getBitSizeOfString(
                    ((ArrayElement.ObjectArrayElement<String>)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayElement.ObjectArrayElement<>(reader.readString());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeString(((ArrayElement.ObjectArrayElement<String>)element).get());
        }
    }

    /**
     * Array traits for zserio bool arrays which are mapped to Java boolean[] array.
     */
    public static class BoolArrayTraits implements ArrayTraits
    {
        @Override
        public PackedArrayTraits getPackedArrayTraits()
        {
            return null;
        }

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
        public ArrayElement read(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayElement.BooleanArrayElement(reader.readBool());
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeBool(((ArrayElement.BooleanArrayElement)element).get());
        }
    }

    /**
     * Array traits for zserio extern bit buffer arrays which are mapped to Java BitBuffer[] array.
     */
    public static class BitBufferArrayTraits implements ArrayTraits
    {
        @Override
        public PackedArrayTraits getPackedArrayTraits()
        {
            return null;
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
            return BitSizeOfCalculator.getBitSizeOfBitBuffer(
                    ((ArrayElement.ObjectArrayElement<BitBuffer>)element).get());
        }

        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return bitPosition + bitSizeOf(bitPosition, element);
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayElement.ObjectArrayElement<>(reader.readBitBuffer());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            writer.writeBitBuffer(((ArrayElement.ObjectArrayElement<BitBuffer>)element).get());
        }
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
        public PackedArrayTraits getPackedArrayTraits()
        {
            return new PackedArrayTraits.ObjectPackedArrayTraits<>(elementFactory);
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
                    "ArrayTraits: initializeOffsets is not implemented for read only ObjectArrayTraits!");
        }

        @Override
        public ArrayElement read(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayElement.ObjectArrayElement<>(elementFactory.create(reader, index));
        }

        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            throw new UnsupportedOperationException(
                    "ArrayTraits: write is not implemented for read only ObjectArrayTraits!");
        }

        protected ElementFactory<E> getElementFactory()
        {
            return elementFactory;
        }

        private final ElementFactory<E> elementFactory;
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

        @Override
        public PackedArrayTraits getPackedArrayTraits()
        {
            return new PackedArrayTraits.WriteObjectPackedArrayTraits<>(getElementFactory());
        }

        @SuppressWarnings("unchecked")
        @Override
        public long initializeOffsets(long bitPosition, ArrayElement element)
        {
            return ((ArrayElement.ObjectArrayElement<E>)element).get().initializeOffsets(bitPosition);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(BitStreamWriter writer, ArrayElement element) throws IOException
        {
            ((ArrayElement.ObjectArrayElement<E>)element).get().write(writer);
        }
    }
}
