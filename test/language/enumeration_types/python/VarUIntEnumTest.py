import unittest

import zserio

from testutils import getZserioApi

class UInt8EnumTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "enumeration_types.zs").varuint_enum

    def testValues(self):
        self.assertEqual(NONE_VALUE, self.api.DarkColor.NONE.value)
        self.assertEqual(DARK_RED_VALUE, self.api.DarkColor.DARK_RED.value)
        self.assertEqual(DARK_BLUE_VALUE, self.api.DarkColor.DARK_BLUE.value)
        self.assertEqual(DARK_GREEN_VALUE, self.api.DarkColor.DARK_GREEN.value)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.write_varuint(self.api.DarkColor.DARK_GREEN.value)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        color = self.api.DarkColor.fromReader(reader)
        self.assertEqual(DARK_GREEN_VALUE, color.value)

    def testBitSizeOf(self):
        self.assertEqual(DARK_COLOR_NONE_BITSIZEOF, self.api.DarkColor.NONE.bitSizeOf())
        self.assertEqual(DARK_COLOR_DARK_RED_BITSIZEOF, self.api.DarkColor.DARK_RED.bitSizeOf())
        self.assertEqual(DARK_COLOR_DARK_BLUE_BITSIZEOF, self.api.DarkColor.DARK_BLUE.bitSizeOf())
        self.assertEqual(DARK_COLOR_DARK_GREEN_BITSIZEOF, self.api.DarkColor.DARK_GREEN.bitSizeOf())

    def testInitializeOffsets(self):
        self.assertEqual(DARK_COLOR_NONE_BITSIZEOF, self.api.DarkColor.NONE.initializeOffsets(0))
        self.assertEqual(DARK_COLOR_DARK_RED_BITSIZEOF + 1, self.api.DarkColor.DARK_RED.initializeOffsets(1))
        self.assertEqual(DARK_COLOR_DARK_BLUE_BITSIZEOF + 2, self.api.DarkColor.DARK_BLUE.initializeOffsets(2))
        self.assertEqual(DARK_COLOR_DARK_GREEN_BITSIZEOF + 3,
                         self.api.DarkColor.DARK_GREEN.initializeOffsets(3))

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        self.api.DarkColor.DARK_RED.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self.assertEqual(DARK_RED_VALUE, reader.read_varuint())

DARK_COLOR_NONE_BITSIZEOF = 8
DARK_COLOR_DARK_RED_BITSIZEOF = 8
DARK_COLOR_DARK_BLUE_BITSIZEOF = 8
DARK_COLOR_DARK_GREEN_BITSIZEOF = 16

NONE_VALUE = 0
DARK_RED_VALUE = 1
DARK_BLUE_VALUE = 2
DARK_GREEN_VALUE = 255
