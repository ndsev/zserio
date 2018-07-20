package explicit_parameters;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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

import zserio.runtime.ZserioError;
import zserio.runtime.SqlDatabase.Mode;
import zserio.runtime.array.UnsignedByteArray;

public class ExplicitParametersTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws IOException, URISyntaxException, SQLException
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
    public void readWithoutCondition() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final TestTable testTable = database.getTestTable();

        final List<TestTableRow> writtenRows = new ArrayList<TestTableRow>();
        fillTestTableRows(writtenRows);
        testTable.write(writtenRows);

        final TestTableParameterProvider parameterProvider = new TestTableParameterProvider();
        final List<TestTableRow> readRows = testTable.read(parameterProvider);
        checkTestTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final TestTable testTable = database.getTestTable();

        final List<TestTableRow> writtenRows = new ArrayList<TestTableRow>();
        fillTestTableRows(writtenRows);
        testTable.write(writtenRows);

        final TestTableParameterProvider parameterProvider = new TestTableParameterProvider();
        final String condition = "name='Name1'";
        final List<TestTableRow> readRows = testTable.read(parameterProvider, condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final TestTableRow readRow = readRows.get(0);
        checkTestTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final TestTable testTable = database.getTestTable();

        final List<TestTableRow> writtenRows = new ArrayList<TestTableRow>();
        fillTestTableRows(writtenRows);
        testTable.write(writtenRows);

        final int updateRowId = 3;
        final TestTableRow updateRow = createTestTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "id=" + updateRowId;
        testTable.update(updateRow, updateCondition);

        final TestTableParameterProvider parameterProvider = new TestTableParameterProvider();
        final List<TestTableRow> readRows = testTable.read(parameterProvider, updateCondition);
        assertEquals(1, readRows.size());

        final TestTableRow readRow = readRows.get(0);
        checkTestTableRow(updateRow, readRow);
    }

    private static class TestTableParameterProvider implements IParameterProvider
    {
        @Override
        public long getTestTable_count1(ResultSet resultSet)
        {
            return TEST_TABLE_COUNT1;
        }

        @Override
        public long getTestTable_count2(ResultSet resultSet)
        {
            return TEST_TABLE_COUNT2;
        }
    }

    private static void fillTestTableRows(List<TestTableRow> rows)
    {
        for (int id = 0; id < NUM_TEST_TABLE_ROWS; ++id)
        {
            rows.add(createTestTableRow(id, "Name" + id));
        }
    }

    private static TestTableRow createTestTableRow(long id, String name)
    {
        final TestTableRow row = new TestTableRow();

        row.setId(id);
        row.setName(name);

        final UnsignedByteArray values1 = new UnsignedByteArray(TEST_TABLE_COUNT1);
        for (int i = 0; i < values1.length(); ++i)
            values1.setElementAt((short)id, i);
        final TestBlob testBlob1 = new TestBlob(values1.length(), values1);
        row.setBlob1(testBlob1);

        final UnsignedByteArray values2 = new UnsignedByteArray(TEST_TABLE_COUNT2);
        for (int i = 0; i < values2.length(); ++i)
            values2.setElementAt((short)(id + 1), i);
        final TestBlob testBlob2 = new TestBlob(values2.length(), values2);
        row.setBlob2(testBlob2);

        return row;
    }

    private static void checkTestTableRows(List<TestTableRow> rows1, List<TestTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkTestTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkTestTableRow(TestTableRow row1, TestTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getBlob1(), row2.getBlob1());
        assertEquals(row1.getBlob2(), row2.getBlob2());
    }

    private static final int    NUM_TEST_TABLE_ROWS = 5;
    private static final int    TEST_TABLE_COUNT1 = 10;
    private static final int    TEST_TABLE_COUNT2 = 11;
    private static final String FILE_NAME = "explicit_parameters_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
