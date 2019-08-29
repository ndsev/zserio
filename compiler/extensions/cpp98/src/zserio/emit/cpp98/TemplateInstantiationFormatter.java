package zserio.emit.cpp98;

public final class TemplateInstantiationFormatter
{
    /**
    * Formats a name of a C++ type created by instantiating a template.
    *
    * This takes care to avoid the ">>" parsing trouble when composing templates.
    *
    * @param templateName  Name of the template.
    * @param args          Arguments to pass.
    *
    * @return              String representing the instantiated template.
    */
    public static String format(String templateName, String... args)
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
}