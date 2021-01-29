import unittest
import zserio

from testutils import getZserioApi

class VarIntRangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).varint_range_check

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
        writer = zserio.BitStreamWriter()
        varIntRangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVarIntRangeCheckCompound = self.api.VarIntRangeCheckCompound.fromReader(reader)
        self.assertEqual(varIntRangeCheckCompound, readVarIntRangeCheckCompound)

VARINT_LOWER_BOUND = zserio.limits.INT64_MIN
VARINT_UPPER_BOUND = zserio.limits.INT64_MAX
