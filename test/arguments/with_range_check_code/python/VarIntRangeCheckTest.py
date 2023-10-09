import zserio

import WithRangeCheckCode

class VarIntRangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarIntLowerBound(self):
        self._checkVarIntValue(VARINT_LOWER_BOUND)

    def testVarIntUpperBound(self):
        self._checkVarIntValue(VARINT_UPPER_BOUND)

    def testVarIntBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarIntValue(VARINT_LOWER_BOUND - 1)

    def testVarIntAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarIntValue(VARINT_UPPER_BOUND + 1)

    def _checkVarIntValue(self, value):
        varIntRangeCheckCompound = self.api.VarIntRangeCheckCompound(value)
        bitBuffer = zserio.serialize(varIntRangeCheckCompound)
        readVarIntRangeCheckCompound = zserio.deserialize(self.api.VarIntRangeCheckCompound, bitBuffer)
        self.assertEqual(varIntRangeCheckCompound, readVarIntRangeCheckCompound)

VARINT_LOWER_BOUND = zserio.limits.INT64_MIN
VARINT_UPPER_BOUND = zserio.limits.INT64_MAX
