import os
import zserio

import ArrayTypes

from testutils import getApiDir


class AutoArrayBitfieldParamTest(ArrayTypes.TestCase):
    def testWriteRead(self):
        parameterizedBitfieldLength = self._createParameterizedBitfieldLength()
        writer = zserio.BitStreamWriter()
        parameterizedBitfieldLength.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkParameterizedBitfieldLengthInStream(reader, parameterizedBitfieldLength)
        reader.bitposition = 0
        readParameterizedBitfieldLength = self.api.ParameterizedBitfieldLength.from_reader(
            reader, self.NUM_BITS_PARAM
        )
        self.assertEqual(parameterizedBitfieldLength, readParameterizedBitfieldLength)

    def testWriteReadFile(self):
        parameterizedBitfieldLength = self._createParameterizedBitfieldLength()
        zserio.serialize_to_file(parameterizedBitfieldLength, self.BLOB_NAME)

        readParameterizedBitfieldLength = zserio.deserialize_from_file(
            self.api.ParameterizedBitfieldLength, self.BLOB_NAME, self.NUM_BITS_PARAM
        )
        self.assertEqual(parameterizedBitfieldLength, readParameterizedBitfieldLength)

    def _createParameterizedBitfieldLength(self):
        parameterizedBitfieldLength = self.api.ParameterizedBitfieldLength(self.NUM_BITS_PARAM)
        dynamicBitfieldArray = list(range(self.DYNAMIC_BITFIELD_ARRAY_SIZE))
        parameterizedBitfieldLength.dynamic_bitfield_array = dynamicBitfieldArray

        return parameterizedBitfieldLength

    def _checkParameterizedBitfieldLengthInStream(self, reader, parameterizedBitfieldLength):
        self.assertEqual(self.NUM_BITS_PARAM, parameterizedBitfieldLength.num_bits)
        self.assertEqual(self.DYNAMIC_BITFIELD_ARRAY_SIZE, reader.read_varsize())
        for i in range(self.DYNAMIC_BITFIELD_ARRAY_SIZE):
            self.assertEqual(i, reader.read_bits(self.NUM_BITS_PARAM))

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "auto_array_bitfield_param.blob")

    NUM_BITS_PARAM = 9
    DYNAMIC_BITFIELD_ARRAY_SIZE = (1 << 9) - 1
