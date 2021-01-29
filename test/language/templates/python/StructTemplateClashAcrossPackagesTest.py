import unittest
import zserio

from testutils import getZserioApi

class StructTemplateClashAcrossPackagesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_template_clash_across_packages

    def testReadWriteInPkg1(self):
        instantiationInPkg1 = self.api.pkg1.InstantiationInPkg1(
            self.api.test_struct.TestStruct_Test_639610D0(self.api.pkg1.Test(42)))

        writer = zserio.BitStreamWriter()
        instantiationInPkg1.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiationInPkg1 = self.api.pkg1.InstantiationInPkg1()
        readInstantiationInPkg1.read(reader)
        self.assertEqual(instantiationInPkg1, readInstantiationInPkg1)

    def testReadWriteInPkg2(self):
        instantiationInPkg2 = self.api.pkg2.InstantiationInPkg2(
            self.api.test_struct.TestStruct_Test_67B82BA5(self.api.pkg2.Test("string")))

        writer = zserio.BitStreamWriter()
        instantiationInPkg2.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiationInPkg2 = self.api.pkg2.InstantiationInPkg2()
        readInstantiationInPkg2.read(reader)
        self.assertEqual(instantiationInPkg2, readInstantiationInPkg2)
