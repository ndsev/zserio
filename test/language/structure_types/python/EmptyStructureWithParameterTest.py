import unittest
import zserio

from testutils import getZserioApi

class EmptyStructureTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "structure_types.zs").empty_structure_with_parameter

    def testParamConstructor(self):
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(1)
        self.assertEqual(1, emptyStructureWithParameter.getParam())

    def testFromReader(self):
        param = 1
        reader = zserio.BitStreamReader([])
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter.fromReader(reader, param)
        self.assertEqual(param, emptyStructureWithParameter.getParam())
        self.assertEqual(0, emptyStructureWithParameter.bitSizeOf())

    def testEq(self):
        emptyStructureWithParameter1 = self.api.EmptyStructureWithParameter(1)
        emptyStructureWithParameter2 = self.api.EmptyStructureWithParameter(1)
        emptyStructureWithParameter3 = self.api.EmptyStructureWithParameter(0)
        self.assertTrue(emptyStructureWithParameter1 == emptyStructureWithParameter2)
        self.assertFalse(emptyStructureWithParameter1 == emptyStructureWithParameter3)

    def testHash(self):
        emptyStructureWithParameter1 = self.api.EmptyStructureWithParameter(1)
        emptyStructureWithParameter2 = self.api.EmptyStructureWithParameter(1)
        emptyStructureWithParameter3 = self.api.EmptyStructureWithParameter(0)
        self.assertEqual(hash(emptyStructureWithParameter1), hash(emptyStructureWithParameter2))
        self.assertTrue(hash(emptyStructureWithParameter1) != hash(emptyStructureWithParameter3))

    def testGetParam(self):
        param = 1
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(param)
        self.assertEqual(param, emptyStructureWithParameter.getParam())

    def testBitSizeOf(self):
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(1)
        self.assertEqual(0, emptyStructureWithParameter.bitSizeOf(1))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(1)
        self.assertEqual(bitPosition, emptyStructureWithParameter.initializeOffsets(bitPosition))

    def testRead(self):
        param = 1
        reader = zserio.BitStreamReader([])
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(param)
        emptyStructureWithParameter.read(reader)
        self.assertEqual(param, emptyStructureWithParameter.getParam())
        self.assertEqual(0, emptyStructureWithParameter.bitSizeOf())

    def testWrite(self):
        param = 1
        writer = zserio.BitStreamWriter()
        emptyStructureWithParameter = self.api.EmptyStructureWithParameter(param)
        emptyStructureWithParameter.write(writer)
        byteArray = writer.getByteArray()
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readEmptyStructureWithParameter = self.api.EmptyStructureWithParameter.fromReader(reader, param)
        self.assertEqual(emptyStructureWithParameter, readEmptyStructureWithParameter)
