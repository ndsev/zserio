import zserio

import WithRangeCheckCode

class VarUInt64RangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarUInt64LowerBound(self):
        self._checkVarUInt64Value(VARUINT64_LOWER_BOUND)

    def testVarUInt64UpperBound(self):
        self._checkVarUInt64Value(VARUINT64_UPPER_BOUND)

    def testVarUInt64BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarUInt64Value(VARUINT64_LOWER_BOUND - 1)

    def testVarUInt64AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarUInt64Value(VARUINT64_UPPER_BOUND + 1)

    def _checkVarUInt64Value(self, value):
        varUInt64RangeCheckCompound = self.api.VarUInt64RangeCheckCompound(value)
        bitBuffer = zserio.serialize(varUInt64RangeCheckCompound)
        readVarUInt64RangeCheckCompound = zserio.deserialize(self.api.VarUInt64RangeCheckCompound, bitBuffer)
        self.assertEqual(varUInt64RangeCheckCompound, readVarUInt64RangeCheckCompound)

VARUINT64_LOWER_BOUND = zserio.limits.VARUINT64_MIN
VARUINT64_UPPER_BOUND = zserio.limits.VARUINT64_MAX
