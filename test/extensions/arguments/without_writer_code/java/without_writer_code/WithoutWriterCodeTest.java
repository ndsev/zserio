package without_writer_code;

import static org.junit.jupiter.api.Assertions.*;

import static test_utils.AssertionUtils.assertMethodNotPresent;
import static test_utils.AssertionUtils.assertMethodPresent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.Test;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class WithoutWriterCodeTest
{
    @Test
    public void checkItemTypeMethods()
    {
        assertMethodNotPresent(ItemType.class, "initializeOffsets(");
        assertMethodNotPresent(ItemType.class, "write(");

        assertMethodPresent(ItemType.class, "ItemType(");
        assertMethodPresent(ItemType.class, "getValue()");
        assertMethodPresent(ItemType.class, "getGenericValue()");
        assertMethodPresent(ItemType.class, "readEnum(zserio.runtime.io.BitStreamReader)");
        assertMethodPresent(ItemType.class, "toEnum(byte)");
        assertMethodPresent(ItemType.class, "bitSizeOf()");
        assertMethodPresent(ItemType.class, "bitSizeOf(long)");
    }

    @Test
    public void checkVersionAvailabilityMethods()
    {
        assertMethodNotPresent(VersionAvailability.class, "initializeOffsets(");
        assertMethodNotPresent(VersionAvailability.class, "write(");

        assertMethodPresent(VersionAvailability.class, "VersionAvailability()");
        assertMethodPresent(VersionAvailability.class, "VersionAvailability(byte)");
        assertMethodPresent(VersionAvailability.class, "VersionAvailability(zserio.runtime.io.BitStreamReader");
        assertMethodPresent(VersionAvailability.class, "bitSizeOf()");
        assertMethodPresent(VersionAvailability.class, "bitSizeOf(long)");
        assertMethodPresent(VersionAvailability.class, "equals(java.lang.Object)");
        assertMethodPresent(VersionAvailability.class, "hashCode()");
        assertMethodPresent(VersionAvailability.class, "toString()");
        assertMethodPresent(VersionAvailability.class, "getValue()");
        assertMethodPresent(VersionAvailability.class, "getGenericValue()");
        assertMethodPresent(VersionAvailability.class, "or(without_writer_code.VersionAvailability)");
        assertMethodPresent(VersionAvailability.class, "and(without_writer_code.VersionAvailability)");
        assertMethodPresent(VersionAvailability.class, "xor(without_writer_code.VersionAvailability)");
        assertMethodPresent(VersionAvailability.class, "not()");
    }

    @Test
    public void checkExtraParamUnionMethods()
    {
        assertMethodNotPresent(ExtraParamUnion.class, "ExtraParamUnion()");
        assertMethodNotPresent(ExtraParamUnion.class, "initializeOffsets(");
        assertMethodNotPresent(ExtraParamUnion.class, "write(");

        assertMethodPresent(ExtraParamUnion.class, "ExtraParamUnion(zserio.runtime.io.BitStreamReader");
        assertMethodPresent(ExtraParamUnion.class, "bitSizeOf()");
        assertMethodPresent(ExtraParamUnion.class, "bitSizeOf(long)");
        assertMethodPresent(ExtraParamUnion.class, "choiceTag()");
        assertMethodPresent(ExtraParamUnion.class, "equals(java.lang.Object)");
        assertMethodPresent(ExtraParamUnion.class, "hashCode()");
        assertMethodPresent(ExtraParamUnion.class, "getValue16()");
        assertMethodPresent(ExtraParamUnion.class, "getValue32()");
        assertMethodPresent(ExtraParamUnion.class, "read(zserio.runtime.io.BitStreamReader)");
    }

    @Test
    public void checkItemMethods()
    {
        assertMethodNotPresent(Item.class, "Item(without_writer_code.ItemType");
        assertMethodNotPresent(Item.class, "initializeOffsets(");
        assertMethodNotPresent(Item.class, "write(");
        assertMethodNotPresent(Item.class, "setExtraParam(");
        assertMethodNotPresent(Item.class, "isExtraParamSet(");
        assertMethodNotPresent(Item.class, "setParam(");

        assertMethodPresent(Item.class, "Item(zserio.runtime.io.BitStreamReader");
        assertMethodPresent(Item.class, "bitSizeOf()");
        assertMethodPresent(Item.class, "bitSizeOf(long)");
        assertMethodPresent(Item.class, "equals(java.lang.Object)");
        assertMethodPresent(Item.class, "hashCode()");
        assertMethodPresent(Item.class, "getExtraParam()");
        assertMethodPresent(Item.class, "getItemType()");
        assertMethodPresent(Item.class, "getParam()");
        assertMethodPresent(Item.class, "isExtraParamUsed()");
        assertMethodPresent(Item.class, "read(zserio.runtime.io.BitStreamReader");
    }

    @Test
    public void checkItemChoiceMethods()
    {
        assertMethodNotPresent(ItemChoice.class, "ItemChoice(boolean)");
        assertMethodNotPresent(ItemChoice.class, "initializeOffsets(");
        assertMethodNotPresent(ItemChoice.class, "write(");
        assertMethodNotPresent(ItemChoice.class, "setItem(");
        assertMethodNotPresent(ItemChoice.class, "setParam(");

        assertMethodPresent(ItemChoice.class, "ItemChoice(zserio.runtime.io.BitStreamReader");
        assertMethodPresent(ItemChoice.class, "bitSizeOf()");
        assertMethodPresent(ItemChoice.class, "bitSizeOf(long)");
        assertMethodPresent(ItemChoice.class, "equals(java.lang.Object)");
        assertMethodPresent(ItemChoice.class, "hashCode()");
        assertMethodPresent(ItemChoice.class, "getHasItem()");
        assertMethodPresent(ItemChoice.class, "getItem()");
        assertMethodPresent(ItemChoice.class, "getParam()");
        assertMethodPresent(ItemChoice.class, "read(zserio.runtime.io.BitStreamReader)");
    }

    @Test
    public void checkItemChoiceHolderMethods()
    {
        assertMethodNotPresent(ItemChoiceHolder.class, "ItemChoiceHolder()");
        assertMethodNotPresent(ItemChoiceHolder.class, "ItemChoiceHolder(boolean");
        assertMethodNotPresent(ItemChoiceHolder.class, "initializeOffsets(");
        assertMethodNotPresent(ItemChoiceHolder.class, "write(");
        assertMethodNotPresent(ItemChoiceHolder.class, "setHasItem(");
        assertMethodNotPresent(ItemChoiceHolder.class, "setItemChoice(");

        assertMethodPresent(ItemChoiceHolder.class, "ItemChoiceHolder(zserio.runtime.io.BitStreamReader)");
        assertMethodPresent(ItemChoiceHolder.class, "bitSizeOf()");
        assertMethodPresent(ItemChoiceHolder.class, "bitSizeOf(long)");
        assertMethodPresent(ItemChoiceHolder.class, "equals(java.lang.Object)");
        assertMethodPresent(ItemChoiceHolder.class, "hashCode()");
        assertMethodPresent(ItemChoiceHolder.class, "getHasItem()");
        assertMethodPresent(ItemChoiceHolder.class, "getItemChoice()");
        assertMethodPresent(ItemChoiceHolder.class, "read(zserio.runtime.io.BitStreamReader)");
    }

    @Test
    public void checkTile()
    {
        assertMethodNotPresent(Tile.class, "Tile()");
        assertMethodNotPresent(Tile.class, "Tile(short");
        assertMethodNotPresent(Tile.class, "initializeOffsets(");
        assertMethodNotPresent(Tile.class, "write(");
        assertMethodNotPresent(Tile.class, "setVersion(");
        assertMethodNotPresent(Tile.class, "isVersionSet(");
        assertMethodNotPresent(Tile.class, "setVersionString(");
        assertMethodNotPresent(Tile.class, "isVersionStringSet(");
        assertMethodNotPresent(Tile.class, "setOptionalVersionInfo(");
        assertMethodNotPresent(Tile.class, "isOptionalVersionInfoSet(");
        assertMethodNotPresent(Tile.class, "setNumElementsOffset(");
        assertMethodNotPresent(Tile.class, "setVersionString(");
        assertMethodNotPresent(Tile.class, "isVersionStringSet(");
        assertMethodNotPresent(Tile.class, "setNumElements(");
        assertMethodNotPresent(Tile.class, "setData(");

        assertMethodPresent(Tile.class, "Tile(zserio.runtime.io.BitStreamReader)");
        assertMethodPresent(Tile.class, "bitSizeOf()");
        assertMethodPresent(Tile.class, "bitSizeOf(long)");
        assertMethodPresent(Tile.class, "equals(java.lang.Object)");
        assertMethodPresent(Tile.class, "hashCode()");
        assertMethodPresent(Tile.class, "getData()");
        assertMethodPresent(Tile.class, "getNumElementsOffset()");
        assertMethodPresent(Tile.class, "getNumElements()");
        assertMethodPresent(Tile.class, "getVersion()");
        assertMethodPresent(Tile.class, "getVersionString()");
        assertMethodPresent(Tile.class, "getOptionalVersionInfo()");
        assertMethodPresent(Tile.class, "read(zserio.runtime.io.BitStreamReader)");
    }

    @Test
    public void checkGeoMapTableMethods()
    {
        assertMethodNotPresent(GeoMapTable.class, "createTable()");
        assertMethodNotPresent(GeoMapTable.class, "createOrdinaryRowIdTable");
        assertMethodNotPresent(GeoMapTable.class, "deleteTable()");
        assertMethodNotPresent(GeoMapTable.class, "write(");
        assertMethodNotPresent(GeoMapTable.class, "update(");
        assertMethodNotPresent(GeoMapTable.class, "getCreateTableQuery(");
        assertMethodNotPresent(GeoMapTable.class, "writeRow(");
        assertMethodNotPresent(GeoMapTable.class, "startTransaction(");
        assertMethodNotPresent(GeoMapTable.class, "endTransaction(");

        assertMethodPresent(GeoMapTable.class, "GeoMapTable(java.sql.Connection");
        assertMethodPresent(GeoMapTable.class, "readRow(");
        assertMethodPresent(GeoMapTable.class, "read()");
        assertMethodPresent(GeoMapTable.class, "read(java.lang.String)");
    }

    @Test
    public void checkGeoMapTableRowMethods()
    {
        assertMethodPresent(GeoMapTableRow.class, "GeoMapTableRow()");
        assertMethodPresent(GeoMapTableRow.class, "getTile()");
        assertMethodPresent(GeoMapTableRow.class, "getTileId()");
        assertMethodPresent(GeoMapTableRow.class, "isNullTile()");
        assertMethodPresent(GeoMapTableRow.class, "isNullTileId()");
        assertMethodPresent(GeoMapTableRow.class, "setNullTile()");
        assertMethodPresent(GeoMapTableRow.class, "setNullTileId()");
        assertMethodPresent(GeoMapTableRow.class, "setTile(");
        assertMethodPresent(GeoMapTableRow.class, "setTileId(");
    }

    @Test
    public void checkWorldDbMethods()
    {
        assertMethodNotPresent(WorldDb.class, "createSchema(");
        assertMethodNotPresent(WorldDb.class, "deleteSchema(");
        assertMethodNotPresent(WorldDb.class, "startTransaction(");
        assertMethodNotPresent(WorldDb.class, "endTransaction(");

        assertMethodPresent(WorldDb.class, "WorldDb(java.sql.Connection)");
        assertMethodPresent(WorldDb.class, "WorldDb(java.sql.Connection,");
        assertMethodPresent(WorldDb.class, "WorldDb(java.lang.String)");
        assertMethodPresent(WorldDb.class, "WorldDb(java.lang.String,");
        assertMethodPresent(WorldDb.class, "tableNames()");
        assertMethodPresent(WorldDb.class, "databaseName()");
        assertMethodPresent(WorldDb.class, "getAmerica()");
        assertMethodPresent(WorldDb.class, "getEurope()");
        assertMethodPresent(WorldDb.class, "initTables(");
    }

    @Test
    public void checkValueTypeMethods() throws IOException
    {
        assertMethodNotPresent(Type.class, "Type()");
        assertMethodNotPresent(Type.class, "Type(long)");
        assertMethodNotPresent(Type.class, "setValue(long)");
        assertMethodNotPresent(Type.class, "initializeOffsets(");
        assertMethodNotPresent(Type.class, "write(");

        assertMethodNotPresent(Value.class, "Value(without_writer_code.Type");
        assertMethodNotPresent(Value.class, "setIsValid(boolean)");
        assertMethodNotPresent(Value.class, "setValue(");
        assertMethodNotPresent(Value.class, "isValueSet()");
        assertMethodNotPresent(Value.class, "resetValue()");
        assertMethodNotPresent(Value.class, "initializeOffsets(");
        assertMethodNotPresent(Value.class, "write(");
    }

    @Test
    public void readConstructor() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writeTile(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final Tile tile = new Tile(reader);
        checkTile(tile);
    }

    @Test
    public void readFile() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writeTile(writer);

        final byte[] bytes = writer.toByteArray();
        try (final FileOutputStream outputStream = new FileOutputStream(new File(BLOB_NAME)))
        {
            outputStream.write(bytes);
            outputStream.flush();
        }

        final Tile tile = SerializeUtil.deserializeFromFile(Tile.class, BLOB_NAME);
        checkTile(tile);
    }

    @Test
    public void readWorldDb() throws SQLException, IOException
    {
        try (final Connection connection = createWorldDb(); final WorldDb worldDb = new WorldDb(connection);)
        {
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
        }
    }

    private Connection createWorldDb() throws SQLException, IOException
    {
        final String uriPath = "jdbc:sqlite::memory:";
        final Properties connectionProps = new Properties();
        connectionProps.setProperty("flags", "CREATE");

        final Connection connection = DriverManager.getConnection(uriPath, connectionProps);

        try (final Statement statement = connection.createStatement())
        {
            statement.executeUpdate("CREATE TABLE europe(tileId INTEGER PRIMARY KEY, tile BLOB)");
            statement.executeUpdate("CREATE TABLE america(tileId INTEGER PRIMARY KEY, tile BLOB)");
        }

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writeTile(writer);
        final byte[] tileBytes = writer.toByteArray();

        try (final PreparedStatement stmtEurope = connection.prepareStatement(
                     "INSERT INTO europe VALUES (?, ?)");)
        {
            stmtEurope.setInt(1, TILE_ID_EUROPE);
            stmtEurope.setBytes(2, tileBytes);
            stmtEurope.executeUpdate();
        }

        try (final PreparedStatement stmtAmerica = connection.prepareStatement(
                     "INSERT INTO america VALUES (?, ?)");)
        {
            stmtAmerica.setInt(1, TILE_ID_AMERICA);
            stmtAmerica.setBytes(2, tileBytes);
            stmtAmerica.executeUpdate();
        }

        return connection;
    }

    private void writeTile(BitStreamWriter writer) throws IOException
    {
        // Tile
        writer.writeBits(VERSION_AVAILABILITY, 3);
        writer.writeBits((long)VERSION, 8);
        writer.writeBool(true);
        writer.writeBits((long)OPTIONAL_VERSION_INFO, 8);
        writer.writeBits(3 + 4, 32); // numElementsOffset
        writer.alignTo(8);
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
            final boolean hasItem = i % 2 == 0; // hasItem == true for even elements
            writer.writeBool(hasItem);
            if (hasItem)
            {
                // Item
                writer.writeBits((long)PARAMS[i], 16);
                // ExtraParamUnion - choiceTag CHOICE_value32
                writer.writeVarSize(ExtraParamUnion.CHOICE_value32);
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
        assertEquals(VERSION_AVAILABILITY, tile.getVersionAvailability().getValue());
        assertEquals(VERSION, tile.getVersion().shortValue());
        assertEquals(OPTIONAL_VERSION_INFO, tile.getOptionalVersionInfo().shortValue());
        assertEquals(NUM_ELEMENTS, tile.getNumElements());

        final ItemChoiceHolder[] data = tile.getData();
        assertEquals(NUM_ELEMENTS, data.length);

        // element 0
        assertTrue(data[0].getHasItem());
        ItemChoice itemChoice0 = data[0].getItemChoice();
        assertTrue(itemChoice0.getHasItem());
        Item item0 = itemChoice0.getItem();
        assertEquals(PARAMS[0], item0.getParam());
        assertEquals(ItemType.WITH_EXTRA_PARAM, item0.getItemType());
        assertEquals(ExtraParamUnion.CHOICE_value32, item0.getExtraParam().choiceTag());
        assertEquals(EXTRA_PARAM, item0.getExtraParam().getValue32());

        // element 1
        assertFalse(data[1].getHasItem());
        ItemChoice itemChoice1 = data[1].getItemChoice();
        assertFalse(itemChoice1.getHasItem());
        assertEquals(PARAMS[1], itemChoice1.getParam());
    }

    private static final String BLOB_NAME = "without_writer_code.blob";
    private static final int TILE_ID_EUROPE = 99;
    private static final int TILE_ID_AMERICA = 11;
    private static final byte VERSION_AVAILABILITY = 1;
    private static final short VERSION = 8;
    private static final short OPTIONAL_VERSION_INFO = 0xBA;
    private static final long NUM_ELEMENTS = 2;
    private static final int PARAMS[] = {13, 21};
    private static final long EXTRA_PARAM = 42;
}
