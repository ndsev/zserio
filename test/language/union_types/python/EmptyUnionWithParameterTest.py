import unittest
import zserio

from testutils import getZserioApi

class EmptyUnionWithParameterTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "union_types.zs").empty_union_with_parameter

    def testParamConstructor(self):
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        self.assertEqual(self.PARAM_VALUE1, emptyUnionWithParameter.getParam())

    def testFromReader(self):
        reader = zserio.BitStreamReader([])
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter.fromReader(reader, self.PARAM_VALUE1)
        self.assertEqual(self.PARAM_VALUE1, emptyUnionWithParameter.getParam())
        self.assertEqual(0, emptyUnionWithParameter.bitSizeOf())

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

    def testChoiceTag(self):
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        self.assertEqual(self.api.EmptyUnionWithParameter.UNDEFINED_CHOICE, emptyUnionWithParameter.choiceTag())

    def testBitSizeOf(self):
        bitPosition = 1
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        self.assertEqual(0, emptyUnionWithParameter.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        self.assertEqual(bitPosition, emptyUnionWithParameter.initializeOffsets(bitPosition))

    def testRead(self):
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        reader = zserio.BitStreamReader([])
        emptyUnionWithParameter.read(reader)
        self.assertEqual(self.PARAM_VALUE1, emptyUnionWithParameter.getParam())
        self.assertEqual(0, emptyUnionWithParameter.bitSizeOf())

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        emptyUnionWithParameter = self.api.EmptyUnionWithParameter(self.PARAM_VALUE1)
        emptyUnionWithParameter.write(writer)
        byteArray = writer.getByteArray()
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readEmptyUnionWithParameter = self.api.EmptyUnionWithParameter.fromReader(reader, self.PARAM_VALUE1)
        self.assertEqual(emptyUnionWithParameter, readEmptyUnionWithParameter)

    PARAM_VALUE1 = 1
    PARAM_VALUE2 = 2
