import unittest
import zserio

from testutils import getZserioApi

class StructureArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_array

        cls.ITEMS = [cls.api.Item(1, 2),
                     cls.api.Item(3, 4),
                     cls.api.Item(5, 6)]
        cls.NUM_ITEMS = len(cls.ITEMS)

    def testStructureArrayElement0(self):
        self._checkStructureArray(0)

    def testStructureArrayElement1(self):
        self._checkStructureArray(1)

    def testStructureArrayElement2(self):
        self._checkStructureArray(2)

    def _writeStructureArrayToStream(self, writer, pos):
        writer.write_bits(self.NUM_ITEMS, 16)

        for item in self.ITEMS:
            writer.write_bits(item.a, 8)
            writer.write_bits(item.b, 8)

        writer.write_bits(pos, 16)

    def _createStructureArray(self, pos):
        return self.api.StructureArray(self.NUM_ITEMS, self.ITEMS, pos)

    def _checkStructureArray(self, pos):
        structureArray = self._createStructureArray(pos)
        self.assertEqual(self.ITEMS[pos], structureArray.get_element())

        writer = zserio.BitStreamWriter()
        structureArray.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeStructureArrayToStream(expectedWriter, pos)
        self.assertTrue(expectedWriter.byte_array == writer.byte_array)
        self.assertTrue(expectedWriter.bitposition == writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructureArray = self.api.StructureArray.from_reader(reader)
        self.assertEqual(structureArray, readStructureArray)
