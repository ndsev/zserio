import unittest
import zserio

from testutils import getZserioApi

class InstantiateWithInstantiateTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_with_instantiate_template_argument

    def testReadWrite(self):
        instantiateWithInstantiateTemplateArgument = self.api.InstantiateWithInstantiateTemplateArgument(
            self.api.Other8(self.api.Data8(13)),
            self.api.Other32(self.api.Data32(0xCAFE))
        )

        writer = zserio.BitStreamWriter()
        instantiateWithInstantiateTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateWithInstantiateTemplateArgument = self.api.InstantiateWithInstantiateTemplateArgument()
        readInstantiateWithInstantiateTemplateArgument.read(reader)
        self.assertEqual(instantiateWithInstantiateTemplateArgument,
                         readInstantiateWithInstantiateTemplateArgument)
