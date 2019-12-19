import unittest
import zserio

from testutils import getZserioApi

class StructTemplateClashTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_template_clash

    def testReadWrite(self):
        testStruct_uint32 = self.api.TestStruct_uint32.fromFields(
            42,
            self.api.Template_A_B_C_7FE93D34.fromFields(self.api.A_B.fromFields(1),
                                                        self.api.C.fromFields(True)),
            self.api.Template_A_B_C_5EB4E3FC.fromFields(self.api.A.fromFields(1),
                                                        self.api.B_C.fromFields("string")))
        instantiationNameClash = self.api.InstantiationNameClash.fromFields(testStruct_uint32)

        writer = zserio.BitStreamWriter()
        instantiationNameClash.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiationNameClash = self.api.InstantiationNameClash()
        readInstantiationNameClash.read(reader)
        self.assertEqual(instantiationNameClash, readInstantiationNameClash)
