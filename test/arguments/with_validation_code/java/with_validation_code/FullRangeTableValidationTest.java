package with_validation_code;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import with_validation_code.full_range_table_validation.FullRangeTableValidationDb;

import zserio.runtime.ZserioError;
import zserio.runtime.SqlDatabase.Mode;
import zserio.runtime.validation.ValidationReport;

public class FullRangeTableValidationTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws IOException, URISyntaxException, SQLException
    {
        FileUtil.deleteFileIfExists(file);
        database = new FullRangeTableValidationDb(file.toString());
        database.createSchema();
    }

    @After
    public void tearDown() throws SQLException
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    @Test
    public void validation() throws SQLException, URISyntaxException, ZserioError
    {
        final ValidationReport report = database.validate(null);
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    private static final String FILE_NAME = "full_range_table_validation_test.sqlite";

    private File file = new File(FILE_NAME);
    private FullRangeTableValidationDb database = null;
};
