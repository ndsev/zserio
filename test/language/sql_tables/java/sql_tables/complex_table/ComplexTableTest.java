package sql_tables.complex_table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_tables.TestDb;

import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedByteArray;

public class ComplexTableTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws IOException, SQLException
    {
        FileUtil.deleteFileIfExists(file);
        database = new TestDb(file.toString());
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
    public void deleteTable() throws SQLException
    {
        assertTrue(isTableInDb());

        final ComplexTable testTable = database.getComplexTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final ComplexTable testTable = database.getComplexTable();

        final List<ComplexTableRow> writtenRows = new ArrayList<ComplexTableRow>();
        fillComplexTableRows(writtenRows);
        testTable.write(writtenRows);

        final ComplexTableParameterProvider parameterProvider = new ComplexTableParameterProvider();
        final List<ComplexTableRow> readRows = testTable.read(parameterProvider);
        checkComplexTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final ComplexTable testTable = database.getComplexTable();

        final List<ComplexTableRow> writtenRows = new ArrayList<ComplexTableRow>();
        fillComplexTableRows(writtenRows);
        testTable.write(writtenRows);

        final ComplexTableParameterProvider parameterProvider = new ComplexTableParameterProvider();
        final String condition = "name='Name1'";
        final List<ComplexTableRow> readRows = testTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final ComplexTableRow readRow = readRows.get(0);
        checkComplexTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final ComplexTable testTable = database.getComplexTable();

        final List<ComplexTableRow> writtenRows = new ArrayList<ComplexTableRow>();
        fillComplexTableRows(writtenRows);
        testTable.write(writtenRows);

        final int updateRowId = 3;
        final ComplexTableRow updateRow = createComplexTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "blobId=" + updateRowId;
        testTable.update(updateRow, updateCondition);

        final ComplexTableParameterProvider parameterProvider = new ComplexTableParameterProvider();
        final List<ComplexTableRow> readRows = testTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final ComplexTableRow readRow = readRows.get(0);
        checkComplexTableRow(updateRow, readRow);
    }

    @Test
    public void nullValues() throws SQLException, IOException, ZserioError
    {
        final ComplexTable testTable = database.getComplexTable();

        final List<ComplexTableRow> writtenRows = new ArrayList<ComplexTableRow>();
        fillComplexTableRowsWithNullValues(writtenRows);
        testTable.write(writtenRows);

        final ComplexTableParameterProvider parameterProvider = new ComplexTableParameterProvider();
        final List<ComplexTableRow> readRows = testTable.read(parameterProvider);
        checkComplexTableRowsWithNullValues(writtenRows, readRows);
    }

    private static class ComplexTableParameterProvider implements ComplexTable.ParameterProvider
    {
        @Override
        public long getCount(ResultSet resultSet)
        {
            return COMPLEX_TABLE_COUNT;
        }
    }

    private static void fillComplexTableRows(List<ComplexTableRow> rows)
    {
        for (int blobId = 0; blobId < NUM_COMPLEX_TABLE_ROWS; ++blobId)
            rows.add(createComplexTableRow(blobId, "Name" + blobId));
    }

    private static ComplexTableRow createComplexTableRow(int blobId, String name)
    {
        final ComplexTableRow row = new ComplexTableRow();

        row.setBlobId(BigInteger.valueOf(blobId));
        row.setAge(Long.MAX_VALUE);
        row.setName(name);
        row.setIsValid(true);
        row.setSalary(9.9f);
        row.setBonus(5.5);
        row.setValue((byte)0x34);
        row.setColor(TestEnum.RED);

        final UnsignedByteArray values = new UnsignedByteArray(COMPLEX_TABLE_COUNT);
        for (int i = 0; i < values.length(); ++i)
            values.setElementAt((short)blobId, i);
        final TestBlob testBlob = new TestBlob(values.length(), 0, values, true);
        row.setBlob(testBlob);

        return row;
    }

    private static void fillComplexTableRowsWithNullValues(List<ComplexTableRow> rows)
    {
        for (int blobId = 0; blobId < NUM_COMPLEX_TABLE_ROWS; ++blobId)
            rows.add(createComplexTableRowWithNullValues(blobId));
    }

    private static ComplexTableRow createComplexTableRowWithNullValues(int blobId)
    {
        final ComplexTableRow row = new ComplexTableRow();

        row.setBlobId(BigInteger.valueOf(blobId));
        row.setNullAge();
        row.setName(null);
        row.setNullIsValid();
        row.setNullSalary();
        row.setNullBonus();
        row.setNullValue();
        row.setColor(null);
        row.setBlob(null);

        return row;
    }

    private static void checkComplexTableRows(List<ComplexTableRow> rows1, List<ComplexTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkComplexTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkComplexTableRow(ComplexTableRow row1, ComplexTableRow row2)
    {
        assertEquals(row1.getBlobId(), row2.getBlobId());
        assertEquals(row1.getAge(), row2.getAge());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getIsValid(), row2.getIsValid());
        assertEquals(row1.getSalary(), row2.getSalary(), Float.MIN_VALUE);
        assertEquals(row1.getBonus(), row2.getBonus(), Double.MIN_VALUE);
        assertEquals(row1.getValue(), row2.getValue());
        assertEquals(row1.getColor(), row2.getColor());
        assertEquals(row1.getBlob(), row2.getBlob());
    }

    private static void checkComplexTableRowsWithNullValues(List<ComplexTableRow> rows1,
            List<ComplexTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkComplexTableRowWithNullValues(rows1.get(i), rows2.get(i));
    }

    private static void checkComplexTableRowWithNullValues(ComplexTableRow row1, ComplexTableRow row2)
    {
        assertEquals(row1.getBlobId(), row2.getBlobId());

        assertTrue(row2.isNullAge());
        assertTrue(row2.isNullName());
        assertTrue(row2.isNullIsValid());
        assertTrue(row2.isNullSalary());
        assertTrue(row2.isNullBonus());
        assertTrue(row2.isNullValue());
        assertTrue(row2.isNullColor());
        assertTrue(row2.isNullBlob());
    }

    private boolean isTableInDb() throws SQLException
    {
        // check if database does contain table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME +
                "'";

        final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);
        try
        {
            final ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return false;

            // read table name
            final String tableName = resultSet.getString(1);
            if (resultSet.wasNull() || !tableName.equals(TABLE_NAME))
                return false;
        }
        finally
        {
            statement.close();
        }

        return true;
    }

    private static final String TABLE_NAME = "complexTable";

    private static final int    NUM_COMPLEX_TABLE_ROWS = 5;
    private static final int    COMPLEX_TABLE_COUNT = 10;
    private static final String FILE_NAME = "complex_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
