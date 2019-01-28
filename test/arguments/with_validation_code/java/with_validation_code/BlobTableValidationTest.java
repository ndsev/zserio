package with_validation_code;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import with_validation_code.blob_table_validation.Blob;
import with_validation_code.blob_table_validation.BlobTableValidationDb;

import zserio.runtime.ZserioError;
import zserio.runtime.SqlDatabase.Mode;
import zserio.runtime.io.ZserioIO;
import zserio.runtime.validation.ValidationReport;

public class BlobTableValidationTest
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
        database = new BlobTableValidationDb(file.toString());
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
        insertRowWithNoneStandardBlob(database);

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(1, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    private void insertRowWithNoneStandardBlob(BlobTableValidationDb database) throws SQLException
    {
        final String sqlCommand = "INSERT INTO blobTable(id, blob) VALUES (?, ?)";
        final PreparedStatement statement = database.prepareStatement(sqlCommand);

        int argIdx = 1;
        statement.setLong(argIdx++, ROW_ID);

        final Blob blob = new Blob(Float.isNaN(NONE_STANDARD_NAN_VALUE), NONE_STANDARD_NAN_VALUE, END_VALUE);
        final byte[] bytesBlob = ZserioIO.write(blob);
        bytesBlob[1] = (byte)0xFF;      // none standard NaN
        bytesBlob[2] = (byte)0xFF;      // set skipped bits
        statement.setBytes(argIdx, bytesBlob);

        statement.execute();
    }

    private static final long  ROW_ID = 0;
    private static final float NONE_STANDARD_NAN_VALUE = Float.intBitsToFloat(0xFFFFFFFF);
    private static final byte  END_VALUE = (byte)0x23;

    private static final String FILE_NAME = "blob_validation_test.sqlite";

    private File file = new File(FILE_NAME);
    private BlobTableValidationDb database = null;
};
