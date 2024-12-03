import Expressions


class LeftShiftOperatorTest(Expressions.TestCase):
    def testDefaultValues(self):
        leftShiftOperator = self.api.LeftShiftOperator()
        self.assertEqual(40, leftShiftOperator.u32)
        self.assertEqual(-40, leftShiftOperator.i32)
        self.assertEqual(32, leftShiftOperator.u32_complex)
        self.assertEqual(-32, leftShiftOperator.i32_complex)
        self.assertEqual(24, leftShiftOperator.u32_plus)
        self.assertEqual(-64, leftShiftOperator.i32_minus)
        self.assertEqual(12, leftShiftOperator.u32_plus_rhs_expr)
        self.assertEqual(-24, leftShiftOperator.i32_minus_rhs_expr)
        self.assertEqual(11534336, leftShiftOperator.u63_complex)
        self.assertEqual(-9216, leftShiftOperator.i64_complex)

    def testGetU63LShift3(self):
        leftShiftOperator = self.api.LeftShiftOperator()
        self.assertEqual(104, leftShiftOperator.get_u63l_shift3())

    def testGetI64LShift4(self):
        leftShiftOperator = self.api.LeftShiftOperator()
        self.assertEqual(-208, leftShiftOperator.get_i64l_shift4())

    def testGetU63LShift(self):
        leftShiftOperator = self.api.LeftShiftOperator()
        self.assertEqual(13312, leftShiftOperator.get_u63l_shift())

    def testGetI64LShift(self):
        leftShiftOperator = self.api.LeftShiftOperator()
        self.assertEqual(-13312, leftShiftOperator.get_i64l_shift())

    def testGetPositiveI32LShift(self):
        leftShiftOperator = self.api.LeftShiftOperator()
        self.assertEqual(13312, leftShiftOperator.get_positive_i32l_shift())

    def testGetI64ComplexLShift(self):
        leftShiftOperator = self.api.LeftShiftOperator()
        self.assertEqual(-3072, leftShiftOperator.get_i64_complex_l_shift())
