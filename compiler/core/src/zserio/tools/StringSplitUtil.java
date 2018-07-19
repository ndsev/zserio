package zserio.tools;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The common string manipulation functions which split the strings.
 */
public class StringSplitUtil
{
    /**
     * Splits text into the paragraphs.
     *
     * @param text Text to split.
     *
     * @return Returns the list of paragraphs.
     */
    public static Iterable<String> splitTextToParagraphs(String text)
    {
        String lineSeparator = System.getProperty("line.separator");
        String[] paragraphList = text.split(lineSeparator + lineSeparator);

        return new ArrayList<String>(Arrays.asList(paragraphList));
    }

    /**
     * Split link string to type name and field name.
     *
     * @param linkString Link string to split.
     *
     * @return The type name and field name.
     */
    public static TypeAndFieldName splitLinkToTypeAndFieldName(String linkString)
    {
        final int lastDotIndex = linkString.lastIndexOf(Dot);
        String typeName;
        String fieldName;
        if (lastDotIndex < 0)
        {
            typeName = linkString;
            fieldName = null;
        }
        else
        {
            typeName = linkString.substring(0, lastDotIndex);
            fieldName = linkString.substring(lastDotIndex + 1);
        }

        return new TypeAndFieldName(typeName, fieldName);
    }

    /**
     * Helper class to hold type name and field name.
     */
    public static class TypeAndFieldName
    {
        /**
         * Constructor.
         *
         * @param typeName  Type name to construct from.
         * @param fieldName Field name to construct from.
         */
        public TypeAndFieldName(String typeName, String fieldName)
        {
            this.typeName = typeName;
            this.fieldName = fieldName;
        }

        /**
         * Returns the type name.
         */
        public String getTypeName()
        {
            return typeName;
        }

        /**
         * Returns the field name.
         */
        public String getFieldName()
        {
            return fieldName;
        }

        private String typeName;
        private String fieldName;
    }

    private static final char Dot = '.';
}
