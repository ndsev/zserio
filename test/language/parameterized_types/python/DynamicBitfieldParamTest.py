import zserio

import ParameterizedTypes

class DynamicBitfieldParamTest(ParameterizedTypes.TestCase):
    def testWrite(self):
        dynamicBitfieldParamHolder = self._createDynamicBitfieldParamHolder()
        writer = zserio.BitStreamWriter()
        dynamicBitfieldParamHolder.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkDynamicBitfieldParamHolderInStream(reader, dynamicBitfieldParamHolder)

        reader.bitposition = 0
        readDynamicBitfieldParamHolder = self.api.DynamicBitfieldParamHolder.from_reader(reader)
        self.assertEqual(dynamicBitfieldParamHolder, readDynamicBitfieldParamHolder)

    def _createDynamicBitfieldParamHolder(self):
        dynamicBitfieldParam = self.api.DynamicBitfieldParam(self.BITFIELD, self.DYNAMIC_BITFIELD_PARAM_VALUE,
            self.DYNAMIC_BITFIELD_EXTRA_VALUE)

        return self.api.DynamicBitfieldParamHolder(self.LENGTH, self.BITFIELD, dynamicBitfieldParam)

    def _checkDynamicBitfieldParamHolderInStream(self, stream, dynamicBitfieldParamHolder):
        self.assertEqual(dynamicBitfieldParamHolder.length, stream.read_bits(4))
        self.assertEqual(dynamicBitfieldParamHolder.bitfield, stream.read_signed_bits(self.LENGTH))

        dynamicBitfieldParam = dynamicBitfieldParamHolder.dynamic_bitfield_param
        self.assertEqual(dynamicBitfieldParam.param, self.BITFIELD)
        self.assertEqual(dynamicBitfieldParam.value, stream.read_bits(16))
        self.assertEqual(dynamicBitfieldParam.extra_value, stream.read_bits(32))

    LENGTH = 5
    BITFIELD = 11
    DYNAMIC_BITFIELD_PARAM_VALUE = 0x0BED
    DYNAMIC_BITFIELD_EXTRA_VALUE = 0x0BEDDEAD
