package zserio.runtime.walker;

import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.walker.WalkFilter;

public class TestWalkFilter implements WalkFilter
{
    @Override
    public boolean beforeArray(Object array, FieldInfo fieldInfo)
    {
        isFirstElement = true;
        return beforeArray;
    }

    @Override
    public boolean afterArray(Object array, FieldInfo fieldInfo)
    {
        isFirstElement = false;
        return afterArray;
    }

    @Override
    public boolean beforeCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        return beforeCompound;
    }

    @Override
    public boolean afterCompound(Object compound, FieldInfo fieldInfo, int elementIndex)
    {
        final boolean goToNext = !(onlyFirstElement && isFirstElement);
        isFirstElement = false;
        return goToNext && afterCompound;
    }

    @Override
    public boolean beforeValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        return beforeValue;
    }

    @Override
    public boolean afterValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        return afterValue;
    }

    public TestWalkFilter beforeArray(boolean beforeArray)
    {
        this.beforeArray = beforeArray;
        return this;
    }

    public TestWalkFilter afterArray(boolean afterArray)
    {
        this.afterArray = afterArray;
        return this;
    }

    public TestWalkFilter onlyFirstElement(boolean onlyFirstElement)
    {
        this.onlyFirstElement = onlyFirstElement;
        return this;
    }

    public TestWalkFilter beforeCompound(boolean beforeCompound)
    {
        this.beforeCompound = beforeCompound;
        return this;
    }

    public TestWalkFilter afterCompound(boolean afterCompound)
    {
        this.afterCompound = afterCompound;
        return this;
    }

    public TestWalkFilter beforeValue(boolean beforeValue)
    {
        this.beforeValue = beforeValue;
        return this;
    }

    public TestWalkFilter afterValue(boolean afterValue)
    {
        this.afterValue = afterValue;
        return this;
    }

    private boolean beforeArray = true;
    private boolean afterArray = true;
    private boolean onlyFirstElement = false;
    private boolean beforeCompound = true;
    private boolean afterCompound = true;
    private boolean beforeValue = true;
    private boolean afterValue = true;
    private boolean isFirstElement = false;
}
