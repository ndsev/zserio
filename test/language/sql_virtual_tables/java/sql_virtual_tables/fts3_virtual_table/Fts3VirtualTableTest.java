package sql_virtual_tables.fts3_virtual_table;

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

import sql_virtual_tables.fts3_virtual_table.Fts3TestDb;
import test_utils.FileUtil;
import test_utils.JdbcUtil;

import zserio.runtime.ZserioError;
import zserio.runtime.SqlDatabase.Mode;

public class Fts3VirtualTableTest
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
        database = new Fts3TestDb(file.toString());
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

        final Fts3VirtualTable testTable = database.getFts3VirtualTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final Fts3VirtualTable testTable = database.getFts3VirtualTable();

        final List<Fts3VirtualTableRow> writtenRows = new ArrayList<Fts3VirtualTableRow>();
        fillFts3VirtualTableRows(writtenRows);
        testTable.write(writtenRows);

        final List<Fts3VirtualTableRow> readRows = testTable.read();
        checkFts3VirtualTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final Fts3VirtualTable testTable = database.getFts3VirtualTable();

        final List<Fts3VirtualTableRow> writtenRows = new ArrayList<Fts3VirtualTableRow>();
        fillFts3VirtualTableRows(writtenRows);
        testTable.write(writtenRows);

        final String condition = "body='Body1'";
        final List<Fts3VirtualTableRow> readRows = testTable.read(condition);

        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final Fts3VirtualTableRow readRow = readRows.get(0);
        checkFts3VirtualTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, URISyntaxException, IOException, ZserioError
    {
        final Fts3VirtualTable testTable = database.getFts3VirtualTable();

        final List<Fts3VirtualTableRow> writtenRows = new ArrayList<Fts3VirtualTableRow>();
        fillFts3VirtualTableRows(writtenRows);
        testTable.write(writtenRows);

        final String updateTitle = "Title3";
        final Fts3VirtualTableRow updateRow = createFts3VirtualTableRow(updateTitle, "UpdatedBody");
        final String updateCondition = "title='" + updateTitle + "'";
        testTable.update(updateRow, updateCondition);

        final List<Fts3VirtualTableRow> readRows = testTable.read(updateCondition);
        assertEquals(1, readRows.size());

        final Fts3VirtualTableRow readRow = readRows.get(0);
        checkFts3VirtualTableRow(updateRow, readRow);
    }

    private static void fillFts3VirtualTableRows(List<Fts3VirtualTableRow> rows)
    {
        for (int id = 0; id < NUM_VIRTUAL_TABLE_ROWS; ++id)
        {
            rows.add(createFts3VirtualTableRow("Title" + id, "Body" + id));
        }
    }

    private static Fts3VirtualTableRow createFts3VirtualTableRow(String title, String body)
    {
        final Fts3VirtualTableRow row = new Fts3VirtualTableRow();
        row.setTitle(title);
        row.setBody(body);

        return row;
    }

    private static void checkFts3VirtualTableRows(List<Fts3VirtualTableRow> rows1,
            List<Fts3VirtualTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkFts3VirtualTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkFts3VirtualTableRow(Fts3VirtualTableRow row1, Fts3VirtualTableRow row2)
    {
        assertEquals(row1.getTitle(), row2.getTitle());
        assertEquals(row1.getBody(), row2.getBody());
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

    private static final String TABLE_NAME = "fts3VirtualTable";

    private static final int    NUM_VIRTUAL_TABLE_ROWS = 5;

    private static final String FILE_NAME = "fts3_virtual_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private Fts3TestDb database = null;
}
