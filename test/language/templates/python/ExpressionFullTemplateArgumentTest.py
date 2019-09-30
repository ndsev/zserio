import unittest
import zserio

from testutils import getZserioApi

class ExpressionFullTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").expression_full_template_argument

    def testReadWrite(self):
        colorInternal = self.api.FullTemplateArgument_Color.fromFields(False, 10)
        colorExternal = (self.api.FullTemplateArgument_templates_expression_full_template_argument_color_Color.
                         fromFields(False, 10))
        fullTemplateArgumentHolder = self.api.FullTemplateArgumentHolder.fromFields(colorInternal,
                                                                                            colorExternal)
        self.assertTrue(fullTemplateArgumentHolder.getTemplateArgumentInternal().hasExpressionField())
        self.assertFalse(fullTemplateArgumentHolder.getTemplateArgumentExternal().hasExpressionField())

        writer = zserio.BitStreamWriter()
        fullTemplateArgumentHolder.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readFullTemplateArgumentHolder = self.api.FullTemplateArgumentHolder()
        readFullTemplateArgumentHolder.read(reader)
        self.assertEqual(fullTemplateArgumentHolder, readFullTemplateArgumentHolder)
