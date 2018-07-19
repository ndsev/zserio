package zserio.runtime.array;

import java.util.Iterator;

/**
 * Helper class that wraps a Number-derived iterator and turns it into a Long iterator.
 *
 * @param <T> The type of the underlying iterator.
 */
class IteratorWrapper<T extends Number> implements Iterator<Long>
{
    public IteratorWrapper(Iterator<T> iterator)
    {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext()
    {
        return iterator.hasNext();
    }

    @Override
    public Long next()
    {
        return Long.valueOf(iterator.next().longValue());
    }

    @Override
    public void remove()
    {
        iterator.remove();
    }

    private final Iterator<T> iterator;
}
