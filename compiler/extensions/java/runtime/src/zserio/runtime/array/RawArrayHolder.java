package zserio.runtime.array;

import java.util.Arrays;

/**
 * Interface for classes which holds raw array.
 *
 * This interface gives the same abstraction for Java native arrays of primitive types like int[] and for
 * ArrayList of objects. Using the Java native arrays for primitive types is preferred because
 * ArrayList of primitive types wrapper like Integer brings performance penalty during boxing/unboxing.
 */
public interface RawArrayHolder
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
     * @return The underlying raw array.
     */
    public <T> T getRawArray();

    /**
     * Raw array holder for Java native array of bytes.
     */
    public static class ByteArray implements RawArrayHolder
    {
        /**
         * Empty constructor.
         */
        public ByteArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public ByteArray(byte[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof ByteArray) ? Arrays.equals(rawArray, ((ByteArray)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return (rawArray == null) ? 0 : Arrays.hashCode(rawArray);
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

        private byte[] rawArray;
    }

    /**
     * Raw array holder for Java native array of shorts.
     */
    public static class ShortArray implements RawArrayHolder
    {
        /**
         * Empty constructor.
         */
        public ShortArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public ShortArray(short[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof ShortArray) ? Arrays.equals(rawArray, ((ShortArray)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return (rawArray == null) ? 0 : Arrays.hashCode(rawArray);
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

        private short[] rawArray;
    }

    /**
     * Raw array holder for Java native array of ints.
     */
    public static class IntArray implements RawArrayHolder
    {
        /**
         * Empty constructor.
         */
        public IntArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public IntArray(int[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof IntArray) ? Arrays.equals(rawArray, ((IntArray)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return (rawArray == null) ? 0 : Arrays.hashCode(rawArray);
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

        private int[] rawArray;
    }

    /**
     * Raw array holder for Java native array of longs.
     */
    public static class LongArray implements RawArrayHolder
    {
        /**
         * Empty constructor.
         */
        public LongArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public LongArray(long[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof LongArray) ? Arrays.equals(rawArray, ((LongArray)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return (rawArray == null) ? 0 : Arrays.hashCode(rawArray);
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

        private long[] rawArray;
    }

    /**
     * Raw array holder for Java native array of floats.
     */
    public static class FloatArray implements RawArrayHolder
    {
        /**
         * Empty constructor.
         */
        public FloatArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public FloatArray(float[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof FloatArray) ? Arrays.equals(rawArray, ((FloatArray)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return (rawArray == null) ? 0 : Arrays.hashCode(rawArray);
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

        private float[] rawArray;
    }

    /**
     * Raw array holder for Java native array of doubles.
     */
    public static class DoubleArray implements RawArrayHolder
    {
        /**
         * Empty constructor.
         */
        public DoubleArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public DoubleArray(double[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof DoubleArray) ? Arrays.equals(rawArray, ((DoubleArray)obj).rawArray) : false;
        }

        @Override
        public int hashCode()
        {
            return (rawArray == null) ? 0 : Arrays.hashCode(rawArray);
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

        private double[] rawArray;
    }

    /**
     * Raw array holder for Java native array of booleans.
     */
    public static class BooleanArray implements RawArrayHolder
    {
        /**
         * Empty constructor.
         */
        public BooleanArray()
        {
        }

        /**
         * Constructor from raw array.
         *
         * @param rawArray Raw array to construct from.
         */
        public BooleanArray(boolean[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof BooleanArray) ? Arrays.equals(rawArray, ((BooleanArray)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return (rawArray == null) ? 0 : Arrays.hashCode(rawArray);
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

        private boolean[] rawArray;
    }

    /**
     * Raw array holder for ArrayList of objects.
     */
    public static class ObjectArray<E> implements RawArrayHolder
    {
        /**
         * Constructor from element class object.
         *
         * @param clazz Element class object.
         */
        public ObjectArray(Class<E> clazz)
        {
            this.clazz = clazz;
        }

        /**
         * Constructor from raw array.
         *
         * @param clazz    Element class object.
         * @param rawArray Raw array to construct from.
         */
        public ObjectArray(Class<E> clazz, E[] rawArray)
        {
            this.clazz = clazz;
            this.rawArray = rawArray;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(java.lang.Object obj)
        {
            return (obj instanceof ObjectArray) ? Arrays.equals(rawArray, ((ObjectArray<E>)obj).rawArray) :
                false;
        }

        @Override
        public int hashCode()
        {
            return (rawArray == null) ? 0 : Arrays.hashCode(rawArray);
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
            rawArray = (E[])java.lang.reflect.Array.newInstance(clazz, capacity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        private final Class<E> clazz;
        private E[] rawArray;
    }
}
