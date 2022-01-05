package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.instantiate_type_as_sql_database_field.InstantiateTypeAsSqlDatabaseFieldDb;
import templates.instantiate_type_as_sql_database_field.StringTable;
import templates.instantiate_type_as_sql_database_field.StringTableRow;

public class InstantiateTypeAsSqlDatabaseFieldTest
{

    @BeforeAll
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @BeforeEach
    public void setUp()
    {
        FileUtil.deleteFileIfExists(dbFile);
    }

    @Test
    public void readWrite() throws IOException, SQLException
    {
        final InstantiateTypeAsSqlDatabaseFieldDb instantiateTypeAsSqlDatabaseFieldDb =
                new InstantiateTypeAsSqlDatabaseFieldDb(DB_FILE_NAME);
        instantiateTypeAsSqlDatabaseFieldDb.createSchema();

        final StringTable stringTable = instantiateTypeAsSqlDatabaseFieldDb.getStringTable();
        final List<StringTableRow> stringTableRows = new ArrayList<StringTableRow>();
        final StringTableRow stringRow1 = new StringTableRow();
        stringRow1.setId(0);
        stringRow1.setData("test");
        stringTableRows.add(stringRow1);
        stringTable.write(stringTableRows);

        final StringTable otherStringTable = instantiateTypeAsSqlDatabaseFieldDb.getOtherStringTable();
        final List<StringTableRow> otherStringTableRows = new ArrayList<StringTableRow>();
        final StringTableRow otherStringRow1 = new StringTableRow();
        otherStringRow1.setId(0);
        otherStringRow1.setData("other test");
        otherStringTableRows.add(otherStringRow1);
        otherStringTable.write(otherStringTableRows);

        instantiateTypeAsSqlDatabaseFieldDb.close();

        final InstantiateTypeAsSqlDatabaseFieldDb readInstantiateTypeAsSqlDatabaseFieldDb =
                new InstantiateTypeAsSqlDatabaseFieldDb(DB_FILE_NAME);
        final List<StringTableRow> readStringTableRows =
                readInstantiateTypeAsSqlDatabaseFieldDb.getStringTable().read();
        final List<StringTableRow> readOtherStringTableRows =
                readInstantiateTypeAsSqlDatabaseFieldDb.getOtherStringTable().read();

        assertEqualRows(stringTableRows, readStringTableRows);
        assertEqualRows(otherStringTableRows, readOtherStringTableRows);

        readInstantiateTypeAsSqlDatabaseFieldDb.close();
    }

    static void assertEqualRows(List<StringTableRow> rows1, List<StringTableRow> rows2)
    {
        assertEquals(rows1.size(), rows2.size());
        for (int i = 0; i < rows1.size(); ++i)
        {
            assertEqualRow(rows1.get(i), rows2.get(i));
        }
    }

    static void assertEqualRow(StringTableRow row1, StringTableRow row2)
    {
        assertEquals(row1.getId(), row2.getId());
        assertEquals(row1.getData(), row2.getData());
    }

    private static final String DB_FILE_NAME = "instantiate_type_as_sql_database_field.sqlite";
    private final File dbFile = new File(DB_FILE_NAME);
}
