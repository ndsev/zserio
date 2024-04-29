import Identifiers


class BitmaskNameClashingWithJavaTest(Identifiers.TestCase):
    def testEmptyConstructor(self):
        bitmaskNameClashingWithJava = self.api.BitmaskNameClashingWithJava()
        self.assertIsNone(bitmaskNameClashingWithJava.string_field)

    def testBitSizeOf(self):
        bitmaskNameClashingWithJava = self.api.BitmaskNameClashingWithJava(self.api.String.Values.WRITE)
        self.assertEqual(self.BIT_SIZE, bitmaskNameClashingWithJava.bitsizeof())

    def testStr(self):
        bitmaskNameClashingWithJava = self.api.BitmaskNameClashingWithJava(self.api.String.Values.READ)
        self.assertEqual("1[READ]", str(bitmaskNameClashingWithJava.string_field))

    BIT_SIZE = 8
