import unittest
import zserio

from testutils import getZserioApi

class VarUInt32RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).varuint32_range_check

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
        varUInt32RangeCheckCompound = self.api.VarUInt32RangeCheckCompound.fromFields(value)
        writer = zserio.BitStreamWriter()
        varUInt32RangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVarUInt32RangeCheckCompound = self.api.VarUInt32RangeCheckCompound.fromReader(reader)
        self.assertEqual(varUInt32RangeCheckCompound, readVarUInt32RangeCheckCompound)

VARUINT32_LOWER_BOUND = zserio.limits.VARUINT32_MIN
VARUINT32_UPPER_BOUND = zserio.limits.VARUINT32_MAX
