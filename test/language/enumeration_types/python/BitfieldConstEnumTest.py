import unittest

import zserio

from testutils import getZserioApi

class BitfieldEnumTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "enumeration_types.zs",
                               extraArgs=["-withTypeInfoCode"]).bitfield_const_enum

    def testValues(self):
        self.assertEqual(NONE_VALUE, self.api.Color.NONE.value)
        self.assertEqual(RED_VALUE, self.api.Color.RED.value)
        self.assertEqual(BLUE_VALUE, self.api.Color.BLUE.value)
        self.assertEqual(GREEN_VALUE, self.api.Color.GREEN.value)

    def testFromString(self):
        self.assertEqual(self.api.Color.from_name("NONE"), self.api.Color.NONE)
        self.assertEqual(self.api.Color.from_name("RED"), self.api.Color.RED)
        self.assertEqual(self.api.Color.from_name("BLUE"), self.api.Color.BLUE)
        self.assertEqual(self.api.Color.from_name("GREEN"), self.api.Color.GREEN)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Color.from_name("NONEXISTING")

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.write_bits(self.api.Color.GREEN.value, COLOR_BITSIZEOF)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        color = self.api.Color.from_reader(reader)
        self.assertEqual(GREEN_VALUE, color.value)

    def testCalcHashCode(self):
        # use hardcoded values to check that the hash code is stable
        self.assertEqual(1702, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Color.NONE))
        self.assertEqual(1704, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Color.RED))
        self.assertEqual(1705, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Color.BLUE))
        self.assertEqual(1709, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Color.GREEN))

    def testBitSizeOf(self):
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.NONE.bitsizeof())
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.RED.bitsizeof())
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.BLUE.bitsizeof())
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.GREEN.bitsizeof())

    def testInitializeOffsets(self):
        self.assertEqual(COLOR_BITSIZEOF, self.api.Color.NONE.initialize_offsets(0))
        self.assertEqual(COLOR_BITSIZEOF + 1, self.api.Color.RED.initialize_offsets(1))
        self.assertEqual(COLOR_BITSIZEOF + 2, self.api.Color.BLUE.initialize_offsets(2))
        self.assertEqual(COLOR_BITSIZEOF + 3, self.api.Color.GREEN.initialize_offsets(3))

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        self.api.Color.RED.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self.assertEqual(RED_VALUE, reader.read_bits(COLOR_BITSIZEOF))

COLOR_BITSIZEOF = 5

NONE_VALUE = 0
RED_VALUE = 2
BLUE_VALUE = 3
GREEN_VALUE = 7
