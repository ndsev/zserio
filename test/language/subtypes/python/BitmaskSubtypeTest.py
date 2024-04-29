import Subtypes


class BitmaskSubtypeTest(Subtypes.TestCase):
    def testSubtype(self):
        self.assertEqual(self.api.Permission.Values.READ, self.api.CONST_READ)
