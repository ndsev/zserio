import ChoiceTypes


class ExpressionSelectorChoiceTest(ChoiceTypes.TestCase):
    def testField8(self):
        expressionSelectorChoice = self.api.ExpressionSelectorChoice(0)
        value8 = 0x7F
        expressionSelectorChoice.field8 = value8
        self.assertEqual(value8, expressionSelectorChoice.field8)
        self.assertEqual(8, expressionSelectorChoice.bitsizeof())

    def testField16(self):
        value16 = 0x7F7F
        expressionSelectorChoice = self.api.ExpressionSelectorChoice(1, field16_=value16)
        self.assertEqual(value16, expressionSelectorChoice.field16)
        self.assertEqual(16, expressionSelectorChoice.bitsizeof())

    def testField32(self):
        expressionSelectorChoice = self.api.ExpressionSelectorChoice(2)
        value32 = 0x7F7F7F7F
        expressionSelectorChoice.field32 = value32
        self.assertEqual(value32, expressionSelectorChoice.field32)
        self.assertEqual(32, expressionSelectorChoice.bitsizeof())
