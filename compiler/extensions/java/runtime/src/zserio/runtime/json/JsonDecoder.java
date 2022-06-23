package zserio.runtime.json;

import java.math.BigInteger;

/**
 * JSON value decoder.
 */
class JsonDecoder
{
    /**
     * Decodes the JSON value from the string.
     *
     * @param content String which contains encoded JSON value.
     * @param pos Position from zero in content where the endoded JSON value begins.
     *
     * @return Decoder result object.
     */
    public static Result decodeValue(String content, int pos)
    {
        if (pos >= content.length())
            return Result.failure();

        final char firstChar = content.charAt(pos);
        switch (firstChar)
        {
        case 'n':
            return decodeLiteral(content, pos, "null", null);

        case 't':
            return decodeLiteral(content, pos, "true", true);

        case 'f':
            return decodeLiteral(content, pos, "false", false);

        case 'N':
            return decodeLiteral(content, pos, "NaN", Double.NaN);

        case 'I':
            return decodeLiteral(content, pos, "Infinity", Double.POSITIVE_INFINITY);

        case '"':
            return decodeString(content, pos);

        case '-':
            if (pos + 1 >= content.length())
                return Result.failure(1);

            final char secondChar = content.charAt(pos + 1);
            if (secondChar == 'I')
                return decodeLiteral(content, pos, "-Infinity", Double.NEGATIVE_INFINITY);

            return decodeNumber(content, pos);

        default:
            return decodeNumber(content, pos);
        }
    }

    /**
     * Decoder result object.
     */
    static class Result
    {
        /**
         * Gets the decoder result.
         *
         * @return true in case of success, otherwise false.
         */
        public boolean success()
        {
            return success;
        }

        /**
         * Gets the decoded JSON value.
         *
         * @return Decoded JSON value or null in case of failure.
         */
        public Object getObject()
        {
            return object;
        }

        /**
         * Gets the number of read characters from the string which contains encoded JSON value.
         *
         * In case of failure, it returns the number of processed (read) characters.
         *
         * @return Number of read characters.
         */
        public int getNumReadChars()
        {
            return numReadChars;
        }

        public static Result failure()
        {
            return new Result(false, null, 0);
        }

        public static Result failure(int numReadChars)
        {
            return new Result(false, numReadChars, 0);
        }

        public static Result success(Object object, int numReadChars)
        {
            return new Result(true, object, numReadChars);
        }

        private Result(boolean success, Object object, int numReadChars)
        {
            this.success = success;
            this.object = object;
            this.numReadChars = numReadChars;
        }

        private final boolean success;
        private final Object object;
        private final int numReadChars;
    }

    private static Result decodeLiteral(String content, int pos, String text, Object decodedObject)
    {
        final int textLength = text.length();
        if (pos + textLength >= content.length())
            return Result.failure(content.length() - pos);

        final String subContent = content.substring(pos, pos + textLength);
        if (subContent.equals(text))
            return Result.success(decodedObject, textLength);

        return Result.failure(textLength);
    }

    private static Result decodeNumber(String content, int pos)
    {
        // find out the end of the number in the string
        int endOfNumberPos = pos;
        if (content.charAt(endOfNumberPos) == '-')
            endOfNumberPos++;
        boolean isDouble = false;
        while (endOfNumberPos < content.length())
        {
            final char nextChar = content.charAt(endOfNumberPos);
            if (Character.isDigit(nextChar))
                continue;
            if (isDouble == false && (nextChar == '.' || nextChar == 'e' || nextChar == 'E'))
            {
                isDouble = true;
                continue;
            }
            break;
        }

        // decode the number from the substring
        final int numberLength = endOfNumberPos - pos;
        final String subContent = content.substring(pos, endOfNumberPos);
        try
        {
            if (isDouble)
            {
                final Double doubleNumber = Double.parseDouble(subContent);
                return Result.success(doubleNumber, numberLength);
            }
            else
            {
                final BigInteger integerNumber = new BigInteger(subContent);
                return Result.success(integerNumber, numberLength);
            }
        }
        catch (NumberFormatException excpt)
        {
            return Result.failure(numberLength);
        }
    }

    private static Result decodeString(String content, int pos)
    {
        final StringBuilder decodedString = new StringBuilder();
        int endOfStringPos = pos + 1; // we know that at the beginning is '"'
        while (endOfStringPos < content.length())
        {
            final char nextChar = content.charAt(endOfStringPos);
            endOfStringPos++;
            if (nextChar == '\\')
            {
                if (endOfStringPos + 1 >= content.length())
                    return Result.failure(endOfStringPos - pos);

                final char nextNextChar = content.charAt(endOfStringPos);
                endOfStringPos++;
                switch (nextNextChar)
                {
                case '\\':
                case '"':
                    decodedString.append(nextNextChar);
                    break;
                case 'b':
                    decodedString.append('\b');
                    break;
                case 'f':
                    decodedString.append('\f');
                    break;
                case 'n':
                    decodedString.append('\n');
                    break;
                case 'r':
                    decodedString.append('\r');
                    break;
                case 't':
                    decodedString.append('\t');
                    break;
                case 'u': // unicode escape
                    final int unicodeEscapeLen = 4;
                    if (endOfStringPos + unicodeEscapeLen >= content.length())
                        return Result.failure(content.length() - pos);
                    final String subContent = content.substring(endOfStringPos,
                            endOfStringPos + unicodeEscapeLen);
                    endOfStringPos += unicodeEscapeLen;
                    decodedString.append(Character.toChars(Integer.parseInt(subContent, 16)));
                    break;
                default:
                    // unknown escape character, not decoded...
                    return Result.failure(endOfStringPos - pos);
                }
            }
            else if (nextChar == '"')
            {
                break;
            }
            else
            {
                decodedString.append(nextChar);
            }
        }

        return Result.success(decodedString.toString(), endOfStringPos - pos);
    }
}
