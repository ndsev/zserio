package zserio.runtime.array;

import zserio.runtime.Mapping;

/**
 * Interface for the array classes.
 *
 * Array classes are classes to which Zserio arrays are mapped in Java.
 *
 * @param <E> The type of elements maintained by this array.
 */
public interface Array<E> extends Iterable<E>
{
    /**
     * Maps the array with the given mapping.
     *
     * @param mapping The mapping to use.
     *
     * @return New array with mapped elements.
     */
    public Array<E> map(Mapping<E> mapping);

    /**
     * Splits an array into a sub array with the given length beginning at the given begin value.
     *
     * @param offset The beginning position.
     * @param length The length of the sub array.
     *
     * @return the split sub array
     */
    public Array<E> subRange(int offset, int length);

    /**
     * Returns the length of the array.
     *
     * @return Number of array elements.
     */
    public int length();

    /**
     * Length used in read methods which means implicit length array.
     */
    public int IMPLICIT_LENGTH = -1;

    /**
     * Length used in read methods which means auto length array.
     */
    public int AUTO_LENGTH = -2;
}
