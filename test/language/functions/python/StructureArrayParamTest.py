import unittest
import zserio

from testutils import getZserioApi

class StructureArrayParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_array_param

        cls.CHILD_BIT_SIZE = 19
        cls.CHILDREN = [cls.api.ChildStructure(cls.CHILD_BIT_SIZE, 0xAABB),
                        cls.api.ChildStructure(cls.CHILD_BIT_SIZE, 0xCCDD)]
        cls.NUM_CHILDREN = len(cls.CHILDREN)

    def testParentStructure(self):
        parentStructure = self._createParentStructure()
        self.assertEqual(self.CHILD_BIT_SIZE, parentStructure.funcGetChildBitSize())

        writer = zserio.BitStreamWriter()
        parentStructure.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeParentStructureToStream(expectedWriter)
        self.assertTrue(expectedWriter.byte_array == writer.byte_array)
        self.assertTrue(expectedWriter.bitposition == writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readParentStructure = self.api.ParentStructure.fromReader(reader)
        self.assertEqual(parentStructure, readParentStructure)

    def _writeParentStructureToStream(self, writer):
        writer.write_bits(self.NUM_CHILDREN, 8)

        for childStructure in self.CHILDREN:
            writer.write_bits(childStructure.value, childStructure.bit_size)

    def _createParentStructure(self):
        return self.api.ParentStructure(self.NUM_CHILDREN, self.CHILDREN)
