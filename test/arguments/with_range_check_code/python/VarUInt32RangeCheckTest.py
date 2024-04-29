import zserio

import WithRangeCheckCode


class VarUInt32RangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarUInt32LowerBound(self):
        self._checkVarUInt32Value(VARUINT32_LOWER_BOUND)

    def testVarUInt32UpperBound(self):
        self._checkVarUInt32Value(VARUINT32_UPPER_BOUND)

    def testVarUInt32BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarUInt32Value(VARUINT32_LOWER_BOUND - 1)

    def testVarUInt32AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarUInt32Value(VARUINT32_UPPER_BOUND + 1)

    def _checkVarUInt32Value(self, value):
        varUInt32RangeCheckCompound = self.api.VarUInt32RangeCheckCompound(value)
        bitBuffer = zserio.serialize(varUInt32RangeCheckCompound)
        readVarUInt32RangeCheckCompound = zserio.deserialize(self.api.VarUInt32RangeCheckCompound, bitBuffer)
        self.assertEqual(varUInt32RangeCheckCompound, readVarUInt32RangeCheckCompound)


VARUINT32_LOWER_BOUND = zserio.limits.VARUINT32_MIN
VARUINT32_UPPER_BOUND = zserio.limits.VARUINT32_MAX
