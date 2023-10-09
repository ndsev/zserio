import zserio

import WithRangeCheckCode

class VarSizeRangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarSizeLowerBound(self):
        self._checkVarSizeValue(VARSIZE_LOWER_BOUND)

    def testVarSizeUpperBound(self):
        self._checkVarSizeValue(VARSIZE_UPPER_BOUND)

    def testVarSizeBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarSizeValue(VARSIZE_LOWER_BOUND - 1)

    def testVarSizeAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarSizeValue(VARSIZE_UPPER_BOUND + 1)

    def _checkVarSizeValue(self, value):
        varSizeRangeCheckCompound = self.api.VarSizeRangeCheckCompound(value)
        bitBuffer = zserio.serialize(varSizeRangeCheckCompound)
        readVarSizeRangeCheckCompound = zserio.deserialize(self.api.VarSizeRangeCheckCompound, bitBuffer)
        self.assertEqual(varSizeRangeCheckCompound, readVarSizeRangeCheckCompound)

VARSIZE_LOWER_BOUND = zserio.limits.VARSIZE_MIN
VARSIZE_UPPER_BOUND = zserio.limits.VARSIZE_MAX
