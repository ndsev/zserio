package zserio.runtime.array;

/**
 * Packing context.
 *
 * This class is used as a base class for all packing contexts.
 *
 * For built-in packable types only a single context (Delta context) is kept. However for Zserio objects, a tree
 * of Delta contexts for all packable fields is created recursively.
 */
public class PackingContext
{
    /**
     * Casts packing context instance to the specified inherited class.
     *
     * @param <T> Class inherited from packing context.
     *
     * @return Packing context instance casted to the inherited class.
     */
    @SuppressWarnings("unchecked")
    public <T extends PackingContext> T cast()
    {
        return (T)this;
    }
}
