import unittest

from testutils import getZserioApi

class StructureDefaultValuesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "default_values.zs").structure_default_values

    def testDefaultBoolValue(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(True, structureDefaultValues.getBoolValue())

    def testDefaultBit4Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(0x0F, structureDefaultValues.getBit4Value())

    def testDefaultInt16Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(0x0BEE, structureDefaultValues.getInt16Value())

    def testDefaultFloat16Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(1.23, structureDefaultValues.getFloat16Value())

    def testDefaultFloat32Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(1.234, structureDefaultValues.getFloat32Value())

    def testDefaultFloat64Value(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(1.2345, structureDefaultValues.getFloat64Value())

    def testDefaultStringValue(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual("string", structureDefaultValues.getStringValue())

    def testDefaultEnumValue(self):
        structureDefaultValues = self.api.StructureDefaultValues()
        self.assertEqual(self.api.BasicColor.BLACK, structureDefaultValues.getEnumValue())
