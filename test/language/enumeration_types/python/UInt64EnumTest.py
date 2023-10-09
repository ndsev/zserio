import zserio

import EnumerationTypes

class UInt64EnumTest(EnumerationTypes.TestCase):
    def testValues(self):
        self.assertEqual(NONE_COLOR_VALUE, self.api.DarkColor.NONE_COLOR.value)
        self.assertEqual(DARK_RED_VALUE, self.api.DarkColor.DARK_RED.value)
        self.assertEqual(DARK_BLUE_VALUE, self.api.DarkColor.DARK_BLUE.value)
        self.assertEqual(DARK_GREEN_VALUE, self.api.DarkColor.DARK_GREEN.value)

    def testFromString(self):
        self.assertEqual(self.api.DarkColor.from_name("NONE_COLOR"), self.api.DarkColor.NONE_COLOR)
        self.assertEqual(self.api.DarkColor.from_name("DARK_RED"), self.api.DarkColor.DARK_RED)
        self.assertEqual(self.api.DarkColor.from_name("DARK_BLUE"), self.api.DarkColor.DARK_BLUE)
        self.assertEqual(self.api.DarkColor.from_name("DARK_GREEN"), self.api.DarkColor.DARK_GREEN)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.DarkColor.from_name("noneColor")

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.write_bits(self.api.DarkColor.DARK_GREEN.value, DARK_COLOR_BITSIZEOF)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        color = self.api.DarkColor.from_reader(reader)
        self.assertEqual(DARK_GREEN_VALUE, color.value)

    def testCalcHashCode(self):
        # use hardcoded values to check that the hash code is stable
        self.assertEqual(1702, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.DarkColor.NONE_COLOR))
        self.assertEqual(1703, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.DarkColor.DARK_RED))
        self.assertEqual(1704, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.DarkColor.DARK_BLUE))
        self.assertEqual(1709, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.DarkColor.DARK_GREEN))

    def testBitSizeOf(self):
        self.assertEqual(DARK_COLOR_BITSIZEOF, self.api.DarkColor.NONE_COLOR.bitsizeof())
        self.assertEqual(DARK_COLOR_BITSIZEOF, self.api.DarkColor.DARK_RED.bitsizeof())
        self.assertEqual(DARK_COLOR_BITSIZEOF, self.api.DarkColor.DARK_BLUE.bitsizeof())
        self.assertEqual(DARK_COLOR_BITSIZEOF, self.api.DarkColor.DARK_GREEN.bitsizeof())

    def testInitializeOffsets(self):
        self.assertEqual(DARK_COLOR_BITSIZEOF, self.api.DarkColor.NONE_COLOR.initialize_offsets(0))
        self.assertEqual(DARK_COLOR_BITSIZEOF + 1, self.api.DarkColor.DARK_RED.initialize_offsets(1))
        self.assertEqual(DARK_COLOR_BITSIZEOF + 2, self.api.DarkColor.DARK_BLUE.initialize_offsets(2))
        self.assertEqual(DARK_COLOR_BITSIZEOF + 3, self.api.DarkColor.DARK_GREEN.initialize_offsets(3))

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        self.api.DarkColor.DARK_RED.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self.assertEqual(DARK_RED_VALUE, reader.read_bits(DARK_COLOR_BITSIZEOF))

DARK_COLOR_BITSIZEOF = 64

NONE_COLOR_VALUE = 0
DARK_RED_VALUE = 1
DARK_BLUE_VALUE = 2
DARK_GREEN_VALUE = 7
