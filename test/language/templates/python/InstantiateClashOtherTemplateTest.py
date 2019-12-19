import unittest
import zserio

from testutils import getZserioApi

class InstantiateClashOtherTemplateTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_clash_other_template

    def testReadWrite(self):
        instantiateClashOtherTemplate = self.api.InstantiateClashOtherTemplate.fromFields(
            self.api.Test_uint32_99604043.fromFields(13))

        writer = zserio.BitStreamWriter()
        instantiateClashOtherTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiateClashOtherTemplate = self.api.InstantiateClashOtherTemplate()
        readInstantiateClashOtherTemplate.read(reader)
        self.assertEqual(instantiateClashOtherTemplate, readInstantiateClashOtherTemplate)
