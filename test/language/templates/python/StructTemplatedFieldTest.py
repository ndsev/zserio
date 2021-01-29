import unittest
import zserio

from testutils import getZserioApi

class StructTemplatedFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_templated_field

    def testReadWrite(self):
        structTemplatedField = self.api.StructTemplatedField(
            self.api.Field_uint32(13),
            self.api.Field_Compound(self.api.Compound(42)),
            self.api.Field_string("string")
        )

        writer = zserio.BitStreamWriter()
        structTemplatedField.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructTemplatedField = self.api.StructTemplatedField()
        readStructTemplatedField.read(reader)
        self.assertEqual(structTemplatedField, readStructTemplatedField)
