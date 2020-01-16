package explicit_parameters.explicit_bitmask_param;

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

public class ExplicitBitmaskParamTest
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
        final BitmaskParamTable bitmaskParamTable = database.getBitmaskParamTable();

        final List<BitmaskParamTableRow> writtenRows = new ArrayList<BitmaskParamTableRow>();
        fillBitmaskParamTableRows(writtenRows);
        bitmaskParamTable.write(writtenRows);

        final BitmaskParamTableParameterProvider parameterProvider = new BitmaskParamTableParameterProvider();
        final List<BitmaskParamTableRow> readRows = bitmaskParamTable.read(parameterProvider);
        checkBitmaskParamTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final BitmaskParamTable bitmaskParamTable = database.getBitmaskParamTable();

        final List<BitmaskParamTableRow> writtenRows = new ArrayList<BitmaskParamTableRow>();
        fillBitmaskParamTableRows(writtenRows);
        bitmaskParamTable.write(writtenRows);

        final BitmaskParamTableParameterProvider parameterProvider = new BitmaskParamTableParameterProvider();
        final String condition = "name='Name1'";
        final List<BitmaskParamTableRow> readRows = bitmaskParamTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final BitmaskParamTableRow readRow = readRows.get(0);
        checkBitmaskParamTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final BitmaskParamTable bitmaskParamTable = database.getBitmaskParamTable();

        final List<BitmaskParamTableRow> writtenRows = new ArrayList<BitmaskParamTableRow>();
        fillBitmaskParamTableRows(writtenRows);
        bitmaskParamTable.write(writtenRows);

        final int updateRowId = 3;
        final BitmaskParamTableRow updateRow = createBitmaskParamTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        bitmaskParamTable.update(updateRow, updateCondition);

        final BitmaskParamTableParameterProvider parameterProvider = new BitmaskParamTableParameterProvider();
        final List<BitmaskParamTableRow> readRows = bitmaskParamTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final BitmaskParamTableRow readRow = readRows.get(0);
        checkBitmaskParamTableRow(updateRow, readRow);
    }

    private static class BitmaskParamTableParameterProvider implements BitmaskParamTable.ParameterProvider
    {
        @Override
        public TestBitmask getCount1(ResultSet resultSet)
        {
            return BITMASK_PARAM_TABLE_COUNT1;
        }

        @Override
        public TestBitmask getCount2(ResultSet resultSet)
        {
            return BITMASK_PARAM_TABLE_COUNT2;
        }
    }

    private static void fillBitmaskParamTableRows(List<BitmaskParamTableRow> rows)
    {
        for (int id = 0; id < NUM_BITMASK_PARAM_TABLE_ROWS; ++id)
        {
            rows.add(createBitmaskParamTableRow(id, "Name" + id));
        }
    }

    private static BitmaskParamTableRow createBitmaskParamTableRow(long id, String name)
    {
        final BitmaskParamTableRow row = new BitmaskParamTableRow();

        row.setId(id);
        row.setName(name);

        final UnsignedByteArray values1 = new UnsignedByteArray(BITMASK_PARAM_TABLE_COUNT1.getValue());
        for (int i = 0; i < values1.length(); ++i)
            values1.setElementAt((short)id, i);
        final TestBlob testBlob1 = new TestBlob(BITMASK_PARAM_TABLE_COUNT1, values1);
        row.setBlob1(testBlob1);

        final UnsignedByteArray values2 = new UnsignedByteArray(BITMASK_PARAM_TABLE_COUNT2.getValue());
        for (int i = 0; i < values2.length(); ++i)
            values2.setElementAt((short)(id + 1), i);
        final TestBlob testBlob2 = new TestBlob(BITMASK_PARAM_TABLE_COUNT2, values2);
        row.setBlob2(testBlob2);

        final UnsignedByteArray values3 = new UnsignedByteArray(BITMASK_PARAM_TABLE_COUNT1.getValue());
        for (int i = 0; i < values3.length(); ++i)
            values3.setElementAt((short)(id + 2), i);
        final TestBlob testBlob3 = new TestBlob(BITMASK_PARAM_TABLE_COUNT1, values3);
        row.setBlob3(testBlob3);

        return row;
    }

    private static void checkBitmaskParamTableRows(List<BitmaskParamTableRow> rows1,
            List<BitmaskParamTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkBitmaskParamTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkBitmaskParamTableRow(BitmaskParamTableRow row1, BitmaskParamTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getBlob1(), row2.getBlob1());
        assertEquals(row1.getBlob2(), row2.getBlob2());
        assertEquals(row1.getBlob3(), row2.getBlob3());

        // check reused explicit count1 parameter
        assertEquals(row2.getBlob1().getCount(), row2.getBlob3().getCount());
    }

    private static final int NUM_BITMASK_PARAM_TABLE_ROWS = 5;
    private static final TestBitmask BITMASK_PARAM_TABLE_COUNT1 = TestBitmask.Values.TEN;
    private static final TestBitmask BITMASK_PARAM_TABLE_COUNT2 = TestBitmask.Values.ELEVEN;
    private static final String FILE_NAME = "explicit_bitmask_param_test.sqlite";

    private final File file = new File(FILE_NAME);
    private ExplicitParametersDb database = null;
}
