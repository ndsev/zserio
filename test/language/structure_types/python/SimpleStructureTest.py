import unittest
import zserio

from testutils import getZserioApi

class SimpleStructureTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "structure_types.zs").simple_structure

    def testConstructor(self):
        simpleStructure = self.api.SimpleStructure()
        self.assertEqual(0, simpleStructure.number_a)
        self.assertEqual(0, simpleStructure.number_b)
        self.assertEqual(0, simpleStructure.number_c)

        numberA = 0x01
        numberB = 0xAA
        numberC = 0x55
        simpleStructure = self.api.SimpleStructure(numberA, numberB, numberC)
        self.assertEqual(numberA, simpleStructure.number_a)
        self.assertEqual(numberB, simpleStructure.number_b)
        self.assertEqual(numberC, simpleStructure.number_c)

        simpleStructure = self.api.SimpleStructure(numberB_=numberB, numberA_=numberA, numberC_=numberC)
        self.assertEqual(numberA, simpleStructure.number_a)
        self.assertEqual(numberB, simpleStructure.number_b)
        self.assertEqual(numberC, simpleStructure.number_c)

    def testFromReader(self):
        numberA = 0x07
        numberB = 0xFF
        numberC = 0x7F
        writer = zserio.BitStreamWriter()
        SimpleStructureTest._writeSimpleStructureToStream(writer, numberA, numberB, numberC)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        simpleStructure = self.api.SimpleStructure.fromReader(reader)
        self.assertEqual(numberA, simpleStructure.number_a)
        self.assertEqual(numberB, simpleStructure.number_b)
        self.assertEqual(numberC, simpleStructure.number_c)

    def testEq(self):
        simpleStructure1 = self.api.SimpleStructure()
        simpleStructure2 = self.api.SimpleStructure()
        self.assertTrue(simpleStructure1 == simpleStructure2)

        numberA = 0x03
        numberB = 0xDE
        numberC = 0x55
        simpleStructure1.number_a = numberA
        simpleStructure1.number_b = numberB
        simpleStructure1.number_c = numberC
        simpleStructure2.number_a = numberA
        simpleStructure2.number_b = numberB + 1
        simpleStructure2.number_c = numberC
        self.assertFalse(simpleStructure1 == simpleStructure2)

        simpleStructure2.number_b = numberB
        self.assertTrue(simpleStructure1 == simpleStructure2)

    def testHash(self):
        simpleStructure1 = self.api.SimpleStructure()
        simpleStructure2 = self.api.SimpleStructure()
        self.assertEqual(hash(simpleStructure1), hash(simpleStructure2))

        numberA = 0x04
        numberB = 0xCD
        numberC = 0x57
        simpleStructure1.number_a = numberA
        simpleStructure1.number_b = numberB
        simpleStructure1.number_c = numberC
        simpleStructure2.number_a = numberA
        simpleStructure2.number_b = numberB + 1
        simpleStructure2.number_c = numberC
        self.assertTrue(hash(simpleStructure1) != hash(simpleStructure2))

        simpleStructure2.number_b = numberB
        self.assertEqual(hash(simpleStructure1), hash(simpleStructure2))

    def testGetSetNumberA(self):
        simpleStructure = self.api.SimpleStructure()
        numberA = 0x02
        simpleStructure.number_a = numberA
        self.assertEqual(numberA, simpleStructure.number_a)

    def testGetSetNumberB(self):
        simpleStructure = self.api.SimpleStructure()
        numberB = 0x23
        simpleStructure.number_b = numberB
        self.assertEqual(numberB, simpleStructure.number_b)

    def testGetSetNumberC(self):
        simpleStructure = self.api.SimpleStructure()
        numberC = 0x11
        simpleStructure.number_c = numberC
        self.assertEqual(numberC, simpleStructure.number_c)

    def testBitSizeOf(self):
        numberA = 0x00
        numberB = 0x01
        numberC = 0x02
        simpleStructure = self.api.SimpleStructure(numberA, numberB, numberC)
        self.assertEqual(self.SIMPLE_STRUCTURE_BIT_SIZE, simpleStructure.bitSizeOf())

    def testInitializeOffsets(self):
        numberA = 0x05
        numberB = 0x10
        numberC = 0x44
        simpleStructure = self.api.SimpleStructure(numberA_=numberA, numberB_=numberB, numberC_=numberC)
        bitPosition = 1
        self.assertEqual(self.SIMPLE_STRUCTURE_BIT_SIZE + bitPosition,
                         simpleStructure.initializeOffsets(bitPosition))

    def testReadWrite(self):
        numberA = 0x07
        numberB = 0x22
        numberC = 0x33
        simpleStructure = self.api.SimpleStructure(numberA, numberC_=numberC, numberB_=numberB)
        writer = zserio.BitStreamWriter()
        simpleStructure.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readSimpleStructure = self.api.SimpleStructure()
        readSimpleStructure.read(reader)
        self.assertEqual(numberA, readSimpleStructure.number_a)
        self.assertEqual(numberB, readSimpleStructure.number_b)
        self.assertEqual(numberC, readSimpleStructure.number_c)
        self.assertTrue(simpleStructure == readSimpleStructure)

    @staticmethod
    def _writeSimpleStructureToStream(writer, numberA, numberB, numberC):
        writer.write_bits(numberA, 3)
        writer.write_bits(numberB, 8)
        writer.write_bits(numberC, 7)

    SIMPLE_STRUCTURE_BIT_SIZE = 18
