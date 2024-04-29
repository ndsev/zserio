import zserio

import Functions


class StructureParentChildValueTest(Functions.TestCase):
    def testParentValue(self):
        parentValue = self._createParentValue()
        self.assertEqual(self.CHILD_VALUE, parentValue.get_value())

        writer = zserio.BitStreamWriter()
        parentValue.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        self._writeParentValueToStream(expectedWriter)
        self.assertTrue(expectedWriter.byte_array == writer.byte_array)
        self.assertTrue(expectedWriter.bitposition == writer.bitposition)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readParentValue = self.api.ParentValue.from_reader(reader)
        self.assertEqual(parentValue, readParentValue)

    def _writeParentValueToStream(self, writer):
        writer.write_bits(self.CHILD_VALUE, 32)

    def _createParentValue(self):
        childValue = self.api.ChildValue(self.CHILD_VALUE)

        return self.api.ParentValue(childValue)

    CHILD_VALUE = 0xABCD
