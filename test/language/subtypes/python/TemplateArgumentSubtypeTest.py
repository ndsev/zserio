import unittest

from testutils import getZserioApi

class TemplateArgumentSubtypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "subtypes.zs").template_argument_subtype

    def testSubtype(self):
        field_uint32 = self.api.Field_uint32.fromFields(10)
        field_compound = self.api.Field_Compound.fromFields(self.api.Compound.fromFields(10))
        templateArgumentStructure = self.api.TemplateArgumentStructure.fromFields(field_uint32, field_uint32,
                                                                                  field_uint32, field_compound,
                                                                                  field_compound,
                                                                                  field_compound)
        self.assertEqual(192, templateArgumentStructure.bitSizeOf())
