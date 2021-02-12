import unittest
import zserio

from testutils import getZserioApi

class OneStringStructureTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "structure_types.zs").one_string_structure

    def testConstructor(self):
        oneStringStructure = self.api.OneStringStructure()
        self.assertEqual("", oneStringStructure.getOneString())

        oneStringStructure = self.api.OneStringStructure(self.ONE_STRING)
        self.assertEqual(self.ONE_STRING, oneStringStructure.getOneString())

        oneStringStructure = self.api.OneStringStructure(oneString_=self.ONE_STRING)
        self.assertEqual(self.ONE_STRING, oneStringStructure.getOneString())

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        OneStringStructureTest._writeOneStringStructureToStream(writer, self.ONE_STRING)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        oneStringStructure = self.api.OneStringStructure.fromReader(reader)
        self.assertEqual(self.ONE_STRING, oneStringStructure.getOneString())

    def testEq(self):
        oneStringStructure1 = self.api.OneStringStructure()
        oneStringStructure2 = self.api.OneStringStructure()
        self.assertTrue(oneStringStructure1 == oneStringStructure2)

        oneStringStructure1.setOneString(self.ONE_STRING)
        self.assertFalse(oneStringStructure1 == oneStringStructure2)

        oneStringStructure2.setOneString(self.ONE_STRING)
        self.assertTrue(oneStringStructure1 == oneStringStructure2)

    def testHash(self):
        oneStringStructure1 = self.api.OneStringStructure()
        oneStringStructure2 = self.api.OneStringStructure()
        self.assertEqual(hash(oneStringStructure1), hash(oneStringStructure2))

        oneStringStructure1.setOneString(self.ONE_STRING)
        self.assertTrue(hash(oneStringStructure1) != hash(oneStringStructure2))

        oneStringStructure2.setOneString(self.ONE_STRING)
        self.assertEqual(hash(oneStringStructure1), hash(oneStringStructure2))

    def testGetSetOneString(self):
        oneStringStructure = self.api.OneStringStructure()
        oneStringStructure.setOneString(self.ONE_STRING)
        self.assertEqual(self.ONE_STRING, oneStringStructure.getOneString())

    def testBitSizeOf(self):
        oneStringStructure = self.api.OneStringStructure(self.ONE_STRING)
        self.assertEqual(self.ONE_STRING_STRUCTURE_BIT_SIZE, oneStringStructure.bitSizeOf())

    def testInitializeOffsets(self):
        oneStringStructure = self.api.OneStringStructure(oneString_=self.ONE_STRING)
        bitPosition = 1
        self.assertEqual(self.ONE_STRING_STRUCTURE_BIT_SIZE + bitPosition,
                         oneStringStructure.initializeOffsets(bitPosition))

    def testReadWrite(self):
        oneStringStructure = self.api.OneStringStructure(self.ONE_STRING)
        writer = zserio.BitStreamWriter()
        oneStringStructure.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readOneStringStructure = self.api.OneStringStructure()
        readOneStringStructure.read(reader)
        self.assertEqual(self.ONE_STRING, readOneStringStructure.getOneString())
        self.assertTrue(oneStringStructure == readOneStringStructure)

    @staticmethod
    def _writeOneStringStructureToStream(writer, oneString):
        writer.writeString(oneString)

    ONE_STRING = "This is a string!"
    ONE_STRING_STRUCTURE_BIT_SIZE = (1 + len(ONE_STRING)) * 8
