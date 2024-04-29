import zserio

import WithRangeCheckCode


class VarUIntRangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarUIntLowerBound(self):
        self._checkVarUIntValue(VARUINT_LOWER_BOUND)

    def testVarUIntUpperBound(self):
        self._checkVarUIntValue(VARUINT_UPPER_BOUND)

    def testVarUIntBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarUIntValue(VARUINT_LOWER_BOUND - 1)

    def testVarUIntAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarUIntValue(VARUINT_UPPER_BOUND + 1)

    def _checkVarUIntValue(self, value):
        varUIntRangeCheckCompound = self.api.VarUIntRangeCheckCompound(value_=value)
        bitBuffer = zserio.serialize(varUIntRangeCheckCompound)
        readVarUIntRangeCheckCompound = zserio.deserialize(self.api.VarUIntRangeCheckCompound, bitBuffer)
        self.assertEqual(varUIntRangeCheckCompound, readVarUIntRangeCheckCompound)


VARUINT_LOWER_BOUND = zserio.limits.UINT64_MIN
VARUINT_UPPER_BOUND = zserio.limits.UINT64_MAX
