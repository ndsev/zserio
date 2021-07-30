package zserio.extension.java.types;

import zserio.ast.PackageName;

public class NativeArrayType extends JavaNativeType
{
    public NativeArrayType(NativeArrayableType elementType)
    {
        super(getPackageName(elementType), getRawArrayName(elementType));

        arrayWrapper = new NativeArrayWrapper();
        rawArrayHolder = new NativeRawArrayHolder(elementType);
        arrayTraits = elementType.getArrayTraits();
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    public NativeArrayWrapper getArrayWrapper()
    {
        return arrayWrapper;
    }

    public NativeRawArrayHolder getRawArrayHolder()
    {
        return rawArrayHolder;
    }

    public NativeArrayTraits getArrayTraits()
    {
        return arrayTraits;
    }

    private static PackageName getPackageName(JavaNativeType elementType)
    {
        return elementType.isSimple() ? PackageName.EMPTY : JAVA_UTIL_PACKAGE;
    }

    private static String getRawArrayName(JavaNativeType elementType)
    {
        return elementType.isSimple() ? elementType.getName() + "[]" : "List<" + elementType.getName() + ">";
    }

    private static final PackageName JAVA_UTIL_PACKAGE =
            new PackageName.Builder().addId("java").addId("util").get();

    private final NativeArrayWrapper arrayWrapper;
    private final NativeRawArrayHolder rawArrayHolder;
    private final NativeArrayTraits arrayTraits;
}
