import unittest

from testutils import getZserioApi

class SubtypeTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "subtypes.zs").subtype_template_argument

    def bitSizeOf(self):
        field_uint32 = self.api.Field_uint32.fromFields(10)
        field_compound = self.api.Field_Compound.fromFields(self.api.Compound.fromFields(10))
        subtypeTemplateArgument = self.api.SubtypeTemplateArgument.fromFields(field_uint32, field_uint32,
                                                                              field_uint32, field_compound,
                                                                              field_compound,
                                                                              field_compound)
        self.assertEqual(192, subtypeTemplateArgument.bitSizeOf())
