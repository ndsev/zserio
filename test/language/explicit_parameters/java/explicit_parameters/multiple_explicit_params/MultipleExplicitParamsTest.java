package explicit_parameters.multiple_explicit_params;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import explicit_parameters.ExplicitParametersDb;

import zserio.runtime.ZserioError;

public class MultipleExplicitParamsTest
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
        database = new ExplicitParametersDb(file.toString());
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
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final MultipleParamsTable multipleParamsTable = database.getMultipleParamsTable();

        final List<MultipleParamsTableRow> writtenRows = new ArrayList<MultipleParamsTableRow>();
        fillMultipleParamsTableRows(writtenRows);
        multipleParamsTable.write(writtenRows);

        final MultipleParamsTableParameterProvider parameterProvider =
                new MultipleParamsTableParameterProvider();
        final List<MultipleParamsTableRow> readRows = multipleParamsTable.read(parameterProvider);
        checkMultipleParamsTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final MultipleParamsTable multipleParamsTable = database.getMultipleParamsTable();

        final List<MultipleParamsTableRow> writtenRows = new ArrayList<MultipleParamsTableRow>();
        fillMultipleParamsTableRows(writtenRows);
        multipleParamsTable.write(writtenRows);

        final MultipleParamsTableParameterProvider parameterProvider =
                new MultipleParamsTableParameterProvider();
        final String condition = "name='Name1'";
        final List<MultipleParamsTableRow> readRows = multipleParamsTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final MultipleParamsTableRow readRow = readRows.get(0);
        checkMultipleParamsTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final MultipleParamsTable multipleParamsTable = database.getMultipleParamsTable();

        final List<MultipleParamsTableRow> writtenRows = new ArrayList<MultipleParamsTableRow>();
        fillMultipleParamsTableRows(writtenRows);
        multipleParamsTable.write(writtenRows);

        final int updateRowId = 3;
        final MultipleParamsTableRow updateRow = createMultipleParamsTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        multipleParamsTable.update(updateRow, updateCondition);

        final MultipleParamsTableParameterProvider parameterProvider =
                new MultipleParamsTableParameterProvider();
        final List<MultipleParamsTableRow> readRows =
                multipleParamsTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final MultipleParamsTableRow readRow = readRows.get(0);
        checkMultipleParamsTableRow(updateRow, readRow);
    }

    private static class MultipleParamsTableParameterProvider implements MultipleParamsTable.ParameterProvider
    {
        @Override
        public long getCount1(ResultSet resultSet)
        {
            return MULTIPLE_PARAMS_TABLE_COUNT1;
        }

        @Override
        public long getCount2(ResultSet resultSet)
        {
            return MULTIPLE_PARAMS_TABLE_COUNT2;
        }

        @Override
        public long getCount(ResultSet resultSet)
        {
            return MULTIPLE_PARAMS_TABLE_COUNT;
        }
    }

    private static void fillMultipleParamsTableRows(List<MultipleParamsTableRow> rows)
    {
        for (int id = 0; id < NUM_MULTIPLE_PARAMS_TABLE_ROWS; ++id)
        {
            rows.add(createMultipleParamsTableRow(id, "Name" + id));
        }
    }

    private static MultipleParamsTableRow createMultipleParamsTableRow(long id, String name)
    {
        final MultipleParamsTableRow row = new MultipleParamsTableRow();

        row.setId(id);
        row.setName(name);

        {
            final short[] values8 = new short[MULTIPLE_PARAMS_TABLE_COUNT1];
            final int[] values16 = new int[MULTIPLE_PARAMS_TABLE_COUNT2];
            for (int i = 0; i < values8.length; ++i)
                values8[i] = (short)id;
            for (int i = 0; i < values16.length; ++i)
                values16[i] = (int)id;
            final TestBlob testBlob1 = new TestBlob(values8.length, values16.length, values8, values16);
            row.setBlob1(testBlob1);
        }

        {
            final short[] values8 = new short[MULTIPLE_PARAMS_TABLE_COUNT];
            final int[] values16 = new int[MULTIPLE_PARAMS_TABLE_COUNT];
            for (int i = 0; i < MULTIPLE_PARAMS_TABLE_COUNT; ++i)
            {
                values8[i] = (short)(id + 1);
                values16[i] = (int)(id + 1);
            }
            final TestBlob testBlob2 = new TestBlob(values8.length, values16.length, values8, values16);
            row.setBlob2(testBlob2);
        }

        {
            final short[] values8 = new short[MULTIPLE_PARAMS_TABLE_COUNT1];
            final int[] values16 = new int[MULTIPLE_PARAMS_TABLE_COUNT1];
            for (int i = 0; i < MULTIPLE_PARAMS_TABLE_COUNT1; ++i)
            {
                values8[i] = (short)(id + 2);
                values16[i] = (int)(id + 2);
            }
            final TestBlob testBlob3 = new TestBlob(values8.length, values16.length, values8, values16);
            row.setBlob3(testBlob3);
        }

        return row;
    }

    private static void checkMultipleParamsTableRows(List<MultipleParamsTableRow> rows1,
            List<MultipleParamsTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkMultipleParamsTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkMultipleParamsTableRow(MultipleParamsTableRow row1, MultipleParamsTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getBlob1(), row2.getBlob1());
        assertEquals(row1.getBlob2(), row2.getBlob2());
        assertEquals(row1.getBlob3(), row2.getBlob3());

        // check reused explicit count1 parameter
        assertEquals(row2.getBlob1().getCount8(), row2.getBlob3().getCount8());
        assertEquals(row2.getBlob1().getCount8(), row2.getBlob3().getCount16());
    }

    private static final int NUM_MULTIPLE_PARAMS_TABLE_ROWS = 5;
    private static final int MULTIPLE_PARAMS_TABLE_COUNT1 = 10;
    private static final int MULTIPLE_PARAMS_TABLE_COUNT2 = 11;
    private static final int MULTIPLE_PARAMS_TABLE_COUNT = 12;
    private static final String FILE_NAME = "multiple_explicit_params_test.sqlite";

    private final File file = new File(FILE_NAME);
    private ExplicitParametersDb database = null;
}
