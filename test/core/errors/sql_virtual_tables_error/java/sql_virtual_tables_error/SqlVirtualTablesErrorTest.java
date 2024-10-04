package sql_virtual_tables_error;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class SqlVirtualTablesErrorTest
{
    @BeforeAll
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrorOutput();
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

    private static ZserioErrorOutput zserioErrors;
}
