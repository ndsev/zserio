package zserio.runtime.walker;

import zserio.runtime.typeinfo.FieldInfo;

/**
 * Interface for filters which can influence the walking.
 */
public interface WalkFilter
{
    /**
     * Called before an array.
     *
     * @param array Zserio array.
     * @param fieldInfo Array field info.
     *
     * @return True when the walking should continue to the array.
     */
    public boolean beforeArray(Object array, FieldInfo fieldInfo);

    /**
     * Called after an array.
     *
     * @param array Zserio array.
     * @param fieldInfo Array field info.
     *
     * @return True when the walking should continue to a next sibling, false to return to the parent.
     */
    public boolean afterArray(Object array, FieldInfo fieldInfo);

    /**
     * Called before a compound object.
     *
     * Note that for uninitialized compounds (i.e. null) the before_value method is called instead!
     *
     * @param compound Compound zserio object.
     * @param fieldInfo Compound field info.
     * @param elementIndex Element index in array or NOT_ELEMENT if the compound is not in array.
     *
     * @return True when the walking should continue into the compound object, false otherwise.
     */
    public boolean beforeCompound(Object compound, FieldInfo fieldInfo, int elementIndex);

    /**
     * Called after a compound object.
     *
     * @param compound Compound zserio object.
     * @param fieldInfo Compound field info.
     * @param elementIndex Element index in array or NOT_ELEMENT if the compound is not in array.
     *
     * @return True when the walking should continue to a next sibling, false to return to the parent.
     */
    public boolean afterCompound(Object compound, FieldInfo fieldInfo, int elementIndex);

    /**
     * Called before a simple (or an unset compound - i.e. null) value.
     *
     * @param value Simple value.
     * @param fieldInfo Field info of the value.
     * @param elementIndex Element index in array or NOT_ELEMENT if the compound is not in array.
     *
     * @return True when the walking should continue to the simple value, false otherwise.
     */
    public boolean beforeValue(Object value, FieldInfo fieldInfo, int elementIndex);

    /**
     * Called after a simple value.
     *
     * @param value Simple value.
     * @param fieldInfo Field info of the value.
     * @param elementIndex Element index in array or NOT_ELEMENT if the compound is not in array.
     *
     * @return True when the walking should continue to a next sibling, false to return to the parent.
     */
    public boolean afterValue(Object value, FieldInfo fieldInfo, int elementIndex);
}
