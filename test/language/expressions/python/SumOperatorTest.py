import unittest

from testutils import getZserioApi

class SumOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").sum_operator

    def testGetSumFixedArray(self):
        fixedArrayData = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        sumFunction = self.api.SumFunction.fromFields(fixedArrayData)

        expectedSum = 0
        for element in fixedArrayData:
            expectedSum += element
        self.assertEqual(expectedSum, sumFunction.funcGetSumFixedArray())
