import unittest
import zserio

from testutils import getZserioApi

class InstantiateTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_template_argument

    def testReadWrite(self):
        instantiateTemplateArgument = self.api.InstantiateTemplateArgument.fromFields(
            self.api.Other_Str.fromFields(self.api.Str.fromFields("test"))
        )

        writer = zserio.BitStreamWriter()
        instantiateTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiateTemplateArgument = self.api.InstantiateTemplateArgument()
        readInstantiateTemplateArgument.read(reader)
        self.assertEqual(instantiateTemplateArgument, readInstantiateTemplateArgument)
