package sql_types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.FileUtil;
import test_utils.JdbcUtil;

public class SqlTypesTest
{
    @BeforeClass
    public static void init()
    {
        JdbcUtil.registerJdbc();
    }

    @Before
    public void setUp() throws IOException, URISyntaxException, SQLException
    {
        FileUtil.deleteFileIfExists(file);
        database = new SqlTypesDb(file.toString());
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
    public void unsignedIntegerTypes() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String uint8SqlType = sqlColumnTypes.get("uint8Type");
        assertTrue(uint8SqlType != null);
        assertTrue(uint8SqlType.equalsIgnoreCase("INTEGER"));

        final String uint16SqlType = sqlColumnTypes.get("uint16Type");
        assertTrue(uint16SqlType != null);
        assertTrue(uint16SqlType.equalsIgnoreCase("INTEGER"));

        final String uint32SqlType = sqlColumnTypes.get("uint32Type");
        assertTrue(uint32SqlType != null);
        assertTrue(uint32SqlType.equalsIgnoreCase("INTEGER"));

        final String uint64SqlType = sqlColumnTypes.get("uint64Type");
        assertTrue(uint64SqlType != null);
        assertTrue(uint64SqlType.equalsIgnoreCase("INTEGER"));
    }

    @Test
    public void signedIntegerTypes() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String int8SqlType = sqlColumnTypes.get("int8Type");
        assertTrue(int8SqlType != null);
        assertTrue(int8SqlType.equalsIgnoreCase("INTEGER"));

        final String int16SqlType = sqlColumnTypes.get("int16Type");
        assertTrue(int16SqlType != null);
        assertTrue(int16SqlType.equalsIgnoreCase("INTEGER"));

        final String int32SqlType = sqlColumnTypes.get("int32Type");
        assertTrue(int32SqlType != null);
        assertTrue(int32SqlType.equalsIgnoreCase("INTEGER"));

        final String int64SqlType = sqlColumnTypes.get("int64Type");
        assertTrue(int64SqlType != null);
        assertTrue(int64SqlType.equalsIgnoreCase("INTEGER"));
    }

    @Test
    public void unsignedBitfieldTypes() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String bitfield8SqlType = sqlColumnTypes.get("bitfield8Type");
        assertTrue(bitfield8SqlType != null);
        assertTrue(bitfield8SqlType.equalsIgnoreCase("INTEGER"));

        final String variableBitfieldSqlType = sqlColumnTypes.get("variableBitfieldType");
        assertTrue(variableBitfieldSqlType != null);
        assertTrue(variableBitfieldSqlType.equalsIgnoreCase("INTEGER"));
    }

    @Test
    public void signedBitfieldTypes() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String intfield8SqlType = sqlColumnTypes.get("intfield8Type");
        assertTrue(intfield8SqlType != null);
        assertTrue(intfield8SqlType.equalsIgnoreCase("INTEGER"));

        final String variableIntfieldSqlType = sqlColumnTypes.get("variableIntfieldType");
        assertTrue(variableIntfieldSqlType != null);
        assertTrue(variableIntfieldSqlType.equalsIgnoreCase("INTEGER"));
    }

    @Test
    public void float16Type() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String float16SqlType = sqlColumnTypes.get("float16Type");
        assertTrue(float16SqlType != null);
        assertTrue(float16SqlType.equalsIgnoreCase("REAL"));
    }

    @Test
    public void float32Type() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String float32SqlType = sqlColumnTypes.get("float32Type");
        assertTrue(float32SqlType != null);
        assertTrue(float32SqlType.equalsIgnoreCase("REAL"));
    }

    @Test
    public void float64Type() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String float64SqlType = sqlColumnTypes.get("float64Type");
        assertTrue(float64SqlType != null);
        assertTrue(float64SqlType.equalsIgnoreCase("REAL"));
    }

    @Test
    public void variableUnsignedIntegerTypes() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String varuint16SqlType = sqlColumnTypes.get("varuint16Type");
        assertTrue(varuint16SqlType != null);
        assertTrue(varuint16SqlType.equalsIgnoreCase("INTEGER"));

        final String varuint32SqlType = sqlColumnTypes.get("varuint32Type");
        assertTrue(varuint32SqlType != null);
        assertTrue(varuint32SqlType.equalsIgnoreCase("INTEGER"));

        final String varuint64SqlType = sqlColumnTypes.get("varuint64Type");
        assertTrue(varuint64SqlType != null);
        assertTrue(varuint64SqlType.equalsIgnoreCase("INTEGER"));

        final String varuintSqlType = sqlColumnTypes.get("varuintType");
        assertTrue(varuintSqlType != null);
        assertTrue(varuintSqlType.equalsIgnoreCase("INTEGER"));
    }

    @Test
    public void variableSignedIntegerTypes() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String varint16SqlType = sqlColumnTypes.get("varint16Type");
        assertTrue(varint16SqlType != null);
        assertTrue(varint16SqlType.equalsIgnoreCase("INTEGER"));

        final String varint32SqlType = sqlColumnTypes.get("varint32Type");
        assertTrue(varint32SqlType != null);
        assertTrue(varint32SqlType.equalsIgnoreCase("INTEGER"));

        final String varint64SqlType = sqlColumnTypes.get("varint64Type");
        assertTrue(varint64SqlType != null);
        assertTrue(varint64SqlType.equalsIgnoreCase("INTEGER"));

        final String varintSqlType = sqlColumnTypes.get("varintType");
        assertTrue(varintSqlType != null);
        assertTrue(varintSqlType.equalsIgnoreCase("INTEGER"));
    }

    @Test
    public void boolType() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String boolSqlType = sqlColumnTypes.get("boolType");
        assertTrue(boolSqlType != null);
        assertTrue(boolSqlType.equalsIgnoreCase("INTEGER"));
    }

    @Test
    public void stringTypes() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String stringSqlType = sqlColumnTypes.get("stringType");
        assertTrue(stringSqlType != null);
        assertTrue(stringSqlType.equalsIgnoreCase("TEXT"));
    }

    @Test
    public void enumType() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String enumSqlType = sqlColumnTypes.get("enumType");
        assertTrue(enumSqlType != null);
        assertTrue(enumSqlType.equalsIgnoreCase("INTEGER"));
    }

    @Test
    public void structureType() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String structureSqlType = sqlColumnTypes.get("structureType");
        assertTrue(structureSqlType != null);
        assertTrue(structureSqlType.equalsIgnoreCase("BLOB"));
    }

    @Test
    public void choiceType() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String choiceSqlType = sqlColumnTypes.get("choiceType");
        assertTrue(choiceSqlType != null);
        assertTrue(choiceSqlType.equalsIgnoreCase("BLOB"));
    }

    @Test
    public void unionType() throws SQLException
    {
        final Map<String, String> sqlColumnTypes = getSqlColumnTypes();

        final String unionSqlType = sqlColumnTypes.get("unionType");
        assertTrue(unionSqlType != null);
        assertTrue(unionSqlType.equalsIgnoreCase("BLOB"));
    }

    private Map<String, String> getSqlColumnTypes() throws SQLException
    {
        final Map<String, String> columnTypes = new HashMap<String, String>();

        // prepare SQL query
        final StringBuilder sqlQuery = new StringBuilder("PRAGMA table_info(");
        sqlQuery.append(TABLE_NAME);
        sqlQuery.append(")");

        // get table info
        final PreparedStatement statement = database.prepareStatement(sqlQuery.toString());
        try
        {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                final String columnName = resultSet.getString(2);
                final String columnType = resultSet.getString(3);
                columnTypes.put(columnName, columnType);
            }
        }
        finally
        {
            statement.close();
        }

        return columnTypes;
    }

    private static final String TABLE_NAME = "sqlTypesTable";

    private static final String FILE_NAME = "sql_types_test.sqlite";

    private final File file = new File(FILE_NAME);
    private SqlTypesDb database = null;
}
