package sql_virtual_columns;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;

import sql_virtual_columns.simple_virtual_columns.SimpleVirtualColumnsDb;
import sql_virtual_columns.simple_virtual_columns.SimpleVirtualColumnsTable;
import sql_virtual_columns.simple_virtual_columns.SimpleVirtualColumnsTableRow;
import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class SimpleVirtualColumnsTest
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
        database = new SimpleVirtualColumnsDb(file.toString());
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

        final SimpleVirtualColumnsTable testTable = database.getSimpleVirtualColumnsTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final SimpleVirtualColumnsTable testTable = database.getSimpleVirtualColumnsTable();

        final List<SimpleVirtualColumnsTableRow> writtenRows = new ArrayList<SimpleVirtualColumnsTableRow>();
        fillSimpleVirtualColumnsTableRows(writtenRows);
        testTable.write(writtenRows);

        final List<SimpleVirtualColumnsTableRow> readRows = testTable.read();
        checkSimpleVirtualColumnsTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final SimpleVirtualColumnsTable testTable = database.getSimpleVirtualColumnsTable();

        final List<SimpleVirtualColumnsTableRow> writtenRows = new ArrayList<SimpleVirtualColumnsTableRow>();
        fillSimpleVirtualColumnsTableRows(writtenRows);
        testTable.write(writtenRows);

        final String condition = "content='Content1'";
        final List<SimpleVirtualColumnsTableRow> readRows = testTable.read(condition);

        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final SimpleVirtualColumnsTableRow readRow = readRows.get(0);
        checkSimpleVirtualColumnsTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final SimpleVirtualColumnsTable testTable = database.getSimpleVirtualColumnsTable();

        final List<SimpleVirtualColumnsTableRow> writtenRows = new ArrayList<SimpleVirtualColumnsTableRow>();
        fillSimpleVirtualColumnsTableRows(writtenRows);
        testTable.write(writtenRows);

        final String updateContent = "UpdatedContent";
        final SimpleVirtualColumnsTableRow updateRow = createSimpleVirtualColumnsTableRow(updateContent);
        final String updateCondition = "content='Content3'";
        testTable.update(updateRow, updateCondition);

        final String readCondition = "content='" + updateContent + "'";
        final List<SimpleVirtualColumnsTableRow> readRows = testTable.read(readCondition);
        assertEquals(1, readRows.size());

        final SimpleVirtualColumnsTableRow readRow = readRows.get(0);
        checkSimpleVirtualColumnsTableRow(updateRow, readRow);
    }

    @Test
    public void checkVirtualColumn() throws SQLException
    {
        assertTrue(isVirtualColumnInTable());
    }

    private static void fillSimpleVirtualColumnsTableRows(List<SimpleVirtualColumnsTableRow> rows)
    {
        for (int id = 0; id < NUM_TABLE_ROWS; ++id)
        {
            rows.add(createSimpleVirtualColumnsTableRow("Content" + id));
        }
    }

    private static SimpleVirtualColumnsTableRow createSimpleVirtualColumnsTableRow(String content)
    {
        final SimpleVirtualColumnsTableRow row = new SimpleVirtualColumnsTableRow();
        row.setContent(content);

        return row;
    }

    private static void checkSimpleVirtualColumnsTableRows(
            List<SimpleVirtualColumnsTableRow> rows1, List<SimpleVirtualColumnsTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkSimpleVirtualColumnsTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkSimpleVirtualColumnsTableRow(
            SimpleVirtualColumnsTableRow row1, SimpleVirtualColumnsTableRow row2)
    {
        assertEquals(row1.getContent(), row2.getContent());
    }

    private boolean isTableInDb() throws SQLException
    {
        // check if database does contain table
        final String sqlQuery =
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_NAME + "'";

        try (final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);
                final ResultSet resultSet = statement.executeQuery();)
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

    private boolean isVirtualColumnInTable() throws SQLException
    {
        final String sqlQuery = "PRAGMA table_info(" + TABLE_NAME + ")";
        try (final PreparedStatement statement = database.connection().prepareStatement(sqlQuery);
                final ResultSet resultSet = statement.executeQuery();)
        {
            while (resultSet.next())
            {
                final String readColumnName = resultSet.getString(2);
                if (!resultSet.wasNull())
                {
                    if (readColumnName.equals(VIRTUAL_COLUMN_NAME))
                        return true;
                }
            }
        }

        return false;
    }

    private static final String TABLE_NAME = "simpleVirtualColumnsTable";
    private static final String VIRTUAL_COLUMN_NAME = "content";

    private static final int NUM_TABLE_ROWS = 5;

    private static final String FILE_NAME = "simple_virtual_columns_test.sqlite";

    private final File file = new File(FILE_NAME);
    private SimpleVirtualColumnsDb database = null;
}
