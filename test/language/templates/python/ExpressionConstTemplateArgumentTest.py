import unittest
import zserio

from testutils import getZserioApi

class ExpressionConstTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").expression_const_template_argument

    def testReadWrite(self):
        constTemplateArgument_LENGTH = self.api.ConstTemplateArgument_LENGTH()
        constTemplateArgument_LENGTH.setOffsetsField([0 for i in range(20)])
        constTemplateArgument_LENGTH.setArrayField([0 for i in range(10)])
        # initializerField will be default
        constTemplateArgument_LENGTH.setOptionalField(1)
        constTemplateArgument_LENGTH.setConstraintField(10)
        constTemplateArgument_LENGTH.setBitField(3)
        self.assertEqual(10, constTemplateArgument_LENGTH.getInitializerField())
        self.assertTrue(constTemplateArgument_LENGTH.hasOptionalField())
        self.assertTrue(constTemplateArgument_LENGTH.funcCheck())

        constTemplateArgumentHolder = (self.api.ConstTemplateArgumentHolder.
                                       fromFields(constTemplateArgument_LENGTH))
        writer = zserio.BitStreamWriter()
        constTemplateArgumentHolder.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readConstTemplateArgumentHolder = self.api.ConstTemplateArgumentHolder()
        readConstTemplateArgumentHolder.read(reader)
        self.assertEqual(constTemplateArgumentHolder, readConstTemplateArgumentHolder)
