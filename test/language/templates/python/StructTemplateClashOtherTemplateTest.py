import unittest
import zserio

from testutils import getZserioApi

class StructTemplateClashOtherTemplateTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_template_clash_other_template

    def testReadWrite(self):
        instantiationNameClashOtherTemplate = self.api.InstantiationNameClashOtherTemplate.fromFields(
            self.api.Test_A_uint32_FA82A3B7.fromFields(42),
            self.api.Test_A_uint32_5D68B0C2.fromFields(self.api.A_uint32.fromFields(10)))

        writer = zserio.BitStreamWriter()
        instantiationNameClashOtherTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiationNameClashOtherTemplate = self.api.InstantiationNameClashOtherTemplate()
        readInstantiationNameClashOtherTemplate.read(reader)
        self.assertEqual(instantiationNameClashOtherTemplate, readInstantiationNameClashOtherTemplate)
