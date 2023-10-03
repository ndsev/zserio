package test_utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils
{
    public static void assertJsonEquals(String expectedJson, String providedJson)
    {
        assertEquals(expectedJson.replaceAll("\n", System.lineSeparator()), providedJson);
    }
}
