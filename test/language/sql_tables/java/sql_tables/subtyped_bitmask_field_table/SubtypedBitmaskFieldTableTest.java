package sql_tables.subtyped_bitmask_field_table;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;

import sql_tables.TestDb;
import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class SubtypedBitmaskFieldTableTest
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
        final SubtypedBitmaskFieldTable table = database.getSubtypedBitmaskFieldTable();

        final List<SubtypedBitmaskFieldTableRow> writtenRows = new ArrayList<SubtypedBitmaskFieldTableRow>();
        fillRows(writtenRows);
        table.write(writtenRows);

        final List<SubtypedBitmaskFieldTableRow> readRows = table.read();
        checkRows(writtenRows, readRows);
    }

    private static void fillRows(List<SubtypedBitmaskFieldTableRow> rows)
    {
        for (int i = 0; i < NUM_ROWS; ++i)
        {
            rows.add(createRow(i));
        }
    }

    private static SubtypedBitmaskFieldTableRow createRow(int i)
    {
        final SubtypedBitmaskFieldTableRow row = new SubtypedBitmaskFieldTableRow();
        row.setId(i);
        row.setBitmaskField(i % 3 == 0 ? TestBitmask.Values.ONE
                        : i % 3 == 1   ? TestBitmask.Values.TWO
                                       : TestBitmask.Values.THREE);

        return row;
    }

    private static void checkRows(
            List<SubtypedBitmaskFieldTableRow> rows1, List<SubtypedBitmaskFieldTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkRow(rows1.get(i), rows2.get(i));
    }

    private static void checkRow(SubtypedBitmaskFieldTableRow row1, SubtypedBitmaskFieldTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getBitmaskField(), row2.getBitmaskField());
    }

    private static final int NUM_ROWS = 5;
    private static final String FILE_NAME = "subtyped_bitmask_field_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
