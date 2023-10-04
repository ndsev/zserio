package zserio.extension.java;

import java.util.Locale;

import zserio.ast.EnumItem;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;

/**
 * The class handles formatting of Java names for member accessors.
 */
final class AccessorNameFormatter
{
    public static String getIsUsedIndicatorName(Field field)
    {
        return getAccessorName(INDICATOR_NAME_PREFIX, field.getName(), IS_USED_INDICATOR_NAME_SUFFIX);
    }

    public static String getIsSetIndicatorName(Field field)
    {
        return getAccessorName(INDICATOR_NAME_PREFIX, field.getName(), IS_SET_INDICATOR_NAME_SUFFIX);
    }

    public static String getIsPresentIndicatorName(Field field)
    {
        return getAccessorName(INDICATOR_NAME_PREFIX, field.getName(), IS_PRESENT_INDICATOR_NAME_SUFFIX);
    }

    public static String getResetterName(Field field)
    {
        return getAccessorName(RESETTER_NAME_PREFIX, field.getName());
    }

    public static String getGetterName(Field field)
    {
        return getAccessorName(GETTER_NAME_PREFIX, field.getName());
    }

    public static String getGetterName(Parameter param)
    {
        return getAccessorName(GETTER_NAME_PREFIX, param.getName());
    }

    public static String getSetterName(Field field)
    {
        return getAccessorName(SETTER_NAME_PREFIX, field.getName());
    }

    public static String getFunctionName(Function function)
    {
        return getAccessorName(FUNCTION_NAME_PREFIX, function.getName());
    }

    public static String getEnumeratorName(EnumItem enumItem)
    {
        return enumItem.isRemoved() ? REMOVED_ENUMERATOR_PREFIX + enumItem.getName() : enumItem.getName();
    }

    private static String getAccessorName(String accessorNamePrefix, String memberName)
    {
        return getAccessorName(accessorNamePrefix, memberName, "");
    }

    private static String getAccessorName(String accessorNamePrefix, String memberName,
            String accessorNameSuffix)
    {
        StringBuilder accessorName = new StringBuilder(accessorNamePrefix);
        if (!memberName.isEmpty())
        {
            final String firstMemberNameChar = String.valueOf(memberName.charAt(0));
            final String restMemberNameChars = memberName.substring(1, memberName.length());
            accessorName.append(firstMemberNameChar.toUpperCase(Locale.ENGLISH));
            accessorName.append(restMemberNameChars);
        }
        if (!accessorNameSuffix.isEmpty())
            accessorName.append(accessorNameSuffix);

        return accessorName.toString();
    }

    private static final String INDICATOR_NAME_PREFIX = "is";
    private static final String IS_USED_INDICATOR_NAME_SUFFIX = "Used";
    private static final String IS_SET_INDICATOR_NAME_SUFFIX = "Set";
    private static final String IS_PRESENT_INDICATOR_NAME_SUFFIX = "Present";
    private static final String RESETTER_NAME_PREFIX = "reset";
    private static final String GETTER_NAME_PREFIX = "get";
    private static final String SETTER_NAME_PREFIX = "set";
    private static final String FUNCTION_NAME_PREFIX = "func";
    private static final String REMOVED_ENUMERATOR_PREFIX ="ZSERIO_REMOVED_";
}
