import zserio

import StructureTypes

# this test is mainly for C++, so just check that it is ok
class FieldConstructorClashingTest(StructureTypes.TestCase):
    def testWriteRead(self):
        fieldConstructorClashing = self._createFieldConstructorClashing()

        writer = zserio.BitStreamWriter()
        fieldConstructorClashing.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readFieldConstructorClashing = self.api.FieldConstructorClashing.from_reader(reader)
        self.assertEqual(fieldConstructorClashing, readFieldConstructorClashing)

    def _createFieldConstructorClashing(self):
        return self.api.FieldConstructorClashing(
            [self.api.CompoundRead(self.api.Field(self.FIELD1), self.api.Field(self.FIELD2))],
            [
                self.api.CompoundPackingRead(
                    self.api.Field(self.FIELD1), self.api.Field(self.FIELD2), self.api.Field(self.FIELD3)
                )
            ]
        )

    FIELD1 = 1
    FIELD2 = 9
    FIELD3 = 5
