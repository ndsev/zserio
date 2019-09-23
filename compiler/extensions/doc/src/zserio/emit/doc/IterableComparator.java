package zserio.emit.doc;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A lexicographical comparator on sequences.
 *
 * @param <T> Type of the element of the sequence. The type itself must implement Comparable&lt;T&gt;.
 */
class IterableComparator<T extends Comparable<T>> implements Comparator<Iterable<T>>
{
    @Override
    public int compare(Iterable<T> a, Iterable<T> b)
    {
        Iterator<T> iteratorA = a.iterator();
        Iterator<T> iteratorB = b.iterator();

        while (iteratorA.hasNext() && iteratorB.hasNext())
        {
            final T valueA = iteratorA.next();
            final T valueB = iteratorB.next();
            final int result = valueA.compareTo(valueB);
            if (result != 0)
                return result;
        }

        if (!iteratorA.hasNext() && !iteratorB.hasNext())
            return 0; // sequences are equal

        // the lengths of the sequences differs -> shorter is "smaller"
        return iteratorA.hasNext() ? +1 : -1;
    }
}
