package zserio.ast;

import zserio.tools.PackageManager;
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
        if (ZserioTypeUtil.isBuiltIn(type))
            return type.getName();

        if (type.getPackage() == null)
            return type.getName();

        return StringJoinUtil.joinStrings(
                type.getPackage().getPackageName(), type.getName(), Package.SEPARATOR);
    }

    /**
     * Checks if given type is build-in type (int8, int16, int<4>, etc...).
     *
     * @param type Zserio type to check.
     *
     * @return true if given type is build-in type, otherwise false.
     */
    public static boolean isBuiltIn(ZserioType type)
    {
        return type.getPackage() == PackageManager.get().builtInPackage;
    }
}
