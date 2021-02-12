import unittest
import zserio

from testutils import getZserioApi

class SubtypeTemplateWithCompoundTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").subtype_template_with_compound

    def testReadWrite(self):
        subtypeTemplateWithCompound = self.api.SubtypeTemplateWithCompound(
            self.api.Compound(13),
            self.api.TemplateCompound_Compound(self.api.Compound(42))
        )

        writer = zserio.BitStreamWriter()
        subtypeTemplateWithCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readSubtypeTemplateWithCompound = self.api.SubtypeTemplateWithCompound()
        readSubtypeTemplateWithCompound.read(reader)
        self.assertEqual(subtypeTemplateWithCompound, readSubtypeTemplateWithCompound)
