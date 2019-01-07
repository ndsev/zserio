import unittest

from testutils import getZserioApi

class ValueOfOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").valueof_operator

    def testGetValueOfWhiteColor(self):
        valueOfFunctions = self.api.ValueOfFunctions.fromFields(self.api.Color.WHITE)
        whiteColorValue = 1
        self.assertEqual(whiteColorValue, valueOfFunctions.funcGetValueOfColor())
        self.assertEqual(whiteColorValue, valueOfFunctions.funcGetValueOfWhiteColor())

    def testGetValueOfBlackColor(self):
        valueOfFunctions = self.api.ValueOfFunctions.fromFields(self.api.Color.BLACK)
        blackColorValue = 2
        self.assertEqual(blackColorValue, valueOfFunctions.funcGetValueOfColor())
        self.assertEqual(blackColorValue, valueOfFunctions.funcGetValueOfBlackColor())
