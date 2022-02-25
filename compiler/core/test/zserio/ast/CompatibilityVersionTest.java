package zserio.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import zserio.tools.ZserioVersion;

public class CompatibilityVersionTest
{
    @Test
    public void versionLessThanMinimiumSupported()
    {
        final ParserException exception = assertThrows(ParserException.class, () ->
                new CompatibilityVersion(new AstLocation(null), "\"2.3.0\""));
        assertThat(exception.getMessage(), containsString(
                "Root package specifies unsupported compatibility version '2.3.0', minimum supported "));
    }

    @Test
    public void versionHigherThanCurrentVersion()
    {
        final Version currentVersion = Version.parseVersion(ZserioVersion.VERSION_STRING);
        final Version higherVersion = new Version(
                currentVersion.getMajor(), currentVersion.getMinor(), currentVersion.getRevision() + 1);
        final ParserException exception = assertThrows(ParserException.class, () ->
                new CompatibilityVersion(new AstLocation(null), "\"" + higherVersion + "\""));
        assertThat(exception.getMessage(), containsString(
                "Root package specifies compatibility version '" + higherVersion + "' " +
                "which is higher than current zserio version '" + ZserioVersion.VERSION_STRING + "'!"));
    }
}