package zserio.extension.java;

import zserio.extension.java.types.NativeArrayableType;

/**
 * FreeMarker template data which keeps information about types which can be used in arrays.
 */
public final class ArrayableInfoTemplateData
{
    public ArrayableInfoTemplateData(NativeArrayableType arrayableType)
    {
        arrayTraits = new ArrayTraitsTemplateData(arrayableType.getArrayTraits());
        arrayElement = arrayableType.getArrayElement().getFullName();
    }

    public ArrayTraitsTemplateData getArrayTraits()
    {
        return arrayTraits;
    }

    public String getArrayElement()
    {
        return arrayElement;
    }

    private final ArrayTraitsTemplateData arrayTraits;
    private final String arrayElement;
}