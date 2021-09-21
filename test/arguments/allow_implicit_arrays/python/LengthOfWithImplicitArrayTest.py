import unittest

from testutils import getZserioApi

class LengthOfWithImplicitArray(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "allow_implicit_arrays.zs",
                               extraArgs=["-allowImplicitArrays"]).lengthof_with_implicit_array

    def testGetLengthOfImplicitArray(self):
        lengthOfWithImplicitArray = self.api.LengthOfWithImplicitArray()
        implicitArrayLength = 12
        lengthOfWithImplicitArray.implicit_array = list(range(implicitArrayLength))
        self.assertEqual(implicitArrayLength, lengthOfWithImplicitArray.get_length_of_implicit_array())
