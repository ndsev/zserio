import unittest
import zserio

from testutils import getZserioApi

class ExpressionBitmaskTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").expression_bitmask_template_argument

    def testReadWrite(self):
        bitmaskTemplateArgument_Permission = self.api.BitmaskTemplateArgument_Permission.fromFields(False, 10)
        self.assertTrue(bitmaskTemplateArgument_Permission.hasExpressionField())

        bitmaskTemplateArgumentHolder = self.api.BitmaskTemplateArgumentHolder.fromFields(
            bitmaskTemplateArgument_Permission
        )
        writer = zserio.BitStreamWriter()
        bitmaskTemplateArgumentHolder.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readBitmaskTemplateArgumentHolder = self.api.BitmaskTemplateArgumentHolder()
        readBitmaskTemplateArgumentHolder.read(reader)
        self.assertEqual(bitmaskTemplateArgumentHolder, readBitmaskTemplateArgumentHolder)
