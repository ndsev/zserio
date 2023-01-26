import unittest
import zserio

from testutils import getZserioApi

class EmptyUnionWithParameterTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").empty_union_with_parameter

    def testParamConstructor(self):
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        self.assertEqual(self.PARAM_VALUE1, emptyUnionWithParameter.param)

    def testFromReader(self):
        reader = zserio.BitStreamReader(bytes())
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter.from_reader(reader, self.PARAM_VALUE1)
        self.assertEqual(self.PARAM_VALUE1, emptyUnionWithParameter.param)
        self.assertEqual(0, emptyUnionWithParameter.bitsizeof())

    def testEq(self):
        emptyUnionWithParameter1 = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        emptyUnionWithParameter2 = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        emptyUnionWithParameter3 = self.api.EmptyUnionWithParameter(self.PARAM_VALUE2)
        self.assertTrue(emptyUnionWithParameter1 == emptyUnionWithParameter2)
        self.assertFalse(emptyUnionWithParameter1 == emptyUnionWithParameter3)

    def testHash(self):
        emptyUnionWithParameter1 = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        emptyUnionWithParameter2 = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        emptyUnionWithParameter3 = self.api.EmptyUnionWithParameter(self.PARAM_VALUE2)
        self.assertEqual(hash(emptyUnionWithParameter1), hash(emptyUnionWithParameter2))
        self.assertTrue(hash(emptyUnionWithParameter1) != hash(emptyUnionWithParameter3))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(31523, emptyUnionWithParameter1.__hash__())
        self.assertEqual(31486, emptyUnionWithParameter3.__hash__())

    def testChoiceTag(self):
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        self.assertEqual(self.api.EmptyUnionWithParameter.UNDEFINED_CHOICE, emptyUnionWithParameter.choice_tag)

    def testBitSizeOf(self):
        bitPosition = 1
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        self.assertEqual(0, emptyUnionWithParameter.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        self.assertEqual(bitPosition, emptyUnionWithParameter.initialize_offsets(bitPosition))

    def testRead(self):
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        reader = zserio.BitStreamReader(bytes())
        emptyUnionWithParameter.read(reader)
        self.assertEqual(self.PARAM_VALUE1, emptyUnionWithParameter.param)
        self.assertEqual(0, emptyUnionWithParameter.bitsizeof())

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        emptyUnionWithParameter.write(writer)
        byteArray = writer.byte_array
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEmptyUnionWithParameter = self.api.EmptyUnionWithParameter.from_reader(reader, self.PARAM_VALUE1)
        self.assertEqual(emptyUnionWithParameter, readEmptyUnionWithParameter)

    PARAM_VALUE1 = 1
    PARAM_VALUE2 = 0
