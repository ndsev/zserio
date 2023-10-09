import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class ParamWithOptionalTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").param_with_optional

    def testBitSizeOf(self):
        holder = self._createHolder()
        bitPosition = 2
        self.assertEqual(self.HOLDER_BIT_SIZE, holder.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        holder = self._createHolder()
        bitPosition = 2
        self.assertEqual(bitPosition + self.HOLDER_BIT_SIZE, holder.initialize_offsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeHolderToStream(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        holder = self.api.Holder.from_reader(reader)
        self._checkHolder(holder)

    def testWriteRead(self):
        holder = self._createHolder()
        bitBuffer = zserio.serialize(holder)

        self.assertEqual(holder.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(holder.initialize_offsets(), bitBuffer.bitsize)

        readHolder = zserio.deserialize(self.api.Holder, bitBuffer)
        self._checkHolder(readHolder)

    def testWriteReadFile(self):
        holder = self._createHolder()

        zserio.serialize_to_file(holder, self.BLOB_NAME)

        readHolder = zserio.deserialize_from_file(self.api.Holder, self.BLOB_NAME)
        self._checkHolder(readHolder)

    def _createHolder(self):
        param = self.api.Param(self.HAS_EXTRA, self.EXTRA_PARAM)
        value = self.api.Value(param, None, self.api.ExtraValue(param.extra_param, self.EXTRA_VALUE))

        return self.api.Holder(param, value)

    def _writeHolderToStream(self, writer):
        writer.write_bool(self.HAS_EXTRA)
        writer.write_bits(self.EXTRA_PARAM, 7)
        writer.write_bits(self.EXTRA_VALUE, 64)

    def _checkHolder(self, holder):
        self.assertEqual(self.HAS_EXTRA, holder.param.has_extra)
        self.assertEqual(self.EXTRA_PARAM, holder.param.extra_param)
        self.assertEqual(self.EXTRA_VALUE, holder.value.extra_value.value)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "param_with_optional.blob")

    HAS_EXTRA = True
    EXTRA_PARAM = 0x00
    EXTRA_VALUE = 0xDEAD
    HOLDER_BIT_SIZE = 1 + 7 + 64
