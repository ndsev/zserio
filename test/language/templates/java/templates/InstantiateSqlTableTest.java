package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import test_utils.JdbcUtil;

import templates.instantiate_sql_table.U32Table;
import templates.instantiate_sql_table.U32TableRow;

public class InstantiateSqlTableTest
{
    @BeforeAll
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Test
    public void readWrite() throws IOException, SQLException
    {
        final Properties connectionProps = new Properties();
        connectionProps.setProperty("flags", "CREATE");
        final String uriPath = "jdbc:sqlite:" + SQLITE_MEM_DB;

        final Connection connection = DriverManager.getConnection(uriPath, connectionProps);
        final U32Table u32Table = new U32Table(connection, "u32Table");
        u32Table.createTable();

        final U32TableRow row = new U32TableRow();
        row.setId(13);
        row.setInfo("info");
        final List<U32TableRow> rows = new ArrayList<U32TableRow>();
        rows.add(row);
        u32Table.write(rows);

        final List<U32TableRow> readRows = u32Table.read();
        assertEquals(1, readRows.size());

        assertEquals(13, readRows.get(0).getId());
        assertEquals("info", readRows.get(0).getInfo());
    }

    private static final String SQLITE_MEM_DB = ":memory:";
}
