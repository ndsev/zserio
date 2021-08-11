package zserio.extension.cpp.types;

import zserio.ast.PackageName;

public class NativeArrayType extends CppNativeType
{
    public NativeArrayType(NativeArrayableType elementType, NativeVectorType nativeVectorType)
    {
        super(ZSERIO_PACKAGE_NAME, "Array");

        this.arrayTraits = elementType.getArrayTraits();

        addIncludeFiles(nativeVectorType);
        addSystemIncludeFile(ARRAY_INCLUDE);
        addIncludeFiles(elementType);
    }

    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
    private static final String ARRAY_INCLUDE = "zserio/Array.h";

    private final NativeArrayTraits arrayTraits;
}
