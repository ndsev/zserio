package sql_tables.column_param_table;

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

import sql_tables.TestDb;

import zserio.runtime.ZserioError;

public class ColumnParamTableTest
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
        database = new TestDb(file.toString());
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

        final ColumnParamTable testTable = database.getColumnParamTable();
        testTable.deleteTable();
        assertFalse(isTableInDb());

        testTable.createTable();
        assertTrue(isTableInDb());
    }

    @Test
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final ColumnParamTable testTable = database.getColumnParamTable();

        final List<ColumnParamTableRow> writtenRows = new ArrayList<ColumnParamTableRow>();
        fillColumnParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final List<ColumnParamTableRow> readRows = testTable.read();
        checkColumnParamTableRows(writtenRows, readRows);
    }

    @Test
    public void readWithCondition() throws SQLException, IOException, ZserioError
    {
        final ColumnParamTable testTable = database.getColumnParamTable();

        final List<ColumnParamTableRow> writtenRows = new ArrayList<ColumnParamTableRow>();
        fillColumnParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final String condition = "name='Name1'";
        final List<ColumnParamTableRow> readRows = testTable.read(condition);

        assertEquals(1, readRows.size());

        final int expectedRowNum = 1;
        final ColumnParamTableRow readRow = readRows.get(0);
        checkColumnParamTableRow(writtenRows.get(expectedRowNum), readRow);
    }

    @Test
    public void update() throws SQLException, IOException, ZserioError
    {
        final ColumnParamTable testTable = database.getColumnParamTable();

        final List<ColumnParamTableRow> writtenRows = new ArrayList<ColumnParamTableRow>();
        fillColumnParamTableRows(writtenRows);
        testTable.write(writtenRows);

        final int updateRowId = 3;
        final ColumnParamTableRow updateRow = createColumnParamTableRow(updateRowId, "UpdatedName");
        final String updateCondition = "blobId=" + updateRowId;
        testTable.update(updateRow, updateCondition);

        final List<ColumnParamTableRow> readRows = testTable.read(updateCondition);
        assertEquals(1, readRows.size());

        final ColumnParamTableRow readRow = readRows.get(0);
        checkColumnParamTableRow(updateRow, readRow);
    }

    private static void fillColumnParamTableRows(List<ColumnParamTableRow> rows)
    {
        for (int blobId = 0; blobId < NUM_COLUMN_PARAM_TABLE_ROWS; ++blobId)
        {
            rows.add(createColumnParamTableRow(blobId, "Name" + blobId));
        }
    }

    private static ColumnParamTableRow createColumnParamTableRow(int blobId, String name)
    {
        final ColumnParamTableRow row = new ColumnParamTableRow();
        row.setBlobId(blobId);
        row.setName(name);
        final ParameterizedBlob parameterizedBlob = new ParameterizedBlob(blobId / 2, PARAMETERIZED_BLOB_VALUE);
        row.setBlob(parameterizedBlob);

        return row;
    }

    private static void checkColumnParamTableRows(List<ColumnParamTableRow> rows1,
            List<ColumnParamTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkColumnParamTableRow(rows1.get(i), rows2.get(i));
    }

    private static void checkColumnParamTableRow(ColumnParamTableRow row1, ColumnParamTableRow row2)
    {
        assertEquals(row1.getBlobId(), row2.getBlobId());
        assertEquals(row1.getName(), row2.getName());
        assertEquals(row1.getBlob(), row2.getBlob());
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

    private static final String TABLE_NAME = "columnParamTable";

    private static final int    PARAMETERIZED_BLOB_VALUE = 0xABCD;
    private static final int    NUM_COLUMN_PARAM_TABLE_ROWS = 5;
    private static final String FILE_NAME = "column_param_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
