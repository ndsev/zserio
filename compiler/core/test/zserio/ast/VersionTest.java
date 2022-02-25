package zserio.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class VersionTest
{
    @Test
    public void parseReleaseVersion()
    {
        final Version version = Version.parseVersion("2.5.0");
        assertEquals(2, version.getMajor());
        assertEquals(5, version.getMinor());
        assertEquals(0, version.getRevision());
        assertNull(version.getPreRelease());
    }

    @Test
    public void parsePreReleaseVersion()
    {
        final Version version = Version.parseVersion("2.5.0-pre1");
        assertEquals(2, version.getMajor());
        assertEquals(5, version.getMinor());
        assertEquals(0, version.getRevision());
        assertEquals(1, version.getPreRelease());
    }

    @Test
    public void parseInvalidVersions()
    {
        assertThrows(Exception.class, () -> Version.parseVersion("wrong.format"));
        assertThrows(Exception.class, () -> Version.parseVersion("2.5"));
        assertThrows(Exception.class, () -> Version.parseVersion("2.5.0pre1"));
        assertThrows(Exception.class, () -> Version.parseVersion("2.5.0.1"));
        assertThrows(Exception.class, () -> Version.parseVersion("2.5-pre1"));
        assertThrows(Exception.class, () -> Version.parseVersion("2.a.b")); // check hex
        assertThrows(Exception.class, () -> Version.parseVersion("2.0x1.0x2")); // check hex
        assertThrows(Exception.class, () -> Version.parseVersion("2.5.0.prd")); // invalid pre-release
        assertThrows(Exception.class, () -> Version.parseVersion("2.5.0.preX")); // invalid pre-release
    }

    @Test
    public void compareVersions()
    {
        final Version v240 = Version.parseVersion("2.4.0");
        final Version v241pre1 = Version.parseVersion("2.4.1-pre1");
        final Version v241pre2 = Version.parseVersion("2.4.1-pre2");
        final Version v241 = Version.parseVersion("2.4.1");
        final Version v250 = Version.parseVersion("2.5.0");

        assertTrue(v240.compareTo(v240) == 0);
        assertTrue(v241pre1.compareTo(v241pre1) == 0);
        assertTrue(v241pre2.compareTo(v241pre2) == 0);
        assertTrue(v241.compareTo(v241) == 0);
        assertTrue(v250.compareTo(v250) == 0);

        assertTrue(v240.compareTo(v241pre1) < 0);
        assertTrue(v241pre1.compareTo(v240) > 0);

        assertTrue(v241pre1.compareTo(v241pre2) < 0);
        assertTrue(v241pre2.compareTo(v241pre1) > 0);

        assertTrue(v241pre2.compareTo(v241) < 0);
        assertTrue(v241.compareTo(v241pre2) > 0);

        assertTrue(v241.compareTo(v250) < 0);
        assertTrue(v250.compareTo(v241) > 0);

        assertTrue(v250.compareTo(v241pre1) > 0);
        assertTrue(v241pre1.compareTo(v250) < 0);
    }

    @Test
    public void versionsEquals()
    {
        final Version v240 = Version.parseVersion("2.4.0");
        final Version v241pre1 = Version.parseVersion("2.4.1-pre1");
        final Version v241 = Version.parseVersion("2.4.1");

        assertEquals(v240, v240);
        assertEquals(v241pre1, v241pre1);
        assertEquals(v241, v241);

        assertEquals(new Version(2, 4, 0), v240);
        assertEquals(new Version(2, 4, 1, 1), v241pre1);
        assertEquals(new Version(2, 4, 1), v241);

        assertNotEquals(v240, v241pre1);
        assertNotEquals(v240, v241);
        assertNotEquals(v241pre1, v241);
    }

    @Test
    public void versionsHashCode()
    {
        assertEquals(new Version(2, 4, 0).hashCode(), Version.parseVersion("2.4.0").hashCode());
        assertEquals(new Version(2, 4, 1, 1).hashCode(), Version.parseVersion("2.4.1-pre1").hashCode());
        assertNotEquals(new Version(2, 4, 0).hashCode(), new Version(2, 4, 1).hashCode());
        assertNotEquals(new Version(2, 4, 1).hashCode(), new Version(2, 4, 1, 1).hashCode());
    }

    @Test
    public void versionToString()
    {
        assertEquals("2.4.0", Version.parseVersion("2.4.0").toString());
        assertEquals("2.4.1-pre1", Version.parseVersion("2.4.1-pre1").toString());
        assertEquals("2.4.1-pre1", new Version(2, 4, 1, 1).toString());
    }
}
