import unittest
import zserio

from testutils import getZserioApi

class DefaultEmptyChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").default_empty_choice

    def testFromReader(self):
        tag = self.VARIANT_B_SELECTOR
        value = 234
        writer = zserio.BitStreamWriter()
        DefaultEmptyChoiceTest._writeDefaultEmptyChoiceToStream(writer, tag, value)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        defaultEmptyChoice = self.api.DefaultEmptyChoice.from_reader(reader, tag)
        self.assertEqual(tag, defaultEmptyChoice.tag)
        self.assertEqual(value, defaultEmptyChoice.b)

    def testChoiceTag(self):
        defaultEmptyChoice = self.api.DefaultEmptyChoice(self.DEFAULT_SELECTOR)
        self.assertEqual(defaultEmptyChoice.UNDEFINED_CHOICE, defaultEmptyChoice.choice_tag)

        defaultEmptyChoice = self.api.DefaultEmptyChoice(self.VARIANT_A_SELECTOR)
        self.assertEqual(defaultEmptyChoice.CHOICE_A, defaultEmptyChoice.choice_tag)

        defaultEmptyChoice = self.api.DefaultEmptyChoice(self.VARIANT_B_SELECTOR)
        self.assertEqual(defaultEmptyChoice.CHOICE_B, defaultEmptyChoice.choice_tag)

    def testReadWrite(self):
        defaultEmptyChoiceA = self.api.DefaultEmptyChoice(self.VARIANT_A_SELECTOR)
        byteValueA = 99
        defaultEmptyChoiceA.a = byteValueA
        writer = zserio.BitStreamWriter()
        defaultEmptyChoiceA.write(writer)
        readDefaultEmptyChoiceA = self.api.DefaultEmptyChoice(self.VARIANT_A_SELECTOR)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readDefaultEmptyChoiceA.read(reader)
        self.assertEqual(byteValueA, readDefaultEmptyChoiceA.a)
        self.assertEqual(defaultEmptyChoiceA, readDefaultEmptyChoiceA)

        shortValueB = 234
        defaultEmptyChoiceB = self.api.DefaultEmptyChoice(self.VARIANT_B_SELECTOR, b_=shortValueB)
        writer = zserio.BitStreamWriter()
        defaultEmptyChoiceB.write(writer)
        readDefaultEmptyChoiceB = self.api.DefaultEmptyChoice(self.VARIANT_B_SELECTOR)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readDefaultEmptyChoiceB.read(reader)
        self.assertEqual(shortValueB, readDefaultEmptyChoiceB.b)
        self.assertEqual(defaultEmptyChoiceB, readDefaultEmptyChoiceB)

        defaultEmptyChoiceDefault = self.api.DefaultEmptyChoice(self.DEFAULT_SELECTOR)
        writer = zserio.BitStreamWriter()
        defaultEmptyChoiceDefault.write(writer)
        readDefaultEmptyChoiceDefault = self.api.DefaultEmptyChoice(self.DEFAULT_SELECTOR)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readDefaultEmptyChoiceDefault.read(reader)
        self.assertEqual(defaultEmptyChoiceDefault, readDefaultEmptyChoiceDefault)

    @staticmethod
    def _writeDefaultEmptyChoiceToStream(writer, tag, value):
        if tag == 1:
            writer.write_signed_bits(value, 8)
        elif tag == 2:
            writer.write_signed_bits(value, 16)
        else:
            pass

    VARIANT_A_SELECTOR = 1
    VARIANT_B_SELECTOR = 2
    DEFAULT_SELECTOR = 3
