package allow_implicit_arrays;

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

import allow_implicit_arrays.table_with_implicit_array.DbWithImplicitArray;
import allow_implicit_arrays.table_with_implicit_array.TableWithImplicitArray;
import allow_implicit_arrays.table_with_implicit_array.TableWithImplicitArrayRow;
import allow_implicit_arrays.table_with_implicit_array.StructWithImplicit;

import zserio.runtime.ZserioError;

public class TableWithImplicitArrayTest
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
        database = new DbWithImplicitArray(file.toString());
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
        final TableWithImplicitArray table = database.getTableWithImplicitArray();

        final List<TableWithImplicitArrayRow> writtenRows = new ArrayList<TableWithImplicitArrayRow>();
        fillRows(writtenRows);
        table.write(writtenRows);

        final List<TableWithImplicitArrayRow> readRows = table.read();
        checkRows(writtenRows, readRows);
    }

    private static void fillRows(List<TableWithImplicitArrayRow> rows)
    {
        for (long i = 0; i < NUM_ROWS; ++i)
        {
            rows.add(createRow(i));
        }
    }

    private static TableWithImplicitArrayRow createRow(long i)
    {
        final TableWithImplicitArrayRow row = new TableWithImplicitArrayRow();
        row.setId(i);
        final long array[] = {1, 2, 3, 4, 5};
        row.setStructWithImplicit(new StructWithImplicit(array));
        row.setText("test" + i);

        return row;
    }

    private static void checkRows(List<TableWithImplicitArrayRow> rows1, List<TableWithImplicitArrayRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkRow(rows1.get(i), rows2.get(i));
    }

    private static void checkRow(TableWithImplicitArrayRow row1, TableWithImplicitArrayRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getStructWithImplicit(), row2.getStructWithImplicit());
        assertEquals(row1.getText(), row2.getText());
    }

    private static final long NUM_ROWS = 5;
    private static final String FILE_NAME = "table_with_implicit_array_test.sqlite";

    private final File file = new File(FILE_NAME);
    private DbWithImplicitArray database = null;
}
