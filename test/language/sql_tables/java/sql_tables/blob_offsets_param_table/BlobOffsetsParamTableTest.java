package sql_tables.blob_offsets_param_table;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_tables.TestDb;

import zserio.runtime.ZserioError;

public class BlobOffsetsParamTableTest
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
        database = new TestDb(file.toString());
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
    public void deleteTable() throws SQLException
    {
        assertTrue(isTableInDb());

        final BlobOffsetsParamTable testTable = database.getBlobOffsetsParamTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final BlobOffsetsParamTable testTable = database.getBlobOffsetsParamTable();

        final List<BlobOffsetsParamTableRow> writtenRows = new ArrayList<BlobOffsetsParamTableRow>();
        fillBlobOffsetsParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final List<BlobOffsetsParamTableRow> readRows = testTable.read();
        checkBlobOffsetsParamTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final BlobOffsetsParamTable testTable = database.getBlobOffsetsParamTable();

        final List<BlobOffsetsParamTableRow> writtenRows = new ArrayList<BlobOffsetsParamTableRow>();
        fillBlobOffsetsParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final String condition = "name='Name1'";
        final List<BlobOffsetsParamTableRow> readRows = testTable.read(condition);

        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final BlobOffsetsParamTableRow readRow = readRows.get(0);
        checkBlobOffsetsParamTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final BlobOffsetsParamTable testTable = database.getBlobOffsetsParamTable();

        final List<BlobOffsetsParamTableRow> writtenRows = new ArrayList<BlobOffsetsParamTableRow>();
        fillBlobOffsetsParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final int updateRowId = 3;
        final BlobOffsetsParamTableRow updateRow = createBlobOffsetsParamTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "blobId=" + updateRowId;
        testTable.update(updateRow, updateCondition);

        final List<BlobOffsetsParamTableRow> readRows = testTable.read(updateCondition);
        assertEquals(1, readRows.size());

        final BlobOffsetsParamTableRow readRow = readRows.get(0);
        checkBlobOffsetsParamTableRow(updateRow, readRow);
    }

    private static void fillBlobOffsetsParamTableRows(List<BlobOffsetsParamTableRow> rows)
    {
        for (int blobId = 0; blobId < NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS; ++blobId)
        {
            rows.add(createBlobOffsetsParamTableRow(blobId, "Name" + blobId));
        }
    }

    private static BlobOffsetsParamTableRow createBlobOffsetsParamTableRow(int blobId, String name)
    {
        final BlobOffsetsParamTableRow row = new BlobOffsetsParamTableRow();
        row.setBlobId(blobId);
        row.setName(name);

        final OffsetsHolder offsetsHolder = new OffsetsHolder();
        final long[] offsets = new long[ARRAY_SIZE];
        offsetsHolder.setOffsets(offsets);
        row.setOffsetsHolder(offsetsHolder);

        final long[] array = new long[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; ++i)
            array[i] = i;
        final ParameterizedBlob parameterizedBlob = new ParameterizedBlob(offsetsHolder, array);
        row.setBlob(parameterizedBlob);

        // we must initialize offsets manually since offsetsHolder is written first to the sqlite table
        parameterizedBlob.initializeOffsets(0);

        return row;
    }

    private static void checkBlobOffsetsParamTableRows(List<BlobOffsetsParamTableRow> rows1,
            List<BlobOffsetsParamTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkBlobOffsetsParamTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkBlobOffsetsParamTableRow(BlobOffsetsParamTableRow row1, BlobOffsetsParamTableRow row2)
    {
        assertEquals(row1.getBlobId(), row2.getBlobId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getOffsetsHolder(), row2.getOffsetsHolder());
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

    private static final String TABLE_NAME = "blobOffsetsParamTable";

    private static final int    ARRAY_SIZE = 10;
    private static final int    NUM_BLOB_OFFSETS_PARAM_TABLE_ROWS = 5;
    private static final String FILE_NAME = "blob_offsets_param_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
