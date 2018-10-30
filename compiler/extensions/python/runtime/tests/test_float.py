import unittest

from zserio.float import (convertUInt16ToFloat, convertFloatToUInt16, convertUInt32ToFloat,
                          convertFloatToUInt32, convertUInt64ToFloat, convertFloatToUInt64)

class FloatUtilTest(unittest.TestCase):

    def testConvertUInt16ToFloat(self):
        # plus zero
        float16ValuePlusZero = self._createFloat16Value(0, 0, 0) # +0.0
        self.assertEqual(0.0, convertUInt16ToFloat(float16ValuePlusZero))

        # minus zero
        float16ValueMinusZero = self._createFloat16Value(1, 0, 0) # -0.0
        self.assertEqual(-0.0, convertUInt16ToFloat(float16ValueMinusZero))

        # plus infinity
        float16ValuePlusInfinity = self._createFloat16Value(0, 0x1F, 0) # +INF
        float64ValuePlusInfinity = self._createFloat64Value(0, 0x7FF, 0) # +INF
        convertedFloat = convertUInt16ToFloat(float16ValuePlusInfinity)
        self.assertEqual(float64ValuePlusInfinity, convertFloatToUInt64(convertedFloat))

        # minus infinity
        float16ValueMinusInfinity = self._createFloat16Value(1, 0x1F, 0) # -INF
        float64ValueMinusInfinity = self._createFloat64Value(1, 0x7FF, 0) # -INF
        convertedFloat = convertUInt16ToFloat(float16ValueMinusInfinity)
        self.assertEqual(float64ValueMinusInfinity, convertFloatToUInt64(convertedFloat))

        # quiet NaN
        float16ValueQuietNan = self._createFloat16Value(0, 0x1F, 0x3FF) # +NaN
        float64ValueQuietNan = self._createFloat64Value(0, 0x7FF, 0xFFC0000000000) # +NaN
        convertedFloat = convertUInt16ToFloat(float16ValueQuietNan)
        self.assertEqual(float64ValueQuietNan, convertFloatToUInt64(convertedFloat))

        # signaling NaN
        float16ValueSignalingNan = self._createFloat16Value(1, 0x1F, 0x3FF) # -NaN
        float64ValueSignalingNan = self._createFloat64Value(1, 0x7FF, 0xFFC0000000000) # -NaN
        convertedFloat = convertUInt16ToFloat(float16ValueSignalingNan)
        self.assertEqual(float64ValueSignalingNan, convertFloatToUInt64(convertedFloat))

        # normal numbers
        float16ValueOne = self._createFloat16Value(0, 15, 0) # 1.0
        self.assertEqual(1.0, convertUInt16ToFloat(float16ValueOne))

        float16ValueOnePlus = self._createFloat16Value(0, 15, 0x01) # 1.0 + 2^-10
        float64ValueOnePlus = self._createFloat64Value(0, 0x3FF, 0x40000000000) # 1.0 + 2^-10
        convertedFloat = convertUInt16ToFloat(float16ValueOnePlus)
        self.assertEqual(float64ValueOnePlus, convertFloatToUInt64(convertedFloat))

        float16ValueMax = self._createFloat16Value(0, 30, 0x3FF) # 2^15 (1 + 2^-1 + ... + 2^-10)
        self.assertEqual(65504.0, convertUInt16ToFloat(float16ValueMax))

        # subnormal numbers
        float16ValueMinSubnormal = self._createFloat16Value(0, 0, 1) # 2^-14 (2^-10)
        float64ValueMinSubnormal = self._createFloat64Value(0, 999, 0) # 2^-24
        convertedFloat = convertUInt16ToFloat(float16ValueMinSubnormal)
        self.assertEqual(float64ValueMinSubnormal, convertFloatToUInt64(convertedFloat))

        float16ValueMaxSubnormal = self._createFloat16Value(0, 0, 0x3FF) # 2^-14 (2^-1 + ... + 2^-10)
        float64ValueMaxSubnormal = self._createFloat64Value(0, 1008,
                                                            0xFF80000000000) # 2^-15 (1 + 2^-1 + ... + 2^-9)
        convertedFloat = convertUInt16ToFloat(float16ValueMaxSubnormal)
        self.assertEqual(float64ValueMaxSubnormal, convertFloatToUInt64(convertedFloat))

    def testConvertFloatToUInt16(self):
        # plus zero
        float16ValuePlusZero = self._createFloat16Value(0, 0, 0) # +0.0
        self.assertEqual(float16ValuePlusZero, convertFloatToUInt16(0.0))

        # minus zero
        float16ValueMinusZero = self._createFloat16Value(1, 0, 0) # -0.0
        self.assertEqual(float16ValueMinusZero, convertFloatToUInt16(-0.0))

        # plus infinity
        float64ValuePlusInfinity = self._createFloat64Value(0, 0x7FF, 0) # +INF
        float16ValuePlusInfinity = self._createFloat16Value(0, 0x1F, 0) # +INF
        convertedFloat = convertUInt64ToFloat(float64ValuePlusInfinity)
        self.assertEqual(float16ValuePlusInfinity, convertFloatToUInt16(convertedFloat))

        # minus infinity
        float64ValueMinusInfinity = self._createFloat64Value(1, 0x7FF, 0) # -INF
        float16ValueMinusInfinity = self._createFloat16Value(1, 0x1F, 0) # -INF
        convertedFloat = convertUInt64ToFloat(float64ValueMinusInfinity)
        self.assertEqual(float16ValueMinusInfinity, convertFloatToUInt16(convertedFloat))

        # quiet NaN
        float64ValueQuietNan = self._createFloat64Value(0, 0x7FF, 0xFFC0000000000) # +NaN
        float16ValueQuietNan = self._createFloat16Value(0, 0x1F, 0x3FF) # +NaN
        convertedFloat = convertUInt64ToFloat(float64ValueQuietNan)
        self.assertEqual(float16ValueQuietNan, convertFloatToUInt16(convertedFloat))

        # signaling NaN
        float64ValueSignalingNan = self._createFloat64Value(1, 0x7FF, 0xFFC0000000000) # -NaN
        float16ValueSignalingNan = self._createFloat16Value(1, 0x1F, 0x3FF) # -NaN
        convertedFloat = convertUInt64ToFloat(float64ValueSignalingNan)
        self.assertEqual(float16ValueSignalingNan, convertFloatToUInt16(convertedFloat))

        # normal numbers
        float16ValueOne = self._createFloat16Value(0, 15, 0) # 1.0
        self.assertEqual(float16ValueOne, convertFloatToUInt16(1.0))

        float64ValueOnePlus = self._createFloat64Value(0, 0x3FF, 0x40000000000) # 1.0 + 2^-10
        float16ValueOnePlus = self._createFloat16Value(0, 15, 0x01) # 1.0 + 2^-10
        convertedFloat = convertUInt64ToFloat(float64ValueOnePlus)
        self.assertEqual(float16ValueOnePlus, convertFloatToUInt16(convertedFloat))

        float16ValueMax = self._createFloat16Value(0, 30, 0x3FF) # 2^15 (1 + 2^-1 + ... + 2^-10)
        self.assertEqual(float16ValueMax, convertFloatToUInt16(65504.0))

        # normal numbers converted to zero
        float64ValueUnderflow = self._createFloat64Value(0, 998, 0) # 2^-25
        convertedFloat = convertUInt64ToFloat(float64ValueUnderflow)
        self.assertEqual(float16ValuePlusZero, convertFloatToUInt16(convertedFloat))

        # normal numbers converted to subnormal numbers
        float64ValueMinUnderflow = self._createFloat64Value(0, 999, 1) # 2^-24 (1 + 2^-52)
        float16ValueMinSubnormal = self._createFloat16Value(0, 0, 1) # 2^-24
        convertedFloat = convertUInt64ToFloat(float64ValueMinUnderflow)
        self.assertEqual(float16ValueMinSubnormal, convertFloatToUInt16(convertedFloat))

        # normal numbers converted to subnormal numbers with rounding
        float64ValueMinUnderflowRounding = self._createFloat64Value(0, 1000, 0x4000000000000) # 2^-23 (1 + 2^-2)
        float16ValueMinSubnormalRounding = self._createFloat16Value(0, 0, 0x3) # 2^-14 (2^-9 + 2^-10)
        convertedFloat = convertUInt64ToFloat(float64ValueMinUnderflowRounding)
        self.assertEqual(float16ValueMinSubnormalRounding, convertFloatToUInt16(convertedFloat))

        # normal numbers converted to infinity
        float64ValueOverflow = self._createFloat64Value(0, 1040, 0) # 2^17
        convertedFloat = convertUInt64ToFloat(float64ValueOverflow)
        self.assertEqual(float16ValuePlusInfinity, convertFloatToUInt16(convertedFloat))

        # normal numbers converted with rounding
        float64ValueRounding = self._createFloat64Value(0, 1023, 0x8040000000000) # 1 + 2^-1 + 2^-11
        float16ValueRounding = self._createFloat16Value(0, 15, 0x201) # 1 + 2^-1 + 2^-10
        convertedFloat = convertUInt64ToFloat(float64ValueRounding)
        self.assertEqual(float16ValueRounding, convertFloatToUInt16(convertedFloat))

        # subnormal numbers
        float64ValueMin32Subnormal = self._createFloat64Value(0, 874, 0) # 2^-126 (2^-23)
        convertedFloat = convertUInt64ToFloat(float64ValueMin32Subnormal)
        self.assertEqual(float16ValuePlusZero, convertFloatToUInt16(convertedFloat))

        float64ValueMax32Subnormal = self._createFloat64Value(0, 896,
                                                              0xFFFFFC0000000) # 2^-126 (2^-1 + ... + 2^-23)
        convertedFloat = convertUInt64ToFloat(float64ValueMax32Subnormal)
        self.assertEqual(float16ValuePlusZero, convertFloatToUInt16(convertedFloat))

    def testConvertUInt32ToFloat(self):
        for dataRow in self.TEST_FLOAT32_DATA:
            float32Value = self._createFloat32Value(dataRow[0], dataRow[1], dataRow[2])
            convertedFloat = convertUInt32ToFloat(float32Value)
            self.assertEqual(dataRow[3], convertedFloat)

    def testConvertFloatToUInt32(self):
        for dataRow in self.TEST_FLOAT32_DATA:
            convertedFloat32Value = convertFloatToUInt32(dataRow[3])
            float32Value = self._createFloat32Value(dataRow[0], dataRow[1], dataRow[2])
            self.assertEqual(float32Value, convertedFloat32Value)

    def testConvertUInt64ToFloat(self):
        for dataRow in self.TEST_FLOAT64_DATA:
            float64Value = self._createFloat64Value(dataRow[0], dataRow[1], dataRow[2])
            convertedFloat = convertUInt64ToFloat(float64Value)
            self.assertEqual(dataRow[3], convertedFloat)

    def testConvertFloatToUInt64(self):
        for dataRow in self.TEST_FLOAT64_DATA:
            convertedFloat64Value = convertFloatToUInt64(dataRow[3])
            float64Value = self._createFloat64Value(dataRow[0], dataRow[1], dataRow[2])
            self.assertEqual(float64Value, convertedFloat64Value)

    def _createFloat16Value(self, sign, exponent, significand):
        return ((sign << self.FLOAT16_SIGN_BIT_POSITION) | (exponent << self.FLOAT16_EXPONENT_BIT_POSITION) |
                significand)

    def _createFloat32Value(self, sign, exponent, significand):
        return ((sign << self.FLOAT32_SIGN_BIT_POSITION) | (exponent << self.FLOAT32_EXPONENT_BIT_POSITION) |
                significand)

    def _createFloat64Value(self, sign, exponent, significand):
        return ((sign << self.FLOAT64_SIGN_BIT_POSITION) | (exponent << self.FLOAT64_EXPONENT_BIT_POSITION) |
                significand)

    FLOAT16_SIGN_BIT_POSITION = 15
    FLOAT16_EXPONENT_BIT_POSITION = 10

    FLOAT32_SIGN_BIT_POSITION = 31
    FLOAT32_EXPONENT_BIT_POSITION = 23

    FLOAT64_SIGN_BIT_POSITION = 63
    FLOAT64_EXPONENT_BIT_POSITION = 52

    TEST_FLOAT32_DATA = [
        [ 0, 0, 0, 0.0 ],
        [ 1, 0, 0, -0.0 ],
        [ 0, 127, 0, +1.0 ],
        [ 1, 127, 0, -1.0 ],
        [ 0, 128, 0x600000, 3.5 ],      # 2^1 (1 + 2^-1 + 2^-2)
        [ 0, 126, 0x600000, 0.875 ],    # 2^-1 (1 + 2^-1 + 2^-2)
        [ 0, 130, 0x1E0000, 9.875 ],    # 2^3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
        [ 0, 126, 0x1E0000, 0.6171875 ] # 2^-3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
    ]

    TEST_FLOAT64_DATA = [
        [ 0, 0, 0, 0.0 ],
        [ 1, 0, 0, -0.0 ],
        [ 0, 1023, 0, +1.0 ],
        [ 1, 1023, 0, -1.0 ],
        [ 0, 1024, 0xC000000000000, 3.5 ],      # 2^1 (1 + 2^-1 + 2^-2)
        [ 0, 1022, 0xC000000000000, 0.875 ],    # 2^-1 (1 + 2^-1 + 2^-2)
        [ 0, 1026, 0x3C00000000000, 9.875 ],    # 2^3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
        [ 0, 1022, 0x3C00000000000, 0.6171875 ] # 2^-3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
    ]
