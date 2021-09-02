package zserio.extension.python;

import zserio.extension.python.types.NativeArrayTraits;

/**
 * FreeMarker template data for array traits.
 */
public class ArrayTraitsTemplateData
{
    public ArrayTraitsTemplateData(NativeArrayTraits nativeTraits)
    {
        this.nativeTraits = nativeTraits;
    }

    public String getName()
    {
        return nativeTraits.getName();
    }

    public boolean getRequiresElementBitSize()
    {
        return nativeTraits.getRequiresElementBitSize();
    }

    public boolean getRequiresElementCreator()
    {
        return nativeTraits.getRequiresElementCreator();
    }

    private final NativeArrayTraits nativeTraits;
}
