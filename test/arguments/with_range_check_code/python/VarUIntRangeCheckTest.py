import unittest
import zserio

from testutils import getZserioApi

class VarUIntRangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).varuint_range_check

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
        writer = zserio.BitStreamWriter()
        varUIntRangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVarUIntRangeCheckCompound = self.api.VarUIntRangeCheckCompound.fromReader(reader)
        self.assertEqual(varUIntRangeCheckCompound, readVarUIntRangeCheckCompound)

VARUINT_LOWER_BOUND = zserio.limits.UINT64_MIN
VARUINT_UPPER_BOUND = zserio.limits.UINT64_MAX
