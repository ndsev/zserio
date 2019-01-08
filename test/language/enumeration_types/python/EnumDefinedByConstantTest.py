import unittest

from testutils import getZserioApi

class EnumDefinedByConstantTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "enumeration_types.zs").enum_defined_by_constant

    def testLightColor(self):
        self.assertEqual(1, self.api.WHITE_COLOR)
        self.assertEqual(1, self.api.Colors.White.value)
        self.assertEqual(self.api.Colors.White.value + 1, self.api.Colors.Black.value)
