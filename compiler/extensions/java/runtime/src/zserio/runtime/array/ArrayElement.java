package zserio.runtime.array;

import java.math.BigInteger;

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
     * Interface for elements of integral arrays.
     */
    public static interface IntegralArrayElement extends ArrayElement
    {
        /**
         * Converts the element value to big integer.
         *
         * @return Element value as BigInteger.
         */
        public BigInteger toBigInteger();
    }

    /**
     * Dummy array element.
     */
    public static class DummyElement implements ArrayElement
    {}

    /**
     * Array element for bytes.
     */
    public static class ByteArrayElement implements IntegralArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public ByteArrayElement(byte element)
        {
            this.element = element;
        }

        @Override
        public BigInteger toBigInteger()
        {
            return BigInteger.valueOf(element);
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

        private final byte element;
    }

    /**
     * Array element for shorts.
     */
    public static class ShortArrayElement implements IntegralArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public ShortArrayElement(short element)
        {
            this.element = element;
        }

        @Override
        public BigInteger toBigInteger()
        {
            return BigInteger.valueOf(element);
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

        private final short element;
    }

    /**
     * Array element for ints.
     */
    public static class IntArrayElement implements IntegralArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public IntArrayElement(int element)
        {
            this.element = element;
        }

        @Override
        public BigInteger toBigInteger()
        {
            return BigInteger.valueOf(element);
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

        private final int element;
    }

    /**
     * Array element for longs.
     */
    public static class LongArrayElement implements IntegralArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public LongArrayElement(long element)
        {
            this.element = element;
        }

        @Override
        public BigInteger toBigInteger()
        {
            return BigInteger.valueOf(element);
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

        private final long element;
    }

    /**
     * Array element for big integers.
     */
    public static class BigIntegerArrayElement implements IntegralArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public BigIntegerArrayElement(BigInteger element)
        {
            this.element = element;
        }

        @Override
        public BigInteger toBigInteger()
        {
            return element;
        }

        /**
         * Gets the element value.
         *
         * @return Element value.
         */
        public BigInteger get()
        {
            return element;
        }

        private final BigInteger element;
    }

    /**
     * Array element for floats.
     */
    public static class FloatArrayElement implements ArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public FloatArrayElement(float element)
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

        private final float element;
    }

    /**
     * Array element for doubles.
     */
    public static class DoubleArrayElement implements ArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public DoubleArrayElement(double element)
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

        private final double element;
    }

    /**
     * Array element for booleans.
     */
    public static class BooleanArrayElement implements ArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public BooleanArrayElement(boolean element)
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

        private final boolean element;
    }

    /**
     * Array element for objects.
     */
    public static class ObjectArrayElement<E> implements ArrayElement
    {
        /**
         * Constructor.
         *
         * @param element Element to construct from.
         */
        public ObjectArrayElement(E element)
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

        private final E element;
    }
}
