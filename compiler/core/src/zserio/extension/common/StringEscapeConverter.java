package zserio.extension.common;

/**
 * Converts escape sequences in strings.
 *
 * This is currently used for C++ extension (converts problematic unicode escape sequences to hexadecimal) and
 * for Java extension (converts not supported hexadecimal sequences to unicode).
 */
public class StringEscapeConverter
{
    /**
     * Converts unicode escape sequences to hexadecimal in given string.
     *
     * Only unicode escape sequences from interval &lt;'\u0000', '\u00FF'&gt; are converted.
     *
     * @param stringToConvert String for escape sequences conversion.
     *
     * @return String with converted escape sequences.
     */
    public static String convertUnicodeToHexEscapes(String stringToConvert)
    {
        final StringBuilder buffer = new StringBuilder();
        final int endIndex = stringToConvert.length();
        int index = 0;
        while (index < endIndex)
        {
            final char character = stringToConvert.charAt(index);
            int newIndex = index + 1;
            buffer.append(character);
            if (character == STRING_ESCAPE_CHARACTER && newIndex + ESCAPE_UNICODE_LENGTH < endIndex)
            {
                final char escapeSpecifier = stringToConvert.charAt(newIndex);
                if (escapeSpecifier == STRING_ESCAPE_CHARACTER)
                {
                    buffer.append(STRING_ESCAPE_CHARACTER);
                    newIndex++;
                }
                else if (escapeSpecifier == ESCAPE_UNICODE_SPECIFIER)
                {
                    final char firstUnicodeChar = stringToConvert.charAt(newIndex + 1);
                    final char secondUnicodeChar = stringToConvert.charAt(newIndex + 2);
                    if (firstUnicodeChar == '0' && secondUnicodeChar == '0')
                    {
                        final char thirdUnicodeChar = stringToConvert.charAt(newIndex + 3);
                        final char fourthUnicodeChar = stringToConvert.charAt(newIndex + 4);
                        buffer.append(ESCAPE_HEXADECIMAL_SPECIFIER);
                        buffer.append(thirdUnicodeChar);
                        buffer.append(fourthUnicodeChar);
                        newIndex += ESCAPE_UNICODE_LENGTH;
                    }
                }
            }

            index = newIndex;
        }

        return buffer.toString();
    }

    /**
     * Converts hexadecimal escape sequences to unicode in given string.
     *
     * @param stringToConvert String for escape sequences conversion.
     *
     * @return String with converted escape sequences.
     */
    public static String convertHexToUnicodeToEscapes(String stringToConvert)
    {
        final StringBuilder buffer = new StringBuilder();
        final int endIndex = stringToConvert.length();
        int index = 0;
        while (index < endIndex)
        {
            final char character = stringToConvert.charAt(index);
            int newIndex = index + 1;
            buffer.append(character);
            if (character == STRING_ESCAPE_CHARACTER && newIndex + ESCAPE_HEXADECIMAL_LENGTH < endIndex)
            {
                final char escapeSpecifier = stringToConvert.charAt(newIndex);
                if (escapeSpecifier == STRING_ESCAPE_CHARACTER)
                {
                    buffer.append(STRING_ESCAPE_CHARACTER);
                    newIndex++;
                }
                else if (escapeSpecifier == ESCAPE_HEXADECIMAL_SPECIFIER)
                {
                    final char firstHexChar = stringToConvert.charAt(newIndex + 1);
                    final char secondHexChar = stringToConvert.charAt(newIndex + 2);
                    buffer.append(ESCAPE_UNICODE_SPECIFIER);
                    buffer.append("00");
                    buffer.append(firstHexChar);
                    buffer.append(secondHexChar);
                    newIndex += ESCAPE_HEXADECIMAL_LENGTH;
                }
            }

            index = newIndex;
        }

        return buffer.toString();
    }

    private static final char STRING_ESCAPE_CHARACTER = '\\';
    private static final char ESCAPE_HEXADECIMAL_SPECIFIER = 'x';
    private static final char ESCAPE_UNICODE_SPECIFIER = 'u';

    private static final int ESCAPE_HEXADECIMAL_LENGTH = 3;
    private static final int ESCAPE_UNICODE_LENGTH = 5;
}
