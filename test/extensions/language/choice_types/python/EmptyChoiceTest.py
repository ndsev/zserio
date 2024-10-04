import os
import zserio

import ChoiceTypes

from testutils import getApiDir


class EmptyChoiceTest(ChoiceTypes.TestCase):
    def testSelectorConstructor(self):
        emptyChoice = self.api.EmptyChoice(1)
        self.assertEqual(1, emptyChoice.selector)

    def testFromReader(self):
        selector = 1
        reader = zserio.BitStreamReader(bytes())
        emptyChoice = self.api.EmptyChoice.from_reader(reader, selector)
        self.assertEqual(selector, emptyChoice.selector)
        self.assertEqual(0, emptyChoice.bitsizeof())

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

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(852, emptyChoice1.__hash__())
        self.assertEqual(851, emptyChoice3.__hash__())

    def testGetSelector(self):
        selector = 1
        emptyChoice = self.api.EmptyChoice(selector)
        self.assertEqual(selector, emptyChoice.selector)

    def testChoiceTag(self):
        emptyChoice = self.api.EmptyChoice(0)
        self.assertEqual(emptyChoice.UNDEFINED_CHOICE, emptyChoice.choice_tag)

    def testBitSizeOf(self):
        emptyChoice = self.api.EmptyChoice(1)
        self.assertEqual(0, emptyChoice.bitsizeof(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyChoice = self.api.EmptyChoice(1)
        self.assertEqual(bitPosition, emptyChoice.initialize_offsets(bitPosition))

    def testRead(self):
        selector = 1
        reader = zserio.BitStreamReader(bytes())
        emptyChoice = self.api.EmptyChoice(selector)
        emptyChoice.read(reader)
        self.assertEqual(selector, emptyChoice.selector)
        self.assertEqual(0, emptyChoice.bitsizeof())

    def testWriteRead(self):
        selector = 1
        emptyChoice = self.api.EmptyChoice(selector)
        writer = zserio.BitStreamWriter()
        emptyChoice.write(writer)
        byteArray = writer.byte_array
        self.assertEqual(0, len(byteArray))

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEmptyChoice = self.api.EmptyChoice.from_reader(reader, selector)
        self.assertEqual(emptyChoice, readEmptyChoice)

    def testWriteReadFile(self):
        selector = 1
        emptyChoice = self.api.EmptyChoice(selector)
        filename = os.path.join(getApiDir(os.path.dirname(__file__)), "empty_choice.blob")
        zserio.serialize_to_file(emptyChoice, filename)

        readEmptyChoice = zserio.deserialize_from_file(self.api.EmptyChoice, filename, selector)
        self.assertEqual(emptyChoice, readEmptyChoice)
