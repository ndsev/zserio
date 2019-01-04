import unittest
import zserio

from testutils import getZserioApi

class VarUInt64RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).varuint64_range_check

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
        varUInt64RangeCheckCompound = self.api.VarUInt64RangeCheckCompound.fromFields(value)
        writer = zserio.BitStreamWriter()
        varUInt64RangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVarUInt64RangeCheckCompound = self.api.VarUInt64RangeCheckCompound.fromReader(reader)
        self.assertEqual(varUInt64RangeCheckCompound, readVarUInt64RangeCheckCompound)

VARUINT64_LOWER_BOUND = zserio.limits.VARUINT64_MIN
VARUINT64_UPPER_BOUND = zserio.limits.VARUINT64_MAX
