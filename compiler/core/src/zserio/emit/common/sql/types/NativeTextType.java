package zserio.emit.common.sql.types;

/**
 * SQLite native type for Texts.
 */
public class NativeTextType implements SqlNativeType
{
    @Override
    public String getFullName()
    {
        return NAME;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getTraditionalName()
    {
        return TRADITIONAL_NAME;
    }

    /**
     * Formats string value to format for SQLite.
     *
     * @param value String value to format.
     *
     * @return String value formatted for SQLite.
     */
    public static String formatLiteral(String value)
    {
        final StringBuilder textLiteral = new StringBuilder();

        for (int i = 0; i < value.length(); i++)
            formatChar(textLiteral, value.charAt(i));

        return textLiteral.toString();
    }

    private static void formatChar(StringBuilder textLiteral, char c)
    {
        if (c == STRING_LITERAL_ZSERIO_QUOTE)
        {
            textLiteral.append(STRING_LITERAL_SQL_QUOTE);
        }
        else if (c == STRING_LITERAL_SQL_QUOTE)
        {
            textLiteral.append(STRING_LITERAL_SQL_QUOTE);
            textLiteral.append(STRING_LITERAL_SQL_QUOTE);
        }
        else if (c < ' ')
        {
            // produce: ...' || x'AB' || '...
            textLiteral.append(STRING_LITERAL_SQL_QUOTE);
            textLiteral.append(" || x'");
            textLiteral.append(String.format("%02X", (int)c));
            textLiteral.append("' || ");
            textLiteral.append(STRING_LITERAL_SQL_QUOTE);
        }
        else
        {
            textLiteral.append(c);
        }
    }

    private final static String NAME = "TEXT";
    private final static char STRING_LITERAL_ZSERIO_QUOTE = '\"';
    private final static char STRING_LITERAL_SQL_QUOTE = '\'';
    private final static String TRADITIONAL_NAME = "VARCHAR";
}
