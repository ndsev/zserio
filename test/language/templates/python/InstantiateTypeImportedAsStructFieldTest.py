import zserio

import Templates


class InstantiateTypeImportedAsStructFieldTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateTypeImportedAsStructField = self.api.InstantiateTypeImportedAsStructField(
            self.api.pkg.Test32(13)
        )

        writer = zserio.BitStreamWriter()
        instantiateTypeImportedAsStructField.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeImportedAsStructField = self.api.InstantiateTypeImportedAsStructField()
        readInstantiateTypeImportedAsStructField.read(reader)
        self.assertEqual(instantiateTypeImportedAsStructField, readInstantiateTypeImportedAsStructField)
