import zserio

import Templates


class ExpressionEnumTemplateArgumentTest(Templates.TestCase):
    def testReadWrite(self):
        enumTemplateArgument_Color = self.api.EnumTemplateArgument_Color(False, 10)
        self.assertTrue(enumTemplateArgument_Color.is_expression_field_used())

        enumTemplateArgumentHolder = self.api.EnumTemplateArgumentHolder(enumTemplateArgument_Color)
        writer = zserio.BitStreamWriter()
        enumTemplateArgumentHolder.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEnumTemplateArgumentHolder = self.api.EnumTemplateArgumentHolder()
        readEnumTemplateArgumentHolder.read(reader)
        self.assertEqual(enumTemplateArgumentHolder, readEnumTemplateArgumentHolder)
