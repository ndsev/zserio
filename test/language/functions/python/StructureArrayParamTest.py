import unittest
import zserio

from testutils import getZserioApi

class StructureArrayParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_array_param

        cls.CHILD_BIT_SIZE = 19
        cls.ANOTHER_CHILD_BIT_SIZE = 17
        cls.CHILDREN = [cls.api.ChildStructure(cls.CHILD_BIT_SIZE, 0xAABB),
                        cls.api.ChildStructure(cls.CHILD_BIT_SIZE, 0xCCDD)]
        cls.ANOTHER_CHILDREN = [cls.api.ChildStructure(cls.ANOTHER_CHILD_BIT_SIZE, 0xAABB),
                        cls.api.ChildStructure(cls.ANOTHER_CHILD_BIT_SIZE, 0xCCDD)]
        cls.NUM_CHILDREN = len(cls.CHILDREN)
        cls.NUM_ANOTHER_CHILDREN = len(cls.ANOTHER_CHILDREN)

    def testParentStructure(self):
        parentStructure = self._createParentStructure()
        self.assertEqual(self.CHILD_BIT_SIZE, parentStructure.get_child_bit_size())
        self.assertEqual(self.ANOTHER_CHILD_BIT_SIZE,
                         parentStructure.not_left_most.get_another_child_bit_size())

        writer = zserio.BitStreamWriter()
        parentStructure.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeParentStructureToStream(expectedWriter)
        self.assertTrue(expectedWriter.byte_array == writer.byte_array)
        self.assertTrue(expectedWriter.bitposition == writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readParentStructure = self.api.ParentStructure.from_reader(reader)
        self.assertEqual(parentStructure, readParentStructure)

    def _writeParentStructureToStream(self, writer):
        writer.write_bits(self.NUM_CHILDREN, 8)
        for childStructure in self.CHILDREN:
            writer.write_bits(childStructure.value, childStructure.bit_size)

        writer.write_bits(self.NUM_ANOTHER_CHILDREN, 8)
        for childStructure in self.ANOTHER_CHILDREN:
            writer.write_bits(childStructure.value, childStructure.bit_size)

    def _createParentStructure(self):
        return self.api.ParentStructure(self.api.NotLeftMost(), self.NUM_CHILDREN, self.CHILDREN,
                                        self.NUM_ANOTHER_CHILDREN, self.ANOTHER_CHILDREN)
