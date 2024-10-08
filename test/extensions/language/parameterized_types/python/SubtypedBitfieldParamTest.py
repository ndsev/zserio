import zserio

import ParameterizedTypes


class SubtypedBitfieldParamTest(ParameterizedTypes.TestCase):
    def testWrite(self):
        subtypedBitfieldParamHolder = self._createSubtypedBitfieldParamHolder()
        writer = zserio.BitStreamWriter()
        subtypedBitfieldParamHolder.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkSubtypedBitfieldParamHolderInStream(reader, subtypedBitfieldParamHolder)

        reader.bitposition = 0
        readSubtypedBitfieldParamHolder = self.api.SubtypedBitfieldParamHolder.from_reader(reader)
        self.assertEqual(subtypedBitfieldParamHolder, readSubtypedBitfieldParamHolder)

    def _createSubtypedBitfieldParamHolder(self):
        subtypedBitfieldParam = self.api.SubtypedBitfieldParam(
            self.SUBTYPED_BITFIELD_PARAM,
            self.SUBTYPED_BITFIELD_PARAM_VALUE,
            self.SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE,
        )

        return self.api.SubtypedBitfieldParamHolder(subtypedBitfieldParam)

    def _checkSubtypedBitfieldParamHolderInStream(self, stream, subtypedBitfieldParamHolder):
        subtypedBitfieldParam = subtypedBitfieldParamHolder.subtyped_bitfield_param
        self.assertEqual(subtypedBitfieldParam.param, self.SUBTYPED_BITFIELD_PARAM)
        self.assertEqual(subtypedBitfieldParam.value, stream.read_bits(16))
        self.assertEqual(subtypedBitfieldParam.extra_value, stream.read_bits(32))

    SUBTYPED_BITFIELD_PARAM = 11
    SUBTYPED_BITFIELD_PARAM_VALUE = 0x0BED
    SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE = 0x0BEDDEAD
