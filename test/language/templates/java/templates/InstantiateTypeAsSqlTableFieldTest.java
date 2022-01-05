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

import templates.instantiate_type_as_sql_table_field.Test32Table;
import templates.instantiate_type_as_sql_table_field.Test32TableRow;
import templates.instantiate_type_as_sql_table_field.Test32;

public class InstantiateTypeAsSqlTableFieldTest
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
        final Test32Table test32Table = new Test32Table(connection, "test32Table");
        test32Table.createTable();

        final Test32TableRow row = new Test32TableRow();
        row.setId(13);
        row.setTest(new Test32(42));
        final List<Test32TableRow> rows = new ArrayList<Test32TableRow>();
        rows.add(row);
        test32Table.write(rows);

        final List<Test32TableRow> readRows = test32Table.read();
        assertEquals(1, readRows.size());

        assertEquals(13, readRows.get(0).getId());
        assertEquals(42, readRows.get(0).getTest().getValue());
    }

    private static final String SQLITE_MEM_DB = ":memory:";
}
