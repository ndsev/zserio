import unittest

from testutils import getZserioApi

class EnumDefinedByConstantTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "enumeration_types.zs",
                               extraArgs=["-withTypeInfoCode"]).enum_defined_by_constant

    def testLightColor(self):
        self.assertEqual(1, self.api.WHITE_COLOR)
        self.assertEqual(1, self.api.Colors.WHITE.value)
        self.assertEqual(self.api.Colors.WHITE.value + 1, self.api.Colors.BLACK.value)
