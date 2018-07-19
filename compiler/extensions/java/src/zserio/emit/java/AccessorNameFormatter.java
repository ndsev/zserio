package zserio.emit.java;

import java.util.Locale;

import zserio.ast.Field;
import zserio.ast.Parameter;

/**
 * The class handles formatting of Java names for member accessors.
 */
final class AccessorNameFormatter
{
    /**
     * Returns the name of indicator accessor.
     *
     * @param field The zserio field for which to get indicator accessor.
     */
    public static String getIndicatorName(Field field)
    {
        return getAccessorName(INDICATOR_NAME_PREFIX, field.getName());
    }

    /**
     * Returns the name of getter accessor.
     *
     * @param field The zserio field for which to get getter accessor.
     */
    public static String getGetterName(Field field)
    {
        return getAccessorName(GETTER_NAME_PREFIX, field.getName());
    }

    /**
     * Returns the name of getter accessor.
     *
     * @param param The zserio paramater for which to get getter accessor.
     */
    public static String getGetterName(Parameter param)
    {
        return getAccessorName(GETTER_NAME_PREFIX, param.getName());
    }

    /**
     * Returns the name of setter accessor.
     *
     * @param field The zserio field for which to get setter accessor.
     */
    public static String getSetterName(Field field)
    {
        return getAccessorName(SETTER_NAME_PREFIX, field.getName());
    }

    /**
     * Returns the name of setter accessor.
     *
     * @param param The zserio paramater for which to get setter accessor.
     */
    public static String getSetterName(Parameter param)
    {
        return getAccessorName(SETTER_NAME_PREFIX, param.getName());
    }

    private static String getAccessorName(String accessorNamePrefix, String memberName)
    {
        StringBuilder accessorName = new StringBuilder(accessorNamePrefix);
        if (!memberName.isEmpty())
        {
            final String firstMemberNameChar = String.valueOf(memberName.charAt(0));
            final String restMemberNameChars = memberName.substring(1, memberName.length());
            accessorName.append(firstMemberNameChar.toUpperCase(Locale.ENGLISH));
            accessorName.append(restMemberNameChars);
        }

        return accessorName.toString();
    }

    private static final String INDICATOR_NAME_PREFIX = "has";
    private static final String GETTER_NAME_PREFIX = "get";
    private static final String SETTER_NAME_PREFIX = "set";
}
