import Subtypes

class EnumSubtypeTest(Subtypes.TestCase):
    def testSubtype(self):
        self.assertEqual(self.api.Color.BLACK, self.api.CONST_BLACK)
