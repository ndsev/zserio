package zserio.runtime.walker;

import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.TypeInfo;

/**
 * Walk filter which allows to walk only to the given maximum depth.
 */
public class DepthWalkFilter implements WalkFilter
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
    public boolean beforeCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
        return enterDepthLevel();
    }

    @Override
    public boolean afterCompound(Object compound, TypeInfo typeInfo, int elementIndex)
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

    // TODO[mikir] Change it in C++ and Python as well?
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
