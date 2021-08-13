package zserio.runtime.array;

/**
 * Common interface for all array elements.
 */
public interface ArrayElement
{
    /**
     * Dummy element instantiation used by array wrapper where element is not applicable.
     */
    public static ArrayElement Dummy = new DummyElement();

    /**
     * Dummy array element.
     */
    public static class DummyElement implements ArrayElement
    {
    }

    /**
     * Array element for bytes.
     */
    public static class ByteArrayElement implements ArrayElement
    {
        /**
         * Sets the element value.
         *
         * @param element Element value to set.
         */
        public void set(byte element)
        {
            this.element = element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public byte get()
        {
            return element;
        }

        private byte element;
    }

    /**
     * Array element for shorts.
     */
    public static class ShortArrayElement implements ArrayElement
    {
        /**
         * Sets the element value.
         *
         * @param element Element value to set.
         */
        public void set(short element)
        {
            this.element = element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public short get()
        {
            return element;
        }

        private short element;
    }

    /**
     * Array element for ints.
     */
    public static class IntArrayElement implements ArrayElement
    {
        /**
         * Sets the element value.
         *
         * @param element Element value to set.
         */
        public void set(int element)
        {
            this.element = element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public int get()
        {
            return element;
        }

        private int element;
    }

    /**
     * Array element for longs.
     */
    public static class LongArrayElement implements ArrayElement
    {
        /**
         * Sets the element value.
         *
         * @param element Element value to set.
         */
        public void set(long element)
        {
            this.element = element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public long get()
        {
            return element;
        }

        private long element;
    }

    /**
     * Array element for floats.
     */
    public static class FloatArrayElement implements ArrayElement
    {
        /**
         * Sets the element value.
         *
         * @param element Element value to set.
         */
        public void set(float element)
        {
            this.element = element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public float get()
        {
            return element;
        }

        private float element;
    }

    /**
     * Array element for doubles.
     */
    public static class DoubleArrayElement implements ArrayElement
    {
        /**
         * Sets the element value.
         *
         * @param element Element value to set.
         */
        public void set(double element)
        {
            this.element = element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public double get()
        {
            return element;
        }

        private double element;
    }

    /**
     * Array element for booleans.
     */
    public static class BooleanArrayElement implements ArrayElement
    {
        /**
         * Sets the element value.
         *
         * @param element Element value to set.
         */
        public void set(boolean element)
        {
            this.element = element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public boolean get()
        {
            return element;
        }

        private boolean element;
    }

    /**
     * Array element for objects.
     */
    public static class ObjectArrayElement<E> implements ArrayElement
    {
        /**
         * Sets the element value.
         *
         * @param element Element value to set.
         */
        public void set(E element)
        {
            this.element = element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public E get()
        {
            return element;
        }

        private E element;
    }
}
