import unittest
import zserio

from testutils import getZserioApi

class SimpleStructureTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "structure_types.zs").simple_structure

    def testEmptyConstructor(self):
        simpleStructure = self.api.SimpleStructure()
        self.assertEqual(0, simpleStructure.getNumberA())
        self.assertEqual(0, simpleStructure.getNumberB())
        self.assertEqual(0, simpleStructure.getNumberC())

    def testFromReader(self):
        numberA = 0x07
        numberB = 0xFF
        numberC = 0x7F
        writer = zserio.BitStreamWriter()
        SimpleStructureTest._writeSimpleStructureToStream(writer, numberA, numberB, numberC)
        reader = zserio.BitStreamReader(writer.getByteArray())
        simpleStructure = self.api.SimpleStructure.fromReader(reader)
        self.assertEqual(numberA, simpleStructure.getNumberA())
        self.assertEqual(numberB, simpleStructure.getNumberB())
        self.assertEqual(numberC, simpleStructure.getNumberC())

    def testFromFields(self):
        numberA = 0x01
        numberB = 0xAA
        numberC = 0x55
        simpleStructure = self.api.SimpleStructure.fromFields(numberA, numberB, numberC)
        self.assertEqual(numberA, simpleStructure.getNumberA())
        self.assertEqual(numberB, simpleStructure.getNumberB())
        self.assertEqual(numberC, simpleStructure.getNumberC())

    def testEq(self):
        simpleStructure1 = self.api.SimpleStructure()
        simpleStructure2 = self.api.SimpleStructure()
        self.assertTrue(simpleStructure1 == simpleStructure2)

        numberA = 0x03
        numberB = 0xDE
        numberC = 0x55
        simpleStructure1.setNumberA(numberA)
        simpleStructure1.setNumberB(numberB)
        simpleStructure1.setNumberC(numberC)
        simpleStructure2.setNumberA(numberA)
        simpleStructure2.setNumberB(numberB + 1)
        simpleStructure2.setNumberC(numberC)
        self.assertFalse(simpleStructure1 == simpleStructure2)

        simpleStructure2.setNumberB(numberB)
        self.assertTrue(simpleStructure1 == simpleStructure2)

    def testHash(self):
        simpleStructure1 = self.api.SimpleStructure()
        simpleStructure2 = self.api.SimpleStructure()
        self.assertEqual(hash(simpleStructure1), hash(simpleStructure2))

        numberA = 0x04
        numberB = 0xCD
        numberC = 0x57
        simpleStructure1.setNumberA(numberA)
        simpleStructure1.setNumberB(numberB)
        simpleStructure1.setNumberC(numberC)
        simpleStructure2.setNumberA(numberA)
        simpleStructure2.setNumberB(numberB + 1)
        simpleStructure2.setNumberC(numberC)
        self.assertTrue(hash(simpleStructure1) != hash(simpleStructure2))

        simpleStructure2.setNumberB(numberB)
        self.assertEqual(hash(simpleStructure1), hash(simpleStructure2))

    def testGetSetNumberA(self):
        simpleStructure = self.api.SimpleStructure()
        numberA = 0x02
        simpleStructure.setNumberA(numberA)
        self.assertEqual(numberA, simpleStructure.getNumberA())

    def testGetSetNumberB(self):
        simpleStructure = self.api.SimpleStructure()
        numberB = 0x23
        simpleStructure.setNumberB(numberB)
        self.assertEqual(numberB, simpleStructure.getNumberB())

    def testGetSetNumberC(self):
        simpleStructure = self.api.SimpleStructure()
        numberC = 0x11
        simpleStructure.setNumberC(numberC)
        self.assertEqual(numberC, simpleStructure.getNumberC())

    def testBitSizeOf(self):
        numberA = 0x00
        numberB = 0x01
        numberC = 0x02
        simpleStructure = self.api.SimpleStructure.fromFields(numberA, numberB, numberC)
        self.assertEqual(self.SIMPLE_STRUCTURE_BIT_SIZE, simpleStructure.bitSizeOf())

    def testInitializeOffsets(self):
        numberA = 0x05
        numberB = 0x10
        numberC = 0x44
        simpleStructure = self.api.SimpleStructure.fromFields(numberA, numberB, numberC)
        bitPosition = 1
        self.assertEqual(self.SIMPLE_STRUCTURE_BIT_SIZE + bitPosition,
                         simpleStructure.initializeOffsets(bitPosition))

    def testReadWrite(self):
        numberA = 0x07
        numberB = 0x22
        numberC = 0x33
        simpleStructure = self.api.SimpleStructure.fromFields(numberA, numberB, numberC)
        writer = zserio.BitStreamWriter()
        simpleStructure.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readSimpleStructure = self.api.SimpleStructure()
        readSimpleStructure.read(reader)
        self.assertEqual(numberA, readSimpleStructure.getNumberA())
        self.assertEqual(numberB, readSimpleStructure.getNumberB())
        self.assertEqual(numberC, readSimpleStructure.getNumberC())
        self.assertTrue(simpleStructure == readSimpleStructure)

    @staticmethod
    def _writeSimpleStructureToStream(writer, numberA, numberB, numberC):
        writer.writeBits(numberA, 3)
        writer.writeBits(numberB, 8)
        writer.writeBits(numberC, 7)

    SIMPLE_STRUCTURE_BIT_SIZE = 18
