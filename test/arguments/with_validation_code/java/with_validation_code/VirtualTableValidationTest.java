package with_validation_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import with_validation_code.virtual_table_validation.VirtualTableValidationDb;

import zserio.runtime.ZserioError;
import zserio.runtime.validation.ValidationReport;

public class VirtualTableValidationTest
{
    @BeforeAll
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @BeforeEach
    public void setUp() throws IOException, SQLException
    {
        FileUtil.deleteFileIfExists(file);
        database = new VirtualTableValidationDb(file.toString());
        database.createSchema();
    }

    @AfterEach
    public void tearDown() throws SQLException
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    @Test
    public void validation() throws SQLException, ZserioError
    {
        final ValidationReport report = database.validate();
        assertEquals(2, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    private static final String FILE_NAME = "virtual_table_validation_test.sqlite";

    private File file = new File(FILE_NAME);
    private VirtualTableValidationDb database = null;
};
