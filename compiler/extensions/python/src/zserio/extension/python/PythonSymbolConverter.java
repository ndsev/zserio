package zserio.extension.python;

import java.util.Locale;
import java.util.regex.Pattern;

public class PythonSymbolConverter
{
    public static String packageSymbolToModuleName(String packageSymbolName)
    {
        return camelCaseToSnakeCase(packageSymbolName);
    }

    public static String camelCaseToSnakeCase(String camelCase)
    {
        return CAMEL_CASE_PATTERN.matcher(camelCase)
                .replaceAll(REPLACEMENT_WITH_UNDERSCORE)
                .toLowerCase(Locale.ENGLISH);
    }

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z0-9])([A-Z])");
    private static final String REPLACEMENT_WITH_UNDERSCORE = "$1_$2";
}
