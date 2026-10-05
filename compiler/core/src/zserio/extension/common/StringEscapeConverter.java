package zserio.extension.common;

/**
 * Converts escape sequences in strings.
 *
 * This is currently used for C++ extension (converts problematic unicode escape sequences to hexadecimal),
 * for Java extension (converts not supported hexadecimal sequences to unicode) and for C++, Java and Python
 * extensions (converts Zserio octal escape sequences to the octal escape sequences of these languages).
 */
public final class StringEscapeConverter
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

    /**
     * Converts Zserio octal escape sequences to three-digit octal escape sequences in given string.
     *
     * Zserio octal escape sequences have the format '\0[0-3]oo' where 'o' is an octal digit and both
     * '[0-3]' and the last 'o' are optional. C++, Java and Python use the format '\ooo' which takes at most
     * three octal digits, e.g. Zserio '\0156' is converted to '\156'.
     *
     * @param stringToConvert String for escape sequences conversion.
     *
     * @return String with converted escape sequences.
     */
    public static String convertOctalEscapes(String stringToConvert)
    {
        final StringBuilder buffer = new StringBuilder();
        final int endIndex = stringToConvert.length();
        int index = 0;
        while (index < endIndex)
        {
            final char character = stringToConvert.charAt(index);
            int newIndex = index + 1;
            buffer.append(character);
            if (character == STRING_ESCAPE_CHARACTER && newIndex < endIndex)
            {
                final char escapeSpecifier = stringToConvert.charAt(newIndex);
                if (escapeSpecifier == STRING_ESCAPE_CHARACTER)
                {
                    buffer.append(STRING_ESCAPE_CHARACTER);
                    newIndex++;
                }
                else if (escapeSpecifier == ESCAPE_OCTAL_SPECIFIER &&
                        isOctalDigit(stringToConvert, newIndex + 1))
                {
                    final int digitsStartIndex = newIndex + 1;
                    final int numDigits = getNumOctalEscapeDigits(stringToConvert, digitsStartIndex);
                    final String digits =
                            stringToConvert.substring(digitsStartIndex, digitsStartIndex + numDigits);
                    for (int i = numDigits; i < ESCAPE_OCTAL_MAX_DIGITS; ++i)
                        buffer.append('0');
                    buffer.append(digits);
                    newIndex = digitsStartIndex + numDigits;
                }
            }

            index = newIndex;
        }

        return buffer.toString();
    }

    private static int getNumOctalEscapeDigits(String stringToConvert, int digitsStartIndex)
    {
        // the same as Zserio lexer: [0-3]? OCTAL_DIGIT OCTAL_DIGIT?
        final char firstDigit = stringToConvert.charAt(digitsStartIndex);
        if (firstDigit <= '3' && isOctalDigit(stringToConvert, digitsStartIndex + 1) &&
                isOctalDigit(stringToConvert, digitsStartIndex + 2))
            return 3;

        return isOctalDigit(stringToConvert, digitsStartIndex + 1) ? 2 : 1;
    }

    private static boolean isOctalDigit(String stringToConvert, int index)
    {
        if (index >= stringToConvert.length())
            return false;

        final char character = stringToConvert.charAt(index);

        return character >= '0' && character <= '7';
    }

    private static final char STRING_ESCAPE_CHARACTER = '\\';
    private static final char ESCAPE_HEXADECIMAL_SPECIFIER = 'x';
    private static final char ESCAPE_UNICODE_SPECIFIER = 'u';
    private static final char ESCAPE_OCTAL_SPECIFIER = '0';

    private static final int ESCAPE_HEXADECIMAL_LENGTH = 3;
    private static final int ESCAPE_UNICODE_LENGTH = 5;
    private static final int ESCAPE_OCTAL_MAX_DIGITS = 3;
}
