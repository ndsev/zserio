package zserio.extension.python;

import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;

public class AccessorNameFormatter
{
    public static String getIndicatorName(Field field)
    {
        return INDICATOR_NAME_PREFIX + PythonSymbolConverter.camelCaseToSnakeCase(field.getName()) +
                INDICATOR_NAME_SUFFIX;
    }

    public static String getFunctionName(Function function)
    {
        return FUNCTION_NAME_PREFIX + PythonSymbolConverter.camelCaseToSnakeCase(function.getName());
    }

    public static String getSqlColumnName(Field field)
    {
        return PythonSymbolConverter.camelCaseToSnakeCase(field.getName()) + "_";
    }

    public static String getPropertyName(Field field)
    {
        return PythonSymbolConverter.camelCaseToSnakeCase(field.getName());
    }

    public static String getPropertyName(Parameter param)
    {
        return PythonSymbolConverter.camelCaseToSnakeCase(param.getName());
    }

    private static final String INDICATOR_NAME_PREFIX = "is_";
    private static final String INDICATOR_NAME_SUFFIX = "_used";
    private static final String FUNCTION_NAME_PREFIX = "func_";
}
