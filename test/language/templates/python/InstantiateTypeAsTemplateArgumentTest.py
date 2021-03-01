import unittest
import zserio

from testutils import getZserioApi

class InstantiateTypeAsTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_type_as_template_argument

    def testReadWrite(self):
        instantiateTypeAsTemplateArgument = self.api.InstantiateTypeAsTemplateArgument(
            self.api.Other_Str(self.api.Str("test"))
        )

        writer = zserio.BitStreamWriter()
        instantiateTypeAsTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeAsTemplateArgument = self.api.InstantiateTypeAsTemplateArgument()
        readInstantiateTypeAsTemplateArgument.read(reader)
        self.assertEqual(instantiateTypeAsTemplateArgument, readInstantiateTypeAsTemplateArgument)
