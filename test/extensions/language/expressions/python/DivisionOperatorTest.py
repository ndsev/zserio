import Expressions


class DivisionOperatorTest(Expressions.TestCase):
    def testDivideFloatByInt(self):
        fun = self.api.DivisionFunction(10, 2)
        self.assertAlmostEqual(fun.divide_float_by_int(), 3.33333, places=5)

    def testDivideIntByFloat(self):
        fun = self.api.DivisionFunction(10, 2)
        self.assertAlmostEqual(fun.divide_int_by_float(), 3.33333, places=5)

    def testDivideFloatByFloat(self):
        fun = self.api.DivisionFunction(10, 2)
        self.assertAlmostEqual(fun.divide_float_by_float(), 3.33333, places=5)

    def testDivideIntByInt(self):
        fun = self.api.DivisionFunction(10, 2)
        self.assertAlmostEqual(fun.divide_int_by_int(), 3, places=5)
