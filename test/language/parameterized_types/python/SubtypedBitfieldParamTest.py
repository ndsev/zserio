import unittest
import zserio

from testutils import getZserioApi

class SubtypedBitfieldParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").subtyped_bitfield_param

    def testWrite(self):
        subtypedBitfieldParamHolder = self._createSubtypedBitfieldParamHolder()
        writer = zserio.BitStreamWriter()
        subtypedBitfieldParamHolder.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkSubtypedBitfieldParamHolderInStream(reader, subtypedBitfieldParamHolder)

        reader.setBitPosition(0)
        readSubtypedBitfieldParamHolder = self.api.SubtypedBitfieldParamHolder.fromReader(reader)
        self.assertEqual(subtypedBitfieldParamHolder, readSubtypedBitfieldParamHolder)

    def _createSubtypedBitfieldParamHolder(self):
        subtypedBitfieldParam = self.api.SubtypedBitfieldParam.fromFields(
            self.SUBTYPED_BITFIELD_PARAM, self.SUBTYPED_BITFIELD_PARAM_VALUE,
            self.SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE)

        return self.api.SubtypedBitfieldParamHolder.fromFields(subtypedBitfieldParam)

    def _checkSubtypedBitfieldParamHolderInStream(self, stream, subtypedBitfieldParamHolder):
        subtypedBitfieldParam = subtypedBitfieldParamHolder.getSubtypedBitfieldParam()
        self.assertEqual(subtypedBitfieldParam.getParam(), self.SUBTYPED_BITFIELD_PARAM)
        self.assertEqual(subtypedBitfieldParam.getValue(), stream.readBits(16))
        self.assertEqual(subtypedBitfieldParam.getExtraValue(), stream.readBits(32))

    SUBTYPED_BITFIELD_PARAM = 11
    SUBTYPED_BITFIELD_PARAM_VALUE = 0x0BED
    SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE = 0x0BEDDEAD
