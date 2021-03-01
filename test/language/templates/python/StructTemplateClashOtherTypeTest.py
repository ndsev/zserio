import unittest
import zserio

from testutils import getZserioApi

class StructTemplateClashOtherTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_template_clash_other_type

    def testReadWrite(self):
        instantiationNameClashOtherType = self.api.InstantiationNameClashOtherType(
            self.api.Test_uint32_99604043(42))

        writer = zserio.BitStreamWriter()
        instantiationNameClashOtherType.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiationNameClashOtherType = self.api.InstantiationNameClashOtherType()
        readInstantiationNameClashOtherType.read(reader)
        self.assertEqual(instantiationNameClashOtherType, readInstantiationNameClashOtherType)
