import unittest

from testutils import getZserioApi

class ExpressionSelectorChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").expression_selector_choice

    def testField8(self):
        expressionSelectorChoice = self.api.ExpressionSelectorChoice(0)
        value8 = 0x7F
        expressionSelectorChoice.setField8(value8)
        self.assertEqual(value8, expressionSelectorChoice.getField8())
        self.assertEqual(8, expressionSelectorChoice.bitSizeOf())

    def testField16(self):
        value16 = 0x7F7F
        expressionSelectorChoice = self.api.ExpressionSelectorChoice(1, field16_=value16)
        self.assertEqual(value16, expressionSelectorChoice.getField16())
        self.assertEqual(16, expressionSelectorChoice.bitSizeOf())

    def testField32(self):
        expressionSelectorChoice = self.api.ExpressionSelectorChoice(2)
        value32 = 0x7F7F7F7F
        expressionSelectorChoice.setField32(value32)
        self.assertEqual(value32, expressionSelectorChoice.getField32())
        self.assertEqual(32, expressionSelectorChoice.bitSizeOf())
