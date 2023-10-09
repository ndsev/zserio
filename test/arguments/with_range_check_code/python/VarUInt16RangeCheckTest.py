import zserio

import WithRangeCheckCode

class VarUInt16RangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarUInt16LowerBound(self):
        self._checkVarUInt16Value(VARUINT16_LOWER_BOUND)

    def testVarUInt16UpperBound(self):
        self._checkVarUInt16Value(VARUINT16_UPPER_BOUND)

    def testVarUInt16BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarUInt16Value(VARUINT16_LOWER_BOUND - 1)

    def testVarUInt16AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarUInt16Value(VARUINT16_UPPER_BOUND + 1)

    def _checkVarUInt16Value(self, value):
        varUInt16RangeCheckCompound = self.api.VarUInt16RangeCheckCompound(value)
        bitBuffer = zserio.serialize(varUInt16RangeCheckCompound)
        readVarUInt16RangeCheckCompound = zserio.deserialize(self.api.VarUInt16RangeCheckCompound, bitBuffer)
        self.assertEqual(varUInt16RangeCheckCompound, readVarUInt16RangeCheckCompound)

VARUINT16_LOWER_BOUND = zserio.limits.VARUINT16_MIN
VARUINT16_UPPER_BOUND = zserio.limits.VARUINT16_MAX
