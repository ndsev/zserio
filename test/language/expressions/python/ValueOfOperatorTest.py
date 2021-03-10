import unittest

from testutils import getZserioApi

class ValueOfOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").valueof_operator

    def testGetValueOfWhiteColor(self):
        valueOfFunctions = self.api.ValueOfFunctions(self.api.Color.WHITE)
        whiteColorValue = 1
        self.assertEqual(whiteColorValue, valueOfFunctions.get_value_of_color())
        self.assertEqual(whiteColorValue, valueOfFunctions.get_value_of_white_color())

    def testGetValueOfBlackColor(self):
        valueOfFunctions = self.api.ValueOfFunctions(self.api.Color.BLACK)
        blackColorValue = 2
        self.assertEqual(blackColorValue, valueOfFunctions.get_value_of_color())
        self.assertEqual(blackColorValue, valueOfFunctions.get_value_of_black_color())
