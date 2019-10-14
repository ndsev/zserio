import unittest
import zserio

from testutils import getZserioApi

class ExpressionConstAfterNestedTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").expression_const_after_nested_template_argument

    def testReadWrite(self):
        constAfterNested = self.api.ConstAfterNested.fromFields(
            self.api.Compound_Element_uint32_SIZE.fromFields(
                [self.api.Element_uint32.fromFields(i+1) for i in range(3)]
            )
        )
        writer = zserio.BitStreamWriter()
        constAfterNested.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readConstAfterNested = self.api.ConstAfterNested()
        readConstAfterNested.read(reader)
        self.assertEqual(constAfterNested, readConstAfterNested)
