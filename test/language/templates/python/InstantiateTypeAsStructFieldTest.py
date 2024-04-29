import zserio

import Templates


class InstantiateTypeAsStructFieldTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateTypeAsStructField = self.api.InstantiateTypeAsStructField(self.api.Test32(13))

        writer = zserio.BitStreamWriter()
        instantiateTypeAsStructField.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeAsStructField = self.api.InstantiateTypeAsStructField()
        readInstantiateTypeAsStructField.read(reader)
        self.assertEqual(instantiateTypeAsStructField, readInstantiateTypeAsStructField)
