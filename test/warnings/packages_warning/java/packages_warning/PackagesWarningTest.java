package packages_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class PackagesWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void duplicatedPackageImport()
    {
        final String warning = "duplicated_package_import_warning.zs:6:8: " +
                "Duplicated import of package 'packages_warning.simple_database'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void duplicatedSingleTypeImport()
    {
        final String warning = "duplicated_single_type_import_warning.zs:6:8: " +
                "Duplicated import of type 'SimpleTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packageImportOverwrite()
    {
        final String warning = "package_import_overwrite_warning.zs:6:8: " +
                "Import of package 'packages_warning.simple_database' overwrites single type import " +
                "'SimpleTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void singleTypeAlreadyImported()
    {
        final String warning = "single_type_already_imported_warning.zs:6:8: " +
                "Single type 'SimpleTable' imported already by package import.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 4;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
