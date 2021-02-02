import unittest
import zserio

from testutils import getZserioApi

class SimpleParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").simple_param

    def testParameterConstructor(self):
        item = self.api.Item(self.LOWER_VERSION)
        self.assertFalse(item.isExtraParamOptionalClauseMet())

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        self._writeItemToStream(writer, self.HIGHER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        reader = zserio.BitStreamReader(writer.getByteArray())
        item = self.api.Item.fromReader(reader, self.HIGHER_VERSION)
        self.assertEqual(self.ITEM_PARAM, item.getParam())
        self.assertTrue(item.isExtraParamOptionalClauseMet())
        self.assertEqual(self.ITEM_EXTRA_PARAM, item.getExtraParam())

        item = self.api.Item(self.HIGHER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        self.assertEqual(self.ITEM_PARAM, item.getParam())
        self.assertTrue(item.isExtraParamOptionalClauseMet())
        self.assertEqual(self.ITEM_EXTRA_PARAM, item.getExtraParam())

    def testEq(self):
        item1 = self.api.Item(self.LOWER_VERSION)
        item2 = self.api.Item(self.LOWER_VERSION)
        self.assertTrue(item1 == item2)

        item3 = self.api.Item(self.HIGHER_VERSION)
        self.assertFalse(item2 == item3)

    def testHash(self):
        item1 = self.api.Item(self.LOWER_VERSION)
        item2 = self.api.Item(self.LOWER_VERSION)
        self.assertEqual(hash(item1), hash(item2))

        item3 = self.api.Item(self.HIGHER_VERSION)
        self.assertTrue(hash(item2) != hash(item3))

    def testBitSizeOf(self):
        item1 = self.api.Item(self.LOWER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        self.assertEqual(self.ITEM_BIT_SIZE_WITHOUT_OPTIONAL, item1.bitSizeOf())

        item2 = self.api.Item(self.HIGHER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        self.assertEqual(self.ITEM_BIT_SIZE_WITH_OPTIONAL, item2.bitSizeOf())

    def testInitializeOffsets(self):
        item1 = self.api.Item(self.LOWER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        bitPosition = 1
        self.assertEqual(bitPosition + self.ITEM_BIT_SIZE_WITHOUT_OPTIONAL,
                         item1.initializeOffsets(bitPosition))

        item2 = self.api.Item(self.HIGHER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        self.assertEqual(bitPosition + self.ITEM_BIT_SIZE_WITH_OPTIONAL, item2.initializeOffsets(bitPosition))

    def testReadWrite(self):
        version = self.HIGHER_VERSION
        item = self.api.Item(version, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)

        writer = zserio.BitStreamWriter()
        item.write(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkItemInStream(reader, item, version)
        reader.setBitPosition(0)

        readItem = self.api.Item.fromReader(reader, version)
        self.assertEqual(item, readItem)

    def _writeItemToStream(self, writer, version, param, extraParam):
        writer.writeBits(param, 16)
        if version >= self.HIGHER_VERSION:
            writer.writeBits(extraParam, 32)

    def _checkItemInStream(self, reader, item, version):
        self.assertEqual(item.getParam(), reader.readBits(16))
        if version >= self.HIGHER_VERSION:
            self.assertEqual(item.getExtraParam(), reader.readBits(32))

    LOWER_VERSION = 9
    HIGHER_VERSION = 10

    ITEM_PARAM = 0xAA
    ITEM_EXTRA_PARAM = 0xBB

    ITEM_BIT_SIZE_WITHOUT_OPTIONAL = 16
    ITEM_BIT_SIZE_WITH_OPTIONAL = 16 + 32
