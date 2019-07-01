import unittest
import apsw
import zserio

from testutils import getZserioApi

class WithoutWriterCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "without_writer_code.zs", extraArgs=["-withoutWriterCode"])

    def testItemTypeMethods(self):
        userType = self.api.ItemType

        self._assertMethodNotPresent(userType, "initializeOffsets")
        self._assertMethodNotPresent(userType, "write")

        self._assertMethodPresent(userType, "fromReader")
        self._assertMethodPresent(userType, "bitSizeOf")

    def testExtraParamUnionMethods(self):
        userType = self.api.ExtraParamUnion

        self._assertMethodNotPresent(userType, "initializeOffsets")
        self._assertMethodNotPresent(userType, "write")
        self._assertMethodNotPresent(userType, "setValue16")
        self._assertMethodNotPresent(userType, "setValue32")

        self._assertMethodPresent(userType, "__init__")
        self._assertMethodPresent(userType, "read")
        self._assertMethodPresent(userType, "fromReader")
        self._assertMethodPresent(userType, "bitSizeOf")
        self._assertMethodPresent(userType, "choiceTag")
        self._assertMethodPresent(userType, "getValue16")
        self._assertMethodPresent(userType, "getValue32")

    def testItemMethods(self):
        userType = self.api.Item

        self._assertMethodNotPresent(userType, "initializeOffsets")
        self._assertMethodNotPresent(userType, "write")
        self._assertMethodNotPresent(userType, "fromFields")
        self._assertMethodNotPresent(userType, "setParam")
        self._assertMethodNotPresent(userType, "setExtraParam")

        self._assertMethodPresent(userType, "__init__")
        self._assertMethodPresent(userType, "read")
        self._assertMethodPresent(userType, "fromReader")
        self._assertMethodPresent(userType, "bitSizeOf")
        self._assertMethodPresent(userType, "getItemType")
        self._assertMethodPresent(userType, "getParam")
        self._assertMethodPresent(userType, "getExtraParam")

    def testItemChoiceMethods(self):
        userType = self.api.ItemChoice

        self._assertMethodNotPresent(userType, "initializeOffsets")
        self._assertMethodNotPresent(userType, "write")
        self._assertMethodNotPresent(userType, "setItem")
        self._assertMethodNotPresent(userType, "setParam")

        self._assertMethodPresent(userType, "__init__")
        self._assertMethodPresent(userType, "read")
        self._assertMethodPresent(userType, "fromReader")
        self._assertMethodPresent(userType, "bitSizeOf")
        self._assertMethodPresent(userType, "getHasItem")
        self._assertMethodPresent(userType, "getItem")
        self._assertMethodPresent(userType, "getParam")

    def testItemChoiceHolderMethods(self):
        userType = self.api.ItemChoiceHolder

        self._assertMethodNotPresent(userType, "initializeOffsets")
        self._assertMethodNotPresent(userType, "write")
        self._assertMethodNotPresent(userType, "fromFields")
        self._assertMethodNotPresent(userType, "setHasItem")
        self._assertMethodNotPresent(userType, "setItemChoice")

        self._assertMethodPresent(userType, "__init__")
        self._assertMethodPresent(userType, "read")
        self._assertMethodPresent(userType, "fromReader")
        self._assertMethodPresent(userType, "bitSizeOf")
        self._assertMethodPresent(userType, "getHasItem")
        self._assertMethodPresent(userType, "getItemChoice")

    def testTileMethods(self):
        userType = self.api.Tile

        self._assertMethodNotPresent(userType, "initializeOffsets")
        self._assertMethodNotPresent(userType, "write")
        self._assertMethodNotPresent(userType, "fromFields")
        self._assertMethodNotPresent(userType, "setVersion")
        self._assertMethodNotPresent(userType, "setNumElementsOffset")
        self._assertMethodNotPresent(userType, "setNumElements")
        self._assertMethodNotPresent(userType, "setData")

        self._assertMethodPresent(userType, "__init__")
        self._assertMethodPresent(userType, "read")
        self._assertMethodPresent(userType, "fromReader")
        self._assertMethodPresent(userType, "bitSizeOf")
        self._assertMethodPresent(userType, "getVersion")
        self._assertMethodPresent(userType, "getNumElementsOffset")
        self._assertMethodPresent(userType, "getNumElements")
        self._assertMethodPresent(userType, "getData")

    def testGeoMapTableMethods(self):
        userType = self.api.GeoMapTable

        self._assertMethodNotPresent(userType, "createTable")
        self._assertMethodNotPresent(userType, "deleteTable")
        self._assertMethodNotPresent(userType, "write")
        self._assertMethodNotPresent(userType, "update")

        self._assertMethodPresent(userType, "__init__")
        self._assertMethodPresent(userType, "read")

    def testWorldDbMethods(self):
        userType = self.api.WorldDb

        self._assertMethodNotPresent(userType, "createSchema")
        self._assertMethodNotPresent(userType, "deleteSchema")

        self._assertMethodPresent(userType, "__init__")
        self._assertMethodPresent(userType, "fromFile")
        self._assertMethodPresent(userType, "close")
        self._assertMethodPresent(userType, "getEurope")
        self._assertMethodPresent(userType, "getAmerica")
        self._assertMethodPresent(userType, "connection")

        # static constants
        self.assertTrue(hasattr(userType, "DATABASE_NAME"))
        self.assertTrue(hasattr(userType, "EUROPE_TABLE_NAME"))
        self.assertTrue(hasattr(userType, "AMERICA_TABLE_NAME"))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeTile(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        tile = self.api.Tile()
        tile.read(reader)

        self._checkTile(tile)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()

        self._writeTile(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        tile = self.api.Tile.fromReader(reader)

        self._checkTile(tile)

    def testReadWorldDb(self):
        connection = self._createWorldDb()

        worldDb = self.api.WorldDb(connection)

        europe = worldDb.getEurope()
        europeRows = europe.read()
        count = 0
        for row in europeRows:
            self.assertEqual(TILE_ID_EUROPE, row[0])
            self._checkTile(row[1])
            count += 1
        self.assertEqual(1, count)

        america = worldDb.getAmerica()
        americaRows = america.read()
        count = 0
        for row in americaRows:
            self.assertEqual(TILE_ID_AMERICA, row[0])
            self._checkTile(row[1])
            count += 1
        self.assertEqual(1, count)

    def _createWorldDb(self):
        connection = apsw.Connection(":memory:")
        cursor = connection.cursor()

        cursor.execute("CREATE TABLE europe(tileId INTEGER PRIMARY KEY, tile BLOB)")
        cursor.execute("CREATE TABLE america(tileId INTEGER PRIMARY KEY, tile BLOB)")

        writer = zserio.BitStreamWriter()
        self._writeTile(writer)
        blob = writer.getByteArray()

        cursor.execute("INSERT INTO europe VALUES(?, ?)", (TILE_ID_EUROPE, blob))
        cursor.execute("INSERT INTO america VALUES(?, ?)", (TILE_ID_AMERICA, blob))

        return connection

    def _writeTile(self, writer):
        # Tile
        writer.writeBits(VERSION, 8)
        writer.writeBits(5, 32) # numElementsOffset
        writer.writeBits(NUM_ELEMENTS, 32)

        # offsets
        # offset of the first element
        offset = zserio.bitposition.bitsToBytes(writer.getBitPosition()) + 4 * NUM_ELEMENTS
        for i in range(NUM_ELEMENTS):
            writer.writeBits(offset, 32)
            hasItem = i % 2 == 0 # hasItem == True for even elements
            if hasItem:
                offset += 8
            else:
                offset += 3

        for i in range(NUM_ELEMENTS):
            writer.alignTo(8) # aligned because of indexed offsets
            # ItemChoiceHolder
            hasItem = i % 2 == 0 # hasItem == True for even elements
            writer.writeBool(hasItem)
            if hasItem:
                # Item
                writer.writeBits(PARAMS[i], 16)
                # ExtraParamUnion - choiceTag CHOICE_value32
                writer.writeVarUInt64(self.api.ExtraParamUnion.CHOICE_value32)
                writer.writeBits(EXTRA_PARAM, 32)
            else:
                writer.writeBits(PARAMS[i], 16)

    def _checkTile(self, tile):
        self.assertEqual(VERSION, tile.getVersion())
        self.assertEqual(NUM_ELEMENTS, tile.getNumElements())

        data = tile.getData()
        self.assertEqual(NUM_ELEMENTS, len(data))

        # element 0
        self.assertTrue(data[0].getHasItem())
        itemChoice0 = data[0].getItemChoice()
        self.assertTrue(itemChoice0.getHasItem())
        item0 = itemChoice0.getItem()
        self.assertEqual(PARAMS[0], item0.getParam())
        self.assertEqual(self.api.ItemType.WITH_EXTRA_PARAM, item0.getItemType())
        self.assertEqual(self.api.ExtraParamUnion.CHOICE_value32, item0.getExtraParam().choiceTag())
        self.assertEqual(EXTRA_PARAM, item0.getExtraParam().getValue32())

        # element 1
        self.assertFalse(data[1].getHasItem())
        itemChoice1 = data[1].getItemChoice()
        self.assertFalse(itemChoice1.getHasItem())
        self.assertEqual(PARAMS[1], itemChoice1.getParam())

    def _assertMethodNotPresent(self, userType, method):
        self.assertFalse(hasattr(userType, method),
                         msg=("Method '%s' is present in '%s'!" % (method, userType.__name__)))

    def _assertMethodPresent(self, userType, method):
        self.assertTrue(hasattr(userType, method),
                        msg=("Method '%s' is not present in '%s'!" % (method, userType.__name__)))

TILE_ID_EUROPE = 99
TILE_ID_AMERICA = 11
VERSION = 8
NUM_ELEMENTS = 2
PARAMS = [13, 21]
EXTRA_PARAM = 42
