import zserio

import ParameterizedTypes


class EnumerationParamTest(ParameterizedTypes.TestCase):
    def testEq(self):
        enumerationParam1 = self.api.EnumerationParam(self.api.BasicColor.WHITE, 0)
        enumerationParam2 = self.api.EnumerationParam(self.api.BasicColor.WHITE, 0)
        self.assertTrue(enumerationParam1 == enumerationParam2)

        enumerationParam2.field = 1
        self.assertFalse(enumerationParam1 == enumerationParam2)

        enumerationParam3 = self.api.EnumerationParam(self.api.BasicColor.BLACK)
        self.assertFalse(enumerationParam1 == enumerationParam3)
        self.assertFalse(enumerationParam2 == enumerationParam3)

    def testHash(self):
        enumerationParam1 = self.api.EnumerationParam(self.api.BasicColor.WHITE, 0)
        enumerationParam2 = self.api.EnumerationParam(self.api.BasicColor.WHITE, 0)
        self.assertEqual(hash(enumerationParam1), hash(enumerationParam2))

        enumerationParam2.field = 1
        self.assertTrue(hash(enumerationParam1) != hash(enumerationParam2))

        enumerationParam3 = self.api.EnumerationParam(self.api.BasicColor.BLACK)
        self.assertTrue(hash(enumerationParam1) != hash(enumerationParam3))
        self.assertTrue(hash(enumerationParam2) != hash(enumerationParam3))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(63011, enumerationParam1.__hash__())
        self.assertEqual(63012, enumerationParam2.__hash__())
        self.assertEqual(1702, enumerationParam3.__hash__())

    def testWriteRead(self):
        enumerationParam = self.api.EnumerationParam(self.api.BasicColor.WHITE, 1)

        bitBuffer = zserio.serialize(enumerationParam)
        readEnumerationParam = zserio.deserialize(
            self.api.EnumerationParam, bitBuffer, self.api.BasicColor.WHITE
        )
        self.assertEqual(enumerationParam, readEnumerationParam)
