import unittest
import os
import apsw
import zserio

from testutils import (getZserioApi, getApiDir,
                       assertMethodPresent, assertMethodNotPresent, assertPropertyPresent)

class WithoutWriterCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "without_writer_code.zs", extraArgs=["-withoutWriterCode"])

    def testItemTypeMethods(self):
        userType = self.api.ItemType

        assertMethodNotPresent(self, userType, "initialize_offsets")
        assertMethodNotPresent(self, userType, "write")

        assertMethodPresent(self, userType, "from_reader")
        assertMethodPresent(self, userType, "bitsizeof")

    def testVersionAvailabilityMethods(self):
        userType = self.api.VersionAvailability

        assertMethodNotPresent(self, userType, "initialize_offsets")
        assertMethodNotPresent(self, userType, "write")

        assertMethodPresent(self, userType, "__init__")
        assertMethodPresent(self, userType, "from_value")
        assertMethodPresent(self, userType, "from_reader")
        assertMethodPresent(self, userType, "__eq__")
        assertMethodPresent(self, userType, "__hash__")
        assertMethodPresent(self, userType, "__str__")
        assertMethodPresent(self, userType, "__or__")
        assertMethodPresent(self, userType, "__and__")
        assertMethodPresent(self, userType, "__xor__")
        assertMethodPresent(self, userType, "__invert__")

        assertPropertyPresent(self, userType, "value", readOnly=True)

    def testExtraParamUnionMethods(self):
        userType = self.api.ExtraParamUnion

        assertMethodNotPresent(self, userType, "initialize_offsets")
        assertMethodNotPresent(self, userType, "write")

        assertMethodPresent(self, userType, "__init__")
        assertMethodPresent(self, userType, "read")
        assertMethodPresent(self, userType, "from_reader")
        assertMethodPresent(self, userType, "bitsizeof")

        assertPropertyPresent(self, userType, "choice_tag", readOnly=True)
        assertPropertyPresent(self, userType, "value16", readOnly=True)
        assertPropertyPresent(self, userType, "value32", readOnly=True)

    def testItemMethods(self):
        userType = self.api.Item

        assertMethodNotPresent(self, userType, "is_extra_param_set")
        assertMethodNotPresent(self, userType, "initialize_offsets")
        assertMethodNotPresent(self, userType, "write")
        assertMethodNotPresent(self, userType, "from_fields")

        assertMethodPresent(self, userType, "__init__")
        assertMethodPresent(self, userType, "read")
        assertMethodPresent(self, userType, "from_reader")
        assertMethodPresent(self, userType, "bitsizeof")

        assertPropertyPresent(self, userType, "item_type", readOnly=True)
        assertPropertyPresent(self, userType, "param", readOnly=True)
        assertPropertyPresent(self, userType, "extra_param", readOnly=True)

    def testItemChoiceMethods(self):
        userType = self.api.ItemChoice

        assertMethodNotPresent(self, userType, "initialize_offsets")
        assertMethodNotPresent(self, userType, "write")

        assertMethodPresent(self, userType, "__init__")
        assertMethodPresent(self, userType, "read")
        assertMethodPresent(self, userType, "from_reader")
        assertMethodPresent(self, userType, "bitsizeof")

        assertPropertyPresent(self, userType, "has_item", readOnly=True)
        assertPropertyPresent(self, userType, "item", readOnly=True)
        assertPropertyPresent(self, userType, "param", readOnly=True)

    def testItemChoiceHolderMethods(self):
        userType = self.api.ItemChoiceHolder

        assertMethodNotPresent(self, userType, "initialize_offsets")
        assertMethodNotPresent(self, userType, "write")
        assertMethodNotPresent(self, userType, "from_fields")

        assertMethodPresent(self, userType, "__init__")
        assertMethodPresent(self, userType, "read")
        assertMethodPresent(self, userType, "from_reader")
        assertMethodPresent(self, userType, "bitsizeof")

        assertPropertyPresent(self, userType, "has_item", readOnly=True)
        assertPropertyPresent(self, userType, "item_choice", readOnly=True)

    def testTileMethods(self):
        userType = self.api.Tile

        assertMethodNotPresent(self, userType, "is_version_set")
        assertMethodNotPresent(self, userType, "is_version_string_set")
        assertMethodNotPresent(self, userType, "initialize_offsets")
        assertMethodNotPresent(self, userType, "write")
        assertMethodNotPresent(self, userType, "from_fields")

        assertMethodPresent(self, userType, "__init__")
        assertMethodPresent(self, userType, "read")
        assertMethodPresent(self, userType, "from_reader")
        assertMethodPresent(self, userType, "bitsizeof")

        assertPropertyPresent(self, userType, "version", readOnly=True)
        assertPropertyPresent(self, userType, "num_elements_offset", readOnly=True)
        assertPropertyPresent(self, userType, "num_elements", readOnly=True)
        assertPropertyPresent(self, userType, "data", readOnly=True)

    def testGeoMapTableMethods(self):
        userType = self.api.GeoMapTable

        assertMethodNotPresent(self, userType, "create_table")
        assertMethodNotPresent(self, userType, "delete_table")
        assertMethodNotPresent(self, userType, "write")
        assertMethodNotPresent(self, userType, "update")

        assertMethodPresent(self, userType, "__init__")
        assertMethodPresent(self, userType, "read")

    def testWorldDbMethods(self):
        userType = self.api.WorldDb

        assertMethodNotPresent(self, userType, "create_schema")
        assertMethodNotPresent(self, userType, "delete_schema")

        assertMethodPresent(self, userType, "__init__")
        assertMethodPresent(self, userType, "from_file")
        assertMethodPresent(self, userType, "close")

        assertPropertyPresent(self, userType, "europe", readOnly=True)
        assertPropertyPresent(self, userType, "america", readOnly=True)
        assertPropertyPresent(self, userType, "connection", readOnly=True)

        # static constants
        self.assertTrue(hasattr(userType, "DATABASE_NAME"))
        self.assertTrue(hasattr(userType, "TABLE_NAME_EUROPE"))
        self.assertTrue(hasattr(userType, "TABLE_NAME_AMERICA"))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeTile(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        tile = self.api.Tile()
        tile.read(reader)

        self._checkTile(tile)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        self._writeTile(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        tile = self.api.Tile.from_reader(reader)

        self._checkTile(tile)

    def testReadFile(self):
        writer = zserio.BitStreamWriter()
        self._writeTile(writer)
        writer.to_file(BLOB_NAME)

        tile = zserio.deserialize_from_file(self.api.Tile, BLOB_NAME)
        self._checkTile(tile)

    def testReadWorldDb(self):
        connection = self._createWorldDb()

        worldDb = self.api.WorldDb(connection)

        europe = worldDb.europe
        europeRows = europe.read()
        count = 0
        for row in europeRows:
            self.assertEqual(TILE_ID_EUROPE, row[0])
            self._checkTile(row[1])
            count += 1
        self.assertEqual(1, count)

        america = worldDb.america
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
        blob = writer.byte_array

        cursor.execute("INSERT INTO europe VALUES(?, ?)", (TILE_ID_EUROPE, blob))
        cursor.execute("INSERT INTO america VALUES(?, ?)", (TILE_ID_AMERICA, blob))

        return connection

    def _writeTile(self, writer):
        # Tile
        writer.write_bits(VERSION_AVAILABILITY, 3)
        writer.write_bits(VERSION, 8)
        writer.write_bits(6, 32) # numElementsOffset
        writer.alignto(8)
        writer.write_bits(NUM_ELEMENTS, 32)

        # offsets
        # offset of the first element
        offset = zserio.bitposition.bits_to_bytes(writer.bitposition) + 4 * NUM_ELEMENTS
        for i in range(NUM_ELEMENTS):
            writer.write_bits(offset, 32)
            hasItem = i % 2 == 0 # hasItem == True for even elements
            if hasItem:
                offset += 8
            else:
                offset += 3

        for i in range(NUM_ELEMENTS):
            writer.alignto(8) # aligned because of indexed offsets
            # ItemChoiceHolder
            hasItem = i % 2 == 0 # hasItem == True for even elements
            writer.write_bool(hasItem)
            if hasItem:
                # Item
                writer.write_bits(PARAMS[i], 16)
                # ExtraParamUnion - choiceTag CHOICE_VALUE32
                writer.write_varsize(self.api.ExtraParamUnion.CHOICE_VALUE32)
                writer.write_bits(EXTRA_PARAM, 32)
            else:
                writer.write_bits(PARAMS[i], 16)

    def _checkTile(self, tile):
        self.assertEqual(VERSION, tile.version)
        self.assertEqual(NUM_ELEMENTS, tile.num_elements)

        data = tile.data
        self.assertEqual(NUM_ELEMENTS, len(data))

        # element 0
        self.assertTrue(data[0].has_item)
        itemChoice0 = data[0].item_choice
        self.assertTrue(itemChoice0.has_item)
        item0 = itemChoice0.item
        self.assertEqual(PARAMS[0], item0.param)
        self.assertEqual(self.api.ItemType.WITH_EXTRA_PARAM, item0.item_type)
        self.assertEqual(self.api.ExtraParamUnion.CHOICE_VALUE32, item0.extra_param.choice_tag)
        self.assertEqual(EXTRA_PARAM, item0.extra_param.value32)

        # element 1
        self.assertFalse(data[1].has_item)
        itemChoice1 = data[1].item_choice
        self.assertFalse(itemChoice1.has_item)
        self.assertEqual(PARAMS[1], itemChoice1.param)

BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "without_writer_code.blob")

TILE_ID_EUROPE = 99
TILE_ID_AMERICA = 11
VERSION_AVAILABILITY = 1
VERSION = 8
NUM_ELEMENTS = 2
PARAMS = [13, 21]
EXTRA_PARAM = 42
