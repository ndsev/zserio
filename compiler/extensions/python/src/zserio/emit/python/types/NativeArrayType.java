package zserio.emit.python.types;

import zserio.ast.PackageName;

public class NativeArrayType extends PythonNativeType
{
    public NativeArrayType(String traitsName)
    {
        this(traitsName, false, false);
    }

    public NativeArrayType(String traitsName, boolean requiresElementBitSize, boolean requiresElementCreator)
    {
        super(ARRAY_PACKAGE_NAME, "array");

        this.traitsName = traitsName;
        this.requiresElementBitSize = requiresElementBitSize;
        this.requiresElementCreator = requiresElementCreator;
    }

    public String getTraitsName()
    {
        return traitsName;
    }

    public boolean getRequiresElementBitSize()
    {
        return requiresElementBitSize;
    }

    public boolean getRequiresElementCreator()
    {
        return requiresElementCreator;
    }

    private static final PackageName ARRAY_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();

    private final String traitsName;
    private final boolean requiresElementBitSize;
    private final boolean requiresElementCreator;
}
