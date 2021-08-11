package sql_tables.blob_field_with_children_initialization_table;

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

public class BlobFieldWithChildrenInitializationTableTest
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
        final BlobFieldWithChildrenInitializationTable table =
                database.getBlobFieldWithChildrenInitializationTable();

        final List<BlobFieldWithChildrenInitializationTableRow> writtenRows =
                new ArrayList<BlobFieldWithChildrenInitializationTableRow>();
        fillRows(writtenRows);
        table.write(writtenRows);

        final List<BlobFieldWithChildrenInitializationTableRow> readRows = table.read();
        checkRows(writtenRows, readRows);
    }

    private static void fillRows(List<BlobFieldWithChildrenInitializationTableRow> rows)
    {
        for (int i = 0; i < NUM_ROWS; ++i)
        {
            rows.add(createRow(i));
        }
    }

    private static BlobFieldWithChildrenInitializationTableRow createRow(int index)
    {
        final BlobFieldWithChildrenInitializationTableRow row =
                new BlobFieldWithChildrenInitializationTableRow();
        row.setId(index);
        final int arrayLength = index;

        final long[] array = new long[arrayLength];
        for (int i = 0; i < arrayLength; ++i)
            array[i] = i;
        ParameterizedArray parameterizedArray = new ParameterizedArray(arrayLength, array);
        BlobWithChildrenInitialization blob =
                new BlobWithChildrenInitialization(arrayLength, parameterizedArray);
        row.setBlob(blob);

        return row;
    }

    private static void checkRows(List<BlobFieldWithChildrenInitializationTableRow> rows1, List<BlobFieldWithChildrenInitializationTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
            checkRow(rows1.get(i), rows2.get(i));
    }

    private static void checkRow(BlobFieldWithChildrenInitializationTableRow row1, BlobFieldWithChildrenInitializationTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getBlob(), row2.getBlob());
    }

    private static final int    NUM_ROWS = 5;
    private static final String FILE_NAME = "blob_field_with_children_initialization_table_test.sqlite";

    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
