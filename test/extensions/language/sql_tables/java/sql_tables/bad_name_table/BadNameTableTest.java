package sql_tables.bad_name_table;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;

import sql_tables.TestDb;
import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class BadNameTableTest
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
    public void tableExists() throws SQLException
    {
        assertTrue(isTableInDb());
    }

    @Test
    public void tableOperations() throws SQLException, IOException
    {
        BadNameTable testTable = database.getSelect();
        BadNameTableRow row = new BadNameTableRow();
        row.setId(1);
        row.setSelect(35);
        row.setWhere(11);
        row.setOrder(5);
        testTable.write(Collections.singletonList(row));
        row.setOrder(99);
        testTable.update(row, "TRUE");
        List<BadNameTableRow> read = testTable.read();
        assertEquals(read.size(), 1);
        BadNameTableRow read1 = read.get(0);
        assertEquals(read1.getSelect(), 35);
        assertEquals(read1.getWhere(), 11);
        assertEquals(read1.getOrder(), 99);
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

    private static final String TABLE_NAME = "select";
    private static final String FILE_NAME = "bad_name_table_test.sqlite";
    private final File file = new File(FILE_NAME);
    private TestDb database = null;
}
