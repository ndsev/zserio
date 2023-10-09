import Identifiers

class StructureNameClashingWithJavaTest(Identifiers.TestCase):
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
