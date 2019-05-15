package optional_members_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class OptionalMembersWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void optionalMemberWithDifferentAutoOptionalParams()
    {
        final String warning = "optional_members_warning.zs:28:55: Parameterized field 'mixedTones' " +
                "has different optional clause than parameters.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void optionalMemberWithDifferentOptionalParams()
    {
        final String warning = "optional_members_warning.zs:25:47: Parameterized field 'blackAndWhiteTones' " +
                "has different optional clause than parameters.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 2;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
