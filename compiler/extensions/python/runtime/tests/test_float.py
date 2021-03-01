import unittest

from zserio.float import (uint16_to_float, float_to_uint16, uint32_to_float,
                          float_to_uint32, uint64_to_float, float_to_uint64)

class FloatUtilTest(unittest.TestCase):

    def test_uint16_to_float(self):
        # plus zero
        float16_value_plus_zero = self._create_float16_value(0, 0, 0) # +0.0
        self.assertEqual(0.0, uint16_to_float(float16_value_plus_zero))

        # minus zero
        float16_value_minus_zero = self._create_float16_value(1, 0, 0) # -0.0
        self.assertEqual(-0.0, uint16_to_float(float16_value_minus_zero))

        # plus infinity
        float16_value_plus_infinity = self._create_float16_value(0, 0x1F, 0) # +INF
        float64_value_plus_infinity = self._create_float64_value(0, 0x7FF, 0) # +INF
        converted_float = uint16_to_float(float16_value_plus_infinity)
        self.assertEqual(float64_value_plus_infinity, float_to_uint64(converted_float))

        # minus infinity
        float16_value_minus_infinity = self._create_float16_value(1, 0x1F, 0) # -INF
        float64_value_minus_infinity = self._create_float64_value(1, 0x7FF, 0) # -INF
        converted_float = uint16_to_float(float16_value_minus_infinity)
        self.assertEqual(float64_value_minus_infinity, float_to_uint64(converted_float))

        # quiet NaN
        float16_value_quiet_nan = self._create_float16_value(0, 0x1F, 0x3FF) # +NaN
        float64_value_quiet_nan = self._create_float64_value(0, 0x7FF, 0xFFC0000000000) # +NaN
        converted_float = uint16_to_float(float16_value_quiet_nan)
        self.assertEqual(float64_value_quiet_nan, float_to_uint64(converted_float))

        # signaling NaN
        float16_value_signaling_nan = self._create_float16_value(1, 0x1F, 0x3FF) # -NaN
        float64_value_signaling_nan = self._create_float64_value(1, 0x7FF, 0xFFC0000000000) # -NaN
        converted_float = uint16_to_float(float16_value_signaling_nan)
        self.assertEqual(float64_value_signaling_nan, float_to_uint64(converted_float))

        # normal numbers
        float16_value_one = self._create_float16_value(0, 15, 0) # 1.0
        self.assertEqual(1.0, uint16_to_float(float16_value_one))

        float16_value_one_plus = self._create_float16_value(0, 15, 0x01) # 1.0 + 2^-10
        float64_value_one_plus = self._create_float64_value(0, 0x3FF, 0x40000000000) # 1.0 + 2^-10
        converted_float = uint16_to_float(float16_value_one_plus)
        self.assertEqual(float64_value_one_plus, float_to_uint64(converted_float))

        float16_value_max = self._create_float16_value(0, 30, 0x3FF) # 2^15 (1 + 2^-1 + ... + 2^-10)
        self.assertEqual(65504.0, uint16_to_float(float16_value_max))

        # subnormal numbers
        float16_value_min_subnormal = self._create_float16_value(0, 0, 1) # 2^-14 (2^-10)
        float64_value_min_subnormal = self._create_float64_value(0, 999, 0) # 2^-24
        converted_float = uint16_to_float(float16_value_min_subnormal)
        self.assertEqual(float64_value_min_subnormal, float_to_uint64(converted_float))

        float16_value_max_subnormal = self._create_float16_value(0, 0, 0x3FF) # 2^-14 (2^-1 + ... + 2^-10)
        float64_value_max_subnormal = self._create_float64_value(0, 1008,
                                                                 0xFF80000000000)# 2^-15 (1 + 2^-1 + ... + 2^-9)
        converted_float = uint16_to_float(float16_value_max_subnormal)
        self.assertEqual(float64_value_max_subnormal, float_to_uint64(converted_float))

    def test_float_to_uint16(self):
        # plus zero
        float16_value_plus_zero = self._create_float16_value(0, 0, 0) # +0.0
        self.assertEqual(float16_value_plus_zero, float_to_uint16(0.0))

        # minus zero
        float16_value_minus_zero = self._create_float16_value(1, 0, 0) # -0.0
        self.assertEqual(float16_value_minus_zero, float_to_uint16(-0.0))

        # plus infinity
        float64_value_plus_infinity = self._create_float64_value(0, 0x7FF, 0) # +INF
        float16_value_plus_infinity = self._create_float16_value(0, 0x1F, 0) # +INF
        converted_float = uint64_to_float(float64_value_plus_infinity)
        self.assertEqual(float16_value_plus_infinity, float_to_uint16(converted_float))

        # minus infinity
        float64_value_minus_infinity = self._create_float64_value(1, 0x7FF, 0) # -INF
        float16_value_minus_infinity = self._create_float16_value(1, 0x1F, 0) # -INF
        converted_float = uint64_to_float(float64_value_minus_infinity)
        self.assertEqual(float16_value_minus_infinity, float_to_uint16(converted_float))

        # quiet NaN
        float64_value_quiet_nan = self._create_float64_value(0, 0x7FF, 0xFFC0000000000) # +NaN
        float16_value_quiet_nan = self._create_float16_value(0, 0x1F, 0x3FF) # +NaN
        converted_float = uint64_to_float(float64_value_quiet_nan)
        self.assertEqual(float16_value_quiet_nan, float_to_uint16(converted_float))

        # signaling NaN
        float64_value_signaling_nan = self._create_float64_value(1, 0x7FF, 0xFFC0000000000) # -NaN
        float16_value_signaling_nan = self._create_float16_value(1, 0x1F, 0x3FF) # -NaN
        converted_float = uint64_to_float(float64_value_signaling_nan)
        self.assertEqual(float16_value_signaling_nan, float_to_uint16(converted_float))

        # normal numbers
        float16_value_one = self._create_float16_value(0, 15, 0) # 1.0
        self.assertEqual(float16_value_one, float_to_uint16(1.0))

        float64_value_one_plus = self._create_float64_value(0, 0x3FF, 0x40000000000) # 1.0 + 2^-10
        float16_value_one_plus = self._create_float16_value(0, 15, 0x01) # 1.0 + 2^-10
        converted_float = uint64_to_float(float64_value_one_plus)
        self.assertEqual(float16_value_one_plus, float_to_uint16(converted_float))

        float16_value_max = self._create_float16_value(0, 30, 0x3FF) # 2^15 (1 + 2^-1 + ... + 2^-10)
        self.assertEqual(float16_value_max, float_to_uint16(65504.0))

        # normal numbers converted to zero
        float64_value_underflow = self._create_float64_value(0, 998, 0) # 2^-25
        converted_float = uint64_to_float(float64_value_underflow)
        self.assertEqual(float16_value_plus_zero, float_to_uint16(converted_float))

        # normal numbers converted to subnormal numbers
        float64_value_min_underflow = self._create_float64_value(0, 999, 1) # 2^-24 (1 + 2^-52)
        float16_value_min_subnormal = self._create_float16_value(0, 0, 1) # 2^-24
        converted_float = uint64_to_float(float64_value_min_underflow)
        self.assertEqual(float16_value_min_subnormal, float_to_uint16(converted_float))

        # normal numbers converted to subnormal numbers with rounding
        float64_value_min_underflow_rounding = self._create_float64_value(0, 1000,
                                                                          0x4000000000000) # 2^-23 (1 + 2^-2)
        float16_value_min_subnormal_rounding = self._create_float16_value(0, 0, 0x3) # 2^-14 (2^-9 + 2^-10)
        converted_float = uint64_to_float(float64_value_min_underflow_rounding)
        self.assertEqual(float16_value_min_subnormal_rounding, float_to_uint16(converted_float))

        # normal numbers converted to infinity
        float64_value_overflow = self._create_float64_value(0, 1040, 0) # 2^17
        converted_float = uint64_to_float(float64_value_overflow)
        self.assertEqual(float16_value_plus_infinity, float_to_uint16(converted_float))

        # normal numbers converted with rounding
        float64_value_rounding = self._create_float64_value(0, 1023, 0x8040000000000) # 1 + 2^-1 + 2^-11
        float16_value_rounding = self._create_float16_value(0, 15, 0x201) # 1 + 2^-1 + 2^-10
        converted_float = uint64_to_float(float64_value_rounding)
        self.assertEqual(float16_value_rounding, float_to_uint16(converted_float))

        # subnormal numbers
        float64_value_min32_subnormal = self._create_float64_value(0, 874, 0) # 2^-126 (2^-23)
        converted_float = uint64_to_float(float64_value_min32_subnormal)
        self.assertEqual(float16_value_plus_zero, float_to_uint16(converted_float))

        float64_value_max32_subnormal = self._create_float64_value(0, 896,
                                                                   0xFFFFFC0000000)# 2^-126 (2^-1 + ... + 2^-23)
        converted_float = uint64_to_float(float64_value_max32_subnormal)
        self.assertEqual(float16_value_plus_zero, float_to_uint16(converted_float))

    def test_uint32_to_float(self):
        for data_row in self.TEST_FLOAT32_DATA:
            float32_value = self._create_float32_value(data_row[0], data_row[1], data_row[2])
            converted_float = uint32_to_float(float32_value)
            self.assertEqual(data_row[3], converted_float)

    def test_float_to_uint32(self):
        for data_row in self.TEST_FLOAT32_DATA:
            converted_float32_value = float_to_uint32(data_row[3])
            float32_value = self._create_float32_value(data_row[0], data_row[1], data_row[2])
            self.assertEqual(float32_value, converted_float32_value)

    def test_uint64_to_float(self):
        for data_row in self.TEST_FLOAT64_DATA:
            float64_value = self._create_float64_value(data_row[0], data_row[1], data_row[2])
            converted_float = uint64_to_float(float64_value)
            self.assertEqual(data_row[3], converted_float)

    def test_float_to_uint64(self):
        for data_row in self.TEST_FLOAT64_DATA:
            converted_float64_value = float_to_uint64(data_row[3])
            float64_value = self._create_float64_value(data_row[0], data_row[1], data_row[2])
            self.assertEqual(float64_value, converted_float64_value)

    def _create_float16_value(self, sign, exponent, significand):
        return ((sign << self.FLOAT16_SIGN_BIT_POSITION) | (exponent << self.FLOAT16_EXPONENT_BIT_POSITION) |
                significand)

    def _create_float32_value(self, sign, exponent, significand):
        return ((sign << self.FLOAT32_SIGN_BIT_POSITION) | (exponent << self.FLOAT32_EXPONENT_BIT_POSITION) |
                significand)

    def _create_float64_value(self, sign, exponent, significand):
        return ((sign << self.FLOAT64_SIGN_BIT_POSITION) | (exponent << self.FLOAT64_EXPONENT_BIT_POSITION) |
                significand)

    FLOAT16_SIGN_BIT_POSITION = 15
    FLOAT16_EXPONENT_BIT_POSITION = 10

    FLOAT32_SIGN_BIT_POSITION = 31
    FLOAT32_EXPONENT_BIT_POSITION = 23

    FLOAT64_SIGN_BIT_POSITION = 63
    FLOAT64_EXPONENT_BIT_POSITION = 52

    TEST_FLOAT32_DATA = [
        [0, 0, 0, 0.0],
        [1, 0, 0, -0.0],
        [0, 127, 0, +1.0],
        [1, 127, 0, -1.0],
        [0, 128, 0x600000, 3.5],      # 2^1 (1 + 2^-1 + 2^-2)
        [0, 126, 0x600000, 0.875],    # 2^-1 (1 + 2^-1 + 2^-2)
        [0, 130, 0x1E0000, 9.875],    # 2^3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
        [0, 126, 0x1E0000, 0.6171875] # 2^-3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
    ]

    TEST_FLOAT64_DATA = [
        [0, 0, 0, 0.0],
        [1, 0, 0, -0.0],
        [0, 1023, 0, +1.0],
        [1, 1023, 0, -1.0],
        [0, 1024, 0xC000000000000, 3.5],      # 2^1 (1 + 2^-1 + 2^-2)
        [0, 1022, 0xC000000000000, 0.875],    # 2^-1 (1 + 2^-1 + 2^-2)
        [0, 1026, 0x3C00000000000, 9.875],    # 2^3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
        [0, 1022, 0x3C00000000000, 0.6171875] # 2^-3 (1 + 2^-3 + 2^-4 + 2^-5 + 2^-6)
    ]
