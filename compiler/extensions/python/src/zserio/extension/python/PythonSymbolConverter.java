package zserio.extension.python;

import java.util.Locale;
import java.util.regex.Pattern;

import zserio.ast.BitmaskValue;
import zserio.ast.EnumItem;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;
import zserio.ast.ScopeSymbol;
import zserio.ast.ServiceMethod;

/**
 * Python symbol converter.
 *
 * Provides conversion of Zserio symbol names to names used in Python generated API.
 */
public class PythonSymbolConverter
{
    public static String symbolToModule(String pythonSymbolName)
    {
        return toLowerSnakeCase(pythonSymbolName);
    }

    public static String convertScopeSymbol(ScopeSymbol scopeSymbol)
    {
        if (scopeSymbol instanceof EnumItem)
        {
            return PythonSymbolConverter.enumItemToSymbol(scopeSymbol.getName());
        }
        else if (scopeSymbol instanceof BitmaskValue)
        {
            return PythonSymbolConverter.bitmaskValueToSymbol(scopeSymbol.getName());
        }
        else if (scopeSymbol instanceof Field)
        {
            return AccessorNameFormatter.getPropertyName((Field)scopeSymbol);
        }
        else if (scopeSymbol instanceof Parameter)
        {
            return AccessorNameFormatter.getPropertyName((Parameter)scopeSymbol);
        }
        else if (scopeSymbol instanceof Function)
        {
            return AccessorNameFormatter.getFunctionName((Function)scopeSymbol);
        }
        else if (scopeSymbol instanceof ServiceMethod)
        {
            return AccessorNameFormatter.getServiceClientMethodName((ServiceMethod)scopeSymbol);
        }
        else // no special handling for other symbols
        {
            return PythonSymbolConverter.toLowerSnakeCase(scopeSymbol.getName());
        }
    }

    public static String enumItemToSymbol(String enumItemName)
    {
        return toUpperSnakeCase(enumItemName);
    }

    public static String bitmaskValueToSymbol(String bitmaskValueName)
    {
        return toUpperSnakeCase(bitmaskValueName);
    }

    public static String constantToSymbol(String constantName)
    {
        return toUpperSnakeCase(constantName);
    }

    public static String toLowerSnakeCase(String symbolName)
    {
        // check if everything is already in lower case to avoid unnecessary conversion
        if (symbolName.equals(symbolName.toLowerCase(Locale.ENGLISH)))
            return symbolName;

        return insertUnderscoresToCamelCase(symbolName).toLowerCase(Locale.ENGLISH);
    }

    private static String toUpperSnakeCase(String symbolName)
    {
        // check if everything is already in upper case to avoid unnecessary conversion
        // (and not to introduce extra underscores after numbers)
        if (symbolName.equals(symbolName.toUpperCase(Locale.ENGLISH)))
            return symbolName;

        return insertUnderscoresToCamelCase(symbolName).toUpperCase(Locale.ENGLISH);
    }

    private static String insertUnderscoresToCamelCase(String symbolName)
    {
        String underscoredSymbolName =
                CAMEL_CASE_PATTERN_1.matcher(symbolName).replaceAll(REPLACEMENT_WITH_UNDERSCORE);
        underscoredSymbolName =
                CAMEL_CASE_PATTERN_2.matcher(underscoredSymbolName).replaceAll(REPLACEMENT_WITH_UNDERSCORE);

        return underscoredSymbolName;
    }

    private static final Pattern CAMEL_CASE_PATTERN_1 = Pattern.compile("([a-z])([A-Z])");
    private static final Pattern CAMEL_CASE_PATTERN_2 = Pattern.compile("([0-9A-Z])([A-Z][a-z])");

    private static final String REPLACEMENT_WITH_UNDERSCORE = "$1_$2";
}
