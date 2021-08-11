package explicit_parameters.explicit_simple_param;

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

public class ExplicitSimpleParamTest
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
        final SimpleParamTable simpleParamTable = database.getSimpleParamTable();

        final List<SimpleParamTableRow> writtenRows = new ArrayList<SimpleParamTableRow>();
        fillSimpleParamTableRows(writtenRows);
        simpleParamTable.write(writtenRows);

        final SimpleParamTableParameterProvider parameterProvider = new SimpleParamTableParameterProvider();
        final List<SimpleParamTableRow> readRows = simpleParamTable.read(parameterProvider);
        checkSimpleParamTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final SimpleParamTable simpleParamTable = database.getSimpleParamTable();

        final List<SimpleParamTableRow> writtenRows = new ArrayList<SimpleParamTableRow>();
        fillSimpleParamTableRows(writtenRows);
        simpleParamTable.write(writtenRows);

        final SimpleParamTableParameterProvider parameterProvider = new SimpleParamTableParameterProvider();
        final String condition = "name='Name1'";
        final List<SimpleParamTableRow> readRows = simpleParamTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final SimpleParamTableRow readRow = readRows.get(0);
        checkSimpleParamTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final SimpleParamTable simpleParamTable = database.getSimpleParamTable();

        final List<SimpleParamTableRow> writtenRows = new ArrayList<SimpleParamTableRow>();
        fillSimpleParamTableRows(writtenRows);
        simpleParamTable.write(writtenRows);

        final int updateRowId = 3;
        final SimpleParamTableRow updateRow = createSimpleParamTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        simpleParamTable.update(updateRow, updateCondition);

        final SimpleParamTableParameterProvider parameterProvider = new SimpleParamTableParameterProvider();
        final List<SimpleParamTableRow> readRows = simpleParamTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final SimpleParamTableRow readRow = readRows.get(0);
        checkSimpleParamTableRow(updateRow, readRow);
    }

    private static class SimpleParamTableParameterProvider implements SimpleParamTable.ParameterProvider
    {
        @Override
        public long getCount1(ResultSet resultSet)
        {
            return SIMPLE_PARAM_TABLE_COUNT1;
        }

        @Override
        public long getCount2(ResultSet resultSet)
        {
            return SIMPLE_PARAM_TABLE_COUNT2;
        }
    }

    private static void fillSimpleParamTableRows(List<SimpleParamTableRow> rows)
    {
        for (int id = 0; id < NUM_SIMPLE_PARAM_TABLE_ROWS; ++id)
        {
            rows.add(createSimpleParamTableRow(id, "Name" + id));
        }
    }

    private static SimpleParamTableRow createSimpleParamTableRow(long id, String name)
    {
        final SimpleParamTableRow row = new SimpleParamTableRow();

        row.setId(id);
        row.setName(name);

        final byte[] values1 = new byte[SIMPLE_PARAM_TABLE_COUNT1];
        for (int i = 0; i < values1.length; ++i)
            values1[i] = (byte)id;
        final TestBlob testBlob1 = new TestBlob(values1.length, values1);
        row.setBlob1(testBlob1);

        final byte[] values2 = new byte[SIMPLE_PARAM_TABLE_COUNT2];
        for (int i = 0; i < values2.length; ++i)
            values2[i] = (byte)(id + 1);
        final TestBlob testBlob2 = new TestBlob(values2.length, values2);
        row.setBlob2(testBlob2);

        final byte[] values3 = new byte[SIMPLE_PARAM_TABLE_COUNT1];
        for (int i = 0; i < values3.length; ++i)
            values3[i] = (byte)(id + 2);
        final TestBlob testBlob3 = new TestBlob(values3.length, values3);
        row.setBlob3(testBlob3);

        return row;
    }

    private static void checkSimpleParamTableRows(List<SimpleParamTableRow> rows1,
            List<SimpleParamTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkSimpleParamTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkSimpleParamTableRow(SimpleParamTableRow row1, SimpleParamTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getBlob1(), row2.getBlob1());
        assertEquals(row1.getBlob2(), row2.getBlob2());
        assertEquals(row1.getBlob3(), row2.getBlob3());

        // check reused explicit count1 parameter
        assertEquals(row2.getBlob1().getCount(), row2.getBlob3().getCount());
    }

    private static final int NUM_SIMPLE_PARAM_TABLE_ROWS = 5;
    private static final int SIMPLE_PARAM_TABLE_COUNT1 = 10;
    private static final int SIMPLE_PARAM_TABLE_COUNT2 = 11;
    private static final String FILE_NAME = "explicit_simple_param_test.sqlite";

    private final File file = new File(FILE_NAME);
    private ExplicitParametersDb database = null;
}
