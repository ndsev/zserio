import unittest
import zserio

from testutils import getZserioApi

class VariableIntRangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).variable_int_range_check

    def testVariableIntLowerBound(self):
        self._checkVariableIntValue(VARIABLE_INT_LOWER_BOUND)

    def testVariableIntUpperBound(self):
        self._checkVariableIntValue(VARIABLE_INT_UPPER_BOUND)

    def testVariableIntBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVariableIntValue(VARIABLE_INT_LOWER_BOUND - 1)

    def testVariableIntAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVariableIntValue(VARIABLE_INT_UPPER_BOUND + 1)

    def _checkVariableIntValue(self, value):
        variableIntRangeCheckCompound = self.api.VariableIntRangeCheckCompound.fromFields(NUM_BITS, value)
        writer = zserio.BitStreamWriter()
        variableIntRangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVariableIntRangeCheckCompound = self.api.VariableIntRangeCheckCompound.fromReader(reader)
        self.assertEqual(variableIntRangeCheckCompound, readVariableIntRangeCheckCompound)

NUM_BITS = 10
VARIABLE_INT_LOWER_BOUND = zserio.bitfield.getSignedBitFieldLowerBound(NUM_BITS)
VARIABLE_INT_UPPER_BOUND = zserio.bitfield.getSignedBitFieldUpperBound(NUM_BITS)
