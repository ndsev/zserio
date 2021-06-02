package arrays_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class ArraysWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void compoundHasNoPackableField()
    {
        final String warning1 = "compound_has_no_packable_field_warning.zs:46:12: " +
                "'StructWithoutPackable' doesn't contain any packable field!";
        assertTrue(zserioWarnings.isPresent(warning1));

        final String warning2 = "compound_has_no_packable_field_warning.zs:48:12: " +
                "'ChoiceWithoutPackableField' doesn't contain any packable field!";
        assertTrue(zserioWarnings.isPresent(warning2));
    }

    @Test
    public void unpackableElementType()
    {
        final String warning1 = "unpackable_element_type_warning.zs:17:12: " +
                "'bool' is not packable element type!";
        assertTrue(zserioWarnings.isPresent(warning1));

        final String warning2 = "unpackable_element_type_warning.zs:18:12: " +
                "'string' is not packable element type!";
        assertTrue(zserioWarnings.isPresent(warning1));

        final String warning3 = "unpackable_element_type_warning.zs:19:12: " +
                "'float64' is not packable element type!";
        assertTrue(zserioWarnings.isPresent(warning2));

        final String warning4 = "unpackable_element_type_warning.zs:21:12: " +
                "'extern' is not packable element type!";
        assertTrue(zserioWarnings.isPresent(warning3));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 6;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
