package zserio.runtime.walker;

import zserio.runtime.typeinfo.FieldInfo;

/**
 * Interface for observers which are called by the walker.
 */
public interface WalkObserver
{
    /**
     * Called for the root compound zserio object which is to be walked-through.
     *
     * @param compound Root compound zserio object.
     */
    public void beginRoot(Object compound);

    /**
     * Called at the end of just walked root compound zserio object.
     *
     * @param compound Root compound zserio object.
     */
    public void endRoot(Object compound);

    /**
     * Called at the beginning of an array.
     *
     * @param array Zserio array.
     * @param fieldInfo Array field info.
     */
    public void beginArray(Object array, FieldInfo fieldInfo);

    /**
     * Called at the end of an array.
     *
     * @param array Zserio array.
     * @param fieldInfo Array field info.
     */
    public void endArray(Object array, FieldInfo fieldInfo);

    /**
     * Called at the beginning of an compound field object.
     *
     * Note that for uninitialized compounds (i.e. null) the visit_value method is called instead!
     *
     * @param compound Compound zserio object.
     * @param fieldInfo Compound field info.
     * @param elementIndex Element index in array or NOT_ELEMENT if the compound is not in array.
     */
    public void beginCompound(Object compound, FieldInfo fieldInfo, int elementIndex);

    /**
     * Called at the end of just walked compound object.
     *
     * @param compound Compound zserio object.
     * @param fieldInfo Compound field info.
     * @param elementIndex Element index in array or NOT_ELEMENT if the compound is not in array.
     */
    public void endCompound(Object compound, FieldInfo fieldInfo, int elementIndex);

    /**
     * Called when a simple (or an unset compound - i.e. None) value is reached.
     *
     * @param value Simple value.
     * @param fieldInfo Field info of the simple value.
     * @param elementIndex Element index in array or NOT_ELEMENT if the compound is not in array.
     */
    public void visitValue(Object value, FieldInfo fieldInfo, int elementIndex);
}
