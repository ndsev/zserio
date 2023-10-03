import unittest

import zserio

from zserio.typeinfo import TypeAttribute

from testutils import getZserioApi

class RemovedEnumItemTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "enumeration_types.zs",
                               extraArgs=["-withTypeInfoCode"]).removed_enum_item

    def testValues(self):
        self.assertEqual(NONE_VALUE, self.api.Traffic.NONE.value)
        self.assertEqual(HEAVY_VALUE, self.api.Traffic.ZSERIO_REMOVED_HEAVY.value)
        self.assertEqual(MID_VALUE, self.api.Traffic.MID.value)
        self.assertEqual(LIGHT_VALUE, self.api.Traffic.LIGHT.value)

    def testFromString(self):
        self.assertEqual(self.api.Traffic.from_name("NONE"), self.api.Traffic.NONE)
        self.assertEqual(self.api.Traffic.from_name("ZSERIO_REMOVED_HEAVY"),
                         self.api.Traffic.ZSERIO_REMOVED_HEAVY)
        self.assertEqual(self.api.Traffic.from_name("MID"), self.api.Traffic.MID)
        self.assertEqual(self.api.Traffic.from_name("LIGHT"), self.api.Traffic.LIGHT)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Traffic.from_name("HEAVY")

    def testFromReader(self):
        writer = zserio.BitStreamWriter()

        writer.write_bits(self.api.Traffic.NONE.value, TRAFFIC_BIT_SIZE)
        writer.write_bits(self.api.Traffic.ZSERIO_REMOVED_HEAVY.value, TRAFFIC_BIT_SIZE)
        writer.write_bits(self.api.Traffic.LIGHT.value, TRAFFIC_BIT_SIZE)
        writer.write_bits(self.api.Traffic.MID.value, TRAFFIC_BIT_SIZE)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self.assertEqual(NONE_VALUE, self.api.Traffic.from_reader(reader).value)
        self.assertEqual(HEAVY_VALUE, self.api.Traffic.from_reader(reader).value)
        self.assertEqual(LIGHT_VALUE, self.api.Traffic.from_reader(reader).value)
        self.assertEqual(MID_VALUE, self.api.Traffic.from_reader(reader).value)

    def testHashCode(self):
        # use hardcoded values to check that the hash code is stable
        self.assertEqual(1703, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Traffic.NONE))
        self.assertEqual(1704, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Traffic.ZSERIO_REMOVED_HEAVY))
        self.assertEqual(1705, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Traffic.LIGHT))
        self.assertEqual(1706, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Traffic.MID))

    def testBitSizeOf(self):
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.NONE.bitsizeof())
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.ZSERIO_REMOVED_HEAVY.bitsizeof())
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.LIGHT.bitsizeof())
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.MID.bitsizeof())

    def testInitializeOffsets(self):
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.NONE.initialize_offsets(0))
        self.assertEqual(TRAFFIC_BIT_SIZE + 1, self.api.Traffic.ZSERIO_REMOVED_HEAVY.initialize_offsets(1))
        self.assertEqual(TRAFFIC_BIT_SIZE + 2, self.api.Traffic.LIGHT.initialize_offsets(2))
        self.assertEqual(TRAFFIC_BIT_SIZE + 3, self.api.Traffic.MID.initialize_offsets(3))

    def testWrite(self):
        writer = zserio.BitStreamWriter()

        self.api.Traffic.NONE.write(writer)
        self.api.Traffic.LIGHT.write(writer)
        self.api.Traffic.MID.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self.assertEqual(self.api.Traffic.NONE, self.api.Traffic.from_reader(reader))
        self.assertEqual(self.api.Traffic.LIGHT, self.api.Traffic.from_reader(reader))
        self.assertEqual(self.api.Traffic.MID, self.api.Traffic.from_reader(reader))

        with self.assertRaises(zserio.PythonRuntimeException) as context:
            self.api.Traffic.ZSERIO_REMOVED_HEAVY.write(writer)
        self.assertEqual("Trying to write removed enumeration item 'Traffic.ZSERIO_REMOVED_HEAVY'!",
                         str(context.exception))

    def testTypeInfo(self):
        type_info = self.api.Traffic.type_info()
        self.assertEqual("enumeration_types.removed_enum_item.Traffic", type_info.schema_name)
        self.assertEqual(self.api.Traffic, type_info.py_type)
        self.assertEqual(2, len(type_info.attributes))
        self.assertIn(TypeAttribute.UNDERLYING_TYPE, type_info.attributes)
        underlying_info = type_info.attributes[TypeAttribute.UNDERLYING_TYPE]
        self.assertEqual("uint8", underlying_info.schema_name)
        self.assertEqual(int, underlying_info.py_type)
        self.assertFalse(underlying_info.attributes)
        self.assertIn(TypeAttribute.ENUM_ITEMS, type_info.attributes)
        items = type_info.attributes[TypeAttribute.ENUM_ITEMS]
        self.assertEqual(4, len(items))

        item_info = items[0]
        self.assertEqual("NONE", item_info.schema_name)
        self.assertEqual(self.api.Traffic.NONE, item_info.py_item)
        self.assertFalse(item_info.is_deprecated)
        self.assertFalse(item_info.is_removed)

        item_info = items[1]
        self.assertEqual("HEAVY", item_info.schema_name)
        self.assertEqual(self.api.Traffic.ZSERIO_REMOVED_HEAVY, item_info.py_item)
        self.assertFalse(item_info.is_deprecated)
        self.assertTrue(item_info.is_removed)

        item_info = items[2]
        self.assertEqual("LIGHT", item_info.schema_name)
        self.assertEqual(self.api.Traffic.LIGHT, item_info.py_item)
        self.assertFalse(item_info.is_deprecated)
        self.assertFalse(item_info.is_removed)

        item_info = items[3]
        self.assertEqual("MID", item_info.schema_name)
        self.assertEqual(self.api.Traffic.MID, item_info.py_item)
        self.assertFalse(item_info.is_deprecated)
        self.assertFalse(item_info.is_removed)

TRAFFIC_BIT_SIZE = 8

NONE_VALUE = 1
HEAVY_VALUE = 2
LIGHT_VALUE = 3
MID_VALUE = 4
