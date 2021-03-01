import unittest
import zserio

from testutils import getZserioApi

class InstantiateTypeOnParameterizedTemplateTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_type_on_parameterized_template

    def testReadWrite(self):
        instantiateTypeOnParameterizedTemplate = self.api.InstantiateTypeOnParameterizedTemplate(
            2, self.api.TestP(2, self.api.Parameterized(2, [13, 42]))
        )

        writer = zserio.BitStreamWriter()
        instantiateTypeOnParameterizedTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeOnParameterizedTemplate = self.api.InstantiateTypeOnParameterizedTemplate()
        readInstantiateTypeOnParameterizedTemplate.read(reader)
        self.assertEqual(instantiateTypeOnParameterizedTemplate, readInstantiateTypeOnParameterizedTemplate)
