import os
import zserio

import StructureTypes
from testutils import getApiDir

class OneStringStructureTest(StructureTypes.TestCase):
    def testConstructor(self):
        oneStringStructure = self.api.OneStringStructure()
        self.assertEqual("", oneStringStructure.one_string)

        oneStringStructure = self.api.OneStringStructure(self.ONE_STRING)
        self.assertEqual(self.ONE_STRING, oneStringStructure.one_string)

        oneStringStructure = self.api.OneStringStructure(one_string_=self.ONE_STRING)
        self.assertEqual(self.ONE_STRING, oneStringStructure.one_string)

    def testFromReader(self):
        writer = zserio.BitStreamWriter()
        OneStringStructureTest._writeOneStringStructureToStream(writer, self.ONE_STRING)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        oneStringStructure = self.api.OneStringStructure.from_reader(reader)
        self.assertEqual(self.ONE_STRING, oneStringStructure.one_string)

    def testEq(self):
        oneStringStructure1 = self.api.OneStringStructure()
        oneStringStructure2 = self.api.OneStringStructure()
        self.assertTrue(oneStringStructure1 == oneStringStructure2)

        oneStringStructure1.one_string = self.ONE_STRING
        self.assertFalse(oneStringStructure1 == oneStringStructure2)

        oneStringStructure2.one_string = self.ONE_STRING
        self.assertTrue(oneStringStructure1 == oneStringStructure2)

    def testHash(self):
        oneStringStructure1 = self.api.OneStringStructure()
        oneStringStructure2 = self.api.OneStringStructure()
        self.assertEqual(hash(oneStringStructure1), hash(oneStringStructure2))

        oneStringStructure1.one_string = self.ONE_STRING
        self.assertTrue(hash(oneStringStructure1) != hash(oneStringStructure2))

        # use hardcoded values to check that the hash code is stable
        # using __hash__ to prevent 32-bit Python hash() truncation
        self.assertEqual(1773897624, oneStringStructure1.__hash__())
        oneStringStructure2.one_string = ""
        self.assertEqual(23, oneStringStructure2.__hash__())

        oneStringStructure2.one_string = self.ONE_STRING
        self.assertEqual(hash(oneStringStructure1), hash(oneStringStructure2))

    def testGetSetOneString(self):
        oneStringStructure = self.api.OneStringStructure()
        oneStringStructure.one_string = self.ONE_STRING
        self.assertEqual(self.ONE_STRING, oneStringStructure.one_string)

    def testBitSizeOf(self):
        oneStringStructure = self.api.OneStringStructure(self.ONE_STRING)
        self.assertEqual(self.ONE_STRING_STRUCTURE_BIT_SIZE, oneStringStructure.bitsizeof())

    def testInitializeOffsets(self):
        oneStringStructure = self.api.OneStringStructure(one_string_=self.ONE_STRING)
        bitPosition = 1
        self.assertEqual(self.ONE_STRING_STRUCTURE_BIT_SIZE + bitPosition,
                         oneStringStructure.initialize_offsets(bitPosition))

    def testWriteRead(self):
        oneStringStructure = self.api.OneStringStructure(self.ONE_STRING)
        writer = zserio.BitStreamWriter()
        oneStringStructure.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readOneStringStructure = self.api.OneStringStructure()
        readOneStringStructure.read(reader)
        self.assertEqual(self.ONE_STRING, readOneStringStructure.one_string)
        self.assertTrue(oneStringStructure == readOneStringStructure)

    def testWriteReadFile(self):
        oneStringStructure = self.api.OneStringStructure(self.ONE_STRING)
        zserio.serialize_to_file(oneStringStructure, self.BLOB_NAME)

        readOneStringStructure = zserio.deserialize_from_file(self.api.OneStringStructure, self.BLOB_NAME)
        self.assertEqual(self.ONE_STRING, readOneStringStructure.one_string)
        self.assertTrue(oneStringStructure == readOneStringStructure)

    @staticmethod
    def _writeOneStringStructureToStream(writer, oneString):
        writer.write_string(oneString)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "one_string_structure.blob")
    ONE_STRING = "This is a string!"
    ONE_STRING_STRUCTURE_BIT_SIZE = (1 + len(ONE_STRING)) * 8
