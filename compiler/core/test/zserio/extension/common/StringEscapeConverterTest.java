package zserio.extension.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringEscapeConverterTest
{
    @Test
    public void convertUnicodeToHexEscapes()
    {
        final String stringToConvert = "Test \\u001a\\u0019\\u1234 conversion.";
        final String convertedString = StringEscapeConverter.convertUnicodeToHexEscapes(stringToConvert);
        final String expectedConvertedString = "Test \\x1a\\x19\\u1234 conversion.";
        assertEquals(expectedConvertedString, convertedString);
    }

    @Test
    public void convertHexToUnicodeToEscapes()
    {
        final String stringToConvert = "Test \\x1a\\x19\\u1234 conversion.";
        final String convertedString = StringEscapeConverter.convertHexToUnicodeToEscapes(stringToConvert);
        final String expectedConvertedString = "Test \\u001a\\u0019\\u1234 conversion.";
        assertEquals(expectedConvertedString, convertedString);
    }

    @Test
    public void convertOctalEscapes()
    {
        final String stringToConvert = "Test \\0156\\031\\012\\07\\0778\\0377\\04 conversion.";
        final String convertedString = StringEscapeConverter.convertOctalEscapes(stringToConvert);
        final String expectedConvertedString = "Test \\156\\031\\012\\007\\0778\\377\\004 conversion.";
        assertEquals(expectedConvertedString, convertedString);
    }

    @Test
    public void convertOctalEscapesFollowedByDigits()
    {
        // Zserio takes at most two octal digits when the first one is greater than 3
        final String stringToConvert = "\\0777\\0456\\038\\05";
        final String convertedString = StringEscapeConverter.convertOctalEscapes(stringToConvert);
        final String expectedConvertedString = "\\0777\\0456\\0038\\005";
        assertEquals(expectedConvertedString, convertedString);
    }

    @Test
    public void convertOctalEscapesSkipsEscapedBackslash()
    {
        final String stringToConvert = "\\\\0156\\\\\\0156\\x01";
        final String convertedString = StringEscapeConverter.convertOctalEscapes(stringToConvert);
        final String expectedConvertedString = "\\\\0156\\\\\\156\\x01";
        assertEquals(expectedConvertedString, convertedString);
    }
}
