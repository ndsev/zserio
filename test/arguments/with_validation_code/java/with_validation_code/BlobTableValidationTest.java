package with_validation_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import with_validation_code.blob_table_validation.Blob;
import with_validation_code.blob_table_validation.BlobTableValidationDb;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ZserioIO;
import zserio.runtime.validation.ValidationReport;

public class BlobTableValidationTest
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
        database = new BlobTableValidationDb(file.toString());
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
        insertRowWithNoneStandardBlob(database.connection());

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(1, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    private void insertRowWithNoneStandardBlob(Connection connection) throws SQLException
    {
        final String sqlCommand = "INSERT INTO blobTable(id, blob, nullableBlob) VALUES (?, ?, ?)";
        final PreparedStatement statement = connection.prepareStatement(sqlCommand);
        try
        {
            int argIdx = 1;
            statement.setLong(argIdx++, ROW_ID);

            final Blob blob = new Blob(Float.isNaN(NONE_STANDARD_NAN_VALUE), NONE_STANDARD_NAN_VALUE, END_VALUE);
            final byte[] bytesBlob = ZserioIO.write(blob);
            bytesBlob[1] = (byte)0xFF;      // none standard NaN
            bytesBlob[2] = (byte)0xFF;      // set skipped bits
            statement.setBytes(argIdx++, bytesBlob);

            statement.setNull(argIdx++, java.sql.Types.BLOB);

            statement.execute();
        }
        finally
        {
            statement.close();
        }
    }

    private static final long  ROW_ID = 0;
    private static final float NONE_STANDARD_NAN_VALUE = Float.intBitsToFloat(0xFFFFFFFF);
    private static final byte  END_VALUE = (byte)0x23;

    private static final String FILE_NAME = "blob_validation_test.sqlite";

    private File file = new File(FILE_NAME);
    private BlobTableValidationDb database = null;
};
