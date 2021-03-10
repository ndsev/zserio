import unittest

from testutils import getZserioApi

class QuestionMarkTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").question_mark

    def testFirstValue(self):
        questionMarkExpression = self.api.QuestionMarkExpression(self.FIRST_VALUE, self.SECOND_VALUE, True)
        self.assertEqual(self.FIRST_VALUE, questionMarkExpression.valid_value())

    def testSecondValue(self):
        questionMarkExpression = self.api.QuestionMarkExpression(self.FIRST_VALUE, self.SECOND_VALUE, False)
        self.assertEqual(self.SECOND_VALUE, questionMarkExpression.valid_value())

    FIRST_VALUE = 0x11
    SECOND_VALUE = 0x22
