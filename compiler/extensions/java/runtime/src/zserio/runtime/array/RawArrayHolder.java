package zserio.runtime.array;

import java.util.ArrayList;

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
            reset(0);
        }

        /**
         * Constructor from the raw array.
         *
         * @param rawArray Java native array of bytes to construct from.
         */
        public ByteArray(byte[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public int size()
        {
            return rawArray.length;
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
            reset(0);
        }

        /**
         * Constructor from the raw array.
         *
         * @param rawArray Java native array of shorts to construct from.
         */
        public ShortArray(short[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public int size()
        {
            return rawArray.length;
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
            reset(0);
        }

        /**
         * Constructor from the raw array.
         *
         * @param rawArray Java native array of ints to construct from.
         */
        public IntArray(int[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public int size()
        {
            return rawArray.length;
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
            reset(0);
        }

        /**
         * Constructor from the raw array.
         *
         * @param rawArray Java native array of longs to construct from.
         */
        public LongArray(long[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public int size()
        {
            return rawArray.length;
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
            reset(0);
        }

        /**
         * Constructor from the raw array.
         *
         * @param rawArray Java native array of floats to construct from.
         */
        public FloatArray(float[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public int size()
        {
            return rawArray.length;
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
            reset(0);
        }

        /**
         * Constructor from the raw array.
         *
         * @param rawArray Java native array of doubles to construct from.
         */
        public DoubleArray(double[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public int size()
        {
            return rawArray.length;
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
            reset(0);
        }

        /**
         * Constructor from the raw array.
         *
         * @param rawArray Java native array of booleans to construct from.
         */
        public BooleanArray(boolean[] rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public int size()
        {
            return rawArray.length;
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
         * Empty constructor.
         */
        public ObjectArray()
        {
            rawArray = new ArrayList<E>();
        }

        /**
         * Constructor from the raw array.
         *
         * @param rawArray ArrayList of objects to construct from.
         */
        public ObjectArray(ArrayList<E> rawArray)
        {
            this.rawArray = rawArray;
        }

        @Override
        public int size()
        {
            return rawArray.size();
        }

        @Override
        public void reset(int capacity)
        {
            rawArray.ensureCapacity(capacity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getRawArray()
        {
            return (T)rawArray;
        }

        private final ArrayList<E> rawArray;
    }
}
