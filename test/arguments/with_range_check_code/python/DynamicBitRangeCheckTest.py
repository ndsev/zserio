import unittest
import zserio

from testutils import getZserioApi

class DynamicBitRangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).dynamic_bit_range_check

    def testDynamicBitLowerBound(self):
        self._checkDynamicBitValue(NUM_BITS, DYNAMIC_BIT_LOWER_BOUND)

    def testDynamicBitUpperBound(self):
        self._checkDynamicBitValue(NUM_BITS, DYNAMIC_BIT_UPPER_BOUND)

    def testDynamicBitBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkDynamicBitValue(NUM_BITS, DYNAMIC_BIT_LOWER_BOUND - 1)

    def testDynamicBitAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkDynamicBitValue(NUM_BITS, DYNAMIC_BIT_UPPER_BOUND + 1)

    def testNumBitsMax(self):
        self._checkDynamicBitValue(64, zserio.limits.UINT64_MAX)

    def testNumBitsAboveMax(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkDynamicBitValue(65, zserio.limits.UINT64_MAX)

    def _checkDynamicBitValue(self, numBits, value):
        dynamicBitRangeCheckCompound = self.api.DynamicBitRangeCheckCompound(numBits_=numBits, value_=value)
        writer = zserio.BitStreamWriter()
        dynamicBitRangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readDynamicBitRangeCheckCompound = self.api.DynamicBitRangeCheckCompound.fromReader(reader)
        self.assertEqual(dynamicBitRangeCheckCompound, readDynamicBitRangeCheckCompound)

NUM_BITS = 10
DYNAMIC_BIT_LOWER_BOUND = zserio.bitfield.getBitFieldLowerBound(NUM_BITS)
DYNAMIC_BIT_UPPER_BOUND = zserio.bitfield.getBitFieldUpperBound(NUM_BITS)
