import os
import zserio

import ParameterizedTypes
from testutils import getApiDir

class NestedParameterizedFieldTest(ParameterizedTypes.TestCase):
    def testBitSizeOf(self):
        topLevel = self._createTopLevel()
        bitPosition = 2
        self.assertEqual(self.TOP_LEVEL_BIT_SIZE, topLevel.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        topLevel = self._createTopLevel()
        bitPosition = 2
        self.assertEqual(bitPosition + self.TOP_LEVEL_BIT_SIZE, topLevel.initialize_offsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeTopLevelToStream(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        topLevel = self.api.TopLevel.from_reader(reader)
        self._checkTopLevel(topLevel)

    def testWriteRead(self):
        topLevel = self._createTopLevel()
        bitBuffer = zserio.serialize(topLevel)

        self.assertEqual(topLevel.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(topLevel.initialize_offsets(), bitBuffer.bitsize)

        readTopLevel = zserio.deserialize(self.api.TopLevel, bitBuffer)
        self._checkTopLevel(readTopLevel)

    def testWriteReadFile(self):
        topLevel = self._createTopLevel()

        zserio.serialize_to_file(topLevel, self.BLOB_NAME)

        readTopLevel = zserio.deserialize_from_file(self.api.TopLevel, self.BLOB_NAME)
        self._checkTopLevel(readTopLevel)

    def _createTopLevel(self):
        param = self.api.Param(self.PARAMETER, self.VALUE, self.EXTRA_VALUE)
        paramHolder = self.api.ParamHolder(self.PARAMETER, param)

        return self.api.TopLevel(paramHolder)

    def _writeTopLevelToStream(self, writer):
        writer.write_bits(self.PARAMETER, 16)
        writer.write_bits(self.VALUE, 16)
        writer.write_bits(self.EXTRA_VALUE, 32)

    def _checkTopLevel(self, topLevel):
        self.assertEqual(self.PARAMETER, topLevel.param_holder.parameter)
        self.assertEqual(self.VALUE, topLevel.param_holder.param.value)
        self.assertEqual(self.EXTRA_VALUE, topLevel.param_holder.param.extra_value)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "nested_parameterized_field.blob")

    PARAMETER = 11
    VALUE = 0xAB
    EXTRA_VALUE = 0xDEAD
    TOP_LEVEL_BIT_SIZE = 16 + 16 + 32
