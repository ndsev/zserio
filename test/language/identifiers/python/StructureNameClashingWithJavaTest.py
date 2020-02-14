import unittest

from testutils import getZserioApi

class StructureNameClashingWithJavaTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "identifiers.zs").structure_name_clashing_with_java

    def testEmptyConstructor(self):
        structureNameClashingWithJava = self.api.StructureNameClashingWithJava()
        self.assertEqual(None, structureNameClashingWithJava.getByteField())
        self.assertEqual(None, structureNameClashingWithJava.getShortField())
        self.assertEqual(None, structureNameClashingWithJava.getIntegerField())
        self.assertEqual(None, structureNameClashingWithJava.getLongField())
        self.assertEqual(None, structureNameClashingWithJava.getBigIntegerField())
        self.assertEqual(None, structureNameClashingWithJava.getFloatField())
        self.assertEqual(None, structureNameClashingWithJava.getDoubleField())
        self.assertEqual(None, structureNameClashingWithJava.getStringField())


    def testBitSizeOf(self):
        structureNameClashingWithJava = self.api.StructureNameClashingWithJava.fromFields(
            self.api.Byte.fromFields(0),
            self.api.Short.fromFields(0),
            self.api.Integer.fromFields(0),
            self.api.Long.fromFields(0),
            self.api.BigInteger.fromFields(0),
            self.api.Float.fromFields(0.0),
            self.api.Double.fromFields(0.0),
            self.api.String.fromFields(""),
        )
        self.assertEqual(self.BIT_SIZE, structureNameClashingWithJava.bitSizeOf())

    BIT_SIZE = 8 * 1 + 8 + 16 + 32 + 64 + 64 + 32 + 64 + 8
