import zserio

import WithRangeCheckCode

class DynamicBitRangeCheckTest(WithRangeCheckCode.TestCase):
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
        dynamicBitRangeCheckCompound = self.api.DynamicBitRangeCheckCompound(num_bits_=numBits, value_=value)
        bitBuffer = zserio.serialize(dynamicBitRangeCheckCompound)
        readDynamicBitRangeCheckCompound = zserio.deserialize(self.api.DynamicBitRangeCheckCompound, bitBuffer)
        self.assertEqual(dynamicBitRangeCheckCompound, readDynamicBitRangeCheckCompound)

NUM_BITS = 10
DYNAMIC_BIT_LOWER_BOUND = zserio.bitfield.bitfield_lowerbound(NUM_BITS)
DYNAMIC_BIT_UPPER_BOUND = zserio.bitfield.bitfield_upperbound(NUM_BITS)
