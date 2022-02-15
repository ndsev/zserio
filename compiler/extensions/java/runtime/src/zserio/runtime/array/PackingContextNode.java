package zserio.runtime.array;

import java.util.ArrayList;
import java.util.List;

import zserio.runtime.ZserioError;

/**
 * Packing context node.
 *
 * This class is used to handle a tree of contexts created by appropriate PackedArrayTraits.
 * For built-in packable types only a single context is kept. However for Zserio objects, a tree
 * of all packable fields is created recursively.
 *
 * When the context node has no children and no context, then it's so called dummy context which is used
 * for unpackable fields or nested arrays.
 */
public class PackingContextNode
{
    /**
     * Creates a new child.
     *
     * @return The child which was just created.
     */
    public PackingContextNode createChild()
    {
        final PackingContextNode child = new PackingContextNode();
        children.add(child);
        return child;
    }

    /**
     * Gets list of children of the current node.
     *
     * @return List of children.
     */
    public List<PackingContextNode> getChildren()
    {
        return children;
    }

    /**
     * Creates a new packing context within the current node.
     */
    public void createContext()
    {
        deltaContext = new DeltaContext();
    }

    /**
     * Gets packing context assigned to this node.
     *
     * Can be called only when the context exists!
     *
     * @return Packing context.
     */
    public DeltaContext getContext()
    {
        if (deltaContext == null)
            throw new ZserioError("DeltaContext: PackingContextNode is not a leaf!");

        return deltaContext;
    }

    private final ArrayList<PackingContextNode> children = new ArrayList<PackingContextNode>();
    private DeltaContext deltaContext = null;
}
