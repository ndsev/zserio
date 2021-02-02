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
        reader = zserio.BitStreamReader(writer.getByteArray())
        defaultEmptyChoice = self.api.DefaultEmptyChoice.fromReader(reader, tag)
        self.assertEqual(tag, defaultEmptyChoice.getTag())
        self.assertEqual(value, defaultEmptyChoice.getB())

    def testReadWrite(self):
        defaultEmptyChoiceA = self.api.DefaultEmptyChoice(self.VARIANT_A_SELECTOR)
        byteValueA = 99
        defaultEmptyChoiceA.setA(byteValueA)
        writer = zserio.BitStreamWriter()
        defaultEmptyChoiceA.write(writer)
        readDefaultEmptyChoiceA = self.api.DefaultEmptyChoice(self.VARIANT_A_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readDefaultEmptyChoiceA.read(reader)
        self.assertEqual(byteValueA, readDefaultEmptyChoiceA.getA())
        self.assertEqual(defaultEmptyChoiceA, readDefaultEmptyChoiceA)

        shortValueB = 234
        defaultEmptyChoiceB = self.api.DefaultEmptyChoice(self.VARIANT_B_SELECTOR, b_=shortValueB)
        writer = zserio.BitStreamWriter()
        defaultEmptyChoiceB.write(writer)
        readDefaultEmptyChoiceB = self.api.DefaultEmptyChoice(self.VARIANT_B_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readDefaultEmptyChoiceB.read(reader)
        self.assertEqual(shortValueB, readDefaultEmptyChoiceB.getB())
        self.assertEqual(defaultEmptyChoiceB, readDefaultEmptyChoiceB)

        defaultEmptyChoiceDefault = self.api.DefaultEmptyChoice(self.DEFAULT_SELECTOR)
        writer = zserio.BitStreamWriter()
        defaultEmptyChoiceDefault.write(writer)
        readDefaultEmptyChoiceDefault = self.api.DefaultEmptyChoice(self.DEFAULT_SELECTOR)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readDefaultEmptyChoiceDefault.read(reader)
        self.assertEqual(defaultEmptyChoiceDefault, readDefaultEmptyChoiceDefault)

    @staticmethod
    def _writeDefaultEmptyChoiceToStream(writer, tag, value):
        if tag == 1:
            writer.writeSignedBits(value, 8)
        elif tag == 2:
            writer.writeSignedBits(value, 16)
        else:
            pass

    VARIANT_A_SELECTOR = 1
    VARIANT_B_SELECTOR = 2
    DEFAULT_SELECTOR = 3
