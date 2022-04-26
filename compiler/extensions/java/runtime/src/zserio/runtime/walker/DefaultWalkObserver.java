package zserio.runtime.walker;

import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.TypeInfo;

/**
 * Default walk observer which just does nothing.
 */
public class DefaultWalkObserver implements WalkObserver
{
    @Override
    public void beginRoot(Object compound)
    {
    }

    @Override
    public void endRoot(Object compound)
    {
    }

    @Override
    public void beginArray(Object array, FieldInfo fieldInfo)
    {
    }

    @Override
    public void endArray(Object array, FieldInfo fieldInfo)
    {
    }

    @Override
    public void beginCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
    }

    @Override
    public void endCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
    }

    @Override
    public void visitValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
    }
};
