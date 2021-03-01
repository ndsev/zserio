import unittest
import zserio

from testutils import getZserioApi

class StructLongTemplateArgumentClashTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_long_template_argument_clash

    def testReadWrite(self):
        t1 = self.api.TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_619A1B35(
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringV")
        )
        t2 = self.api.TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_1B45EF08(
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            self.api.ThisIsVeryVeryVeryLongNamedStructure_(42)
        )
        structLongTemplateArgumentClash = self.api.StructLongTemplateArgumentClash(t1, t2)

        writer = zserio.BitStreamWriter()
        structLongTemplateArgumentClash.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructLongTemplateArgumentClash = self.api.StructLongTemplateArgumentClash()
        readStructLongTemplateArgumentClash.read(reader)
        self.assertEqual(structLongTemplateArgumentClash, readStructLongTemplateArgumentClash)
