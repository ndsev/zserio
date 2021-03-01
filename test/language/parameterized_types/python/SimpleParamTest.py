import unittest
import zserio

from testutils import getZserioApi

class SimpleParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").simple_param

    def testParameterConstructor(self):
        item = self.api.Item(self.LOWER_VERSION)
        self.assertFalse(item.is_extra_param_used())

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        self._writeItemToStream(writer, self.HIGHER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        item = self.api.Item.from_reader(reader, self.HIGHER_VERSION)
        self.assertEqual(self.ITEM_PARAM, item.param)
        self.assertTrue(item.is_extra_param_used())
        self.assertEqual(self.ITEM_EXTRA_PARAM, item.extra_param)

        item = self.api.Item(self.HIGHER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        self.assertEqual(self.ITEM_PARAM, item.param)
        self.assertTrue(item.is_extra_param_used())
        self.assertEqual(self.ITEM_EXTRA_PARAM, item.extra_param)

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
        self.assertEqual(self.ITEM_BIT_SIZE_WITHOUT_OPTIONAL, item1.bitsizeof())

        item2 = self.api.Item(self.HIGHER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        self.assertEqual(self.ITEM_BIT_SIZE_WITH_OPTIONAL, item2.bitsizeof())

    def testInitializeOffsets(self):
        item1 = self.api.Item(self.LOWER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        bitPosition = 1
        self.assertEqual(bitPosition + self.ITEM_BIT_SIZE_WITHOUT_OPTIONAL,
                         item1.initialize_offsets(bitPosition))

        item2 = self.api.Item(self.HIGHER_VERSION, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)
        self.assertEqual(bitPosition + self.ITEM_BIT_SIZE_WITH_OPTIONAL, item2.initialize_offsets(bitPosition))

    def testReadWrite(self):
        version = self.HIGHER_VERSION
        item = self.api.Item(version, self.ITEM_PARAM, self.ITEM_EXTRA_PARAM)

        writer = zserio.BitStreamWriter()
        item.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkItemInStream(reader, item, version)
        reader.bitposition = 0

        readItem = self.api.Item.from_reader(reader, version)
        self.assertEqual(item, readItem)

    def _writeItemToStream(self, writer, version, param, extraParam):
        writer.write_bits(param, 16)
        if version >= self.HIGHER_VERSION:
            writer.write_bits(extraParam, 32)

    def _checkItemInStream(self, reader, item, version):
        self.assertEqual(item.param, reader.read_bits(16))
        if version >= self.HIGHER_VERSION:
            self.assertEqual(item.extra_param, reader.read_bits(32))

    LOWER_VERSION = 9
    HIGHER_VERSION = 10

    ITEM_PARAM = 0xAA
    ITEM_EXTRA_PARAM = 0xBB

    ITEM_BIT_SIZE_WITHOUT_OPTIONAL = 16
    ITEM_BIT_SIZE_WITH_OPTIONAL = 16 + 32
