package zserio.runtime.walker;

import zserio.runtime.typeinfo.FieldInfo;

/**
 * Walk filter which allows to walk only to the given maximum array length.
 */
public final class ArrayLengthWalkFilter implements WalkFilter
{
    /**
     * Constructor.
     *
     * @param maxArrayLength Maximum array length to walk to.
     */
    public ArrayLengthWalkFilter(int maxArrayLength)
    {
        this.maxArrayLength = maxArrayLength;
    }

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
    public boolean beforeCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        return filterArrayElement(elementIndex);
    }

    @Override
    public boolean afterCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        return filterArrayElement(elementIndex);
    }

    @Override
    public boolean beforeValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        return filterArrayElement(elementIndex);
    }

    @Override
    public boolean afterValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        return filterArrayElement(elementIndex);
    }

    private boolean filterArrayElement(int elementIndex)
    {
        return (elementIndex == WalkerConst.NOT_ELEMENT) ? true : elementIndex < maxArrayLength;
    }

    private final int maxArrayLength;
};
