package zserio.runtime.walker;

import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.TypeInfo;

/**
 * Default walk filter which filters nothing.
 */
public class DefaultWalkFilter implements WalkFilter
{
    @Override
    public boolean beforeArray(Object array, FieldInfo fieldInfo)
    {
        return true;
    }

    @Override
    public boolean afterArray(Object array, FieldInfo fieldInfo)
    {
        return true;
    }

    @Override
    public boolean beforeCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
        return true;
    }

    @Override
    public boolean afterCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
        return true;
    }

    @Override
    public boolean beforeValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        return true;
    }

    @Override
    public boolean afterValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        return true;
    }
};
