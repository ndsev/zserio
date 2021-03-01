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
        self.assertEqual(fixedArrayLength, lengthOfFunctions.func_get_length_of_fixed_array())

    def testGetLengthOfVariableArray(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        variableArrayLength = 11
        lengthOfFunctions.num_elements = variableArrayLength
        lengthOfFunctions.variable_array = list(range(variableArrayLength))
        self.assertEqual(variableArrayLength, lengthOfFunctions.func_get_length_of_variable_array())

    def testGetLengthOfImplicitArray(self):
        lengthOfFunctions = self.api.LengthOfFunctions()
        implicitArrayLength = 12
        lengthOfFunctions.implicit_array = list(range(implicitArrayLength))
        self.assertEqual(implicitArrayLength, lengthOfFunctions.func_get_length_of_implicit_array())
