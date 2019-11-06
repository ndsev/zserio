import unittest
import zserio

from testutils import getZserioApi

class InstantiateTypeAsStructFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_type_as_struct_field

    def testReadWrite(self):
        instantiateTypeAsStructField = self.api.InstantiateTypeAsStructField.fromFields(
            self.api.Test32.fromFields(13)
        )

        writer = zserio.BitStreamWriter()
        instantiateTypeAsStructField.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInstantiateTypeAsStructField = self.api.InstantiateTypeAsStructField()
        readInstantiateTypeAsStructField.read(reader)
        self.assertEqual(instantiateTypeAsStructField, readInstantiateTypeAsStructField)
