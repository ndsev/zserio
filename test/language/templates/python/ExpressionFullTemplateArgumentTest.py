import zserio

import Templates

class ExpressionFullTemplateArgumentTest(Templates.TestCase):
    def testReadWrite(self):
        colorInternal = self.api.FullTemplateArgument_Color_7C6F461F(False, 10)
        self.assertTrue(colorInternal.is_expression_field_used())

        colorExternal = self.api.FullTemplateArgument_Color_F30EBCB3(False, 10)
        self.assertFalse(colorExternal.is_expression_field_used())

        fullTemplateArgumentHolder = self.api.FullTemplateArgumentHolder(colorInternal, colorExternal)
        writer = zserio.BitStreamWriter()
        fullTemplateArgumentHolder.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readFullTemplateArgumentHolder = self.api.FullTemplateArgumentHolder()
        readFullTemplateArgumentHolder.read(reader)
        self.assertEqual(fullTemplateArgumentHolder, readFullTemplateArgumentHolder)
