package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.sql_table_templated_field.SqlTableTemplatedFieldDb;
import templates.sql_table_templated_field.TemplatedTable_uint32;
import templates.sql_table_templated_field.TemplatedTable_Union;
import templates.sql_table_templated_field.TemplatedTable_uint32Row;
import templates.sql_table_templated_field.TemplatedTable_UnionRow;
import templates.sql_table_templated_field.Data_uint32;
import templates.sql_table_templated_field.Data_Union;
import templates.sql_table_templated_field.Union;

public class SqlTableTemplatedFieldTest
{

    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp()
    {
        FileUtil.deleteFileIfExists(dbFile);
    }

    @Test
    public void readWrite() throws IOException, SQLException
    {
        final SqlTableTemplatedFieldDb sqlTableTemplatedFieldDb = new SqlTableTemplatedFieldDb(DB_FILE_NAME);
        sqlTableTemplatedFieldDb.createSchema();

        final TemplatedTable_uint32 uint32Table = sqlTableTemplatedFieldDb.getUint32Table();
        final List<TemplatedTable_uint32Row> uint32TableRows = new ArrayList<TemplatedTable_uint32Row>();
        final TemplatedTable_uint32Row uint32Row1 = new TemplatedTable_uint32Row();
        uint32Row1.setId(0);
        uint32Row1.setData(new Data_uint32(42));
        uint32TableRows.add(uint32Row1);
        uint32Table.write(uint32TableRows);

        final TemplatedTable_Union unionTable = sqlTableTemplatedFieldDb.getUnionTable();
        final List<TemplatedTable_UnionRow> unionTableRows = new ArrayList<TemplatedTable_UnionRow>();
        final TemplatedTable_UnionRow unionRow1 = new TemplatedTable_UnionRow();
        unionRow1.setId(13);
        final Union union1 = new Union();
        union1.setValueString("string");
        unionRow1.setData(new Data_Union(union1));
        unionTableRows.add(unionRow1);
        unionTable.write(unionTableRows);

        sqlTableTemplatedFieldDb.close();

        final SqlTableTemplatedFieldDb readSqlTableTemplatedFieldDb =
                new SqlTableTemplatedFieldDb(DB_FILE_NAME);
        final List<TemplatedTable_uint32Row> readUint32TableRows =
                readSqlTableTemplatedFieldDb.getUint32Table().read();
        final List<TemplatedTable_UnionRow> readUnionTableRows =
                readSqlTableTemplatedFieldDb.getUnionTable().read();

        assertEqualUint32Rows(uint32TableRows, readUint32TableRows);
        assertEqualUnionRows(unionTableRows, readUnionTableRows);

        readSqlTableTemplatedFieldDb.close();
    }

    static void assertEqualUint32Rows(List<TemplatedTable_uint32Row> rows1, List<TemplatedTable_uint32Row> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
        {
            assertEqualRow(rows1.get(i), rows2.get(i));
        }
    }

    static void assertEqualUnionRows(List<TemplatedTable_UnionRow> rows1, List<TemplatedTable_UnionRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
        {
            assertEqualRow(rows1.get(i), rows2.get(i));
        }
    }

    static void assertEqualRow(TemplatedTable_uint32Row row1, TemplatedTable_uint32Row row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getData(), row2.getData());
    }

    static void assertEqualRow(TemplatedTable_UnionRow row1, TemplatedTable_UnionRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getData(), row2.getData());
    }

    private static final String DB_FILE_NAME = "sql_table_templated_field.sqlite";
    private final File dbFile = new File(DB_FILE_NAME);
}
