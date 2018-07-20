package sql_tables_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class SqlTablesWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void badOrderedPrimaryKey()
    {
        final String warning = ":9:5: Primary key column 'classId' is in bad order in sql table " +
                "'BadOrderedPrimaryKeyTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void duplicatedPrimaryKey()
    {
        final String warning = ":6:29: Duplicated primary key column 'classId' in sql table " +
                "'DuplicatedPrimaryKeyTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void multiplePrimaryKeys()
    {
        final String warning = ":9:5: Multiple primary keys in sql table 'MultiplePrimaryKeysTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void noPrimaryKey()
    {
        final String warning = ":3:1: No primary key in sql table 'NoPrimaryKeyTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void notFirstPrimaryKey()
    {
        final String warning = ":6:29: Primary key column 'classId' is not the first one in sql table " +
                "'NotFirstPrimaryKeyTable'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void notNullPrimaryKey()
    {
        final String warning1 = ":5:5: Primary key column 'schoolId' can contain NULL in sql table " +
                "'NotNullPrimaryKeyTable1'.";
        assertTrue(zserioWarnings.isPresent(warning1));

        final String warning2 = ":14:5: Primary key column 'schoolId' can contain NULL in sql table " +
                "'NotNullPrimaryKeyTable2'.";
        assertTrue(zserioWarnings.isPresent(warning2));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 7;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
