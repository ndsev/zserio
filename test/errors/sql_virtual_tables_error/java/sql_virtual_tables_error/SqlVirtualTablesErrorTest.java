package sql_virtual_tables_error;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioErrors;

public class SqlVirtualTablesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void noFields()
    {
        final String error = "no_fields_error.zs:3:11: Virtual table must have at least one field!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void withoutRowId()
    {
        final String error = "without_rowid_error.zs:12:5: Virtual table cannot be without rowid!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
