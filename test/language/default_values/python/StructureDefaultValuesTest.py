import unittest

from testutils import getZserioApi

class StructureDefaultValuesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "default_values.zs").structure_default_values

    def testDefaultBoolValue(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(True, structureDefaultValues.bool_value)

    def testDefaultBit4Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(0x0F, structureDefaultValues.bit4_value)

    def testDefaultInt16Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(0x0BEE, structureDefaultValues.int16_value)

    def testDefaultFloat16Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(1.23, structureDefaultValues.float16_value)

    def testDefaultFloat32Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(1.234, structureDefaultValues.float32_value)

    def testDefaultFloat64Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(1.2345, structureDefaultValues.float64_value)

    def testDefaultStringValue(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual("string", structureDefaultValues.string_value)

    def testDefaultEnumValue(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(self.api.BasicColor.BLACK, structureDefaultValues.enum_value)

    def testDefaultBitmaskValue(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(self.api.Permission.Values.READ_WRITE, structureDefaultValues.bitmask_value)
