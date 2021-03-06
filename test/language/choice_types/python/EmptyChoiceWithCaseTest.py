import unittest
import zserio

from testutils import getZserioApi

class EmptyChoiceWithCaseTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").empty_choice_with_case

    def testSelectorConstructor(self):
        emptyChoiceWithCase = self.api.EmptyChoiceWithCase(1)
        self.assertEqual(1, emptyChoiceWithCase.selector)

    def testFromReader(self):
        selector = 1
        reader = zserio.BitStreamReader([])
        emptyChoiceWithCase = self.api.EmptyChoiceWithCase.from_reader(reader, selector)
        self.assertEqual(selector, emptyChoiceWithCase.selector)
        self.assertEqual(0, emptyChoiceWithCase.bitsizeof())

    def testEq(self):
        emptyChoiceWithCase1 = self.api.EmptyChoiceWithCase(1)
        emptyChoiceWithCase2 = self.api.EmptyChoiceWithCase(1)
        emptyChoiceWithCase3 = self.api.EmptyChoiceWithCase(0)
        self.assertTrue(emptyChoiceWithCase1 == emptyChoiceWithCase2)
        self.assertFalse(emptyChoiceWithCase1 == emptyChoiceWithCase3)

    def testHash(self):
        emptyChoiceWithCase1 = self.api.EmptyChoiceWithCase(1)
        emptyChoiceWithCase2 = self.api.EmptyChoiceWithCase(1)
        emptyChoiceWithCase3 = self.api.EmptyChoiceWithCase(0)
        self.assertEqual(hash(emptyChoiceWithCase1), hash(emptyChoiceWithCase2))
        self.assertTrue(hash(emptyChoiceWithCase1) != hash(emptyChoiceWithCase3))

    def testGetSelector(self):
        selector = 1
        emptyChoiceWithCase = self.api.EmptyChoiceWithCase(selector)
        self.assertEqual(selector, emptyChoiceWithCase.selector)

    def testBitSizeOf(self):
        emptyChoiceWithCase = self.api.EmptyChoiceWithCase(1)
        self.assertEqual(0, emptyChoiceWithCase.bitsizeof(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyChoiceWithCase = self.api.EmptyChoiceWithCase(1)
        self.assertEqual(bitPosition, emptyChoiceWithCase.initialize_offsets(bitPosition))

    def testRead(self):
        selector = 1
        reader = zserio.BitStreamReader([])
        emptyChoiceWithCase = self.api.EmptyChoiceWithCase(selector)
        emptyChoiceWithCase.read(reader)
        self.assertEqual(selector, emptyChoiceWithCase.selector)
        self.assertEqual(0, emptyChoiceWithCase.bitsizeof())

    def testWrite(self):
        selector = 1
        emptyChoiceWithCase = self.api.EmptyChoiceWithCase(selector)
        writer = zserio.BitStreamWriter()
        emptyChoiceWithCase.write(writer)
        byteArray = writer.byte_array
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEmptyChoiceWithCase = self.api.EmptyChoiceWithCase.from_reader(reader, selector)
        self.assertEqual(emptyChoiceWithCase, readEmptyChoiceWithCase)
