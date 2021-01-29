import unittest
import zserio

from testutils import getZserioApi

class VarUInt16RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).varuint16_range_check

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
        writer = zserio.BitStreamWriter()
        varUInt16RangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVarUInt16RangeCheckCompound = self.api.VarUInt16RangeCheckCompound.fromReader(reader)
        self.assertEqual(varUInt16RangeCheckCompound, readVarUInt16RangeCheckCompound)

VARUINT16_LOWER_BOUND = zserio.limits.VARUINT16_MIN
VARUINT16_UPPER_BOUND = zserio.limits.VARUINT16_MAX
