package zserio.extension.cpp.types;

import zserio.ast.PackageName;
import zserio.extension.cpp.CppFullNameFormatter;
import zserio.extension.cpp.TypesContext;

public class NativeArrayType extends CppNativeType
{
    public NativeArrayType(CppNativeType elementType, String arrayTraitsName, boolean hasTemplatedTraits,
            TypesContext typesContext, NativeVectorType nativeVectorType)
    {
        super(nativeVectorType.getPackageName(), nativeVectorType.getName() + "<" +
                elementType.getFullName() +
                (nativeVectorType.needsAllocatorArgument() ?
                        ", " + typesContext.getAllocatorDefinition().getAllocatorType() +
                                "<" + elementType.getFullName() + ">" : "") + ">");
        this.arrayTraitsName = CppFullNameFormatter.getFullName(ZSERIO_PACKAGE_NAME, arrayTraitsName);
        this.hasTemplatedTraits = hasTemplatedTraits;

        addIncludeFiles(nativeVectorType);
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

    private static final PackageName ZSERIO_PACKAGE_NAME = new PackageName.Builder().addId("zserio").get();
    private static final String ARRAY_TRAITS_INCLUDE = "zserio/Arrays.h";

    private final String arrayTraitsName;
    private final boolean hasTemplatedTraits;
}
