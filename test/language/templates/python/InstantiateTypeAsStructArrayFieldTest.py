import unittest
import zserio

from testutils import getZserioApi

class InstantiateTypeAsStructArrayFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_type_as_struct_array_field

    def testReadWrite(self):
        instantiateTypeAsStructArrayField = self.api.InstantiateTypeAsStructArrayField([self.api.Test32(13),
                                                                                        self.api.Test32(17),
                                                                                        self.api.Test32(23)])

        writer = zserio.BitStreamWriter()
        instantiateTypeAsStructArrayField.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeAsStructArrayField = self.api.InstantiateTypeAsStructArrayField()
        readInstantiateTypeAsStructArrayField.read(reader)
        self.assertEqual(instantiateTypeAsStructArrayField, readInstantiateTypeAsStructArrayField)
