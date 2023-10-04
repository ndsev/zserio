import unittest

from testutils import getZserioApi

class EnumUsedByEnumTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "enumeration_types.zs",
                               extraArgs=["-withTypeInfoCode"]).enum_used_by_enum

    def testLightColor(self):
        self.assertEqual(VALUE_LIGHT_RED, self.api.LightColor.LIGHT_RED.value)
        self.assertEqual(VALUE_LIGHT_GREEN, self.api.LightColor.LIGHT_GREEN.value)
        self.assertEqual(VALUE_LIGHT_BLUE, self.api.LightColor.LIGHT_BLUE.value)

    def testDarkColor(self):
        self.assertEqual(VALUE_DARK_RED, self.api.DarkColor.DARK_RED.value)
        self.assertEqual(VALUE_DARK_GREEN, self.api.DarkColor.DARK_GREEN.value)
        self.assertEqual(VALUE_DARK_BLUE, self.api.DarkColor.DARK_BLUE.value)

    def testColor(self):
        self.assertEqual(VALUE_NONE, self.api.Color.NONE.value)
        self.assertEqual(VALUE_LIGHT_RED, self.api.Color.LIGHT_RED.value)
        self.assertEqual(VALUE_LIGHT_GREEN, self.api.Color.LIGHT_GREEN.value)
        self.assertEqual(VALUE_LIGHT_BLUE, self.api.Color.LIGHT_BLUE.value)
        self.assertEqual(VALUE_LIGHT_PINK, self.api.Color.LIGHT_PINK.value)
        self.assertEqual(VALUE_DARK_RED, self.api.Color.DARK_RED.value)
        self.assertEqual(VALUE_DARK_GREEN, self.api.Color.DARK_GREEN.value)
        self.assertEqual(VALUE_DARK_BLUE, self.api.Color.DARK_BLUE.value)
        self.assertEqual(VALUE_DARK_PINK, self.api.Color.DARK_PINK.value)

VALUE_NONE = 0x00
VALUE_LIGHT_RED = 0x01
VALUE_LIGHT_GREEN = 0x02
VALUE_LIGHT_BLUE = 0x03
VALUE_LIGHT_PINK = 0x04
VALUE_DARK_RED = 0x11
VALUE_DARK_GREEN = 0x12
VALUE_DARK_BLUE = 0x13
VALUE_DARK_PINK = 0x14
