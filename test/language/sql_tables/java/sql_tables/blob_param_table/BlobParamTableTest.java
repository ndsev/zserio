package sql_tables.blob_param_table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
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
import zserio.runtime.array.UnsignedIntArray;

public class BlobParamTableTest
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

        final BlobParamTable testTable = database.getBlobParamTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final BlobParamTable testTable = database.getBlobParamTable();

        final List<BlobParamTableRow> writtenRows = new ArrayList<BlobParamTableRow>();
        fillBlobParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final List<BlobParamTableRow> readRows = testTable.read();
        checkBlobParamTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final BlobParamTable testTable = database.getBlobParamTable();

        final List<BlobParamTableRow> writtenRows = new ArrayList<BlobParamTableRow>();
        fillBlobParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final String condition = "name='Name1'";
        final List<BlobParamTableRow> readRows = testTable.read(condition);

        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final BlobParamTableRow readRow = readRows.get(0);
        checkBlobParamTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final BlobParamTable testTable = database.getBlobParamTable();

        final List<BlobParamTableRow> writtenRows = new ArrayList<BlobParamTableRow>();
        fillBlobParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final int updateRowId = 3;
        final BlobParamTableRow updateRow = createBlobParamTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "blobId=" + updateRowId;
        testTable.update(updateRow, updateCondition);

        final List<BlobParamTableRow> readRows = testTable.read(updateCondition);
        assertEquals(1, readRows.size());

        final BlobParamTableRow readRow = readRows.get(0);
        checkBlobParamTableRow(updateRow, readRow);
    }

    @Test
    public void nullValues() throws SQLException, IOException, ZserioError
    {
        final BlobParamTable testTable = database.getBlobParamTable();

        final List<BlobParamTableRow> writtenRows = new ArrayList<BlobParamTableRow>();
        fillBlobParamTableRowsWithNullValues(writtenRows);
        testTable.write(writtenRows);

        final List<BlobParamTableRow> readRows = testTable.read();
        checkBlobParamTableRows(writtenRows, readRows);
    }

    private static void fillBlobParamTableRows(List<BlobParamTableRow> rows)
    {
        for (int blobId = 0; blobId < NUM_BLOB_PARAM_TABLE_ROWS; ++blobId)
            rows.add(createBlobParamTableRow(blobId, "Name" + blobId));
    }

    private static BlobParamTableRow createBlobParamTableRow(int blobId, String name)
    {
        final BlobParamTableRow row = new BlobParamTableRow();
        row.setBlobId(blobId);
        row.setName(name);
        final Parameters parameters = new Parameters(PARAMETERS_COUNT);
        row.setParameters(parameters);
        final UnsignedIntArray array = new UnsignedIntArray(PARAMETERS_COUNT);
        for (int i = 0; i < PARAMETERS_COUNT; ++i)
            array.setElementAt(i, i);
        final ParameterizedBlob parameterizedBlob = new ParameterizedBlob(parameters, array);
        row.setBlob(parameterizedBlob);

        return row;
    }

    private static void fillBlobParamTableRowsWithNullValues(List<BlobParamTableRow> rows)
    {
        for (int blobId = 0; blobId < NUM_BLOB_PARAM_TABLE_ROWS; ++blobId)
            rows.add(createBlobParamTableRowWithNullValues(blobId));
    }

    private static BlobParamTableRow createBlobParamTableRowWithNullValues(int blobId)
    {
        final BlobParamTableRow row = new BlobParamTableRow();
        row.setBlobId(blobId);
        row.setName(null);
        row.setParameters(null);
        row.setBlob(null);

        return row;
    }

    private static void checkBlobParamTableRows(List<BlobParamTableRow> rows1,
            List<BlobParamTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkBlobParamTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkBlobParamTableRow(BlobParamTableRow row1, BlobParamTableRow row2)
    {
        assertEquals(row1.getBlobId(), row2.getBlobId());
        if (row1.isNullName())
            assertTrue(row2.isNullName());
        else
            assertEquals(row1.getName(), row2.getName());
        if (row1.isNullParameters())
            assertTrue(row2.isNullParameters());
        else
            assertEquals(row1.getParameters(), row2.getParameters());
        if (row1.isNullBlob())
            assertTrue(row2.isNullParameters());
        else
            assertEquals(row1.getBlob(), row2.getBlob());
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

    private static final String TABLE_NAME = "blobParamTable";

    private static final int    PARAMETERS_COUNT = 10;
    private static final int    NUM_BLOB_PARAM_TABLE_ROWS = 5;
    private static final String FILE_NAME = "blob_param_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
