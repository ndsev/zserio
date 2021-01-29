import unittest
import zserio

from testutils import getZserioApi

class InstantiateTypeImportedAsStructFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_type_imported_as_struct_field

    def testReadWrite(self):
        instantiateTypeImportedAsStructField = self.api.InstantiateTypeImportedAsStructField(
            self.api.pkg.Test32(13)
        )

        writer = zserio.BitStreamWriter()
        instantiateTypeImportedAsStructField.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiateTypeImportedAsStructField = self.api.InstantiateTypeImportedAsStructField()
        readInstantiateTypeImportedAsStructField.read(reader)
        self.assertEqual(instantiateTypeImportedAsStructField, readInstantiateTypeImportedAsStructField)
