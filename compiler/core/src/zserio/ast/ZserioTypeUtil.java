package zserio.ast;

import zserio.tools.StringJoinUtil;

/**
 * This class implements various utilities on ZserioType common for multiple emitters.
 */
public class ZserioTypeUtil
{
    /**
     * Returns the full zserio name with the package name as defined by Zserio source file.
     *
     * @param type ZserioType to get the full name.
     *
     * @return Full zserio name.
     */
    public static String getFullName(ZserioType type)
    {
        if (type instanceof BuiltInType)
            return type.getName();

        return StringJoinUtil.joinStrings(type.getPackage().getPackageName().toString(), type.getName(),
                FULL_NAME_SEPARATOR);
    }

    /**
     * Return the full zserio name as is referenced in the type reference.
     *
     * @param typeReference Type reference.
     *
     * @return Full zserio name.
     */
    static String getReferencedFullName(TypeReference typeReference)
    {
        return StringJoinUtil.joinStrings(typeReference.getReferencedPackageName().toString(),
                typeReference.getReferencedTypeName(), FULL_NAME_SEPARATOR);
    }

    private static final String FULL_NAME_SEPARATOR = ".";
}
