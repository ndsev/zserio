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

        return getFullName(type.getPackage().getPackageName(), type.getName());
    }

    /**
     * Returns the full zserio name.
     *
     * @param packageName Package name.
     * @param name        Type or symbol name.
     *
     * @return Full zserio name.
     */
    public static String getFullName(PackageName packageName, String name)
    {
        return StringJoinUtil.joinStrings(packageName.toString(), name, FULL_NAME_SEPARATOR);
    }

    /**
     * Returns the full zserio name as is referenced in the type reference.
     *
     * @param typeReference Type reference.
     *
     * @return Full zserio name.
     */
    static String getReferencedFullName(TypeReference typeReference)
    {
        return getFullName(typeReference.getReferencedPackageName(), typeReference.getReferencedTypeName());
    }

    private static final String FULL_NAME_SEPARATOR = ".";
}
