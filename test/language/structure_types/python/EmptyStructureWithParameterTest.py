import zserio

import StructureTypes


class EmptyStructureWithParameterTest(StructureTypes.TestCase):
    def testParamConstructor(self):
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(1)
        self.assertEqual(1, emptyStructureWithParameter.param)

    def testFromReader(self):
        param = 1
        reader = zserio.BitStreamReader(bytes())
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter.from_reader(reader, param)
        self.assertEqual(param, emptyStructureWithParameter.param)
        self.assertEqual(0, emptyStructureWithParameter.bitsizeof())

    def testEq(self):
        emptyStructureWithParameter1 = self.api.EmptyStructureWithParameter(1)
        emptyStructureWithParameter2 = self.api.EmptyStructureWithParameter(1)
        emptyStructureWithParameter3 = self.api.EmptyStructureWithParameter(0)
        self.assertTrue(emptyStructureWithParameter1 == emptyStructureWithParameter2)
        self.assertFalse(emptyStructureWithParameter1 == emptyStructureWithParameter3)

    def testHash(self):
        emptyStructureWithParameter1 = self.api.EmptyStructureWithParameter(1)
        emptyStructureWithParameter2 = self.api.EmptyStructureWithParameter(1)
        emptyStructureWithParameter3 = self.api.EmptyStructureWithParameter(0)
        self.assertEqual(hash(emptyStructureWithParameter1), hash(emptyStructureWithParameter2))
        self.assertTrue(hash(emptyStructureWithParameter1) != hash(emptyStructureWithParameter3))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(852, emptyStructureWithParameter1.__hash__())
        self.assertEqual(851, emptyStructureWithParameter3.__hash__())

    def testGetParam(self):
        param = 1
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(param)
        self.assertEqual(param, emptyStructureWithParameter.param)

    def testBitSizeOf(self):
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(1)
        self.assertEqual(0, emptyStructureWithParameter.bitsizeof(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(1)
        self.assertEqual(bitPosition, emptyStructureWithParameter.initialize_offsets(bitPosition))

    def testRead(self):
        param = 1
        reader = zserio.BitStreamReader(bytes())
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(param)
        emptyStructureWithParameter.read(reader)
        self.assertEqual(param, emptyStructureWithParameter.param)
        self.assertEqual(0, emptyStructureWithParameter.bitsizeof())

    def testWrite(self):
        param = 1
        writer = zserio.BitStreamWriter()
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(param)
        emptyStructureWithParameter.write(writer)
        byteArray = writer.byte_array
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEmptyStructureWithParameter = self.api.EmptyStructureWithParameter.from_reader(reader, param)
        self.assertEqual(emptyStructureWithParameter, readEmptyStructureWithParameter)
