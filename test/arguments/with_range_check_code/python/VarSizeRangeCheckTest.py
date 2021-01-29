import unittest
import zserio

from testutils import getZserioApi

class VarSizeRangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).varsize_range_check

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
        writer = zserio.BitStreamWriter()
        varSizeRangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVarSizeRangeCheckCompound = self.api.VarSizeRangeCheckCompound.fromReader(reader)
        self.assertEqual(varSizeRangeCheckCompound, readVarSizeRangeCheckCompound)

VARSIZE_LOWER_BOUND = zserio.limits.VARSIZE_MIN
VARSIZE_UPPER_BOUND = zserio.limits.VARSIZE_MAX
