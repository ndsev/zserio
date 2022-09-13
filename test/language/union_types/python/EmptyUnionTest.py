import unittest
import zserio

from testutils import getZserioApi

class EmptyUnionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").empty_union

    def testEmptyConstructor(self):
        emptyUnion = self.api.EmptyUnion()
        self.assertEqual(0, emptyUnion.bitsizeof())

    def testFromReader(self):
        reader = zserio.BitStreamReader([])
        emptyUnion = self.api.EmptyUnion.from_reader(reader)
        self.assertEqual(0, emptyUnion.bitsizeof())

    def testEq(self):
        emptyUnion1 = self.api.EmptyUnion()
        emptyUnion2 = self.api.EmptyUnion()
        self.assertTrue(emptyUnion1 == emptyUnion2)

    def testHash(self):
        emptyUnion1 = self.api.EmptyUnion()
        emptyUnion2 = self.api.EmptyUnion()
        self.assertEqual(hash(emptyUnion1), hash(emptyUnion2))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(850, emptyUnion1.__hash__())

    def testChoiceTag(self):
        emptyUnion = self.api.EmptyUnion()
        self.assertEqual(self.api.EmptyUnion.UNDEFINED_CHOICE, emptyUnion.choice_tag)

    def testBitSizeOf(self):
        bitPosition = 1
        emptyUnion = self.api.EmptyUnion()
        self.assertEqual(0, emptyUnion.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyUnion = self.api.EmptyUnion()
        self.assertEqual(bitPosition, emptyUnion.initialize_offsets(bitPosition))

    def testRead(self):
        emptyUnion = self.api.EmptyUnion()
        reader = zserio.BitStreamReader([])
        emptyUnion.read(reader)
        self.assertEqual(0, emptyUnion.bitsizeof())

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        emptyUnion = self.api.EmptyUnion()
        emptyUnion.write(writer)
        byteArray = writer.byte_array
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEmptyUnion = self.api.EmptyUnion.from_reader(reader)
        self.assertEqual(emptyUnion, readEmptyUnion)
