import zserio

import Templates


class ExpressionEnumTemplateArgumentConflictTest(Templates.TestCase):
    def testReadWrite(self):
        enumTemplateArgumentConflict_Letters = self.api.EnumTemplateArgumentConflict_Letters(False, 10)
        self.assertTrue(enumTemplateArgumentConflict_Letters.is_expression_field_used())

        enumTemplateArgumentConflictHolder = self.api.EnumTemplateArgumentConflictHolder(
            enumTemplateArgumentConflict_Letters
        )
        writer = zserio.BitStreamWriter()
        enumTemplateArgumentConflictHolder.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readEnumTemplateArgumentConflictHolder = self.api.EnumTemplateArgumentConflictHolder()
        readEnumTemplateArgumentConflictHolder.read(reader)
        self.assertEqual(enumTemplateArgumentConflictHolder, readEnumTemplateArgumentConflictHolder)
