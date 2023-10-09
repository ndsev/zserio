import zserio

import Templates

class UnionTemplatedFieldTest(Templates.TestCase):
    def testReadWrite(self):
        uintUnion = self.api.TemplatedUnion_uint16_uint32(field1_=42)
        floatUnion = self.api.TemplatedUnion_float32_float64()
        floatUnion.field2 = 4.2
        compoundUnion = self.api.TemplatedUnion_Compound_uint16_Compound_uint32(
            field3_=self.api.Compound_Compound_uint16(self.api.Compound_uint16(13))
        )
        unionTemplatedField = self.api.UnionTemplatedField(uintUnion, floatUnion, compoundUnion)

        writer = zserio.BitStreamWriter()
        unionTemplatedField.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readUnionTemplatedField = self.api.UnionTemplatedField()
        readUnionTemplatedField.read(reader)
        self.assertEqual(unionTemplatedField, readUnionTemplatedField)
