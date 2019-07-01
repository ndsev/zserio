package without_writer_code;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Test;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.SqlDatabase;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class WithoutWriterCodeTest
{
    @Test
    public void checkItemTypeMethods()
    {
        final Set<String> methods = getMethods(ItemType.class);

        assertMethodNotPresent(methods, "initializeOffsets(");
        assertMethodNotPresent(methods, "write(");

        assertMethodPresent(methods, "ItemType(");
        assertMethodPresent(methods, "readEnum(");
        assertMethodPresent(methods, "toEnum(byte)");
        assertMethodPresent(methods, "bitSizeOf()");
        assertMethodPresent(methods, "bitSizeOf(long)");
        assertMethodPresent(methods, "getValue()");
        assertMethodPresent(methods, "getGenericValue()");
    }

    @Test
    public void checkExtraParamUnionMethods()
    {
        final Set<String> methods = getMethods(ExtraParamUnion.class);

        assertMethodNotPresent(methods, "ExtraParamUnion()");
        assertMethodNotPresent(methods, "initializeOffsets(");
        assertMethodNotPresent(methods, "write(");

        assertMethodPresent(methods, "ExtraParamUnion(java.io.File");
        assertMethodPresent(methods, "ExtraParamUnion(zserio.runtime.io.BitStreamReader");
        assertMethodPresent(methods, "bitSizeOf()");
        assertMethodPresent(methods, "bitSizeOf(long)");
        assertMethodPresent(methods, "choiceTag()");
        assertMethodPresent(methods, "equals(java.lang.Object)");
        assertMethodPresent(methods, "hashCode()");
        assertMethodPresent(methods, "getValue16()");
        assertMethodPresent(methods, "getValue32()");
        assertMethodPresent(methods, "read(zserio.runtime.io.BitStreamReader)");
    }

    @Test
    public void checkItemMethods()
    {
        final Set<String> methods = getMethods(Item.class);

        assertMethodNotPresent(methods, "Item(without_writer_code.ItemType");
        assertMethodNotPresent(methods, "initializeOffsets(");
        assertMethodNotPresent(methods, "write(");
        assertMethodNotPresent(methods, "setExtraParam(");
        assertMethodNotPresent(methods, "setParam(");

        assertMethodPresent(methods, "Item(java.io.File");
        assertMethodPresent(methods, "Item(zserio.runtime.io.BitStreamReader");
        assertMethodPresent(methods, "bitSizeOf()");
        assertMethodPresent(methods, "bitSizeOf(long)");
        assertMethodPresent(methods, "equals(java.lang.Object)");
        assertMethodPresent(methods, "hashCode()");
        assertMethodPresent(methods, "getExtraParam()");
        assertMethodPresent(methods, "getItemType()");
        assertMethodPresent(methods, "getParam()");
        assertMethodPresent(methods, "hasExtraParam()");
        assertMethodPresent(methods, "read(zserio.runtime.io.BitStreamReader");
    }

    @Test
    public void checkItemChoiceMethods()
    {
        final Set<String> methods = getMethods(ItemChoice.class);

        assertMethodNotPresent(methods, "ItemChoice(boolean)");
        assertMethodNotPresent(methods, "initializeOffsets(");
        assertMethodNotPresent(methods, "write(");
        assertMethodNotPresent(methods, "setItem(");
        assertMethodNotPresent(methods, "setParam(");

        assertMethodPresent(methods, "ItemChoice(java.io.File");
        assertMethodPresent(methods, "ItemChoice(zserio.runtime.io.BitStreamReader");
        assertMethodPresent(methods, "bitSizeOf()");
        assertMethodPresent(methods, "bitSizeOf(long)");
        assertMethodPresent(methods, "equals(java.lang.Object)");
        assertMethodPresent(methods, "hashCode()");
        assertMethodPresent(methods, "getHasItem()");
        assertMethodPresent(methods, "getItem()");
        assertMethodPresent(methods, "getParam()");
        assertMethodPresent(methods, "read(zserio.runtime.io.BitStreamReader)");
    }

    @Test
    public void checkItemChoiceHolderMethods()
    {
        final Set<String> methods = getMethods(ItemChoiceHolder.class);

        assertMethodNotPresent(methods, "ItemChoiceHolder()");
        assertMethodNotPresent(methods, "ItemChoiceHolder(boolean");
        assertMethodNotPresent(methods, "initializeOffsets(");
        assertMethodNotPresent(methods, "write(");
        assertMethodNotPresent(methods, "setHasItem(");
        assertMethodNotPresent(methods, "setItemChoice(");

        assertMethodPresent(methods, "ItemChoiceHolder(java.io.File)");
        assertMethodPresent(methods, "ItemChoiceHolder(zserio.runtime.io.BitStreamReader)");
        assertMethodPresent(methods, "bitSizeOf()");
        assertMethodPresent(methods, "bitSizeOf(long)");
        assertMethodPresent(methods, "equals(java.lang.Object)");
        assertMethodPresent(methods, "hashCode()");
        assertMethodPresent(methods, "getHasItem()");
        assertMethodPresent(methods, "getItemChoice()");
        assertMethodPresent(methods, "read(zserio.runtime.io.BitStreamReader)");
    }

    @Test
    public void checkTile()
    {
        final Set<String> methods = getMethods(Tile.class);

        assertMethodNotPresent(methods, "Tile()");
        assertMethodNotPresent(methods, "Tile(short");
        assertMethodNotPresent(methods, "initializeOffsets(");
        assertMethodNotPresent(methods, "write(");
        assertMethodNotPresent(methods, "setVersion(");
        assertMethodNotPresent(methods, "setNumElementsOffset(");
        assertMethodNotPresent(methods, "setNumElements(");
        assertMethodNotPresent(methods, "setData(");

        assertMethodPresent(methods, "Tile(java.io.File)");
        assertMethodPresent(methods, "Tile(zserio.runtime.io.BitStreamReader)");
        assertMethodPresent(methods, "bitSizeOf()");
        assertMethodPresent(methods, "bitSizeOf(long)");
        assertMethodPresent(methods, "equals(java.lang.Object)");
        assertMethodPresent(methods, "hashCode()");
        assertMethodPresent(methods, "getData()");
        assertMethodPresent(methods, "getNumElementsOffset()");
        assertMethodPresent(methods, "getNumElements()");
        assertMethodPresent(methods, "getVersion()");
        assertMethodPresent(methods, "read(zserio.runtime.io.BitStreamReader)");
    }

    @Test
    public void checkGeoMapTableMethods()
    {
        final Set<String> methods = getMethods(GeoMapTable.class);

        assertMethodNotPresent(methods, "createTable()");
        assertMethodNotPresent(methods, "createOrdinaryRowIdTable");
        assertMethodNotPresent(methods, "deleteTable()");
        assertMethodNotPresent(methods, "write(");
        assertMethodNotPresent(methods, "update(");
        assertMethodNotPresent(methods, "getCreateTableQuery(");
        assertMethodNotPresent(methods, "writeRow(");
        assertMethodNotPresent(methods, "startTransaction(");
        assertMethodNotPresent(methods, "endTransaction(");

        assertMethodPresent(methods, "GeoMapTable(java.sql.Connection");
        assertMethodPresent(methods, "readRow(");
        assertMethodPresent(methods, "read()");
        assertMethodPresent(methods, "read(java.lang.String)");
    }

    @Test
    public void checkGeoMapTableRowMethods()
    {
        final Set<String> methods = getMethods(GeoMapTableRow.class);

        assertMethodPresent(methods, "GeoMapTableRow()");
        assertMethodPresent(methods, "getTile()");
        assertMethodPresent(methods, "getTileId()");
        assertMethodPresent(methods, "isNullTile()");
        assertMethodPresent(methods, "isNullTileId()");
        assertMethodPresent(methods, "setNullTile()");
        assertMethodPresent(methods, "setNullTileId()");
        assertMethodPresent(methods, "setTile(");
        assertMethodPresent(methods, "setTileId(");
    }

    @Test
    public void checkWorldDbMethods()
    {
        final Set<String> methods = getMethods(WorldDb.class);

        assertMethodNotPresent(methods, "createSchema(");
        assertMethodNotPresent(methods, "deleteSchema(");
        assertMethodNotPresent(methods, "startTransaction(");
        assertMethodNotPresent(methods, "endTransaction(");

        assertMethodPresent(methods, "WorldDb(java.sql.Connection)");
        assertMethodPresent(methods, "WorldDb(java.sql.Connection,");
        assertMethodPresent(methods, "WorldDb(java.lang.String)");
        assertMethodPresent(methods, "WorldDb(java.lang.String,");
        assertMethodPresent(methods, "tableNames()");
        assertMethodPresent(methods, "databaseName()");
        assertMethodPresent(methods, "getAmerica()");
        assertMethodPresent(methods, "getEurope()");
        assertMethodPresent(methods, "initTables(");
    }

    @Test
    public void readConstructor() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        writeTile(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray());
        final Tile tile = new Tile(reader);

        checkTile(tile);
    }

    @Test
    public void readWorldDb() throws SQLException, IOException
    {
        final Connection connection = createWorldDb();
        final WorldDb worldDb = new WorldDb(connection);

        final GeoMapTable europe = worldDb.getEurope();
        final List<GeoMapTableRow> europeRows = europe.read();

        final GeoMapTable america = worldDb.getAmerica();
        final List<GeoMapTableRow> americaRows = america.read();

        assertEquals(1, europeRows.size());
        assertEquals(TILE_ID_EUROPE, europeRows.get(0).getTileId());
        checkTile(europeRows.get(0).getTile());

        assertEquals(1, americaRows.size());
        assertEquals(TILE_ID_AMERICA, americaRows.get(0).getTileId());
        checkTile(americaRows.get(0).getTile());

        worldDb.close();
        connection.close();
    }

    private Set<String> getMethods(Class<?> userType)
    {
        final HashSet<String> methods = new HashSet<String>();

        for (Constructor<?> constructor : userType.getDeclaredConstructors())
            methods.add(constructor.toString());
        for (Method method : userType.getDeclaredMethods())
            methods.add(method.toString());

        return methods;
    }

    private void assertMethodNotPresent(Set<String> methods, String methodPattern)
    {
        boolean present = false;
        for (String method : methods)
        {
            if (method.indexOf(methodPattern) != -1)
            {
                present = true;
                break;
            }
        }
        assertFalse(present);
    }

    private void assertMethodPresent(Set<String> methods, String methodPattern)
    {
        boolean present = false;
        for (String method : methods)
        {
            if (method.indexOf(methodPattern) != -1)
            {
                present = true;
                break;
            }
        }
        assertTrue(present);
    }

    private Connection createWorldDb() throws SQLException, IOException
    {
        final String uriPath = "jdbc:sqlite::memory:";
        final Properties connectionProps = new Properties();
        connectionProps.setProperty("flags", "CREATE");

        final Connection connection = DriverManager.getConnection(uriPath, connectionProps);

        final Statement statement = connection.createStatement();
        try
        {
            statement.executeUpdate("CREATE TABLE europe(tileId INTEGER PRIMARY KEY, tile BLOB)");
            statement.executeUpdate( "CREATE TABLE america(tileId INTEGER PRIMARY KEY, tile BLOB)");
        }
        finally
        {
            statement.close();
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writeTile(writer);
        final byte[] tileBytes = writer.toByteArray();

        final PreparedStatement stmtEurope = connection.prepareStatement("INSERT INTO europe VALUES (?, ?)");
        try
        {
            stmtEurope.setInt(1, TILE_ID_EUROPE);
            stmtEurope.setBytes(2, tileBytes);
            stmtEurope.executeUpdate();
        }
        finally
        {
            stmtEurope.close();
        }

        final PreparedStatement stmtAmerica = connection.prepareStatement("INSERT INTO america VALUES (?, ?)");
        try
        {
            stmtAmerica.setInt(1, TILE_ID_AMERICA);
            stmtAmerica.setBytes(2, tileBytes);
            stmtAmerica.executeUpdate();
        }
        finally
        {
            stmtAmerica.close();
        }

        return connection;
    }

    private void writeTile(BitStreamWriter writer) throws IOException
    {
        // Tile
        writer.writeBits((long)VERSION, 8);
        writer.writeBits(5, 32); // numElementsOffset
        writer.writeBits(NUM_ELEMENTS, 32);

        // offsets
        long offset = BitPositionUtil.bitsToBytes(writer.getBitPosition()) + 4 * NUM_ELEMENTS;
        for (int i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.writeBits(offset, 32);
            final boolean hasItem = i % 2 == 0;
            if (hasItem)
                offset += 8;
            else
                offset += 3;
        }

        for (int i = 0; i < NUM_ELEMENTS; ++i)
        {
            writer.alignTo(8); // aligned because of indexed offsets
            // ItemChoiceHolder
            final boolean hasItem = i %2 == 0; // hasItem == true for even elements
            writer.writeBool(hasItem);
            if (hasItem)
            {
                // Item
                writer.writeBits((long)PARAMS[i], 16);
                //ExtraParamUnion - choiceTag CHOICE_value32
                writer.writeVarUInt64(ExtraParamUnion.CHOICE_value32);
                writer.writeBits(EXTRA_PARAM, 32);
            }
            else
            {
                writer.writeBits(PARAMS[i], 16);
            }
        }
    }

    private void checkTile(Tile tile)
    {
        assertEquals(VERSION, tile.getVersion());
        assertEquals(NUM_ELEMENTS, tile.getNumElements());

        ObjectArray<ItemChoiceHolder> data = tile.getData();
        assertEquals(NUM_ELEMENTS, data.length());

        // element 0
        assertTrue(data.elementAt(0).getHasItem());
        ItemChoice itemChoice0 = data.elementAt(0).getItemChoice();
        assertTrue(itemChoice0.getHasItem());
        Item item0 = itemChoice0.getItem();
        assertEquals(PARAMS[0], item0.getParam());
        assertEquals(ItemType.WITH_EXTRA_PARAM, item0.getItemType());
        assertEquals(ExtraParamUnion.CHOICE_value32, item0.getExtraParam().choiceTag());
        assertEquals(EXTRA_PARAM, item0.getExtraParam().getValue32());

        // element 1
        assertFalse(data.elementAt(1).getHasItem());
        ItemChoice itemChoice1 = data.elementAt(1).getItemChoice();
        assertFalse(itemChoice1.getHasItem());
        assertEquals(PARAMS[1], itemChoice1.getParam());
    }

    private static final int TILE_ID_EUROPE = 99;
    private static final int TILE_ID_AMERICA = 11;
    private static final short VERSION = 8;
    private static final long NUM_ELEMENTS = 2;
    private static final int PARAMS[] = { 13, 21 };
    private static final long EXTRA_PARAM = 42;
}
