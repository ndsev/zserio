import unittest
import zserio

from testutils import getZserioApi

class EmptyChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").empty_choice

    def testSelectorConstructor(self):
        emptyChoice = self.api.EmptyChoice(1)
        self.assertEqual(1, emptyChoice.getSelector())

    def testFromReader(self):
        selector = 1
        reader = zserio.BitStreamReader([])
        emptyChoice = self.api.EmptyChoice.fromReader(reader, selector)
        self.assertEqual(selector, emptyChoice.getSelector())
        self.assertEqual(0, emptyChoice.bitSizeOf())

    def testEq(self):
        emptyChoice1 = self.api.EmptyChoice(1)
        emptyChoice2 = self.api.EmptyChoice(1)
        emptyChoice3 = self.api.EmptyChoice(0)
        self.assertTrue(emptyChoice1 == emptyChoice2)
        self.assertFalse(emptyChoice1 == emptyChoice3)

    def testHash(self):
        emptyChoice1 = self.api.EmptyChoice(1)
        emptyChoice2 = self.api.EmptyChoice(1)
        emptyChoice3 = self.api.EmptyChoice(0)
        self.assertEqual(hash(emptyChoice1), hash(emptyChoice2))
        self.assertTrue(hash(emptyChoice1) != hash(emptyChoice3))

    def testGetSelector(self):
        selector = 1
        emptyChoice = self.api.EmptyChoice(selector)
        self.assertEqual(selector, emptyChoice.getSelector())

    def testBitSizeOf(self):
        emptyChoice = self.api.EmptyChoice(1)
        self.assertEqual(0, emptyChoice.bitSizeOf(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyChoice = self.api.EmptyChoice(1)
        self.assertEqual(bitPosition, emptyChoice.initializeOffsets(bitPosition))

    def testRead(self):
        selector = 1
        reader = zserio.BitStreamReader([])
        emptyChoice = self.api.EmptyChoice(selector)
        emptyChoice.read(reader)
        self.assertEqual(selector, emptyChoice.getSelector())
        self.assertEqual(0, emptyChoice.bitSizeOf())

    def testWrite(self):
        selector = 1
        emptyChoice = self.api.EmptyChoice(selector)
        writer = zserio.BitStreamWriter()
        emptyChoice.write(writer)
        byteArray = writer.getByteArray()
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readEmptyChoice = self.api.EmptyChoice.fromReader(reader, selector)
        self.assertEqual(emptyChoice, readEmptyChoice)
