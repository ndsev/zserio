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
    public void importInDefaultPackage()
    {
        final String error = "import_in_default_package_error.zs:1:1: ZserioParser: " +
                "input file without 'package' specification can't use imports!";
        assertTrue(zserioErrors.isPresent(error));
    }
    
    @Test
    public void importedUnknownSingleType()
    {
        final String error = "imported_unknown_single_type_error.zs:3:1: " +
                "Unknown type UnknownTable in imported package simple_database!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
