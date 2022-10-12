package with_validation_code;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.net.URISyntaxException;
import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import with_validation_code.column_type_validation.ColumnTypeDb;
import with_validation_code.column_type_validation.ColumnTypeTable;
import with_validation_code.column_type_validation.ColumnTypeTableRow;
import with_validation_code.column_type_validation.Blob;

import zserio.runtime.validation.ValidationError;
import zserio.runtime.validation.ValidationReport;
import zserio.runtime.io.ZserioIO;

public class ColumnTypeValidationTest
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
        database = new ColumnTypeDb(file.toString());
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
    public void validation() throws SQLException, IOException
    {
        populateDb(database);

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getErrors().isEmpty());
    }

    @Test
    public void validationNullValues() throws SQLException, IOException
    {
        populateDbWithNullValues(database);

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getErrors().isEmpty());
    }

    @Test
    public void validationFloatWhileIntegerExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET int8Value = 1.3 WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.int8Value type check failed (REAL doesn't match to INTEGER)!",
                error.getMessage());
    }

    @Test
    public void validationStringWhileIntegerExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET uint8Value = 'STRING' WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.uint8Value type check failed (TEXT doesn't match to INTEGER)!",
                error.getMessage());
    }

    @Test
    public void validationNumericStringWhileIntegerExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET uint8Value = '13' WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(0, report.getErrors().size());
        // see https://sqlite.org/datatype3.html#affinity - will be converted to INTEGER
    }

    @Test
    public void validationEmptyStringWhileIntegerExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET int16Value = '' WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.int16Value type check failed (TEXT doesn't match to INTEGER)!",
                error.getMessage());
    }

    @Test
    public void validationBlobWhileIntegerExpected() throws SQLException, IOException
    {
        populateDb(database);

        try (
            final PreparedStatement statement = database.connection().prepareStatement(
                    "UPDATE columnTypeTable SET int64Value = ? WHERE id = 2");
        )
        {
            final byte[] blobBytes = ZserioIO.write(new Blob(13));
            statement.setBytes(1, blobBytes);
            statement.execute();
        }

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("2", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.int64Value type check failed (BLOB doesn't match to INTEGER)!",
                error.getMessage());
    }

    @Test
    public void validationIntegerWhileFloatExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET float16Value = 13 WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(0, report.getErrors().size());
        // see https://sqlite.org/datatype3.html#affinity - will be converted to REAL
    }

    @Test
    public void validationStringWhileFloatExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET float32Value = 'STRING' WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.float32Value type check failed (TEXT doesn't match to REAL)!",
                error.getMessage());
    }

    @Test
    public void validationNumericStringWhileFloatExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET float64Value = '1.3' WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(0, report.getErrors().size());
        // see https://sqlite.org/datatype3.html#affinity - will be converted to REAL
    }

    @Test
    public void validationEmptyStringWhileFloatExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET float16Value = '' WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.float16Value type check failed (TEXT doesn't match to REAL)!",
                error.getMessage());
    }

    @Test
    public void validationBlobWhileFloatExpected() throws SQLException, IOException
    {
        populateDb(database);

        try (
            final PreparedStatement statement = database.connection().prepareStatement(
                    "UPDATE columnTypeTable SET float16Value = ? WHERE id = 2");
        )
        {
            final byte[] blobBytes = ZserioIO.write(new Blob(13));
            statement.setBytes(1, blobBytes);
            statement.execute();
        }

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("2", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.float16Value type check failed (BLOB doesn't match to REAL)!",
                error.getMessage());
    }

    @Test
    public void validationIntegerWhileStringExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET stringValue = 3 WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(0, report.getErrors().size());
        // see https://sqlite.org/datatype3.html#affinity - will be converted to TEXT
    }

    @Test
    public void validationFloatWhileStringExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET stringValue = 1.3 WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(0, report.getErrors().size());
        // see https://sqlite.org/datatype3.html#affinity - will be converted to TEXT
    }

    @Test
    public void validationBlobWhileStringExpected() throws SQLException, IOException
    {
        populateDb(database);

        try (
            final PreparedStatement statement = database.connection().prepareStatement(
                    "UPDATE columnTypeTable SET stringValue = ? WHERE id = 2");
        )
        {
            final byte[] blobBytes = ZserioIO.write(new Blob(13));
            statement.setBytes(1, blobBytes);
            statement.execute();
        }

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("2", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.stringValue type check failed (BLOB doesn't match to TEXT)!",
                error.getMessage());
    }

    @Test
    public void validationIntegerWhileBlobExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET blobValue = 13 WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.blobValue type check failed (INTEGER doesn't match to BLOB)!",
                error.getMessage());
    }

    @Test
    public void validationFloatWhileBlobExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET blobValue = 1.3 WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.blobValue type check failed (REAL doesn't match to BLOB)!",
                error.getMessage());
    }

    @Test
    public void validationStringWhileBlobExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET blobValue = 'STRING' WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.blobValue type check failed (TEXT doesn't match to BLOB)!",
                error.getMessage());
    }

    @Test
    public void validationEmptyStringWhileBlobExpected() throws SQLException, IOException
    {
        populateDb(database);

        executeUpdate("UPDATE columnTypeTable SET blobValue = '' WHERE id = 1");

        final ValidationReport report = database.validate();
        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertEquals(1, report.getErrors().size());
        final ValidationError error = report.getErrors().get(0);
        assertEquals("1", error.getRowKeyValues().get(0));
        assertEquals("Column ColumnTypeTable.blobValue type check failed (TEXT doesn't match to BLOB)!",
                error.getMessage());
    }

    private void populateDb(ColumnTypeDb db) throws SQLException, IOException
    {
        final List<ColumnTypeTableRow> rows = new ArrayList<ColumnTypeTableRow>();
        for (int i = 1; i < ENTRY_COUNT + 1; ++i)
        {
            final ColumnTypeTableRow row = new ColumnTypeTableRow();
            row.setId(i);
            row.setInt8Value((byte)(-1 * i));
            row.setInt16Value((short)(-2 * i));
            row.setInt32Value((int)(-3 * i));
            row.setInt64Value((long)(-4 * i));
            row.setUint8Value((short)(1 * i));
            row.setUint16Value((int)(2 * i));
            row.setUint32Value((long)(3 * i));
            row.setUint64Value(BigInteger.valueOf(4 * i));
            row.setFloat16Value(1.1f * i);
            row.setFloat32Value(1.2f * i);
            row.setFloat64Value(1.3 * i);
            row.setStringValue("stringValue " + i);
            row.setBlobValue(new Blob(i));
            rows.add(row);
        }

        db.getColumnTypeTable().write(rows);
    }

    private void populateDbWithNullValues(ColumnTypeDb db) throws SQLException, IOException
    {
        final List<ColumnTypeTableRow> rows = new ArrayList<ColumnTypeTableRow>();
        for (int i = 1; i <= ENTRY_COUNT; ++i)
        {
            final ColumnTypeTableRow row = new ColumnTypeTableRow();
            rows.add(row);
        }

        db.getColumnTypeTable().write(rows);
    }

    private void executeUpdate(String sql) throws SQLException
    {
        try (final Statement statement = database.connection().createStatement())
        {
            statement.executeUpdate(sql);
        }
    }

    private static final int ENTRY_COUNT = 2;
    private static final String FILE_NAME = "column_type_validation_test.sqlite";

    private File file = new File(FILE_NAME);
    private ColumnTypeDb database = null;
}
