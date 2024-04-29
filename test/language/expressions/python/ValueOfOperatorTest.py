import Expressions


class ValueOfOperatorTest(Expressions.TestCase):
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
