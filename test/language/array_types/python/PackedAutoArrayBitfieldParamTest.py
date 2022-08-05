import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class PackedAutoArrayBitfieldParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").packed_auto_array_bitfield_param

    def testWriteRead(self):
        parameterizedBitfieldLength = self._createParameterizedBitfieldLength()
        writer = zserio.BitStreamWriter()
        parameterizedBitfieldLength.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readParameterizedBitfieldLength = self.api.ParameterizedBitfieldLength.from_reader(reader,
                                                                                           self.NUM_BITS_PARAM)
        self.assertEqual(parameterizedBitfieldLength, readParameterizedBitfieldLength)

    def testWriteReadFile(self):
        parameterizedBitfieldLength = self._createParameterizedBitfieldLength()
        zserio.serialize_to_file(parameterizedBitfieldLength, self.BLOB_NAME)

        readParameterizedBitfieldLength = zserio.deserialize_from_file(self.api.ParameterizedBitfieldLength,
                                                                       self.BLOB_NAME, self.NUM_BITS_PARAM)
        self.assertEqual(parameterizedBitfieldLength, readParameterizedBitfieldLength)

    def _createParameterizedBitfieldLength(self):
        parameterizedBitfieldLength = self.api.ParameterizedBitfieldLength(self.NUM_BITS_PARAM)
        dynamicBitfieldArray = list(range(self.DYNAMIC_BITFIELD_ARRAY_SIZE))
        parameterizedBitfieldLength.dynamic_bitfield_array = dynamicBitfieldArray

        return parameterizedBitfieldLength

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "packed_auto_array_bitfield_param.blob")

    NUM_BITS_PARAM = 9
    DYNAMIC_BITFIELD_ARRAY_SIZE = (1 << 9) - 1
