package zserio.extension.python;

import java.util.Locale;
import java.util.regex.Pattern;

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
        final StringBuilder result = new StringBuilder();
        final String[] symbolNameParts = symbolName.split("_", -1); // -1 means include trailing empty strings
        int count = 0;
        for (String symbolNamePart : symbolNameParts)
        {
            // don't change hex numbers delimited by underscores
            if (HEXADECIMAL_PATTERN.matcher(symbolNamePart).matches())
            {
                result.append(symbolNamePart);
            }
            else
            {
                result.append(CAMEL_CASE_PATTERN.matcher(symbolNamePart).replaceAll(
                        REPLACEMENT_WITH_UNDERSCORE));
            }

            count++;
            if (count < symbolNameParts.length)
                result.append("_");
        }

        return result.toString();
    }

    private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z0-9])([A-Z])");
    private static final String REPLACEMENT_WITH_UNDERSCORE = "$1_$2";
}
