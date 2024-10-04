package packages_warning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class PackagesWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void duplicatedPackageImport()
    {
        final String warning = "duplicated_package_import_warning.zs:6:8: "
                + "Duplicated import of package 'packages_warning.simple_database'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void duplicatedSingleTypeImport()
    {
        final String warning = "duplicated_single_type_import_warning.zs:6:8: "
                + "Duplicated import of 'packages_warning.simple_database.SimpleTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void packageImportOverwrite()
    {
        final String warning = "package_import_overwrite_warning.zs:6:8: "
                + "Import of package 'packages_warning.simple_database' overwrites single import of "
                + "'packages_warning.simple_database.SimpleTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void singleTypeAlreadyImported()
    {
        final String warning = "single_type_already_imported_warning.zs:6:8: "
                + "Single import of 'packages_warning.simple_database.SimpleTable' "
                + "already covered by package import.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
