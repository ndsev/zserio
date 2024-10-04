package explicit_parameters.explicit_same_as_field;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;

import explicit_parameters.ExplicitParametersDb;
import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class ExplicitSameAsFieldTest
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
        final SameAsFieldTable sameAsFieldTable = database.getSameAsFieldTable();

        final List<SameAsFieldTableRow> writtenRows = new ArrayList<SameAsFieldTableRow>();
        fillSameAsFieldTableRows(writtenRows);
        sameAsFieldTable.write(writtenRows);

        final SameAsFieldTableParameterProvider parameterProvider = new SameAsFieldTableParameterProvider();
        final List<SameAsFieldTableRow> readRows = sameAsFieldTable.read(parameterProvider);
        checkSameAsFieldTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final SameAsFieldTable sameAsFieldTable = database.getSameAsFieldTable();

        final List<SameAsFieldTableRow> writtenRows = new ArrayList<SameAsFieldTableRow>();
        fillSameAsFieldTableRows(writtenRows);
        sameAsFieldTable.write(writtenRows);

        final SameAsFieldTableParameterProvider parameterProvider = new SameAsFieldTableParameterProvider();
        final String condition = "name='Name1'";
        final List<SameAsFieldTableRow> readRows = sameAsFieldTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final SameAsFieldTableRow readRow = readRows.get(0);
        checkSameAsFieldTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final SameAsFieldTable sameAsFieldTable = database.getSameAsFieldTable();

        final List<SameAsFieldTableRow> writtenRows = new ArrayList<SameAsFieldTableRow>();
        fillSameAsFieldTableRows(writtenRows);
        sameAsFieldTable.write(writtenRows);

        final int updateRowId = 3;
        final SameAsFieldTableRow updateRow = createSameAsFieldTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        sameAsFieldTable.update(updateRow, updateCondition);

        final SameAsFieldTableParameterProvider parameterProvider = new SameAsFieldTableParameterProvider();
        final List<SameAsFieldTableRow> readRows = sameAsFieldTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final SameAsFieldTableRow readRow = readRows.get(0);
        checkSameAsFieldTableRow(updateRow, readRow);
    }

    private static class SameAsFieldTableParameterProvider implements SameAsFieldTable.ParameterProvider
    {
        @Override
        public long getCount(ResultSet resultSet)
        {
            return SAME_AS_FIELD_TABLE_COUNT_EXPLICIT;
        }
    }

    private static void fillSameAsFieldTableRows(List<SameAsFieldTableRow> rows)
    {
        for (int id = 0; id < NUM_SAME_AS_FIELD_TABLE_ROWS; ++id)
        {
            rows.add(createSameAsFieldTableRow(id, "Name" + id));
        }
    }

    private static SameAsFieldTableRow createSameAsFieldTableRow(long id, String name)
    {
        final SameAsFieldTableRow row = new SameAsFieldTableRow();

        row.setId(id);
        row.setName(name);
        row.setCount(SAME_AS_FIELD_TABLE_COUNT);

        final byte[] values = new byte[SAME_AS_FIELD_TABLE_COUNT];
        for (int i = 0; i < values.length; ++i)
            values[i] = (byte)id;
        final TestBlob testBlob = new TestBlob(values.length, values);
        row.setBlob(testBlob);

        final byte[] valuesExplicit = new byte[SAME_AS_FIELD_TABLE_COUNT_EXPLICIT];
        for (int i = 0; i < valuesExplicit.length; ++i)
            valuesExplicit[i] = (byte)(id + 1);
        final TestBlob testBlobExplicit = new TestBlob(valuesExplicit.length, valuesExplicit);
        row.setBlobExplicit(testBlobExplicit);

        return row;
    }

    private static void checkSameAsFieldTableRows(
            List<SameAsFieldTableRow> rows1, List<SameAsFieldTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkSameAsFieldTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkSameAsFieldTableRow(SameAsFieldTableRow row1, SameAsFieldTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getCount(), row2.getCount());
        assertEquals(row1.getBlob(), row2.getBlob());
        assertEquals(row1.getBlobExplicit(), row2.getBlobExplicit());
    }

    private static final int NUM_SAME_AS_FIELD_TABLE_ROWS = 5;
    private static final int SAME_AS_FIELD_TABLE_COUNT = 10;
    private static final int SAME_AS_FIELD_TABLE_COUNT_EXPLICIT = 11;
    private static final String FILE_NAME = "explicit_same_as_field_test.sqlite";

    private final File file = new File(FILE_NAME);
    private ExplicitParametersDb database = null;
}
