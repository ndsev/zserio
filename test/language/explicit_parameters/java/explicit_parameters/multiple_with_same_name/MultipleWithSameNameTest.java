package explicit_parameters.multiple_with_same_name;

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

public class MultipleWithSameNameTest
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
        final MultipleWithSameNameTable multipleWithSameNameTable = database.getMultipleWithSameNameTable();

        final List<MultipleWithSameNameTableRow> writtenRows = new ArrayList<MultipleWithSameNameTableRow>();
        fillMultipleWithSameNameTableRows(writtenRows);
        multipleWithSameNameTable.write(writtenRows);

        final MultipleWithSameNameTableParameterProvider parameterProvider =
                new MultipleWithSameNameTableParameterProvider();
        final List<MultipleWithSameNameTableRow> readRows = multipleWithSameNameTable.read(parameterProvider);
        checkMultipleParamsTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final MultipleWithSameNameTable multipleWithSameNameTable = database.getMultipleWithSameNameTable();

        final List<MultipleWithSameNameTableRow> writtenRows = new ArrayList<MultipleWithSameNameTableRow>();
        fillMultipleWithSameNameTableRows(writtenRows);
        multipleWithSameNameTable.write(writtenRows);

        final MultipleWithSameNameTableParameterProvider parameterProvider = new
                MultipleWithSameNameTableParameterProvider();
        final String condition = "name='Name1'";
        final List<MultipleWithSameNameTableRow> readRows =
                multipleWithSameNameTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final MultipleWithSameNameTableRow readRow = readRows.get(0);
        checkMultipleParamsTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final MultipleWithSameNameTable multipleWithSameNameTable = database.getMultipleWithSameNameTable();

        final List<MultipleWithSameNameTableRow> writtenRows = new ArrayList<MultipleWithSameNameTableRow>();
        fillMultipleWithSameNameTableRows(writtenRows);
        multipleWithSameNameTable.write(writtenRows);

        final int updateRowId = 3;
        final MultipleWithSameNameTableRow updateRow =
                createMultipleWithSameNameTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        multipleWithSameNameTable.update(updateRow, updateCondition);

        final MultipleWithSameNameTableParameterProvider parameterProvider =
                new MultipleWithSameNameTableParameterProvider();
        final List<MultipleWithSameNameTableRow> readRows =
                multipleWithSameNameTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final MultipleWithSameNameTableRow readRow = readRows.get(0);
        checkMultipleParamsTableRow(updateRow, readRow);
    }

    private static class MultipleWithSameNameTableParameterProvider
            implements MultipleWithSameNameTable.ParameterProvider
    {
        @Override
        public long getParam1(ResultSet resultSet)
        {
            return PARAM1;
        }

        @Override
        public float getParam2(ResultSet resultSet)
        {
            return PARAM2;
        }
    }

    private static void fillMultipleWithSameNameTableRows(List<MultipleWithSameNameTableRow> rows)
    {
        for (int id = 0; id < NUM_ROWS; ++id)
        {
            rows.add(createMultipleWithSameNameTableRow(id, "Name" + id));
        }
    }

    private static MultipleWithSameNameTableRow createMultipleWithSameNameTableRow(long id, String name)
    {
        final MultipleWithSameNameTableRow row = new MultipleWithSameNameTableRow();

        row.setId(id);
        row.setName(name);

        final Parameterized1 parameterized1 = new Parameterized1(PARAM1, id * 10);
        row.setParameterized1(parameterized1);

        final Parameterized2 parameterized2 = new Parameterized2(PARAM2, id * 1.5f);
        row.setParameterized2(parameterized2);

        return row;
    }

    private static void checkMultipleParamsTableRows(List<MultipleWithSameNameTableRow> rows1,
            List<MultipleWithSameNameTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkMultipleParamsTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkMultipleParamsTableRow(MultipleWithSameNameTableRow row1,
            MultipleWithSameNameTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getParameterized1(), row2.getParameterized1());
        assertEquals(row1.getParameterized2(), row2.getParameterized2());
    }

    private static final int NUM_ROWS = 5;
    private static final long PARAM1 = 100;
    private static final float PARAM2 = 10.0f;
    private static final String FILE_NAME = "multiple_with_same_name_test.sqlite";

    private final File file = new File(FILE_NAME);
    private ExplicitParametersDb database = null;
}
