package sql_tables.multiple_pk_table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
import zserio.runtime.SqlDatabase.Mode;

public class MultiplePkTableTest
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
    public void deleteTable() throws SQLException
    {
        assertTrue(isTableInDb());

        final MultiplePkTable testTable = database.getMultiplePkTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final MultiplePkTable testTable = database.getMultiplePkTable();

        final List<MultiplePkTableRow> writtenRows = new ArrayList<MultiplePkTableRow>();
        fillMultiplePkTableRows(writtenRows);
        testTable.write(writtenRows);

        final List<MultiplePkTableRow> readRows = testTable.read();
        checkMultiplePkTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final MultiplePkTable testTable = database.getMultiplePkTable();

        final List<MultiplePkTableRow> writtenRows = new ArrayList<MultiplePkTableRow>();
        fillMultiplePkTableRows(writtenRows);
        testTable.write(writtenRows);

        final String condition = "name='Name1'";
        final List<MultiplePkTableRow> readRows = testTable.read(condition);
        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final MultiplePkTableRow readRow = readRows.get(0);
        checkMultiplePkTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final MultiplePkTable testTable = database.getMultiplePkTable();

        final List<MultiplePkTableRow> writtenRows = new ArrayList<MultiplePkTableRow>();
        fillMultiplePkTableRows(writtenRows);
        testTable.write(writtenRows);

        final int updateRowId = 3;
        final MultiplePkTableRow updateRow = createMultiplePkTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "blobId=" + updateRowId;
        testTable.update(updateRow, updateCondition);

        final List<MultiplePkTableRow> readRows = testTable.read(updateCondition);
        assertEquals(1, readRows.size());

        final MultiplePkTableRow readRow = readRows.get(0);
        checkMultiplePkTableRow(updateRow, readRow);
    }

    private static void fillMultiplePkTableRows(List<MultiplePkTableRow> rows)
    {
        for (int blobId = 0; blobId < NUM_MULTIPLE_PK_TABLE_ROWS; ++blobId)
        {
            rows.add(createMultiplePkTableRow(blobId, "Name" + blobId));
        }
    }

    private static MultiplePkTableRow createMultiplePkTableRow(int blobId, String name)
    {
        final MultiplePkTableRow row = new MultiplePkTableRow();
        row.setBlobId(blobId);
        row.setAge(10);
        row.setName(name);

        return row;
    }

    private static void checkMultiplePkTableRows(List<MultiplePkTableRow> rows1, List<MultiplePkTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkMultiplePkTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkMultiplePkTableRow(MultiplePkTableRow row1, MultiplePkTableRow row2)
    {
        assertEquals(row1.getBlobId(), row2.getBlobId());
        assertEquals(row1.getAge(), row2.getAge());
        assertEquals(row1.getName(), row2.getName());
    }

    private boolean isTableInDb() throws SQLException
    {
        // check if database does contain table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME +
                "'";

        final PreparedStatement statement = database.prepareStatement(sqlQuery);
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

    private static final String TABLE_NAME = "multiplePkTable";

    private static final int    NUM_MULTIPLE_PK_TABLE_ROWS = 5;
    private static final String FILE_NAME = "multiple_pk_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
