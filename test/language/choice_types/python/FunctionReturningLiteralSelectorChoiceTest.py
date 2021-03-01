import unittest

from testutils import getZserioApi

class FunctionReturningLiteralSelectorChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").function_returning_literal_selector_choice

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
