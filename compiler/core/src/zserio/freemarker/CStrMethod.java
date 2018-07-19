package zserio.freemarker;

import java.util.List;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * Freemarker function that escapes strings so that they can be put into C string literals.
 *
 * This works similarly to Freemarker's built-in string_value?j_str but as it's not a built-in, the usage is:
 * {@code c_str(string_value)}.
 */
public class CStrMethod implements TemplateMethodModelEx
{
    @SuppressWarnings("unchecked")
    @Override
    public Object exec(@SuppressWarnings("rawtypes") List args) throws TemplateModelException
    {
        return execImpl((List<TemplateModel>)args);
    }

    private Object execImpl(List<TemplateModel> args) throws TemplateModelException
    {
        if (args.size() != 1)
        {
            throw new TemplateModelException(NAME + " requires exactly one argument");
        }

        TemplateModel model = args.get(0);
        if (!(model instanceof TemplateScalarModel))
        {
            throw new TemplateModelException(NAME + " requires one string argument");
        }

        final TemplateScalarModel stringModel = (TemplateScalarModel)model;
        final String value = stringModel.getAsString();
        if (value != null && value.length() > 0)
        {
            return new SimpleScalar(process(value));
        }

        // empty package name -> return an empty string
        return SimpleScalar.EMPTY_STRING;
    }

    /**
     * Produce C-escaped version of given string.
     */
    private static String process(String value)
    {
        final StringBuilder builder = new StringBuilder();

        for (int index = 0; index < value.length(); index++)
            builder.append(processChar(value.charAt(index)));

        return builder.toString();
    }

    /**
     * Handle a single character in the literal.
     *
     * Returns either the character itself or its escape sequence, if the character needs to be escaped.
     *
     * For escaping arbitrary characters octal escapes are used to avoid the issue with an \x escape merging
     * with whatever text follows the escape.
     * @param c Input character.
     * @return Possibly escaped value.
     */
    private static String processChar(char c)
    {
        switch (c)
        {
        case '\0':
            return "\\0";
        case '\u0007':
            return "\\a";
        case '\u0008':
            return "\\b";
        case '\n':
            return "\\n";
        case '\u000b':
            return "\\v";
        case '\f':
            return "\\f";
        case '\r':
            return "\\r";
        case '\t':
            return "\\t";
        case '"':
            return "\\\"";
        default:
            if (c < ' ')
                // use octal escape because it avoids the ambiguity of hex escapes ("\xaaBoom")
                return String.format("\\%03o", (int)c);
            else
                return String.valueOf(c);
        }
    }

    public static final String NAME = "c_str";
}
