import Expressions

class StringTypeTest(Expressions.TestCase):
    def testReturnValue(self):
        stringTypeExpression = self._createStringTypeExpression(True)
        self.assertEqual(self.VALUE, stringTypeExpression.return_value())

    def testReturnDefaultValue(self):
        stringTypeExpression = self._createStringTypeExpression(True)
        self.assertEqual(self.api.STRING_CONSTANT if self.api.CHOOSER
                         else self.FALSE + self.SPACE + self.api.STRING_CONSTANT,
                         stringTypeExpression.return_default_value())

    def testReturnDefaultChosen(self):
        stringTypeExpression = self._createStringTypeExpression(True)
        self.assertEqual(self.CHOSEN + self.SPACE + self.api.STRING_CONSTANT if self.api.CHOOSER
                         else "", stringTypeExpression.return_default_chosen())

    def testAppendix(self):
        stringTypeExpression = self._createStringTypeExpression(False)
        self.assertEqual(self.APPEND + self.IX_LITERAL, stringTypeExpression.appendix())

    def testAppendToConst(self):
        stringTypeExpression = self._createStringTypeExpression(False)
        self.assertEqual(self.api.STRING_CONSTANT + self.UNDERSCORE + self.APPEND + self.IX_LITERAL,
                         stringTypeExpression.append_to_const())

    def testValueOrLiteral(self):
        stringTypeExpression1 = self._createStringTypeExpression(True)
        self.assertEqual(self.VALUE, stringTypeExpression1.value_or_literal())
        stringTypeExpression2 = self._createStringTypeExpression(False)
        self.assertEqual(self.LITERAL, stringTypeExpression2.value_or_literal())

    def testValueOrLiteralExpression(self):
        stringTypeExpression1 = self._createStringTypeExpression(True)
        self.assertEqual(self.VALUE, stringTypeExpression1.value_or_literal_expression())
        stringTypeExpression2 = self._createStringTypeExpression(False)
        self.assertEqual(self.LITERAL + self.SPACE + self.EXPRESSION,
                         stringTypeExpression2.value_or_literal_expression())

    def testValueOrConst(self):
        stringTypeExpression1 = self._createStringTypeExpression(True)
        self.assertEqual(self.VALUE, stringTypeExpression1.value_or_const())
        stringTypeExpression2 = self._createStringTypeExpression(False)
        self.assertEqual(self.api.STRING_CONSTANT, stringTypeExpression2.value_or_const())

    def testValueOrConstExpression(self):
        stringTypeExpression1 = self._createStringTypeExpression(True)
        self.assertEqual(self.VALUE, stringTypeExpression1.value_or_const_expression())
        stringTypeExpression2 = self._createStringTypeExpression(False)
        self.assertEqual(self.api.STRING_CONSTANT + self.SPACE + self.EXPRESSION,
                         stringTypeExpression2.value_or_const_expression())

    def _createStringTypeExpression(self, hasValue):
        return self.api.StringTypeExpression(hasValue, self.VALUE if hasValue else None)

    VALUE = "value"
    APPEND = "append"
    IX_LITERAL = "ix"
    LITERAL = "literal"
    EXPRESSION = "expression"
    FALSE = "false"
    CHOSEN = "chosen"
    SPACE = " "
    UNDERSCORE = "_"
