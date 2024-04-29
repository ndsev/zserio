import Expressions


class BitmaskTypeTest(Expressions.TestCase):
    def testBitSizeOfNoColor(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.colors = self.api.Colors()
        bitmaskTypeExpression.has_not_color_red = True

        self.assertEqual(9, bitmaskTypeExpression.bitsizeof())

    def testBitSizeOfRed(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.colors = self.api.Colors.Values.RED
        bitmaskTypeExpression.has_color_red = True

        self.assertEqual(9, bitmaskTypeExpression.bitsizeof())

    def testBitSizeOfGreen(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.colors = self.api.Colors.Values.GREEN
        bitmaskTypeExpression.has_color_green = True
        bitmaskTypeExpression.has_not_color_red = True
        bitmaskTypeExpression.has_other_color_than_red = True

        self.assertEqual(11, bitmaskTypeExpression.bitsizeof())

    def testBitSizeOfBlue(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.colors = self.api.Colors.Values.BLUE
        bitmaskTypeExpression.has_color_blue = True
        bitmaskTypeExpression.has_not_color_red = True
        bitmaskTypeExpression.has_other_color_than_red = True

        self.assertEqual(11, bitmaskTypeExpression.bitsizeof())

    def testBitSizeOfBlueGreen(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.colors = self.api.Colors.Values.BLUE | self.api.Colors.Values.GREEN
        bitmaskTypeExpression.has_color_blue = True
        bitmaskTypeExpression.has_color_green = True
        bitmaskTypeExpression.has_not_color_red = True
        bitmaskTypeExpression.has_other_color_than_red = True

        self.assertEqual(12, bitmaskTypeExpression.bitsizeof())

    def testBitSizeOfAllColors(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.colors = (
            self.api.Colors.Values.RED | self.api.Colors.Values.BLUE | self.api.Colors.Values.GREEN
        )
        bitmaskTypeExpression.has_color_red = True
        bitmaskTypeExpression.has_color_blue = True
        bitmaskTypeExpression.has_color_green = True
        bitmaskTypeExpression.has_all_colors = True
        bitmaskTypeExpression.has_other_color_than_red = True

        self.assertEqual(13, bitmaskTypeExpression.bitsizeof())
