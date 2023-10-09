import zserio

import Templates

class StructTemplatedFieldTest(Templates.TestCase):
    def testReadWrite(self):
        structTemplatedField = self.api.StructTemplatedField(
            self.api.Field_uint32(13),
            self.api.Field_Compound(self.api.Compound(42)),
            self.api.Field_string("string")
        )

        writer = zserio.BitStreamWriter()
        structTemplatedField.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructTemplatedField = self.api.StructTemplatedField()
        readStructTemplatedField.read(reader)
        self.assertEqual(structTemplatedField, readStructTemplatedField)
