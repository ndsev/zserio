import unittest

from testutils import getZserioApi

class BitmaskNameClashingWithJavaTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "identifiers.zs").bitmask_name_clashing_with_java

    def testEmptyConstructor(self):
        bitmaskNameClashingWithJava = self.api.BitmaskNameClashingWithJava()
        self.assertIsNone(bitmaskNameClashingWithJava.getStringField())

    def testBitSizeOf(self):
        bitmaskNameClashingWithJava = self.api.BitmaskNameClashingWithJava(self.api.String.Values.WRITE)
        self.assertEqual(self.BIT_SIZE, bitmaskNameClashingWithJava.bitSizeOf())

    def testStr(self):
        bitmaskNameClashingWithJava = self.api.BitmaskNameClashingWithJava(self.api.String.Values.READ)
        self.assertEqual("1[READ]", str(bitmaskNameClashingWithJava.getStringField()))

    BIT_SIZE = 8
