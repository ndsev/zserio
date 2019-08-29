package sql_tables.subtyped_enum_field_table;

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

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import sql_tables.TestDb;

import zserio.runtime.ZserioError;

public class SubtypedEnumFieldTableTest
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
    public void readWithoutCondition() throws SQLException, IOException, ZserioError
    {
        final SubtypedEnumFieldTable table = database.getSubtypedEnumFieldTable();

        final List<SubtypedEnumFieldTableRow> writtenRows = new ArrayList<SubtypedEnumFieldTableRow>();
        fillRows(writtenRows);
        table.write(writtenRows);

        final List<SubtypedEnumFieldTableRow> readRows = table.read();
        checkRows(writtenRows, readRows);
    }

    private static void fillRows(List<SubtypedEnumFieldTableRow> rows)
    {
        for (int i = 0; i < NUM_ROWS; ++i)
        {
            rows.add(createRow(i));
        }
    }

    private static SubtypedEnumFieldTableRow createRow(int i)
    {
        final SubtypedEnumFieldTableRow row = new SubtypedEnumFieldTableRow();
        row.setId(i);
        row.setEnumField(i % 3 == 0
                ? TestEnum.ONE
                : i % 3 == 1
                        ? TestEnum.TWO
                        : TestEnum.THREE);

        return row;
    }

    private static void checkRows(List<SubtypedEnumFieldTableRow> rows1, List<SubtypedEnumFieldTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkRow(rows1.get(i), rows2.get(i));
    }

    private static void checkRow(SubtypedEnumFieldTableRow row1, SubtypedEnumFieldTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getEnumField(), row2.getEnumField());
    }

    private static final int    NUM_ROWS = 5;
    private static final String FILE_NAME = "subtyped_enum_field_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
