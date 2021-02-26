import unittest
import zserio

from testutils import getZserioApi

class EmptyChoiceWithDefaultTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").empty_choice_with_default

    def testSelectorConstructor(self):
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(1)
        self.assertEqual(1, emptyChoiceWithDefault.selector)

    def testFromReader(self):
        selector = 1
        reader = zserio.BitStreamReader([])
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault.fromReader(reader, selector)
        self.assertEqual(selector, emptyChoiceWithDefault.selector)
        self.assertEqual(0, emptyChoiceWithDefault.bitSizeOf())

    def testEq(self):
        emptyChoiceWithDefault1 = self.api.EmptyChoiceWithDefault(1)
        emptyChoiceWithDefault2 = self.api.EmptyChoiceWithDefault(1)
        emptyChoiceWithDefault3 = self.api.EmptyChoiceWithDefault(0)
        self.assertTrue(emptyChoiceWithDefault1 == emptyChoiceWithDefault2)
        self.assertFalse(emptyChoiceWithDefault1 == emptyChoiceWithDefault3)

    def testHash(self):
        emptyChoiceWithDefault1 = self.api.EmptyChoiceWithDefault(1)
        emptyChoiceWithDefault2 = self.api.EmptyChoiceWithDefault(1)
        emptyChoiceWithDefault3 = self.api.EmptyChoiceWithDefault(0)
        self.assertEqual(hash(emptyChoiceWithDefault1), hash(emptyChoiceWithDefault2))
        self.assertTrue(hash(emptyChoiceWithDefault1) != hash(emptyChoiceWithDefault3))

    def testGetSelector(self):
        selector = 1
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(selector)
        self.assertEqual(selector, emptyChoiceWithDefault.selector)

    def testBitSizeOf(self):
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(1)
        self.assertEqual(0, emptyChoiceWithDefault.bitSizeOf(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(1)
        self.assertEqual(bitPosition, emptyChoiceWithDefault.initializeOffsets(bitPosition))

    def testRead(self):
        selector = 1
        reader = zserio.BitStreamReader([])
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(selector)
        emptyChoiceWithDefault.read(reader)
        self.assertEqual(selector, emptyChoiceWithDefault.selector)
        self.assertEqual(0, emptyChoiceWithDefault.bitSizeOf())

    def testWrite(self):
        selector = 1
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(selector)
        writer = zserio.BitStreamWriter()
        emptyChoiceWithDefault.write(writer)
        byteArray = writer.getByteArray()
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readEmptyChoiceWithDefault = self.api.EmptyChoiceWithDefault.fromReader(reader, selector)
        self.assertEqual(emptyChoiceWithDefault, readEmptyChoiceWithDefault)
