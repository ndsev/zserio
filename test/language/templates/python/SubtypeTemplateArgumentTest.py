import unittest

from testutils import getZserioApi

class SubtypeTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").subtype_template_argument

    def testBitSizeOf(self):
        field_uint32 = self.api.Field_uint32(10)
        field_compound = self.api.Field_Compound(self.api.Compound(10))
        subtypeTemplateArgument = self.api.SubtypeTemplateArgument(field_uint32, field_uint32,
                                                                   field_uint32, field_compound,
                                                                   field_compound,
                                                                   field_compound)
        self.assertEqual(192, subtypeTemplateArgument.bitSizeOf())
