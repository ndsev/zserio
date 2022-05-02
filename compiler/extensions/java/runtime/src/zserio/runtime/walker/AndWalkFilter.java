package zserio.runtime.walker;

import java.util.List;

import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.TypeInfo;

/**
 * Walk filter which implements composition of particular filters.
 *
 * The filters are called sequentially and logical and is applied on theirs results.
 * Note that all filters are always called.
 */
public class AndWalkFilter implements WalkFilter
{
    /**
     * Constructor.
     *
     * @param walkFilters List of filters to use in composition.
     */
    public AndWalkFilter(List<WalkFilter> walkFilters)
    {
        this.walkFilters = walkFilters;
    }

    @Override
    public boolean beforeArray(Object array, FieldInfo fieldInfo)
    {
        boolean result = true;
        for (WalkFilter walkFilter : walkFilters)
            result &= walkFilter.beforeArray(array, fieldInfo);

        return result;
    }

    @Override
    public boolean afterArray(Object array, FieldInfo fieldInfo)
    {
        boolean result = true;
        for (WalkFilter walkFilter : walkFilters)
            result &= walkFilter.afterArray(array, fieldInfo);

        return result;
    }

    @Override
    public boolean beforeCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
        boolean result = true;
        for (WalkFilter walkFilter : walkFilters)
            result &= walkFilter.beforeCompound(compound, typeInfo, elementIndex);

        return result;
    }

    @Override
    public boolean afterCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
        boolean result = true;
        for (WalkFilter walkFilter : walkFilters)
            result &= walkFilter.afterCompound(compound, typeInfo, elementIndex);

        return result;
    }

    @Override
    public boolean beforeValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        boolean result = true;
        for (WalkFilter walkFilter : walkFilters)
            result &= walkFilter.beforeValue(value, fieldInfo, elementIndex);

        return result;
    }

    @Override
    public boolean afterValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        boolean result = true;
        for (WalkFilter walkFilter : walkFilters)
            result &= walkFilter.afterValue(value, fieldInfo, elementIndex);

        return result;
    }

    private final List<WalkFilter> walkFilters;
};
