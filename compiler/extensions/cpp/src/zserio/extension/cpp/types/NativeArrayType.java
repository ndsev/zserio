package zserio.extension.cpp.types;

import zserio.ast.PackageName;
import zserio.extension.cpp.CppFullNameFormatter;

public class NativeArrayType extends CppNativeType
{
    public NativeArrayType(CppNativeType elementType, String arrayTraitsName, boolean hasTemplatedTraits)
    {
        super(STD_PACKAGE_NAME, "vector<" + elementType.getFullName() + ">");
        this.arrayTraitsName = CppFullNameFormatter.getFullName(ZSERIO_PACKAGE_NAME, arrayTraitsName);
        this.hasTemplatedTraits = hasTemplatedTraits;
        addSystemIncludeFile("vector");
        addSystemIncludeFile(ARRAY_TRAITS_INCLUDE);
        addIncludeFiles(elementType);
    }

    public boolean hasTemplatedTraits()
    {
        return hasTemplatedTraits;
    }

    public boolean requiresElementBitSize()
    {
        return false;
    }

    public boolean requiresElementFactory()
    {
        return false;
    }

    public String getArrayTraitsName()
    {
        return arrayTraitsName;
    }

    private static final PackageName STD_PACKAGE_NAME = new PackageName.Builder().addId("std").get();
    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
    private static final String ARRAY_TRAITS_INCLUDE = "zserio/Arrays.h";

    private final String arrayTraitsName;
    private final boolean hasTemplatedTraits;
}
