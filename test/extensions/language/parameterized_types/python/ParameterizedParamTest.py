import zserio

import ParameterizedTypes


class ParameterizedParamTest(ParameterizedTypes.TestCase):
    def testWrite(self):
        parameterizedParamHolder = self._createParameterizedParamHolder()
        writer = zserio.BitStreamWriter()
        parameterizedParamHolder.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkParameterizedParamHolderInStream(reader, parameterizedParamHolder)

        reader.bitposition = 0
        readParameterizedParamHolder = self.api.ParameterizedParamHolder.from_reader(reader)
        self.assertEqual(parameterizedParamHolder, readParameterizedParamHolder)

    def _createParameterizedParamHolder(self):
        param = self.api.Param(self.PARAMETER, self.PARAM_VALUE, self.PARAM_EXTRA_VALUE)
        parameterizedParam = self.api.ParameterizedParam(
            param, self.PARAMETERIZED_PARAM_VALUE, self.PARAMETERIZED_PARAM_EXTRA_VALUE
        )

        return self.api.ParameterizedParamHolder(self.PARAMETER, param, parameterizedParam)

    def _checkParameterizedParamHolderInStream(self, stream, parameterizedParamHolder):
        self.assertEqual(parameterizedParamHolder.parameter, stream.read_bits(16))

        param = parameterizedParamHolder.param
        self.assertEqual(param.parameter, self.PARAMETER)
        self.assertEqual(param.value, stream.read_bits(16))
        self.assertEqual(param.extra_value, stream.read_bits(32))

        parameterizedParam = parameterizedParamHolder.parameterized_param
        self.assertEqual(parameterizedParam.param, param)
        self.assertEqual(parameterizedParam.value, stream.read_bits(16))
        self.assertEqual(parameterizedParam.extra_value, stream.read_bits(32))

    PARAMETER = 11
    PARAM_VALUE = 0x0BED
    PARAM_EXTRA_VALUE = 0x0BEDDEAD
    PARAMETERIZED_PARAM_VALUE = 0x0BAD
    PARAMETERIZED_PARAM_EXTRA_VALUE = 0x0BADDEAD
