package zserio.extension.python;

import java.util.Locale;
import java.util.regex.Pattern;

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
        return CAMEL_CASE_PATTERN.matcher(symbolName).replaceAll(REPLACEMENT_WITH_UNDERSCORE);
    }

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z])([A-Z])");
    private static final String REPLACEMENT_WITH_UNDERSCORE = "$1_$2";
}
