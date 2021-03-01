import unittest

from testutils import getZserioApi

class StructureNameClashingWithJavaTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "identifiers.zs").structure_name_clashing_with_java

    def testEmptyConstructor(self):
        structureNameClashingWithJava = self.api.StructureNameClashingWithJava()
        self.assertEqual(None, structureNameClashingWithJava.byte_field)
        self.assertEqual(None, structureNameClashingWithJava.short_field)
        self.assertEqual(None, structureNameClashingWithJava.integer_field)
        self.assertEqual(None, structureNameClashingWithJava.long_field)
        self.assertEqual(None, structureNameClashingWithJava.big_integer_field)
        self.assertEqual(None, structureNameClashingWithJava.float_field)
        self.assertEqual(None, structureNameClashingWithJava.double_field)
        self.assertEqual(None, structureNameClashingWithJava.string_field)


    def testBitSizeOf(self):
        structureNameClashingWithJava = self.api.StructureNameClashingWithJava(
            self.api.Byte(0),
            self.api.Short(0),
            self.api.Integer(0),
            self.api.Long(0),
            self.api.BigInteger(0),
            self.api.Float(0.0),
            self.api.Double(0.0),
            self.api.String(""),
        )
        self.assertEqual(self.BIT_SIZE, structureNameClashingWithJava.bitsizeof())

    BIT_SIZE = 8 * 1 + 8 + 16 + 32 + 64 + 64 + 32 + 64 + 8
