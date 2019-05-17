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
    public void ambiguousSimpleType()
    {
        final String error = "ambiguous_single_type_error.zs:13:5: Ambiguous type reference 'SimpleTable' " +
                "found in packages 'simple_database' and 'complex_database'!";
        assertTrue(zserioErrors.isPresent(error));
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
                "Unknown type 'UnknownTable' in imported package 'simple_database'!";
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
