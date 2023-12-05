package zserio.runtime.walker;

import zserio.runtime.typeinfo.FieldInfo;

/**
 * Walk filter which allows to walk only to the given maximum depth.
 */
public final class DepthWalkFilter implements WalkFilter
{
    /**
     * Constructor.
     *
     * @param maxDepth Maximum depth to walk to.
     */
    public DepthWalkFilter(int maxDepth)
    {
        this.maxDepth = maxDepth;
        depth = 1;
    }

    @Override
    public boolean beforeArray(Object array, FieldInfo fieldInfo)
    {
        return enterDepthLevel();
    }

    @Override
    public boolean afterArray(Object array, FieldInfo fieldInfo)
    {
        return leaveDepthLevel();
    }

    @Override
    public boolean beforeCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        return enterDepthLevel();
    }

    @Override
    public boolean afterCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        return leaveDepthLevel();
    }

    @Override
    public boolean beforeValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        return (depth <= maxDepth);
    }

    @Override
    public boolean afterValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        return true;
    }

    private boolean enterDepthLevel()
    {
        final boolean enter = (depth <= maxDepth);
        depth += 1;
        return enter;
    }

    private boolean leaveDepthLevel()
    {
        depth -= 1;
        return true;
    }

    private final int maxDepth;
    private int depth;
};
