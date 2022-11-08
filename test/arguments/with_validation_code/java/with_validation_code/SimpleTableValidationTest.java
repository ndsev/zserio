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
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import with_validation_code.simple_table_validation.RootStruct;
import with_validation_code.simple_table_validation.SimpleTable;
import with_validation_code.simple_table_validation.SimpleTableValidationDb;
import with_validation_code.simple_table_validation.TestEnum;
import with_validation_code.simple_table_validation.TestBitmask;

import zserio.runtime.ZserioError;
import zserio.runtime.io.SerializeUtil;
import zserio.runtime.validation.ValidationError;
import zserio.runtime.validation.ValidationReport;

public class SimpleTableValidationTest
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
        database = new SimpleTableValidationDb(file.toString());
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
        populateDb(database.connection(), new TestParameterProvider(), false);

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
        assertEquals(ENTRY_COUNT, parameterProvider.getLocalCount1_callCount());
    }

    @Test
    public void validationBlobFailure() throws SQLException
    {
        populateDb(database.connection(), new TestParameterProvider(), true);

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
    public void validateSingleTable() throws SQLException
    {
        populateDb(database.connection(), new TestParameterProvider(), false);

        final SimpleTable table = database.getSimpleTable();
        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = table.validate(parameterProvider.getSimpleTableParameterProvider());

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());
        assertTrue(report.getErrors().isEmpty());
    }

    @Test
    public void extraColumn() throws SQLException
    {
        populateDb(database.connection(), new TestParameterProvider(), false);

        executeUpdate("ALTER TABLE simpleTable ADD COLUMN extraColumn TEXT");

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
    public void missingColumn() throws SQLException
    {
        populateDb(database.connection(), new TestParameterProvider(), false);

        executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, " +
                "fieldBool INTEGER NOT NULL, fieldDynamicBit INTEGER NOT NULL, " +
                "fieldVarInt16 INTEGER NOT NULL, fieldString TEXT NOT NULL, fieldBlob BLOB NOT NULL, " +
                "fieldEnum INTEGER, fieldBitmask INTEGER NOT NULL)");
        executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldDynamicBit, fieldVarInt16, " +
                "fieldString, fieldBlob, fieldEnum, fieldBitmask FROM simpleTable");
        executeUpdate("DROP TABLE simpleTable");
        executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        final ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldBit5", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.COLUMN_MISSING, error.getType());
        assertEquals("column SimpleTable.fieldBit5 is missing", error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void wrongColumnType() throws SQLException
    {
        populateDb(database.connection(), new TestParameterProvider(), false);

        executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, " +
                "fieldBool INTEGER NOT NULL, fieldBit5 TEXT, fieldDynamicBit INTEGER NOT NULL, " +
                "fieldVarInt16 INTEGER NOT NULL, fieldString TEXT NOT NULL, fieldBlob BLOB NOT NULL, " +
                "fieldEnum INTEGER, fieldBitmask INTEGER NOT NULL)");
        executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldBit5, fieldDynamicBit, " +
                "fieldVarInt16, fieldString, fieldBlob, fieldEnum, fieldBitmask FROM simpleTable");
        executeUpdate("DROP TABLE simpleTable");
        executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(1, errors.size());
        final ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldBit5", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.INVALID_COLUMN_TYPE, error.getType());
        assertEquals("column SimpleTable.fieldBit5 has type 'TEXT' but 'INTEGER' is expected",
                error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void wrongColumnNotNullConstraint() throws SQLException
    {
        populateDb(database.connection(), new TestParameterProvider(), false);

        executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER PRIMARY KEY NOT NULL, " +
                "fieldBool INTEGER NOT NULL, fieldBit5 INTEGER NOT NULL, fieldDynamicBit INTEGER NOT NULL, " +
                "fieldVarInt16 INTEGER NOT NULL, fieldString TEXT NOT NULL, fieldBlob BLOB, " +
                "fieldEnum INTEGER, fieldBitmask INTEGER NOT NULL)");
        executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldBit5, fieldDynamicBit, " +
                "fieldVarInt16, fieldString, fieldBlob, fieldEnum, fieldBitmask FROM simpleTable");
        executeUpdate("DROP TABLE simpleTable");
        executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(0, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        final List<ValidationError> errors = report.getErrors();
        assertEquals(2, errors.size());
        ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldBit5", error.getFieldName());
        assertEquals(null, error.getRowKeyValues());
        assertEquals(ValidationError.Type.INVALID_COLUMN_CONSTRAINT, error.getType());
        assertEquals("column SimpleTable.fieldBit5 is NOT NULL-able, but the column is expected to be " +
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
    public void wrongColumnPrimaryKeyConstraint() throws SQLException
    {
        populateDb(database.connection(), new TestParameterProvider(), false);

        executeUpdate("CREATE TABLE simpleTableTemp (rowid INTEGER NOT NULL, " +
                "fieldBool INTEGER NOT NULL, fieldBit5 INTEGER, fieldDynamicBit INTEGER NOT NULL, " +
                "fieldVarInt16 INTEGER NOT NULL, fieldString TEXT NOT NULL, fieldBlob BLOB NOT NULL, " +
                "fieldEnum INTEGER, fieldBitmask INTEGER NOT NULL)");
        executeUpdate("INSERT INTO simpleTableTemp SELECT rowid, fieldBool, fieldBit5, fieldDynamicBit, " +
                "fieldVarInt16, fieldString, fieldBlob, fieldEnum, fieldBitmask FROM simpleTable");
        executeUpdate("DROP TABLE simpleTable");
        executeUpdate("ALTER TABLE simpleTableTemp RENAME TO simpleTable");

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
    public void outOfRange() throws SQLException
    {
        populateDb(database.connection(), new TestParameterProvider(), false);

        executeUpdate("UPDATE simpleTable SET fieldBit5 = 32 WHERE rowid = " +
                FIELD_BIT5_OUT_OF_RANGE_ROW_ID);
        executeUpdate("UPDATE simpleTable SET fieldVarInt16 = 16384 WHERE rowid = " +
                FIELD_VARINT16_OUT_OF_RANGE_ROW_ID);
        executeUpdate("UPDATE simpleTable SET fieldBitmask = -1 WHERE rowid = " +
                FIELD_BITMASK_OUT_OF_RANGE_ROW_ID);

        final TestParameterProvider parameterProvider = new TestParameterProvider();
        final ValidationReport report = database.validate(parameterProvider);

        assertEquals(1, report.getNumberOfValidatedTables());
        assertEquals(ENTRY_COUNT, report.getNumberOfValidatedRows());
        assertTrue(report.getTotalParameterProviderTime() <= report.getTotalValidationTime());

        // TODO[Mi-L@]: In C++ we can also detect outOfRange error for dynamic bit fields, but in Java we just
        //              don't implement the check.
        final List<ValidationError> errors = report.getErrors();
        assertEquals(3, errors.size());

        ValidationError error = errors.get(0);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldBit5", error.getFieldName());
        List<String> errorRowKeyValues = error.getRowKeyValues();
        assertEquals(1, errorRowKeyValues.size());
        assertEquals(Byte.toString(FIELD_BIT5_OUT_OF_RANGE_ROW_ID), errorRowKeyValues.get(0));
        assertEquals(ValidationError.Type.VALUE_OUT_OF_RANGE, error.getType());
        assertEquals("Value 32 of SimpleTable.fieldBit5 exceeds the range of 0..31", error.getMessage());

        error = errors.get(1);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldVarInt16", error.getFieldName());
        errorRowKeyValues = error.getRowKeyValues();
        assertEquals(1, errorRowKeyValues.size());
        assertEquals(Byte.toString(FIELD_VARINT16_OUT_OF_RANGE_ROW_ID), errorRowKeyValues.get(0));
        assertEquals(ValidationError.Type.VALUE_OUT_OF_RANGE, error.getType());
        assertEquals("Value 16384 of SimpleTable.fieldVarInt16 exceeds the range of -16383..16383",
                error.getMessage());

        error = errors.get(2);
        assertEquals("simpleTable", error.getTableName());
        assertEquals("fieldBitmask", error.getFieldName());
        errorRowKeyValues = error.getRowKeyValues();
        assertEquals(1, errorRowKeyValues.size());
        assertEquals(Byte.toString(FIELD_BITMASK_OUT_OF_RANGE_ROW_ID), errorRowKeyValues.get(0));
        assertEquals(ValidationError.Type.VALUE_OUT_OF_RANGE, error.getType());
        assertEquals("Value -1 of SimpleTable.fieldBitmask exceeds the range of 0..255", error.getMessage());


        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    @Test
    public void invalidEnumValue() throws SQLException
    {
        final Connection connection = database.connection();
        populateDb(connection, new TestParameterProvider(), false);

        // set fieldEnum to an invalid enum value
        final String sql = "UPDATE simpleTable SET fieldEnum = 0 WHERE fieldEnum = ?";
        try (final PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setLong(1, TestEnum.RED.getValue());
            statement.execute();
        }

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
        assertEquals(Byte.toString(FIELD_ENUM_RED_ROW_ID), errorRowKeyValues.get(0));
        assertEquals(ValidationError.Type.INVALID_ENUM_VALUE, error.getType());
        assertEquals("Enumeration value 0 of SimpleTable.fieldEnum is not valid!", error.getMessage());

        final StackTraceElement[] stackTrace = error.getStackTrace();
        assertNotNull(stackTrace);
        // 0 is Thread.getStackTrace, 1 is zserio.runtime.validation.ValidationError.<init>
        assertTrue(stackTrace.length > 1);
        assertEquals("zserio.runtime.validation.ValidationError", stackTrace[1].getClassName());
        assertEquals("<init>", stackTrace[1].getMethodName());
    }

    private void populateDb(Connection connection, TestParameterProvider parameterProvider,
            boolean wrongOffset) throws SQLException
    {
        for (byte id = 0; id < ENTRY_COUNT; id++)
        {
            // make the first entry have wrong offset - if requested by caller
            // first is used to check that the validation continues past the erroneous blob
            insertTestRow(connection, parameterProvider, id, wrongOffset && id == 0);
        }
    }

    private void insertTestRow(Connection connection, TestParameterProvider parameterProvider,
            byte id, boolean wrongOffset) throws SQLException
    {
        final byte fieldBit5 = (byte)(id + 1);
        final long fieldDynamicBit = id;
        final short fieldVarInt16  = id;
        final String fieldString = "Test" + id;
        final RootStruct fieldBlob = createTestRootStruct((int)SIMPLE_TABLE_LOCAL_COUNT, id);
        final TestEnum fieldEnum = (id == FIELD_ENUM_RED_ROW_ID) ? TestEnum.RED : TestEnum.BLUE;
        final TestBitmask fieldBitmask = (id % 2 == 0) ? TestBitmask.Values.READ : TestBitmask.Values.WRITE;

        insertRow(connection, id, id == 0, fieldBit5, fieldDynamicBit, fieldVarInt16, fieldString, fieldBlob,
                wrongOffset, fieldEnum, fieldBitmask);
    }

    private static RootStruct createTestRootStruct(int count, byte id)
    {
        final RootStruct struct = new RootStruct(count);
        final short[] filler = new short[(int)struct.getCount()];

        for (int idx = 0; idx < filler.length; idx++)
            filler[idx] = id;

        struct.setFiller(filler);

        return struct;
    }

    private void insertRow(Connection connection, long id, boolean fieldBool, byte fieldBit5,
            long fieldDynamicBit, short fieldVarInt16, String fieldString, RootStruct fieldBlob,
            boolean wrongOffset, TestEnum fieldEnum, TestBitmask fieldBitmask) throws SQLException
    {
        /**
         * Populate the test DB.
         */
        final String sql =
                "INSERT INTO simpleTable (rowid, fieldBool, fieldBit5, fieldDynamicBit, fieldVarInt16, " +
                        "fieldString, fieldBlob, fieldEnum, fieldBitmask) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (final PreparedStatement statement = connection.prepareStatement(sql))
        {
            int argIdx = 1;
            statement.setLong(argIdx++, id);
            statement.setInt(argIdx++, fieldBool ? 1 : 0);
            statement.setByte(argIdx++, fieldBit5);
            statement.setLong(argIdx++, fieldDynamicBit);
            statement.setShort(argIdx++, fieldVarInt16);
            statement.setString(argIdx++, fieldString);
            final byte[] bytesFieldBlob = SerializeUtil.serializeToBytes(fieldBlob);
            if (wrongOffset)
                corruptOffsetInFieldBlob(bytesFieldBlob);
            statement.setBytes(argIdx++, bytesFieldBlob);
            if (id == FIELD_ENUM_NULL_ROW_ID)
                statement.setNull(argIdx++, java.sql.Types.INTEGER);
            else
                statement.setLong(argIdx++, fieldEnum.getValue());
            statement.setShort(argIdx++, fieldBitmask.getValue());

            statement.execute();
        }
    }

    private void executeUpdate(String sql) throws SQLException
    {
        try (final Statement statement = database.connection().createStatement())
        {
            statement.executeUpdate(sql);
        }
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

    private static class TestParameterProvider implements SimpleTableValidationDb.ParameterProvider
    {
        private static class SimpleTableParameterProvider implements SimpleTable.ParameterProvider
        {
            @Override
            public long getLocalCount1(ResultSet resultSet)
            {
                LocalCount1_callCount++;
                return SIMPLE_TABLE_LOCAL_COUNT;
            }

            public int getLocalCount1_callCount()
            {
                return LocalCount1_callCount;
            }

            private int LocalCount1_callCount = 0;
        }

        @Override
        public SimpleTable.ParameterProvider getSimpleTableParameterProvider()
        {
            return simpleTableParameterProvider;
        }

        public int getLocalCount1_callCount()
        {
            return simpleTableParameterProvider.getLocalCount1_callCount();
        }

        private final SimpleTableParameterProvider simpleTableParameterProvider =
                new SimpleTableParameterProvider();
    }

    private static final int ENTRY_COUNT = 5;
    private static final long SIMPLE_TABLE_LOCAL_COUNT = 10;

    private static final byte FIELD_BIT5_OUT_OF_RANGE_ROW_ID = 0;
    private static final byte FIELD_DYNAMIC_BIT_OUT_OF_RANGE_ROW_ID = 1;
    private static final byte FIELD_VARINT16_OUT_OF_RANGE_ROW_ID = 2;
    private static final byte FIELD_BITMASK_OUT_OF_RANGE_ROW_ID = 3;
    private static final byte FIELD_ENUM_RED_ROW_ID = 1;
    private static final byte FIELD_ENUM_NULL_ROW_ID = 2;

    private static final String FILE_NAME = "simple_table_validation_test.sqlite";

    private File file = new File(FILE_NAME);
    private SimpleTableValidationDb database = null;
};
