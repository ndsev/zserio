import unittest
import zserio

from testutils import getZserioApi

class InstantiateNestedTemplateTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_nested_template

    def testReadWrite(self):
        instantiateNestedTemplate = self.api.InstantiateNestedTemplate.fromFields(
            self.api.TStr.fromFields(self.api.NStr.fromFields("test"))
        )

        writer = zserio.BitStreamWriter()
        instantiateNestedTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiateNestedTemplate = self.api.InstantiateNestedTemplate()
        readInstantiateNestedTemplate.read(reader)
        self.assertEqual(instantiateNestedTemplate, readInstantiateNestedTemplate)
