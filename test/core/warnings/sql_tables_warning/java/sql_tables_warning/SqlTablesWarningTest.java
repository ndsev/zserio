package sql_tables_warning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class SqlTablesWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void badOrderedPrimaryKey()
    {
        final String warning = "bad_ordered_primary_key_warning.zs:9:9: "
                + "Primary key column 'classId' is in bad order in sql table 'BadOrderedPrimaryKeyTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void duplicatedPrimaryKey()
    {
        final String warning = "duplicated_primary_key_warning.zs:6:33: "
                + "Duplicated primary key column 'classId' in sql table 'DuplicatedPrimaryKeyTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void multiplePrimaryKeys()
    {
        final String warning = "multiple_primary_keys_warning.zs:9:9: "
                + "Multiple primary keys in sql table 'MultiplePrimaryKeysTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void noPrimaryKey()
    {
        final String warning = "no_primary_key_warning.zs:3:11: "
                + "No primary key in sql table 'NoPrimaryKeyTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void notFirstPrimaryKey()
    {
        final String warning = "not_first_primary_key_warning.zs:6:29: "
                + "Primary key column 'classId' is not the first one in sql table 'NotFirstPrimaryKeyTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void notNullPrimaryKey()
    {
        final String warning1 = "not_null_primary_key_warning.zs:5:17: "
                + "Primary key column 'schoolId' can contain NULL in sql table 'NotNullPrimaryKeyTable1'.";
        assertTrue(zserioWarnings.isPresent(warning1));

        final String warning2 = "not_null_primary_key_warning.zs:14:17: "
                + "Primary key column 'schoolId' can contain NULL in sql table 'NotNullPrimaryKeyTable2'.";
        assertTrue(zserioWarnings.isPresent(warning2));
    }

    private static ZserioErrorOutput zserioWarnings;
}
