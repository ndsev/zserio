import unittest

from testutils import getZserioApi

class LengthOfOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").lengthof_operator

    def testGetLengthOfFixedArray(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        fixedArrayLength = 10
        lengthOfFunctions.fixed_array = list(range(fixedArrayLength))
        self.assertEqual(fixedArrayLength, lengthOfFunctions.funcGetLengthOfFixedArray())

    def testGetLengthOfVariableArray(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        variableArrayLength = 11
        lengthOfFunctions.num_elements = variableArrayLength
        lengthOfFunctions.variable_array = list(range(variableArrayLength))
        self.assertEqual(variableArrayLength, lengthOfFunctions.funcGetLengthOfVariableArray())

    def testGetLengthOfImplicitArray(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        implicitArrayLength = 12
        lengthOfFunctions.implicit_array = list(range(implicitArrayLength))
        self.assertEqual(implicitArrayLength, lengthOfFunctions.funcGetLengthOfImplicitArray())
