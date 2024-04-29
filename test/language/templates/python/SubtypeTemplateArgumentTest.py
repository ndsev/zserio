import Templates


class SubtypeTemplateArgumentTest(Templates.TestCase):
    def testBitSizeOf(self):
        field_uint32 = self.api.Field_uint32(10)
        field_compound = self.api.Field_Compound(self.api.Compound(10))
        subtypeTemplateArgument = self.api.SubtypeTemplateArgument(
            field_uint32, field_uint32, field_uint32, field_compound, field_compound, field_compound
        )
        self.assertEqual(192, subtypeTemplateArgument.bitsizeof())
