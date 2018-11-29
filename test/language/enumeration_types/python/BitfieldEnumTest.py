import unittest

import zserio

from testutils import getZserioApi

class BitfieldEnumTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "enumeration_types.zs").bitfield_enum

    def testValues(self):
        self.assertEqual(NONE_VALUE, self.api.Color.NONE.value)
        self.assertEqual(RED_VALUE, self.api.Color.RED.value)
        self.assertEqual(BLUE_VALUE, self.api.Color.BLUE.value)
        self.assertEqual(BLACK_VALUE, self.api.Color.BLACK.value)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.writeBits(self.api.Color.BLACK.value, COLOR_BITSIZEOF)
        byteArray = writer.getByteArray()
        reader = zserio.BitStreamReader(byteArray)
        color = self.api.Color.fromReader(reader)
        self.assertEqual(BLACK_VALUE, color.value)

    def testBitSizeOf(self):
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.NONE.bitSizeOf())
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.RED.bitSizeOf())
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.BLUE.bitSizeOf())
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.BLACK.bitSizeOf())

    def testInitializeOffsets(self):
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.NONE.initializeOffsets(0))
        self.assertEqual(COLOR_BITSIZEOF + 1, self.api.Color.RED.initializeOffsets(1))
        self.assertEqual(COLOR_BITSIZEOF + 2, self.api.Color.BLUE.initializeOffsets(2))
        self.assertEqual(COLOR_BITSIZEOF + 3, self.api.Color.BLACK.initializeOffsets(3))

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        self.api.Color.RED.write(writer)
        byteArray = writer.getByteArray()
        reader = zserio.BitStreamReader(byteArray)
        self.assertEqual(RED_VALUE, reader.readBits(COLOR_BITSIZEOF))

COLOR_BITSIZEOF = 3

NONE_VALUE = 0
RED_VALUE = 2
BLUE_VALUE = 3
BLACK_VALUE = 7
