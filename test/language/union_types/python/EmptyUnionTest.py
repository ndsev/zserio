import unittest
import zserio

from testutils import getZserioApi

class EmptyUnionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").empty_union

    def testEmptyConstructor(self):
        emptyUnion = self.api.EmptyUnion()
        self.assertEqual(0, emptyUnion.bitSizeOf())

    def testFromReader(self):
        reader = zserio.BitStreamReader([])
        emptyUnion = self.api.EmptyUnion.fromReader(reader)
        self.assertEqual(0, emptyUnion.bitSizeOf())

    def testEq(self):
        emptyUnion1 = self.api.EmptyUnion()
        emptyUnion2 = self.api.EmptyUnion()
        self.assertTrue(emptyUnion1 == emptyUnion2)

    def testHash(self):
        emptyUnion1 = self.api.EmptyUnion()
        emptyUnion2 = self.api.EmptyUnion()
        self.assertEqual(hash(emptyUnion1), hash(emptyUnion2))

    def testChoiceTag(self):
        emptyUnion = self.api.EmptyUnion()
        self.assertEqual(self.api.EmptyUnion.UNDEFINED_CHOICE, emptyUnion.choiceTag())

    def testBitSizeOf(self):
        bitPosition = 1
        emptyUnion = self.api.EmptyUnion()
        self.assertEqual(0, emptyUnion.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyUnion = self.api.EmptyUnion()
        self.assertEqual(bitPosition, emptyUnion.initializeOffsets(bitPosition))

    def testRead(self):
        emptyUnion = self.api.EmptyUnion()
        reader = zserio.BitStreamReader([])
        emptyUnion.read(reader)
        self.assertEqual(0, emptyUnion.bitSizeOf())

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        emptyUnion = self.api.EmptyUnion()
        emptyUnion.write(writer)
        byteArray = writer.getByteArray()
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.getByteArray())
        readEmptyUnion = self.api.EmptyUnion.fromReader(reader)
        self.assertEqual(emptyUnion, readEmptyUnion)
