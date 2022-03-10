package sql_virtual_columns;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_virtual_columns.hidden_virtual_columns.HiddenVirtualColumnsDb;
import sql_virtual_columns.hidden_virtual_columns.HiddenVirtualColumnsTable;
import sql_virtual_columns.hidden_virtual_columns.HiddenVirtualColumnsTableRow;

import zserio.runtime.ZserioError;

public class HiddenVirtualColumnsTest
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
        database = new HiddenVirtualColumnsDb(file.toString());
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
    public void deleteTable() throws SQLException
    {
        assertTrue(isTableInDb());

        final HiddenVirtualColumnsTable testTable = database.getHiddenVirtualColumnsTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final HiddenVirtualColumnsTable testTable = database.getHiddenVirtualColumnsTable();

        final List<HiddenVirtualColumnsTableRow> writtenRows = new ArrayList<HiddenVirtualColumnsTableRow>();
        fillHiddenVirtualColumnsTableRows(writtenRows);
        testTable.write(writtenRows);

        final List<HiddenVirtualColumnsTableRow> readRows = testTable.read();
        checkHiddenVirtualColumnsTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final HiddenVirtualColumnsTable testTable = database.getHiddenVirtualColumnsTable();

        final List<HiddenVirtualColumnsTableRow> writtenRows = new ArrayList<HiddenVirtualColumnsTableRow>();
        fillHiddenVirtualColumnsTableRows(writtenRows);
        testTable.write(writtenRows);

        final String condition = "searchTags='Search Tags1'";
        final List<HiddenVirtualColumnsTableRow> readRows = testTable.read(condition);

        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final HiddenVirtualColumnsTableRow readRow = readRows.get(0);
        checkHiddenVirtualColumnsTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final HiddenVirtualColumnsTable testTable = database.getHiddenVirtualColumnsTable();

        final List<HiddenVirtualColumnsTableRow> writtenRows = new ArrayList<HiddenVirtualColumnsTableRow>();
        fillHiddenVirtualColumnsTableRows(writtenRows);
        testTable.write(writtenRows);

        final int updateDocId = 1;
        final HiddenVirtualColumnsTableRow updateRow = createHiddenVirtualColumnsTableRow(updateDocId,
                "Updated Search Tags");
        final String updateCondition = "docId='" + updateDocId + "'";
        testTable.update(updateRow, updateCondition);

        final List<HiddenVirtualColumnsTableRow> readRows = testTable.read(updateCondition);
        assertEquals(1, readRows.size());

        final HiddenVirtualColumnsTableRow readRow = readRows.get(0);
        checkHiddenVirtualColumnsTableRow(updateRow, readRow);
    }

    @Test
    public void checkVirtualColumns() throws SQLException
    {
        assertTrue(isHiddenVirtualColumnInTable("docId"));
        assertTrue(isHiddenVirtualColumnInTable("languageCode"));
    }

    private static void fillHiddenVirtualColumnsTableRows(List<HiddenVirtualColumnsTableRow> rows)
    {
        for (int id = 0; id < NUM_TABLE_ROWS; ++id)
        {
            rows.add(createHiddenVirtualColumnsTableRow(id, "Search Tags" + id));
        }
    }

    private static HiddenVirtualColumnsTableRow createHiddenVirtualColumnsTableRow(int docId, String searchTags)
    {
        final HiddenVirtualColumnsTableRow row = new HiddenVirtualColumnsTableRow();
        row.setDocId(docId);
        row.setLanguageCode(LANGUAGE_CODE_VALUE);
        row.setSearchTags(searchTags);
        row.setFrequency(FREQUENCY_VALUE);

        return row;
    }

    private static void checkHiddenVirtualColumnsTableRows(List<HiddenVirtualColumnsTableRow> rows1,
            List<HiddenVirtualColumnsTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkHiddenVirtualColumnsTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkHiddenVirtualColumnsTableRow(HiddenVirtualColumnsTableRow row1,
            HiddenVirtualColumnsTableRow row2)
    {
        assertEquals(row1.getDocId(), row2.getDocId());
        assertEquals(row1.getLanguageCode(), row2.getLanguageCode());
        assertEquals(row1.getSearchTags(), row2.getSearchTags());
        assertEquals(row1.getFrequency(), row2.getFrequency());
    }

    private boolean isTableInDb() throws SQLException
    {
        // check if database does contain table
        final String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME +
                "'";

        try (
            final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);
            final ResultSet resultSet = statement.executeQuery();
        )
        {
            if (!resultSet.next())
                return false;

            // read table name
            final String tableName = resultSet.getString(1);
            if (resultSet.wasNull() || !tableName.equals(TABLE_NAME))
                return false;
        }

        return true;
    }

    private boolean isHiddenVirtualColumnInTable(String columnName) throws SQLException
    {
        final String sqlQuery = "SELECT " + columnName + " FROM " + TABLE_NAME + " LIMIT 0";

        // try select to check if hidden column exists
        try (final PreparedStatement statement = database.connection().prepareStatement(sqlQuery))
        {
            return statement.execute();
        }
        catch (SQLException exception)
        {
            return false;
        }
    }

    private static final String TABLE_NAME = "hiddenVirtualColumnsTable";

    private static final int    NUM_TABLE_ROWS = 5;

    private static final short  LANGUAGE_CODE_VALUE = 1;
    private static final long   FREQUENCY_VALUE = 0xDEAD;

    private static final String FILE_NAME = "hidden_virtual_columns_test.sqlite";

    private final File file = new File(FILE_NAME);
    private HiddenVirtualColumnsDb database = null;
}
