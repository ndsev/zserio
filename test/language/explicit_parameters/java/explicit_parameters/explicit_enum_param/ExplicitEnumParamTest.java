package explicit_parameters.explicit_enum_param;

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

public class ExplicitEnumParamTest
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
        final EnumParamTable enumParamTable = database.getEnumParamTable();

        final List<EnumParamTableRow> writtenRows = new ArrayList<EnumParamTableRow>();
        fillEnumParamTableRows(writtenRows);
        enumParamTable.write(writtenRows);

        final EnumParamTableParameterProvider parameterProvider = new EnumParamTableParameterProvider();
        final List<EnumParamTableRow> readRows = enumParamTable.read(parameterProvider);
        checkEnumParamTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final EnumParamTable enumParamTable = database.getEnumParamTable();

        final List<EnumParamTableRow> writtenRows = new ArrayList<EnumParamTableRow>();
        fillEnumParamTableRows(writtenRows);
        enumParamTable.write(writtenRows);

        final EnumParamTableParameterProvider parameterProvider = new EnumParamTableParameterProvider();
        final String condition = "name='Name1'";
        final List<EnumParamTableRow> readRows = enumParamTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final EnumParamTableRow readRow = readRows.get(0);
        checkEnumParamTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final EnumParamTable enumParamTable = database.getEnumParamTable();

        final List<EnumParamTableRow> writtenRows = new ArrayList<EnumParamTableRow>();
        fillEnumParamTableRows(writtenRows);
        enumParamTable.write(writtenRows);

        final int updateRowId = 3;
        final EnumParamTableRow updateRow = createEnumParamTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        enumParamTable.update(updateRow, updateCondition);

        final EnumParamTableParameterProvider parameterProvider = new EnumParamTableParameterProvider();
        final List<EnumParamTableRow> readRows = enumParamTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final EnumParamTableRow readRow = readRows.get(0);
        checkEnumParamTableRow(updateRow, readRow);
    }

    private static class EnumParamTableParameterProvider implements EnumParamTable.ParameterProvider
    {
        @Override
        public TestEnum getCount1(ResultSet resultSet)
        {
            return ENUM_PARAM_TABLE_COUNT1;
        }

        @Override
        public TestEnum getCount2(ResultSet resultSet)
        {
            return ENUM_PARAM_TABLE_COUNT2;
        }
    }

    private static void fillEnumParamTableRows(List<EnumParamTableRow> rows)
    {
        for (int id = 0; id < NUM_ENUM_PARAM_TABLE_ROWS; ++id)
        {
            rows.add(createEnumParamTableRow(id, "Name" + id));
        }
    }

    private static EnumParamTableRow createEnumParamTableRow(long id, String name)
    {
        final EnumParamTableRow row = new EnumParamTableRow();

        row.setId(id);
        row.setName(name);

        final byte[] values1 = new byte[ENUM_PARAM_TABLE_COUNT1.getValue()];
        for (int i = 0; i < values1.length; ++i)
            values1[i] = (byte)id;
        final TestBlob testBlob1 = new TestBlob(ENUM_PARAM_TABLE_COUNT1, values1);
        row.setBlob1(testBlob1);

        final byte[] values2 = new byte[ENUM_PARAM_TABLE_COUNT2.getValue()];
        for (int i = 0; i < values2.length; ++i)
            values2[i] = (byte)(id + 1);
        final TestBlob testBlob2 = new TestBlob(ENUM_PARAM_TABLE_COUNT2, values2);
        row.setBlob2(testBlob2);

        final byte[] values3 = new byte[ENUM_PARAM_TABLE_COUNT1.getValue()];
        for (int i = 0; i < values3.length; ++i)
            values3[i] = (byte)(id + 2);
        final TestBlob testBlob3 = new TestBlob(ENUM_PARAM_TABLE_COUNT1, values3);
        row.setBlob3(testBlob3);

        return row;
    }

    private static void checkEnumParamTableRows(List<EnumParamTableRow> rows1, List<EnumParamTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkEnumParamTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkEnumParamTableRow(EnumParamTableRow row1, EnumParamTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getBlob1(), row2.getBlob1());
        assertEquals(row1.getBlob2(), row2.getBlob2());
        assertEquals(row1.getBlob3(), row2.getBlob3());

        // check reused explicit count1 parameter
        assertEquals(row2.getBlob1().getCount(), row2.getBlob3().getCount());
    }

    private static final int NUM_ENUM_PARAM_TABLE_ROWS = 5;
    private static final TestEnum ENUM_PARAM_TABLE_COUNT1 = TestEnum.TEN;
    private static final TestEnum ENUM_PARAM_TABLE_COUNT2 = TestEnum.ELEVEN;
    private static final String FILE_NAME = "explicit_enum_param_test.sqlite";

    private final File file = new File(FILE_NAME);
    private ExplicitParametersDb database = null;
}
