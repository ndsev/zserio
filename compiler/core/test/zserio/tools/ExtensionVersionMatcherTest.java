package zserio.tools;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ExtensionVersionMatcherTest
{
    @Test
    public void nullWithNullVersion()
    {
        assertFalse(ExtensionVersionMatcher.match((String)null, (String)null));
    }

    @Test
    public void zserioWithNullVersion()
    {
        assertFalse(ExtensionVersionMatcher.match(ZserioVersion.VERSION_STRING, null));
    }

    @Test
    public void zserioWithEmptyStringVersion()
    {
        assertFalse(ExtensionVersionMatcher.match(ZserioVersion.VERSION_STRING, ""));
    }

    @Test
    public void zserioWithSingleNumberVersion()
    {
        assertFalse(ExtensionVersionMatcher.match(ZserioVersion.VERSION_STRING, "2"));
    }

    @Test
    public void zserioWithTwoNumbersVersion()
    {
        assertFalse(ExtensionVersionMatcher.match(ZserioVersion.VERSION_STRING, "2.0"));
    }

    @Test
    public void exactVersionMatch()
    {
        assertTrue(ExtensionVersionMatcher.match("2.0.0", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.match("2.0.0-pre1", "2.0.0-pre1"));
        assertTrue(ExtensionVersionMatcher.match("2.0.0-pre5", "2.0.0-pre5"));
        assertTrue(ExtensionVersionMatcher.match("2.0.0-pre10", "2.0.0-pre10"));
        assertTrue(ExtensionVersionMatcher.match("2.1.0", "2.1.0"));
        assertTrue(ExtensionVersionMatcher.match("2.1.0-pre1", "2.1.0-pre1"));
        assertTrue(ExtensionVersionMatcher.match(
                ZserioVersion.VERSION_STRING, String.valueOf(ZserioVersion.VERSION_STRING.toCharArray())));
        assertTrue(ExtensionVersionMatcher.match("20.10.10", "20.10.10"));
        assertTrue(ExtensionVersionMatcher.match("200.100.100", "200.100.100"));
    }

    @Test
    public void compatibleVersion()
    {
        assertTrue(ExtensionVersionMatcher.match("2.0.0", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.match("2.0.1", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.match("2.0.1-pre1", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.match("2.0.2", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.match("2.0.2", "2.0.1"));
        assertTrue(ExtensionVersionMatcher.match("2.0.2", "2.0.2"));
        assertTrue(ExtensionVersionMatcher.match("2.0.3", "2.0.2"));
        assertTrue(ExtensionVersionMatcher.match("2.0.3-pre1", "2.0.2"));
        assertTrue(ExtensionVersionMatcher.match("2.0.3-pre10", "2.0.2"));

        assertTrue(ExtensionVersionMatcher.match("2.0.0", "2.0.0-pre1"));
        assertTrue(ExtensionVersionMatcher.match("2.0.1", "2.0.0-pre2"));
        assertTrue(ExtensionVersionMatcher.match("2.0.1-pre2", "2.0.0-pre2"));
        assertTrue(ExtensionVersionMatcher.match("2.0.2", "2.0.0-pre3"));
        assertTrue(ExtensionVersionMatcher.match("2.0.20", "2.0.0-pre3"));
        assertTrue(ExtensionVersionMatcher.match("2.0.20-pre3", "2.0.0-pre3"));

        assertTrue(ExtensionVersionMatcher.match("2.0.0-pre3", "2.0.0-pre3"));
        assertTrue(ExtensionVersionMatcher.match("2.0.20-pre3", "2.0.20-pre3"));
    }

    @Test
    public void extensionExpectsTooOldZserio()
    {
        assertFalse(ExtensionVersionMatcher.match("2.0.0", "1.4.0"));
        assertFalse(ExtensionVersionMatcher.match("2.1.0", "2.0.0"));
        assertFalse(ExtensionVersionMatcher.match("2.2.0", "2.1.0"));
        assertFalse(ExtensionVersionMatcher.match("2.3.0", "2.2.0"));
    }

    @Test
    public void extensionExpectsLaterZserio()
    {
        assertFalse(ExtensionVersionMatcher.match("2.0.0", "2.0.1"));
        assertFalse(ExtensionVersionMatcher.match("2.0.1", "2.0.2"));
        assertFalse(ExtensionVersionMatcher.match("2.0.1", "2.0.2-pre1"));
        assertFalse(ExtensionVersionMatcher.match("2.0.0-pre1", "2.0.0"));
        assertFalse(ExtensionVersionMatcher.match("2.0.1-pre1", "2.0.1"));
        assertFalse(ExtensionVersionMatcher.match("2.0.2-pre1", "2.0.2"));
    }
}
