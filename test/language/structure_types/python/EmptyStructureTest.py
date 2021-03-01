import unittest
import zserio

from testutils import getZserioApi

class EmptyStructureTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "structure_types.zs").empty_structure

    def testFromReader(self):
        reader = zserio.BitStreamReader([])
        emptyStructure = self.api.EmptyStructure.from_reader(reader)
        self.assertEqual(0, emptyStructure.bitsizeof())

    def testEq(self):
        emptyStructure1 = self.api.EmptyStructure()
        emptyStructure2 = self.api.EmptyStructure()
        self.assertTrue(emptyStructure1 == emptyStructure2)

    def testHash(self):
        emptyStructure1 = self.api.EmptyStructure()
        emptyStructure2 = self.api.EmptyStructure()
        self.assertEqual(hash(emptyStructure1), hash(emptyStructure2))

    def testBitSizeOf(self):
        bitPosition = 1
        emptyStructure = self.api.EmptyStructure()
        self.assertEqual(0, emptyStructure.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        bitPosition = 1
        emptyStructure = self.api.EmptyStructure()
        self.assertEqual(bitPosition, emptyStructure.initialize_offsets(bitPosition))

    def testRead(self):
        reader = zserio.BitStreamReader([])
        emptyStructure = self.api.EmptyStructure()
        emptyStructure.read(reader)
        self.assertEqual(0, emptyStructure.bitsizeof())

    def testWrite(self):
        writer = zserio.BitStreamWriter()
        emptyStructure = self.api.EmptyStructure()
        emptyStructure.write(writer)
        byteArray = writer.byte_array
        self.assertEqual(0, len(byteArray))
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEmptyStructure = self.api.EmptyStructure.from_reader(reader)
        self.assertEqual(emptyStructure, readEmptyStructure)
