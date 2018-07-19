package zserio.emit.java.types;

import java.util.Arrays;

import zserio.tools.StringJoinUtil;

public class NativeObjectArrayType extends NativeArrayType
{
    public NativeObjectArrayType(JavaNativeType elementType)
    {
        super(formatGenericType(OBJECT_ARRAY_CLASS, elementType.getFullName()), elementType);
    }

    private static String formatGenericType(String typeName, String... argumentTypes)
    {
        final String arguments = StringJoinUtil.joinStrings(Arrays.asList(argumentTypes), ", ");
        return typeName + "<" + arguments + ">";
    }

    @Override
    public boolean requiresElementFactory()
    {
        return true;
    }

    private final static String OBJECT_ARRAY_CLASS = "ObjectArray";
}
