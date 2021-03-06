package zserio.extension.python;

import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;
import zserio.ast.ServiceMethod;

/**
 * Accessor name formatter.
 *
 * Provides names of accessors (e.g. property names) as they will be generated in Python.
 */
class AccessorNameFormatter
{
    public static String getIndicatorName(Field field)
    {
        return INDICATOR_NAME_PREFIX + PythonSymbolConverter.toLowerSnakeCase(field.getName()) +
                INDICATOR_NAME_SUFFIX;
    }

    public static String getFunctionName(Function function)
    {
        return PythonSymbolConverter.toLowerSnakeCase(function.getName());
    }

    public static String getSqlColumnName(Field field)
    {
        return PythonSymbolConverter.toLowerSnakeCase(field.getName()) + "_";
    }

    public static String getPropertyName(Field field)
    {
        return PythonSymbolConverter.toLowerSnakeCase(field.getName());
    }

    public static String getPropertyName(Parameter param)
    {
        return PythonSymbolConverter.toLowerSnakeCase(param.getName());
    }

    public static String getServiceClientMethodName(ServiceMethod method)
    {
        return PythonSymbolConverter.toLowerSnakeCase(method.getName());
    }

    private static final String INDICATOR_NAME_PREFIX = "is_";
    private static final String INDICATOR_NAME_SUFFIX = "_used";
}
