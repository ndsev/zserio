import unittest

from testutils import getZserioApi

class EnumSubtypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "subtypes.zs").enum_subtype

    def testSubtype(self):
        self.assertEqual(self.api.Color.BLACK, self.api.CONST_BLACK)
