import unittest
import zserio

from testutils import getZserioApi

class WithoutWriterCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "without_writer_code.zs",
                               extraArgs=["-withoutWriterCode"])

    def testItemType(self):
        userType = self.api.ItemType

        self._assertMethodNotPresent(userType, "initializeOffsets")
        self._assertMethodNotPresent(userType, "write")

        self._assertMethodPresent(userType, "fromReader")
        self._assertMethodPresent(userType, "bitSizeOf")

    def testExtraParamUnion(self):
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

    def testItem(self):
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

    def testItemChoice(self):
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

    def testItemChoiceHolder(self):
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

    def testTile(self):
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

    def _writeTile(self, writer):
        # Tile
        writer.writeBits(VERSION, 8)
        writer.writeBits(5, 32) # numElementsOffset
        writer.writeBits(NUM_ELEMENTS, 32)
        for i in range(NUM_ELEMENTS):
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

VERSION = 1
NUM_ELEMENTS = 2
PARAMS = [13, 21]
EXTRA_PARAM = 42
