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
        return INDICATOR_NAME_PREFIX + camelCaseToSnakeCase(field.getName()) + INDICATOR_NAME_SUFFIX;
    }

    public static String getFunctionName(Function function)
    {
        return FUNCTION_NAME_PREFIX + camelCaseToSnakeCase(function.getName());
    }

    public static String getSqlColumnName(Field field)
    {
        return camelCaseToSnakeCase(field.getName()) + "_";
    }

    public static String getPropertyName(Field field)
    {
        return camelCaseToSnakeCase(field.getName());
    }

    public static String getPropertyName(Parameter param)
    {
        return camelCaseToSnakeCase(param.getName());
    }

    public static String camelCaseToSnakeCase(String camelCase)
    {
        return CAMEL_CASE_PATTERN.matcher(camelCase)
                .replaceAll(REPLACEMENT_WITH_UNDERSCORE)
                .toLowerCase(Locale.ENGLISH);
    }

    private static final String INDICATOR_NAME_PREFIX = "is_";
    private static final String INDICATOR_NAME_SUFFIX = "_used";
    private static final String FUNCTION_NAME_PREFIX = "func_";

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z0-9])([A-Z])");
    private static final String REPLACEMENT_WITH_UNDERSCORE = "$1_$2";
}
