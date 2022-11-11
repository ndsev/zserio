package zserio.runtime.array;

import java.math.BigInteger;
import java.util.Arrays;

import zserio.runtime.HashCodeUtil;
import zserio.runtime.SizeOf;
import zserio.runtime.ZserioEnum;
import zserio.runtime.io.BitBuffer;

/**
 * Interface for classes which holds raw array.
 *
 * This interface gives the same abstraction for Java native arrays of primitive types like int[] and for
 * Java native arrays of objects. Using the Java native arrays for primitive types is preferred because
 * ArrayList of primitive types wrapper like Integer brings performance penalty during boxing/unboxing.
 */
public interface RawArray
{
    /**
     * Gets the raw array size.
     *
     * @return Number of elements stored in the raw array.
     */
    public int size();

    /**
     * Resets the raw array.
     *
     * @param capacity Desired capacity of the raw array after reset.
     */
    public void reset(int capacity);

    /**
     * Gets the underlying raw array.
     *
     * @param <T> Java array type to be returned.
     *
     * @return The underlying raw array.
     */
    public <T> T getRawArray();

    /**
     * Sets the raw array element.
     *
     * @param element Raw array element to set.
     * @param index Index of element to set.
     */
    public void setElement(ArrayElement element, int index);

    /**
     * Gets the raw array element.
     *
     * @param index Index of element.
     *
     * @return The raw array element.
     */
    public ArrayElement getElement(int index);

    /**
     * Raw array for Java native array of bytes.
     */
    public static class ByteRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public ByteRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public ByteRawArray(byte[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof ByteRawArray) ? Arrays.equals(rawArray, ((ByteRawArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new byte[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ByteArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.ByteArrayElement(rawArray[index]);
        }

        private byte[] rawArray;
    }

    /**
     * Raw array for Java native array of shorts.
     */
    public static class ShortRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public ShortRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public ShortRawArray(short[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof ShortRawArray) ? Arrays.equals(rawArray, ((ShortRawArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new short[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ShortArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.ShortArrayElement(rawArray[index]);
        }

        private short[] rawArray;
    }

    /**
     * Raw array for Java native array of ints.
     */
    public static class IntRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public IntRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public IntRawArray(int[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof IntRawArray) ? Arrays.equals(rawArray, ((IntRawArray)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new int[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.IntArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.IntArrayElement(rawArray[index]);
        }

        private int[] rawArray;
    }

    /**
     * Raw array for Java native array of longs.
     */
    public static class LongRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public LongRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public LongRawArray(long[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof LongRawArray) ? Arrays.equals(rawArray, ((LongRawArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new long[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.LongArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.LongArrayElement(rawArray[index]);
        }

        private long[] rawArray;
    }

    /**
     * Raw array for Java native array of BigIntegers.
     */
    public static class BigIntegerRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public BigIntegerRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public BigIntegerRawArray(BigInteger[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof BigIntegerRawArray) ?
                    Arrays.equals(rawArray, ((BigIntegerRawArray)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new BigInteger[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.BigIntegerArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.BigIntegerArrayElement(rawArray[index]);
        }

        private BigInteger[] rawArray;
    }

    /**
     * Raw array for Java native array of floats.
     */
    public static class FloatRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public FloatRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public FloatRawArray(float[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof FloatRawArray) ? Arrays.equals(rawArray, ((FloatRawArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new float[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.FloatArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.FloatArrayElement(rawArray[index]);
        }

        private float[] rawArray;
    }

    /**
     * Raw array for Java native array of doubles.
     */
    public static class DoubleRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public DoubleRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public DoubleRawArray(double[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof DoubleRawArray) ? Arrays.equals(rawArray, ((DoubleRawArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new double[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.DoubleArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.DoubleArrayElement(rawArray[index]);
        }

        private double[] rawArray;
    }

    /**
     * Raw array for Java native array of booleans.
     */
    public static class BooleanRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public BooleanRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public BooleanRawArray(boolean[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof BooleanRawArray) ? Arrays.equals(rawArray, ((BooleanRawArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new boolean[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.BooleanArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.BooleanArrayElement(rawArray[index]);
        }

        private boolean[] rawArray;
    }

    /**
     * Raw array for Java native array of bytes.
     */
    public static class BytesRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public BytesRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public BytesRawArray(byte[][] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof BytesRawArray) ? Arrays.deepEquals(rawArray, ((BytesRawArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new byte[capacity][];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ObjectArrayElement<byte[]>)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.ObjectArrayElement<>(rawArray[index]);
        }

        private byte[][] rawArray;
    }

    /**
     * Raw array for Java native array of Strings.
     */
    public static class StringRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public StringRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public StringRawArray(String[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof StringRawArray) ? Arrays.equals(rawArray, ((StringRawArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new String[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ObjectArrayElement<String>)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.ObjectArrayElement<>(rawArray[index]);
        }

        private String[] rawArray;
    }

    /**
     * Raw array for Java native array of BitBuffer.
     */
    public static class BitBufferRawArray implements RawArray
    {
        /**
         * Empty constructor.
         */
        public BitBufferRawArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public BitBufferRawArray(BitBuffer[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof BitBufferRawArray)
                    ? Arrays.equals(rawArray, ((BitBufferRawArray)obj).rawArray)
                    : false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @Override
        public void reset(int capacity)
        {
            rawArray = new BitBuffer[capacity];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ObjectArrayElement<BitBuffer>)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.ObjectArrayElement<>(rawArray[index]);
        }

        private BitBuffer[] rawArray;
    }

    /**
     * Raw array for Java native array of enums.
     */
    public static class EnumRawArray<E extends ZserioEnum & SizeOf> implements RawArray
    {
        /**
         * Constructor from element class object.
         *
         * @param elementClass Element class object.
         */
        public EnumRawArray(Class<E> elementClass)
        {
            this.elementClass = elementClass;
        }

        /**
         * Constructor from raw array.
         *
         * @param elementClass Element class object.
         * @param rawArray     Raw array to construct from.
         */
        public EnumRawArray(Class<E> elementClass, E[] rawArray)
        {
            this.elementClass = elementClass;
            this.rawArray = rawArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof EnumRawArray) ?
                    Arrays.equals(rawArray, ((EnumRawArray<E>)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void reset(int capacity)
        {
            rawArray = (E[])java.lang.reflect.Array.newInstance(elementClass, capacity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ObjectArrayElement<E>)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.ObjectArrayElement<>(rawArray[index]);
        }

        private final Class<E> elementClass;
        private E[] rawArray;
    }

    /**
     * Raw array for Java native array of objects.
     */
    public static class ObjectRawArray<E extends SizeOf> implements RawArray
    {
        /**
         * Constructor from element class object.
         *
         * @param elementClass Element class object.
         */
        public ObjectRawArray(Class<E> elementClass)
        {
            this.elementClass = elementClass;
        }

        /**
         * Constructor from raw array.
         *
         * @param elementClass Element class object.
         * @param rawArray     Raw array to construct from.
         */
        public ObjectRawArray(Class<E> elementClass, E[] rawArray)
        {
            this.elementClass = elementClass;
            this.rawArray = rawArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof ObjectRawArray) ?
                    Arrays.equals(rawArray, ((ObjectRawArray<E>)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, rawArray);
        }

        @Override
        public int size()
        {
            return (rawArray == null) ? 0 : rawArray.length;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void reset(int capacity)
        {
            rawArray = (E[])java.lang.reflect.Array.newInstance(elementClass, capacity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ObjectArrayElement<E>)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            return new ArrayElement.ObjectArrayElement<>(rawArray[index]);
        }

        private final Class<E> elementClass;
        private E[] rawArray;
    }
}
