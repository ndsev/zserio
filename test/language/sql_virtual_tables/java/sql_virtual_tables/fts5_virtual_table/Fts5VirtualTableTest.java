package sql_virtual_tables.fts5_virtual_table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sql_virtual_tables.fts5_virtual_table.Fts5TestDb;
import test_utils.FileUtil;
import test_utils.JdbcUtil;

import zserio.runtime.ZserioError;

public class Fts5VirtualTableTest
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
        database = new Fts5TestDb(file.toString());
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

        final Fts5VirtualTable testTable = database.getFts5VirtualTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final Fts5VirtualTable testTable = database.getFts5VirtualTable();

        final List<Fts5VirtualTableRow> writtenRows = new ArrayList<Fts5VirtualTableRow>();
        fillFts5VirtualTableRows(writtenRows);
        testTable.write(writtenRows);

        final List<Fts5VirtualTableRow> readRows = testTable.read();
        checkFts5VirtualTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final Fts5VirtualTable testTable = database.getFts5VirtualTable();

        final List<Fts5VirtualTableRow> writtenRows = new ArrayList<Fts5VirtualTableRow>();
        fillFts5VirtualTableRows(writtenRows);
        testTable.write(writtenRows);

        final String condition = "body='Body1'";
        final List<Fts5VirtualTableRow> readRows = testTable.read(condition);

        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final Fts5VirtualTableRow readRow = readRows.get(0);
        checkFts5VirtualTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final Fts5VirtualTable testTable = database.getFts5VirtualTable();

        final List<Fts5VirtualTableRow> writtenRows = new ArrayList<Fts5VirtualTableRow>();
        fillFts5VirtualTableRows(writtenRows);
        testTable.write(writtenRows);

        final String updateTitle = "Title3";
        final Fts5VirtualTableRow updateRow = createFts5VirtualTableRow(updateTitle, "UpdatedBody");
        final String updateCondition = "title='" + updateTitle + "'";
        testTable.update(updateRow, updateCondition);

        final List<Fts5VirtualTableRow> readRows = testTable.read(updateCondition);
        assertEquals(1, readRows.size());

        final Fts5VirtualTableRow readRow = readRows.get(0);
        checkFts5VirtualTableRow(updateRow, readRow);
    }

    private static void fillFts5VirtualTableRows(List<Fts5VirtualTableRow> rows)
    {
        for (int id = 0; id < NUM_VIRTUAL_TABLE_ROWS; ++id)
        {
            rows.add(createFts5VirtualTableRow("Title" + id, "Body" + id));
        }
    }

    private static Fts5VirtualTableRow createFts5VirtualTableRow(String title, String body)
    {
        final Fts5VirtualTableRow row = new Fts5VirtualTableRow();
        row.setTitle(title);
        row.setBody(body);

        return row;
    }

    private static void checkFts5VirtualTableRows(List<Fts5VirtualTableRow> rows1,
            List<Fts5VirtualTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkFts5VirtualTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkFts5VirtualTableRow(Fts5VirtualTableRow row1, Fts5VirtualTableRow row2)
    {
        assertEquals(row1.getTitle(), row2.getTitle());
        assertEquals(row1.getBody(), row2.getBody());
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

    private static final String TABLE_NAME = "fts5VirtualTable";

    private static final int    NUM_VIRTUAL_TABLE_ROWS = 5;

    private static final String FILE_NAME = "fts5_virtual_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private Fts5TestDb database = null;
}
