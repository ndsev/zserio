package zserio.extension.python;

import java.util.Locale;
import java.util.regex.Pattern;

import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;

public class AccessorNameFormatter
{
    public static String getIndicatorName(Field field)
    {
        return getAccessorName(INDICATOR_NAME_PREFIX, field.getName(), INDICATOR_NAME_SUFFIX);
    }

    public static String getFunctionName(Function function)
    {
        return getAccessorName(FUNCTION_NAME_PREFIX, function.getName());
    }

    public static String getSqlColumnName(Field field)
    {
        return field.getName() + "_";
    }

    public static String getPropertyName(Field field)
    {
        return camelCaseToSnakeCase(field.getName());
    }

    public static String getPropertyName(Parameter param)
    {
        return camelCaseToSnakeCase(param.getName());
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

    private static String camelCaseToSnakeCase(String camelCase)
    {
        return CAMEL_CASE_PATTERN.matcher(camelCase)
                .replaceAll(REPLACEMENT_WITH_UNDERSCORE)
                .toLowerCase(Locale.ENGLISH);
    }

    private static final String INDICATOR_NAME_PREFIX = "is";
    private static final String INDICATOR_NAME_SUFFIX = "Used";
    private static final String FUNCTION_NAME_PREFIX = "func";

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z0-9])([A-Z])");
    private static final String REPLACEMENT_WITH_UNDERSCORE = "$1_$2";
}
