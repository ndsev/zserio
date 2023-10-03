import unittest

import zserio

from zserio.typeinfo import TypeAttribute

from testutils import getZserioApi

class MultipleRemovedEnumItemsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "enumeration_types.zs",
                               extraArgs=["-withTypeInfoCode"]).multiple_removed_enum_items

    def testValues(self):
        self.assertEqual(NONE_VALUE, self.api.Traffic.NONE.value)
        self.assertEqual(HEAVY_VALUE, self.api.Traffic.ZSERIO_REMOVED_HEAVY.value)
        self.assertEqual(MID_VALUE, self.api.Traffic.ZSERIO_REMOVED_MID.value)
        self.assertEqual(LIGHT_VALUE, self.api.Traffic.ZSERIO_REMOVED_LIGHT.value)

    def testFromString(self):
        self.assertEqual(self.api.Traffic.from_name("NONE"), self.api.Traffic.NONE)
        self.assertEqual(self.api.Traffic.from_name("ZSERIO_REMOVED_HEAVY"),
                         self.api.Traffic.ZSERIO_REMOVED_HEAVY)
        self.assertEqual(self.api.Traffic.from_name("ZSERIO_REMOVED_MID"),
                         self.api.Traffic.ZSERIO_REMOVED_MID)
        self.assertEqual(self.api.Traffic.from_name("ZSERIO_REMOVED_LIGHT"),
                         self.api.Traffic.ZSERIO_REMOVED_LIGHT)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Traffic.from_name("HEAVY")
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Traffic.from_name("MID")
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Traffic.from_name("LIGHT")

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        writer.write_bits(self.api.Traffic.NONE.value, TRAFFIC_BIT_SIZE)
        writer.write_bits(self.api.Traffic.ZSERIO_REMOVED_HEAVY.value, TRAFFIC_BIT_SIZE)
        writer.write_bits(self.api.Traffic.ZSERIO_REMOVED_LIGHT.value, TRAFFIC_BIT_SIZE)
        writer.write_bits(self.api.Traffic.ZSERIO_REMOVED_MID.value, TRAFFIC_BIT_SIZE)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)

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
                                                                    self.api.Traffic.ZSERIO_REMOVED_LIGHT))
        self.assertEqual(1706, zserio.hashcode.calc_hashcode_object(zserio.hashcode.HASH_SEED,
                                                                    self.api.Traffic.ZSERIO_REMOVED_MID))

    def testBitSizeOf(self):
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.NONE.bitsizeof())
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.ZSERIO_REMOVED_HEAVY.bitsizeof())
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.ZSERIO_REMOVED_LIGHT.bitsizeof())
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.ZSERIO_REMOVED_MID.bitsizeof())

    def testInitializeOffsets(self):
        self.assertEqual(TRAFFIC_BIT_SIZE, self.api.Traffic.NONE.initialize_offsets(0))
        self.assertEqual(TRAFFIC_BIT_SIZE + 1, self.api.Traffic.ZSERIO_REMOVED_HEAVY.initialize_offsets(1))
        self.assertEqual(TRAFFIC_BIT_SIZE + 2, self.api.Traffic.ZSERIO_REMOVED_LIGHT.initialize_offsets(2))
        self.assertEqual(TRAFFIC_BIT_SIZE + 3, self.api.Traffic.ZSERIO_REMOVED_MID.initialize_offsets(3))

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        self.api.Traffic.NONE.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self.assertEqual(self.api.Traffic.NONE, self.api.Traffic.from_reader(reader))

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Traffic.ZSERIO_REMOVED_HEAVY.write(writer)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Traffic.ZSERIO_REMOVED_LIGHT.write(writer)

        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.Traffic.ZSERIO_REMOVED_MID.write(writer)

    def testTypeInfo(self):
        type_info = self.api.Traffic.type_info()
        self.assertEqual("enumeration_types.multiple_removed_enum_items.Traffic", type_info.schema_name)
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
        self.assertEqual(self.api.Traffic.ZSERIO_REMOVED_LIGHT, item_info.py_item)
        self.assertFalse(item_info.is_deprecated)
        self.assertTrue(item_info.is_removed)

        item_info = items[3]
        self.assertEqual("MID", item_info.schema_name)
        self.assertEqual(self.api.Traffic.ZSERIO_REMOVED_MID, item_info.py_item)
        self.assertFalse(item_info.is_deprecated)
        self.assertTrue(item_info.is_removed)

TRAFFIC_BIT_SIZE = 8

NONE_VALUE = 1
HEAVY_VALUE = 2
LIGHT_VALUE = 3
MID_VALUE = 4
