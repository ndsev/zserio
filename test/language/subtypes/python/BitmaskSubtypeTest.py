import unittest

from testutils import getZserioApi

class BitmaskSubtypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "subtypes.zs").bitmask_subtype

    def testSubtype(self):
        self.assertEqual(self.api.Permission.Values.READ, self.api.CONST_READ)
