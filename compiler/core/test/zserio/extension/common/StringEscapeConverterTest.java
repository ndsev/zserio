package zserio.extension.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
}
