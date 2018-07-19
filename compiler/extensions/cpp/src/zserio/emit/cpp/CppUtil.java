package zserio.emit.cpp;

import zserio.tools.StringJoinUtil;

final public class CppUtil
{
    /**
     * Format a static cast of value and valueSuffix to targetType.
     * @param value
     * @param valueSuffix
     * @param targetType
     * @return A string with the C++ expression of the cast.
     */
    public static String formatStaticCast(String value, String valueSuffix, String targetType)
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append("static_cast<");
        buffer.append(targetType);
        buffer.append(">(");
        buffer.append(value);
        buffer.append(valueSuffix);
        buffer.append(')');

        return buffer.toString();
    }

    /**
     * Format a name of a C++ type created by instantiating a template.
     *
     * This takes care to avoid the ">>" parsing trouble when composing templates.
     *
     * @param templateName  Name of the template.
     * @param args          Arguments to pass.
     * @return              String representing the instantiated template.
     */
    public static String formatTemplateInstantiation(String templateName, String... args)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(templateName);
        builder.append('<');

        boolean first = true;
        for (String arg: args)
        {
            if (!first)
                builder.append(", ");
            first = false;
            builder.append(arg);
        }

        // avoid the ">>" problem
        if (builder.charAt(builder.length() - 1) == '>')
            builder.append(' ');

        builder.append('>');
        return builder.toString();
    }

    public static String formatConstRef(String typeName)
    {
        return "const " + typeName + '&';
    }

    /**
     * Make a full name of a method.
     *
     * This is useful for static methods.
     *
     * @param typeName   Full name of the type.
     * @param methodName Name of the method.
     * @return Full name of the method.
     */
    public static String makeFullMethodName(String typeName, String methodName)
    {
        return typeName + NAMESPACE_SEPARATOR + methodName;
    }

    public static String makeNamespaceFromPath(Iterable<String> namespacePath)
    {
        return StringJoinUtil.joinStrings(namespacePath, NAMESPACE_SEPARATOR);
    }

    private static final String NAMESPACE_SEPARATOR = "::";
}
