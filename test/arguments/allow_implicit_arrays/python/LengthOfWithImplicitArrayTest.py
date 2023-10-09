import AllowImplicitArrays

class LengthOfWithImplicitArray(AllowImplicitArrays.TestCase):
    def testGetLengthOfImplicitArray(self):
        lengthOfWithImplicitArray = self.api.LengthOfWithImplicitArray()
        implicitArrayLength = 12
        lengthOfWithImplicitArray.implicit_array = list(range(implicitArrayLength))
        self.assertEqual(implicitArrayLength, lengthOfWithImplicitArray.get_length_of_implicit_array())
