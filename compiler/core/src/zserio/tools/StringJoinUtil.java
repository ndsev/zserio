package zserio.tools;

/**
 * The common string manipulation functions which join the strings.
 */
public class StringJoinUtil
{
    /**
     * Joins two strings using given separator.
     *
     * @param string1   The first string to join.
     * @param string2   The second string to join.
     * @param separator The separator character to use.
     *
     * @return Returns joined string.
     */
    public static String joinStrings(String string1, String string2, String separator)
    {
        Joiner joiner = new Joiner(separator);
        joiner.append(string1);
        joiner.append(string2);

        return joiner.toString();
    }

    /**
     * Joins three strings using given separator.
     *
     * @param string1   The first string to join.
     * @param string2   The second string to join.
     * @param string3   The third string to join.
     * @param separator The separator character to use.
     *
     * @return Returns joined string.
     */
    public static String joinStrings(String string1, String string2, String string3, String separator)
    {
        Joiner joiner = new Joiner(separator);
        joiner.append(string1);
        joiner.append(string2);
        joiner.append(string3);

        return joiner.toString();
    }

    /**
     * Joins four strings using given separator.
     *
     * @param string1   The first string to join.
     * @param string2   The second string to join.
     * @param string3   The third string to join.
     * @param string4   The fourth string to join.
     * @param separator The separator character to use.
     *
     * @return Returns joined string.
     */
    public static String joinStrings(String string1, String string2, String string3, String string4,
                                     String separator)
    {
        Joiner joiner = new Joiner(separator);
        joiner.append(string1);
        joiner.append(string2);
        joiner.append(string3);
        joiner.append(string4);

        return joiner.toString();
    }

    /**
     * Joins string list using given separator.
     *
     * @param stringList The list of strings to join.
     * @param separator  The separator character to use.
     *
     * @return Returns joined string.
     */
    public static String joinStrings(Iterable<String> stringList, String separator)
    {
        Joiner joiner = new Joiner(separator);
        joiner.append(stringList);

        return joiner.toString();
    }

    /**
     * Joins two string lists using given separator.
     *
     * @param stringList1 The first list of strings to join.
     * @param stringList2 The second list of strings to join.
     * @param separator   The separator character to use.
     *
     * @return Returns joined string.
     */
    public static String joinStrings(Iterable<String> stringList1, Iterable<String> stringList2,
            String separator)
    {
        Joiner joiner = new Joiner(separator);
        joiner.append(stringList1);
        joiner.append(stringList2);

        return joiner.toString();
    }

    /**
     * Joins string and string list using given separator.
     *
     * @param string     The first string to join.
     * @param stringList The list of strings to join.
     * @param separator  The separator character to use.
     *
     * @return Returns joined string.
     */
    public static String joinStrings(String string, Iterable<String> stringList, String separator)
    {
        Joiner joiner = new Joiner(separator);
        joiner.append(string);
        joiner.append(stringList);

        return joiner.toString();
    }

    /**
     * Helper class for joining strings with given separator.
     */
    public static class Joiner
    {
        /**
         * Constructor from separator string.
         *
         * @param separator Separator string to use during joining.
         */
        public Joiner(String separator)
        {
            this.separator = separator;
            first = true;
            stringBuilder = new StringBuilder();
        }

        /**
         * Appends another string.
         *
         * If this string is not the first one, the method appends the separator at first.
         *
         * @param string String to append.
         */
        public void append(String string)
        {
            if (!string.isEmpty())
            {
                if (!first)
                    stringBuilder.append(separator);
                else
                    first = false;

                stringBuilder.append(string);
            }
        }

        /**
         * Appends list of strings.
         *
         * @param stringList The list of strings to append.
         */
        public void append(Iterable<String> stringList)
        {
            for (String string : stringList)
                append(string);
        }

        /**
         * Gets the result in string format.
         */
        @Override
        public String toString()
        {
            return stringBuilder.toString();
        }

        private final String separator;
        private boolean first;
        private final StringBuilder stringBuilder;
    }
}
