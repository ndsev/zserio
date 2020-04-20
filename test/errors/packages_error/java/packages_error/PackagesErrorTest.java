package packages_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class PackagesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void ambiguousSingleSymbolImport()
    {
        final String errors[] =
        {
            "imported.zs:3:14:     Found here",
            "ambiguous_single_symbol_import_error.zs:5:14:     Found here",
            "ambiguous_single_symbol_import_error.zs:9:20: Ambiguous symbol 'CONST'"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void ambiguousSingleTypeImport()
    {
        final String errors[] =
        {
            "imported.zs:3:8:     Found here",
            "ambiguous_single_type_import_error.zs:5:8:     Found here",
            "ambiguous_single_type_import_error.zs:12:5: Ambiguous type reference 'Structure'"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void importedDefaultPackage()
    {
        final String error = "imported_default_package_error.zs:4:8: Default package cannot be imported!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void importedUnknownSingleType()
    {
        final String error = "imported_unknown_single_type_error.zs:4:8: " +
                "Unresolved import of 'simple_database.UnknownTable'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void multipleDefaultPackage()
    {
        final String error = "default_package.zs:1:1: Multiple default packages are not allowed!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void transitiveImport()
    {
        final String error = "transitive_import_error.zs:8:5: Unresolved referenced type 'BottomStructure'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void upperCaseLetterInPackageName()
    {
        final String error = "upper_case_letter_in_Package_name_error.zs:1:9: " +
                "Package name cannot contain upper case letters!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongImportName()
    {
        final String error = "wrong_import_name.zs: No such file!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void wrongPackageName()
    {
        final String error = "bad_package_name.zs:2:9: Package 'bad_really_bad' does not match to the source " +
                "file name!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
