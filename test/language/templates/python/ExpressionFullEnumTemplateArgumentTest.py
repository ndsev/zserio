import unittest
import zserio

from testutils import getZserioApi

class ExpressionFullEnumTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").expression_full_enum_template_argument

    def testReadWrite(self):
        colorInternal = self.api.FullEnumTemplateArgument_Color.fromFields(False, 10)
        colorExternal = (self.api.
                         FullEnumTemplateArgument_templates_expression_full_enum_template_argument_color_Color.
                         fromFields(False, 10))
        fullEnumTemplateArgumentHolder = self.api.FullEnumTemplateArgumentHolder.fromFields(colorInternal,
                                                                                            colorExternal)
        self.assertTrue(fullEnumTemplateArgumentHolder.getEnumTemplateArgumentInternal().hasExpressionField())
        self.assertFalse(fullEnumTemplateArgumentHolder.getEnumTemplateArgumentExternal().hasExpressionField())

        writer = zserio.BitStreamWriter()
        fullEnumTemplateArgumentHolder.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readFullEnumTemplateArgumentHolder = self.api.FullEnumTemplateArgumentHolder()
        readFullEnumTemplateArgumentHolder.read(reader)
        self.assertEqual(fullEnumTemplateArgumentHolder, readFullEnumTemplateArgumentHolder)
