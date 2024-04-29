import EnumerationTypes


class EnumDefinedByConstantTest(EnumerationTypes.TestCase):
    def testLightColor(self):
        self.assertEqual(1, self.api.WHITE_COLOR)
        self.assertEqual(1, self.api.Colors.WHITE.value)
        self.assertEqual(self.api.Colors.WHITE.value + 1, self.api.Colors.BLACK.value)
