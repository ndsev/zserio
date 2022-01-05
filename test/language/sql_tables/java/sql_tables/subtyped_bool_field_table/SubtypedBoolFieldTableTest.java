package sql_tables.subtyped_bool_field_table;

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

public class SubtypedBoolFieldTableTest
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
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final SubtypedBoolFieldTable table = database.getSubtypedBoolFieldTable();

        final List<SubtypedBoolFieldTableRow> writtenRows = new ArrayList<SubtypedBoolFieldTableRow>();
        fillRows(writtenRows);
        table.write(writtenRows);

        final List<SubtypedBoolFieldTableRow> readRows = table.read();
        checkRows(writtenRows, readRows);
    }

    private static void fillRows(List<SubtypedBoolFieldTableRow> rows)
    {
        for (int i = 0; i < NUM_ROWS; ++i)
        {
            rows.add(createRow(i));
        }
    }

    private static SubtypedBoolFieldTableRow createRow(int i)
    {
        final SubtypedBoolFieldTableRow row = new SubtypedBoolFieldTableRow();
        row.setId(i);
        row.setBoolField(i % 2 == 0);

        return row;
    }

    private static void checkRows(List<SubtypedBoolFieldTableRow> rows1, List<SubtypedBoolFieldTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkRow(rows1.get(i), rows2.get(i));
    }

    private static void checkRow(SubtypedBoolFieldTableRow row1, SubtypedBoolFieldTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getBoolField(), row2.getBoolField());
    }

    private static final int    NUM_ROWS = 5;
    private static final String FILE_NAME = "subtyped_bool_field_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
