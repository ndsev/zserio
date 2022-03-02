package sql_without_rowid_tables_warning;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class SqlWithoutRowIdTablesWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void integerPrimaryKey()
    {
        final String warning = "integer_primary_key_warning.zs:3:11: " +
                "Single integer primary key in without rowid table 'WithoutRowIdTable' " +
                "brings performance drop.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
