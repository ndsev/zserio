package zserio.runtime;

/**
 * Interface for mapping array elements.
 *
 * This interface is used for <code>map</code> method in all array classes.
 *
 * @see zserio.runtime.array.Array
 *
 * @param <E> The type of elements maintained by this mapping.
 */
public interface Mapping<E>
{
    /**
     * Creates a mapping of one array element.
     *
     * @param in An element which to map.
     *
     * @return An element to which map the element <code>in</code>.
     */
    E map(E in);
}
