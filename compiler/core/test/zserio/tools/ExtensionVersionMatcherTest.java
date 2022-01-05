package zserio.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

import java.util.List;


public class ExtensionVersionMatcherTest
{
    @Test
    public void splitVersionString()
    {
        List<String> splitted = null;

        // invalid format
        splitted = ExtensionVersionMatcher.splitVersionString(null);
        assertTrue(splitted.isEmpty());

        splitted = ExtensionVersionMatcher.splitVersionString("2.0.");
        assertTrue(splitted.isEmpty());

        splitted = ExtensionVersionMatcher.splitVersionString("2");
        assertTrue(splitted.isEmpty());

        splitted = ExtensionVersionMatcher.splitVersionString("200");
        assertTrue(splitted.isEmpty());

        splitted = ExtensionVersionMatcher.splitVersionString("200-pre1");
        assertTrue(splitted.isEmpty());

        splitted = ExtensionVersionMatcher.splitVersionString("2.0.0rc1");
        assertTrue(splitted.isEmpty());

        // valid format
        splitted = ExtensionVersionMatcher.splitVersionString("2.0.0");
        assertArrayEquals(new String[]{"2", "0", "0", null}, splitted.toArray());

        splitted = ExtensionVersionMatcher.splitVersionString("2.0.0-test");
        assertArrayEquals(new String[]{"2", "0", "0", "-test"}, splitted.toArray());

        splitted = ExtensionVersionMatcher.splitVersionString("2.0.0-pre1");
        assertArrayEquals(new String[]{"2", "0", "0", "-pre1"}, splitted.toArray());

        splitted = ExtensionVersionMatcher.splitVersionString("2.0.0-pre10");
        assertArrayEquals(new String[]{"2", "0", "0", "-pre10"}, splitted.toArray());

        splitted = ExtensionVersionMatcher.splitVersionString("20.30.40");
        assertArrayEquals(new String[]{"20", "30", "40", null}, splitted.toArray());

        splitted = ExtensionVersionMatcher.splitVersionString("20.30.40-rc1");
        assertArrayEquals(new String[]{"20", "30", "40", "-rc1"}, splitted.toArray());

        splitted = ExtensionVersionMatcher.splitVersionString("20.30.40-pre1");
        assertArrayEquals(new String[]{"20", "30", "40", "-pre1"}, splitted.toArray());

        splitted = ExtensionVersionMatcher.splitVersionString("200.300.400");
        assertArrayEquals(new String[]{"200", "300", "400", null}, splitted.toArray());

        splitted = ExtensionVersionMatcher.splitVersionString("200.300.400-pre111");
        assertArrayEquals(new String[]{"200", "300", "400", "-pre111"}, splitted.toArray());
    }

    @Test
    public void nullWithNullVersion()
    {
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion(null, null));
    }

    @Test
    public void zserioWithNullVersion()
    {
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion(ZserioVersion.VERSION_STRING, null));
    }

    @Test
    public void zserioWithEmptyStringVersion()
    {
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion(ZserioVersion.VERSION_STRING, ""));
    }

    @Test
    public void zserioWithSingleNumberVersion()
    {
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion(ZserioVersion.VERSION_STRING, "2"));
    }

    @Test
    public void zserioWithTwoNumbersVersion()
    {
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion(ZserioVersion.VERSION_STRING, "2.0"));
    }

    @Test
    public void exactVersionMatch()
    {
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.0", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.0-pre1", "2.0.0-pre1"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.0-pre5", "2.0.0-pre5"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.0-pre10", "2.0.0-pre10"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.1.0", "2.1.0"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.1.0-pre1", "2.1.0-pre1"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion(ZserioVersion.VERSION_STRING,
                String.valueOf(ZserioVersion.VERSION_STRING.toCharArray())));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("20.10.10", "20.10.10"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("200.100.100", "200.100.100"));
    }

    @Test
    public void compatibleVersion()
    {
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.0", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.1", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.1-pre1", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.2", "2.0.0"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.2", "2.0.1"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.2", "2.0.2"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.3", "2.0.2"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.3-pre1", "2.0.2"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.3-pre10", "2.0.2"));
    }

    @Test
    public void extensionExpectsTooOldZserio()
    {
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.0", "1.4.0"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.1.0", "2.0.0"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.2.0", "2.1.0"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.3.0", "2.2.0"));
    }

    @Test
    public void extensionExpectsLaterZserio()
    {
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.0", "2.0.1"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.1", "2.0.2"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.1", "2.0.2-pre1"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.0-pre1", "2.0.0"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.1-pre1", "2.0.1"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.2-pre1", "2.0.2"));
    }

    @Test
    public void extensionExpectsPreRelease()
    {
        // pre-released extensions must match exactly
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.0", "2.0.0-pre1"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.1", "2.0.0-pre2"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.1-pre2", "2.0.0-pre2"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.2", "2.0.0-pre3"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.20", "2.0.0-pre3"));
        assertFalse(ExtensionVersionMatcher.matchExtensionVersion("2.0.20-pre3", "2.0.0-pre3"));

        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.0-pre3", "2.0.0-pre3"));
        assertTrue(ExtensionVersionMatcher.matchExtensionVersion("2.0.20-pre3", "2.0.20-pre3"));
    }
}
