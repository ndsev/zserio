import Expressions


class IsSetOperatorTest(Expressions.TestCase):
    def testHasNone(self):
        testBitmask = self.api.TestBitmask()
        parameterized = self.api.Parameterized(testBitmask)
        isSetOperator = self.api.IsSetOperator(testBitmask, parameterized)

        self.assertFalse(isSetOperator.has_int())
        self.assertFalse(isSetOperator.has_string())
        self.assertFalse(isSetOperator.has_both())
        self.assertFalse(isSetOperator.parameterized.has_int())
        self.assertFalse(isSetOperator.parameterized.has_string())
        self.assertFalse(isSetOperator.parameterized.has_both())

        self.assertFalse(isSetOperator.parameterized.is_int_field_used())
        self.assertFalse(isSetOperator.parameterized.is_int_field_set())
        self.assertFalse(isSetOperator.parameterized.is_string_field_used())
        self.assertFalse(isSetOperator.parameterized.is_string_field_set())

    def testHasInt(self):
        testBitmask = self.api.TestBitmask.Values.INT
        parameterized = self.api.Parameterized(testBitmask, int_field_=13)
        isSetOperator = self.api.IsSetOperator(testBitmask, parameterized)

        self.assertTrue(isSetOperator.has_int())
        self.assertFalse(isSetOperator.has_string())
        self.assertFalse(isSetOperator.has_both())
        self.assertTrue(isSetOperator.parameterized.has_int())
        self.assertFalse(isSetOperator.parameterized.has_string())
        self.assertFalse(isSetOperator.parameterized.has_both())

        self.assertTrue(isSetOperator.parameterized.is_int_field_used())
        self.assertTrue(isSetOperator.parameterized.is_int_field_set())
        self.assertFalse(isSetOperator.parameterized.is_string_field_used())
        self.assertFalse(isSetOperator.parameterized.is_string_field_set())

    def testHasString(self):
        testBitmask = self.api.TestBitmask.Values.STRING
        parameterized = self.api.Parameterized(testBitmask, string_field_="test")
        isSetOperator = self.api.IsSetOperator(testBitmask, parameterized)

        self.assertFalse(isSetOperator.has_int())
        self.assertTrue(isSetOperator.has_string())
        self.assertFalse(isSetOperator.has_both())
        self.assertFalse(isSetOperator.parameterized.has_int())
        self.assertTrue(isSetOperator.parameterized.has_string())
        self.assertFalse(isSetOperator.parameterized.has_both())

        self.assertFalse(isSetOperator.parameterized.is_int_field_used())
        self.assertFalse(isSetOperator.parameterized.is_int_field_set())
        self.assertTrue(isSetOperator.parameterized.is_string_field_used())
        self.assertTrue(isSetOperator.parameterized.is_string_field_set())

    def testHasBoth(self):
        testBitmask = self.api.TestBitmask.Values.BOTH
        parameterized = self.api.Parameterized(testBitmask, int_field_=13, string_field_="test")
        isSetOperator = self.api.IsSetOperator(testBitmask, parameterized)

        self.assertTrue(isSetOperator.has_int())
        self.assertTrue(isSetOperator.has_string())
        self.assertTrue(isSetOperator.has_both())
        self.assertTrue(isSetOperator.parameterized.has_int())
        self.assertTrue(isSetOperator.parameterized.has_string())
        self.assertTrue(isSetOperator.parameterized.has_both())

        self.assertTrue(isSetOperator.parameterized.is_int_field_used())
        self.assertTrue(isSetOperator.parameterized.is_int_field_set())
        self.assertTrue(isSetOperator.parameterized.is_string_field_used())
        self.assertTrue(isSetOperator.parameterized.is_string_field_set())
