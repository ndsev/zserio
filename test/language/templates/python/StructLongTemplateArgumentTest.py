import unittest
import zserio

from testutils import getZserioApi

class StructLongTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_long_template_argument

    def testReadWrite(self):
        templ = self.api.TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_(
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringV")
        )
        structLongTemplateArgument = self.api.StructLongTemplateArgument(templ)

        writer = zserio.BitStreamWriter()
        structLongTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructLongTemplateArgument = self.api.StructLongTemplateArgument()
        readStructLongTemplateArgument.read(reader)
        self.assertEqual(structLongTemplateArgument, readStructLongTemplateArgument)
