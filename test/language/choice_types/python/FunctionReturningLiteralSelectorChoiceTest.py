import ChoiceTypes

class FunctionReturningLiteralSelectorChoiceTest(ChoiceTypes.TestCase):
    def testField8(self):
        selector = self.api.Selector(False)
        testChoice = self.api.TestChoice(selector)
        testChoice.field8 = 0x7F
        self.assertEqual(0x7F, testChoice.field8)
        self.assertEqual(8, testChoice.bitsizeof())

    def testField16(self):
        selector = self.api.Selector(True)
        testChoice = self.api.TestChoice(selector, field16_=0x7F7F)
        self.assertEqual(0x7F7F, testChoice.field16)
        self.assertEqual(16, testChoice.bitsizeof())
