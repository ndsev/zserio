import unittest
import zserio

from testutils import getZserioApi

class ExpressionConstTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").expression_const_template_argument

    def testReadWrite(self):
        constTemplateArgument_LENGTH = self.api.ConstTemplateArgument_LENGTH.fromFields([0 for i in range(10)],
                                                                                        10)
        constTemplateArgumentHolder = (self.api.ConstTemplateArgumentHolder.
                                       fromFields(constTemplateArgument_LENGTH))
        self.assertTrue(constTemplateArgumentHolder.getConstTemplateArgument().hasExtraField())

        writer = zserio.BitStreamWriter()
        constTemplateArgumentHolder.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readConstTemplateArgumentHolder = self.api.ConstTemplateArgumentHolder()
        readConstTemplateArgumentHolder.read(reader)
        self.assertEqual(constTemplateArgumentHolder, readConstTemplateArgumentHolder)
