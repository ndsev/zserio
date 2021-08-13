package zserio.runtime.array;

import java.util.Arrays;

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

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ByteArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            element.set(rawArray[index]);

            return element;
        }

        private final ArrayElement.ByteArrayElement element = new ArrayElement.ByteArrayElement();
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

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.ShortArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            element.set(rawArray[index]);

            return element;
        }

        private final ArrayElement.ShortArrayElement element = new ArrayElement.ShortArrayElement();
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

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.IntArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            element.set(rawArray[index]);

            return element;
        }

        private final ArrayElement.IntArrayElement element = new ArrayElement.IntArrayElement();
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

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.LongArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            element.set(rawArray[index]);

            return element;
        }

        private final ArrayElement.LongArrayElement element = new ArrayElement.LongArrayElement();
        private long[] rawArray;
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

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.FloatArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            element.set(rawArray[index]);

            return element;
        }

        private final ArrayElement.FloatArrayElement element = new ArrayElement.FloatArrayElement();
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

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.DoubleArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            element.set(rawArray[index]);

            return element;
        }

        private final ArrayElement.DoubleArrayElement element = new ArrayElement.DoubleArrayElement();
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

        @Override
        public void setElement(ArrayElement element, int index)
        {
            rawArray[index] = ((ArrayElement.BooleanArrayElement)element).get();
        }

        @Override
        public ArrayElement getElement(int index)
        {
            element.set(rawArray[index]);

            return element;
        }

        private final ArrayElement.BooleanArrayElement element = new ArrayElement.BooleanArrayElement();
        private boolean[] rawArray;
    }

    /**
     * Raw array for Java native array of objects.
     */
    public static class ObjectRawArray<E> implements RawArray
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
            element.set(rawArray[index]);

            return element;
        }

        private final ArrayElement.ObjectArrayElement<E> element = new ArrayElement.ObjectArrayElement<E>();
        private final Class<E> elementClass;
        private E[] rawArray;
    }
}
