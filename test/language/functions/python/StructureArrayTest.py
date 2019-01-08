import unittest
import zserio

from testutils import getZserioApi

class StructureArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_array

        cls.ITEMS = [cls.api.Item.fromFields(1, 2),
                     cls.api.Item.fromFields(3, 4),
                     cls.api.Item.fromFields(5, 6)]
        cls.NUM_ITEMS = len(cls.ITEMS)

    def testStructureArrayElement0(self):
        self._checkStructureArray(0)

    def testStructureArrayElement1(self):
        self._checkStructureArray(1)

    def testStructureArrayElement2(self):
        self._checkStructureArray(2)

    def _writeStructureArrayToStream(self, writer, pos):
        writer.writeBits(self.NUM_ITEMS, 16)

        for item in self.ITEMS:
            writer.writeBits(item.getA(), 8)
            writer.writeBits(item.getB(), 8)

        writer.writeBits(pos, 16)

    def _createStructureArray(self, pos):
        return self.api.StructureArray.fromFields(self.NUM_ITEMS, self.ITEMS, pos)

    def _checkStructureArray(self, pos):
        structureArray = self._createStructureArray(pos)
        self.assertEqual(self.ITEMS[pos], structureArray.funcGetElement())

        writer = zserio.BitStreamWriter()
        structureArray.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeStructureArrayToStream(expectedWriter, pos)
        self.assertTrue(expectedWriter.getByteArray() == writer.getByteArray())

        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructureArray = self.api.StructureArray.fromReader(reader)
        self.assertEqual(structureArray, readStructureArray)
