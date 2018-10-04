package with_validation_code;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import with_validation_code.simple_table_validation.RootStruct;
import with_validation_code.simple_table_validation.SimpleTable;
import with_validation_code.simple_table_validation.SimpleTableValidationDb;
import with_validation_code.simple_table_validation.TestEnum;

import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedByteArray;
import zserio.runtime.io.ZserioIO;
import zserio.runtime.validation.ValidationError;
import zserio.runtime.validation.ValidationReport;

public class SimpleTableValidationTest
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
        database = new SimpleTableValidationDb(file.toString());
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
        populateDb(database, new TestParameterProvider(), false);

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
        assertEquals(ENTRY_COUNT, parameterProvider.getSimpleTable_count_callCount());
    }

    @Test
    public void validationBlobFailure() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), true);

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        final ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldBlob", error.getFieldName());
        assertEquals(ValidationError.Type.BLOB_PARSE_FAILED, error.getType());
        assertEquals("Read: Wrong offset for field RootStruct.end: 14 != 4278190094!",
                error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
        assertEquals("with_validation_code.simple_table_validation.RootStruct", stackTrace[0].getClassName());
        assertEquals("read", stackTrace[0].getMethodName());
    }

    @Test
    public void validateSingleTable() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), false);

        final SimpleTable table = database.getSimpleTable();
        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = table.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    @Test
    public void extraColumn() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), false);

        database.executeUpdate("ALTER TABLE simpleTable ADD COLUMN extraColumn TEXT");

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        final ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("extraColumn", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.COLUMN_SUPERFLUOUS, error.getType());
        assertEquals("superfluous column simpleTable.extraColumn of type TEXT encountered",
                error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void missingColumn() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), false);

        database.executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, " +
                "fieldBool INTEGER NOT NULL, fieldBlob BLOB NOT NULL, fieldEnum INTEGER NOT NULL)");
        database.executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldBlob, fieldEnum " +
                         "from simpleTable");
        database.executeUpdate("DROP TABLE simpleTable");
        database.executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        final ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldNonBlob", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.COLUMN_MISSING, error.getType());
        assertEquals("column SimpleTable.fieldNonBlob is missing", error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void wrongColumnType() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), false);

        database.executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, " +
                "fieldBool INTEGER NOT NULL, fieldNonBlob TEXT, fieldBlob BLOB NOT NULL, " +
                "fieldEnum INTEGER NOT NULL)");
        database.executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldNonBlob, fieldBlob, "
                + "fieldEnum from simpleTable");
        database.executeUpdate("DROP TABLE simpleTable");
        database.executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        final ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldNonBlob", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.INVALID_COLUMN_TYPE, error.getType());
        assertEquals("column SimpleTable.fieldNonBlob has type 'TEXT' but 'INTEGER' is expected",
                error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void wrongColumnNotNullConstraint() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), false);

        database.executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, " +
                "fieldBool INTEGER NOT NULL, fieldNonBlob INTEGER NOT NULL, fieldBlob BLOB, " +
                "fieldEnum INTEGER NOT NULL)");
        database.executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldNonBlob, fieldBlob, "
                + "fieldEnum from simpleTable");
        database.executeUpdate("DROP TABLE simpleTable");
        database.executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(2, errors.size());
        ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldNonBlob", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.INVALID_COLUMN_CONSTRAINT, error.getType());
        assertEquals("column SimpleTable.fieldNonBlob is NOT NULL-able, but the column is expected to be " +
                "NULL-able", error.getMessage());

        error = errors.get(1);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldBlob", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.INVALID_COLUMN_CONSTRAINT, error.getType());
        assertEquals("column SimpleTable.fieldBlob is NULL-able, but the column is expected to be " +
                "NOT NULL-able", error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void wrongColumnPrimaryKeyConstraint() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), false);

        database.executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER NOT NULL, " +
                "fieldBool INTEGER NOT NULL, fieldNonBlob INTEGER, fieldBlob BLOB NOT NULL, " +
                "fieldEnum INTEGER NOT NULL)");
        database.executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldNonBlob, fieldBlob, "
                + "fieldEnum from simpleTable");
        database.executeUpdate("DROP TABLE simpleTable");
        database.executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("rowid", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.INVALID_COLUMN_CONSTRAINT, error.getType());
        assertEquals("column SimpleTable.rowid is not primary key, but the column is expected to be " +
                "primary key", error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void outOfRange() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), false);

        // set fieldNonBlob to a value outside its Zserio type
        database.executeUpdate("UPDATE simpleTable SET fieldNonBlob = -1 WHERE fieldNonBlob = " +
                FIELD_NON_BLOB_OUT_OF_RANGE_ROW_ID);

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        final ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldNonBlob", error.getFieldName());
        final List<String> errorRowKeyValues = error.getRowKeyValues();
        assertEquals(1, errorRowKeyValues.size());
        assertEquals(Byte.toString(FIELD_NON_BLOB_OUT_OF_RANGE_ROW_ID), errorRowKeyValues.get(0));
        assertEquals(ValidationError.Type.VALUE_OUT_OF_RANGE, error.getType());
        assertEquals("Value -1 of SimpleTable.fieldNonBlob exceeds the range of 0..31", error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void invalidEnumValue() throws SQLException, URISyntaxException
    {
        populateDb(database, new TestParameterProvider(), false);

        // set fieldEnum to an invalid enum value
        database.executeUpdate("UPDATE simpleTable SET fieldEnum = 1 WHERE fieldEnum = " +
                TestEnum.RED.getValue());

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        final ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldEnum", error.getFieldName());
        final List<String> errorRowKeyValues = error.getRowKeyValues();
        assertEquals(1, errorRowKeyValues.size());
        assertEquals(Byte.toString(ENUM_RED_ROW_ID), errorRowKeyValues.get(0));
        assertEquals(ValidationError.Type.INVALID_ENUM_VALUE, error.getType());
        assertEquals("Enumeration value 1 of SimpleTable.fieldEnum is not valid!", error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    private void populateDb(SimpleTableValidationDb database, TestParameterProvider parameterProvider,
            boolean wrongOffset) throws SQLException
    {
        for (byte id = 0; id < ENTRY_COUNT; id++)
        {
            // make the first entry have wrong offset - if requested by caller
            // first is used to check that the validation continues past the erroneous blob
            insertTestRow(database, parameterProvider, id, wrongOffset && id == 0);
        }
    }

    private void insertTestRow(SimpleTableValidationDb database, TestParameterProvider parameterProvider,
            byte id, boolean wrongOffset) throws SQLException
    {
        final byte fieldNonBlob = id;
        final RootStruct fieldBlob =
                createTestRootStruct((int)parameterProvider.getSimpleTable_localCount1(null), id);
        final TestEnum fieldEnum = (id == ENUM_RED_ROW_ID) ? TestEnum.RED : TestEnum.BLUE;

        insertRow(database, id, id == 0, fieldNonBlob, fieldBlob, wrongOffset, fieldEnum);
    }

    private static RootStruct createTestRootStruct(int count, byte id)
    {
        final RootStruct struct = new RootStruct(count);
        UnsignedByteArray filler = new UnsignedByteArray((int)struct.getCount());

        for (int idx = 0; idx < filler.length(); idx++)
            filler.setElementAt(id, idx);

        struct.setFiller(filler);

        return struct;
    }

    private void insertRow(SimpleTableValidationDb database, long id, boolean fieldBool, byte fieldNonBlob,
            RootStruct fieldBlob, boolean wrongOffset, TestEnum fieldEnum) throws SQLException
    {
        /**
         * Populate the test DB.
         */
        final String sql = "INSERT INTO simpleTable (rowid, fieldBool, fieldNonBlob, fieldBlob, fieldEnum) " +
            "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = database.prepareStatement(sql);

        int argIdx = 1;
        statement.setLong(argIdx++, id);
        statement.setInt(argIdx++, fieldBool ? 1 : 0);
        statement.setInt(argIdx++, fieldNonBlob);

        final byte[] bytesFieldBlob = ZserioIO.write(fieldBlob);
        if (wrongOffset)
            corruptOffsetInFieldBlob(bytesFieldBlob);
        statement.setBytes(argIdx++, bytesFieldBlob);
        statement.setLong(argIdx++, fieldEnum.getValue());

        statement.execute();
    }

    private static void corruptOffsetInFieldBlob(byte[] bytesFieldBlob)
    {
        /*
         * Corrupt an offset field in the byte representation of SimpleTable.fieldBlob.
         *
         * This has to be done in the serialized byte array as corrupting the
         * offset before write() results in a ZserioError during the write.
         */
        bytesFieldBlob[0] = -1;
    }

    private static class TestParameterProvider implements IParameterProvider
    {
        @Override
        public long getSimpleTable_localCount1(ResultSet resultSet)
        {
            SimpleTable_count_callCount++;
            return SIMPLE_TABLE_COUNT;
        }

        public int getSimpleTable_count_callCount()
        {
            return SimpleTable_count_callCount;
        }

        private int SimpleTable_count_callCount = 0;
    }

    private static final int ENTRY_COUNT = 5;
    private static final long SIMPLE_TABLE_COUNT = 10;

    private static final byte ENUM_RED_ROW_ID = 1;
    private static final byte FIELD_NON_BLOB_OUT_OF_RANGE_ROW_ID = 0;

    private static final String FILE_NAME = "simple_table_validation_test.sqlite";

    private File file = new File(FILE_NAME);
    private SimpleTableValidationDb database = null;
};
