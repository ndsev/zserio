import unittest
import zserio

from testutils import getZserioApi

class VariableBitRangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).variable_bit_range_check

    def testVariableBitLowerBound(self):
        self._checkVariableBitValue(VARIABLE_BIT_LOWER_BOUND)

    def testVariableBitUpperBound(self):
        self._checkVariableBitValue(VARIABLE_BIT_UPPER_BOUND)

    def testVariableBitBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVariableBitValue(VARIABLE_BIT_LOWER_BOUND - 1)

    def testVariableBitAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVariableBitValue(VARIABLE_BIT_UPPER_BOUND + 1)

    def _checkVariableBitValue(self, value):
        variableBitRangeCheckCompound = self.api.VariableBitRangeCheckCompound.fromFields(NUM_BITS, value)
        writer = zserio.BitStreamWriter()
        variableBitRangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVariableBitRangeCheckCompound = self.api.VariableBitRangeCheckCompound.fromReader(reader)
        self.assertEqual(variableBitRangeCheckCompound, readVariableBitRangeCheckCompound)

NUM_BITS = 10
VARIABLE_BIT_LOWER_BOUND = zserio.bitfield.getBitFieldLowerBound(NUM_BITS)
VARIABLE_BIT_UPPER_BOUND = zserio.bitfield.getBitFieldUpperBound(NUM_BITS)
