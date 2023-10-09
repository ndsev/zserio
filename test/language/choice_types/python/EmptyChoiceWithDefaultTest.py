import zserio

import ChoiceTypes

class EmptyChoiceWithDefaultTest(ChoiceTypes.TestCase):
    def testSelectorConstructor(self):
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(1)
        self.assertEqual(1, emptyChoiceWithDefault.selector)

    def testFromReader(self):
        selector = 1
        reader = zserio.BitStreamReader(bytes())
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault.from_reader(reader, selector)
        self.assertEqual(selector, emptyChoiceWithDefault.selector)
        self.assertEqual(0, emptyChoiceWithDefault.bitsizeof())

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

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(852, emptyChoiceWithDefault1.__hash__())
        self.assertEqual(851, emptyChoiceWithDefault3.__hash__())

    def testGetSelector(self):
        selector = 1
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(selector)
        self.assertEqual(selector, emptyChoiceWithDefault.selector)

    def testChoiceTag(self):
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(0)
        self.assertEqual(emptyChoiceWithDefault.UNDEFINED_CHOICE, emptyChoiceWithDefault.choice_tag)

        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(1)
        self.assertEqual(emptyChoiceWithDefault.UNDEFINED_CHOICE, emptyChoiceWithDefault.choice_tag)

    def testBitSizeOf(self):
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(1)
        self.assertEqual(0, emptyChoiceWithDefault.bitsizeof(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(1)
        self.assertEqual(bitPosition, emptyChoiceWithDefault.initialize_offsets(bitPosition))

    def testRead(self):
        selector = 1
        reader = zserio.BitStreamReader(bytes())
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(selector)
        emptyChoiceWithDefault.read(reader)
        self.assertEqual(selector, emptyChoiceWithDefault.selector)
        self.assertEqual(0, emptyChoiceWithDefault.bitsizeof())

    def testWrite(self):
        selector = 1
        emptyChoiceWithDefault = self.api.EmptyChoiceWithDefault(selector)
        writer = zserio.BitStreamWriter()
        emptyChoiceWithDefault.write(writer)
        byteArray = writer.byte_array
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEmptyChoiceWithDefault = self.api.EmptyChoiceWithDefault.from_reader(reader, selector)
        self.assertEqual(emptyChoiceWithDefault, readEmptyChoiceWithDefault)
