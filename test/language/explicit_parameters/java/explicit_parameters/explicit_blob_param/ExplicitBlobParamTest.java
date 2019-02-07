package explicit_parameters.explicit_blob_param;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
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

import explicit_parameters.ExplicitParametersDb;

import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedByteArray;
import zserio.runtime.array.UnsignedShortArray;

public class ExplicitBlobParamTest
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
        database = new ExplicitParametersDb(file.toString());
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
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final BlobParamTable blobParamTable = database.getBlobParamTable();

        final List<BlobParamTableRow> writtenRows = new ArrayList<BlobParamTableRow>();
        fillBlobParamTableRows(writtenRows);
        blobParamTable.write(writtenRows);

        final BlobParamTableParameterProvider parameterProvider = new BlobParamTableParameterProvider();
        final List<BlobParamTableRow> readRows = blobParamTable.read(parameterProvider);
        checkBlobParamTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final BlobParamTable blobParamTable = database.getBlobParamTable();

        final List<BlobParamTableRow> writtenRows = new ArrayList<BlobParamTableRow>();
        fillBlobParamTableRows(writtenRows);
        blobParamTable.write(writtenRows);

        final BlobParamTableParameterProvider parameterProvider = new BlobParamTableParameterProvider();
        final String condition = "name='Name1'";
        final List<BlobParamTableRow> readRows = blobParamTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final BlobParamTableRow readRow = readRows.get(0);
        checkBlobParamTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final BlobParamTable blobParamTable = database.getBlobParamTable();

        final List<BlobParamTableRow> writtenRows = new ArrayList<BlobParamTableRow>();
        fillBlobParamTableRows(writtenRows);
        blobParamTable.write(writtenRows);

        final int updateRowId = 3;
        final BlobParamTableRow updateRow = createBlobParamTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        blobParamTable.update(updateRow, updateCondition);

        final BlobParamTableParameterProvider parameterProvider = new BlobParamTableParameterProvider();
        final List<BlobParamTableRow> readRows = blobParamTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final BlobParamTableRow readRow = readRows.get(0);
        checkBlobParamTableRow(updateRow, readRow);
    }

    private static class BlobParamTableParameterProvider implements BlobParamTable.ParameterProvider
    {
        @Override
        public Header getHeader(ResultSet resultSet)
        {
            return header;
        }

        @Override
        public Header getBlob(ResultSet resultSet)
        {
            return blob;
        }

        Header header = new Header(BLOB_PARAM_TABLE_HEADER_COUNT);
        Header blob = new Header(BLOB_PARAM_TABLE_BLOB_COUNT);
    }

    private static void fillBlobParamTableRows(List<BlobParamTableRow> rows)
    {
        for (int id = 0; id < NUM_BLOB_PARAM_TABLE_ROWS; ++id)
        {
            rows.add(createBlobParamTableRow(id, "Name" + id));
        }
    }

    private static BlobParamTableRow createBlobParamTableRow(long id, String name)
    {
        final BlobParamTableRow row = new BlobParamTableRow();

        row.setId(id);
        row.setName(name);

        final Header header = new Header(BLOB_PARAM_TABLE_HEADER_COUNT);

        final UnsignedByteArray values1 = new UnsignedByteArray(BLOB_PARAM_TABLE_HEADER_COUNT);
        for (int i = 0; i < values1.length(); ++i)
            values1.setElementAt((short)id, i);
        final TestBlob testBlob1 = new TestBlob(header, values1);
        row.setBlob1(testBlob1);

        final Header blob = new Header(BLOB_PARAM_TABLE_BLOB_COUNT);
        final UnsignedByteArray values2 = new UnsignedByteArray(BLOB_PARAM_TABLE_BLOB_COUNT);
        for (int i = 0; i < values2.length(); ++i)
            values2.setElementAt((short)(id + 1), i);
        final TestBlob testBlob2 = new TestBlob(blob, values2);
        row.setBlob2(testBlob2);

        final UnsignedByteArray values3 = new UnsignedByteArray(BLOB_PARAM_TABLE_HEADER_COUNT);
        for (int i = 0; i < values3.length(); ++i)
            values3.setElementAt((short)(id + 2), i);
        final TestBlob testBlob3 = new TestBlob(header, values3);
        row.setBlob3(testBlob3);

        return row;
    }

    private static void checkBlobParamTableRows(List<BlobParamTableRow> rows1, List<BlobParamTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkBlobParamTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkBlobParamTableRow(BlobParamTableRow row1, BlobParamTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getBlob1(), row2.getBlob1());
        assertEquals(row1.getBlob2(), row2.getBlob2());
        assertEquals(row1.getBlob3(), row2.getBlob3());

        // check reused explicit header parameter
        assertEquals(row2.getBlob1().getBlob(), row2.getBlob3().getBlob());
    }

    private static final int NUM_BLOB_PARAM_TABLE_ROWS = 5;
    private static final int BLOB_PARAM_TABLE_HEADER_COUNT = 10;
    private static final int BLOB_PARAM_TABLE_BLOB_COUNT = 11;
    private static final String FILE_NAME = "explicit_blob_param_test.sqlite";

    private final File file = new File(FILE_NAME);
    private ExplicitParametersDb database = null;
}
