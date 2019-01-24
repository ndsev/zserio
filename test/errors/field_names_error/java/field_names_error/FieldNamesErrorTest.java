package field_names_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class FieldNamesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void definedType()
    {
        final String error = ":9:5: 'Defined' is a defined type in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void reservedJavaKeyword()
    {
        final String error = ":6:13: 'abstract' is a reserved keyword and may not be used here!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void reservedCppKeyword()
    {
        final String error = ":6:13: 'auto' is a reserved keyword and may not be used here!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void reservedPythonKeyword()
    {
        final String error = ":6:13: 'def' is a reserved keyword and may not be used here!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void reservedZserioKeyword()
    {
        final String error = "6:11: expecting ID, found 'index' (reserved keyword)";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
