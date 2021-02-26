import unittest

from testutils import getZserioApi

class FunctionSelectorChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").function_selector_choice

    def testField8(self):
        selector = self.api.Selector(8)
        testChoice = self.api.TestChoice(selector)
        testChoice.field8 = 0x7F
        self.assertEqual(0x7F, testChoice.field8)
        self.assertEqual(8, testChoice.bitSizeOf())

    def testField16(self):
        selector = self.api.Selector(numBits_=16)
        testChoice = self.api.TestChoice(selector, field16_=0x7F7F)
        self.assertEqual(0x7F7F, testChoice.field16)
        self.assertEqual(16, testChoice.bitSizeOf())
