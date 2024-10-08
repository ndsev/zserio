import zserio

import UnionTypes


class UnionWithParameterizedFieldTest(UnionTypes.TestCase):
    def testConstructor(self):
        testUnion = self.api.TestUnion()
        testUnion.array_holder = self.api.ArrayHolder(10)
        self.assertEqual(10, testUnion.array_holder.size)

        testUnion = self.api.TestUnion(field_=13)
        self.assertEqual(self.api.TestUnion.CHOICE_FIELD, testUnion.choice_tag)

        testUnion = self.api.TestUnion(array_holder_=self.api.ArrayHolder(10))
        self.assertEqual(10, testUnion.array_holder.size)

    def testFromReader(self):
        testUnion = self.api.TestUnion()
        testUnion.array_holder = self.api.ArrayHolder(10, list(range(10)))
        bitBuffer = zserio.serialize(testUnion)
        readTestUnion = zserio.deserialize(self.api.TestUnion, bitBuffer)
        self.assertEqual(10, readTestUnion.array_holder.size)

    def testHash(self):
        testUnion1 = self.api.TestUnion()
        testUnion2 = self.api.TestUnion()
        self.assertEqual(hash(testUnion1), hash(testUnion2))
        testUnion1.field = 33
        self.assertNotEqual(hash(testUnion1), hash(testUnion2))
        testUnion2.field = 33
        self.assertEqual(hash(testUnion1), hash(testUnion2))
        testUnion2.field = 32
        self.assertNotEqual(hash(testUnion1), hash(testUnion2))
        testUnion2.array_holder = self.api.ArrayHolder(10, [0] * 10)
        self.assertNotEqual(hash(testUnion1), hash(testUnion2))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(31520, testUnion1.__hash__())
        self.assertEqual(1174142900, testUnion2.__hash__())

        testUnion1.array_holder = self.api.ArrayHolder(10, [0] * 10)
        self.assertEqual(hash(testUnion1), hash(testUnion2))
