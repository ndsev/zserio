import unittest

from testutils import getZserioApi

class BitmaskTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").bitmask_type

    def testBitSizeOfNoColor(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.setColors(self.api.Colors())
        bitmaskTypeExpression.setHasNotColorRed(True)

        self.assertEqual(9, bitmaskTypeExpression.bitSizeOf())

    def testBitSizeOfRed(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.setColors(self.api.Colors.Values.RED)
        bitmaskTypeExpression.setHasColorRed(True)

        self.assertEqual(9, bitmaskTypeExpression.bitSizeOf())

    def testBitSizeOfGreen(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.setColors(self.api.Colors.Values.GREEN)
        bitmaskTypeExpression.setHasColorGreen(True)
        bitmaskTypeExpression.setHasNotColorRed(True)
        bitmaskTypeExpression.setHasOtherColorThanRed(True)

        self.assertEqual(11, bitmaskTypeExpression.bitSizeOf())

    def testBitSizeOfBlue(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.setColors(self.api.Colors.Values.BLUE)
        bitmaskTypeExpression.setHasColorBlue(True)
        bitmaskTypeExpression.setHasNotColorRed(True)
        bitmaskTypeExpression.setHasOtherColorThanRed(True)

        self.assertEqual(11, bitmaskTypeExpression.bitSizeOf())

    def testBitSizeOfBlueGreen(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.setColors(self.api.Colors.Values.BLUE | self.api.Colors.Values.GREEN)
        bitmaskTypeExpression.setHasColorBlue(True)
        bitmaskTypeExpression.setHasColorGreen(True)
        bitmaskTypeExpression.setHasNotColorRed(True)
        bitmaskTypeExpression.setHasOtherColorThanRed(True)

        self.assertEqual(12, bitmaskTypeExpression.bitSizeOf())

    def testBitSizeOfAllColors(self):
        bitmaskTypeExpression = self.api.BitmaskTypeExpression()
        bitmaskTypeExpression.setColors(self.api.Colors.Values.RED |
                                        self.api.Colors.Values.BLUE |
                                        self.api.Colors.Values.GREEN)
        bitmaskTypeExpression.setHasColorRed(True)
        bitmaskTypeExpression.setHasColorBlue(True)
        bitmaskTypeExpression.setHasColorGreen(True)
        bitmaskTypeExpression.setHasAllColors(True)
        bitmaskTypeExpression.setHasOtherColorThanRed(True)

        self.assertEqual(13, bitmaskTypeExpression.bitSizeOf())
