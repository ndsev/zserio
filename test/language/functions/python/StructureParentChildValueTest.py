import unittest
import zserio

from testutils import getZserioApi

class StructureParentChildValueTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_parent_child_value

    def testParentValue(self):
        parentValue = self._createParentValue()
        self.assertEqual(self.CHILD_VALUE, parentValue.funcGetValue())

        writer = zserio.BitStreamWriter()
        parentValue.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeParentValueToStream(expectedWriter)
        self.assertTrue(expectedWriter.getByteArray() == writer.getByteArray())

        reader = zserio.BitStreamReader(writer.getByteArray())
        readParentValue = self.api.ParentValue.fromReader(reader)
        self.assertEqual(parentValue, readParentValue)

    def _writeParentValueToStream(self, writer):
        writer.writeBits(self.CHILD_VALUE, 32)

    def _createParentValue(self):
        childValue = self.api.ChildValue.fromFields(self.CHILD_VALUE)

        return self.api.ParentValue.fromFields(childValue)

    CHILD_VALUE = 0xABCD
