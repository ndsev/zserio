package zserio.emit.python;

import java.util.Locale;

import zserio.ast.Field;
import zserio.ast.FunctionType;
import zserio.ast.Parameter;

public class AccessorNameFormatter
{
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

    public static String getIndicatorName(Field field)
    {
        return getAccessorName(INDICATOR_NAME_PREFIX, field.getName());
    }

    public static String getFunctionName(FunctionType functionType)
    {
        return getAccessorName(FUNCTION_NAME_PREFIX, functionType.getName());
    }

    public static String getSqlColumnName(Field field)
    {
        return field.getName() + "_";
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

    private static final String GETTER_NAME_PREFIX = "get";
    private static final String SETTER_NAME_PREFIX = "set";
    private static final String INDICATOR_NAME_PREFIX = "has";
    private static final String FUNCTION_NAME_PREFIX = "func";
}
